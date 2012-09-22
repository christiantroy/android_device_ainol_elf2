/* alsa_default.cpp
 **
 ** Copyright 2009 Wind River Systems
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

#define LOG_TAG "ALSAModule"
#include <utils/Log.h>

#include "AudioHardwareALSA.h"
#include <media/AudioRecord.h>
#include <cutils/properties.h>

#undef DISABLE_HARWARE_RESAMPLING

#define ALSA_NAME_MAX 128

#define ALSA_STRCAT(x,y) \
    if (strlen(x) + strlen(y) < ALSA_NAME_MAX) \
        strcat(x, y);

#ifndef ALSA_DEFAULT_SAMPLE_RATE
#define ALSA_DEFAULT_SAMPLE_RATE 44100	 // in Hz
#endif

namespace android_audio_legacy
{
using android::Mutex;

static int s_device_open(const hw_module_t*, const char*, hw_device_t**);
static int s_device_close(hw_device_t*);
static status_t s_init(alsa_device_t *, ALSAHandleList &);
static status_t s_open(alsa_handle_t *, uint32_t, int);
static status_t s_close(alsa_handle_t *);
static status_t s_route(alsa_handle_t *, uint32_t, int);

static hw_module_methods_t s_module_methods = {
    open            : s_device_open
};

extern "C" hw_module_t HAL_MODULE_INFO_SYM = {
    tag             : HARDWARE_MODULE_TAG,
    version_major   : 1,
    version_minor   : 0,
    id              : ALSA_HARDWARE_MODULE_ID,
    name            : "ALSA module",
    author          : "Wind River",
    methods         : &s_module_methods,
    dso             : 0,
    reserved        : { 0, },
};

static int s_device_open(const hw_module_t* module, const char* name,
        hw_device_t** device)
{
    alsa_device_t *dev;
    dev = (alsa_device_t *) malloc(sizeof(*dev));
    if (!dev) return -ENOMEM;

    memset(dev, 0, sizeof(*dev));

    /* initialize the procs */
    dev->common.tag = HARDWARE_DEVICE_TAG;
    dev->common.version = 0;
    dev->common.module = (hw_module_t *) module;
    dev->common.close = s_device_close;
    dev->init = s_init;
    dev->open = s_open;
    dev->close = s_close;
    dev->route = s_route;

    *device = &dev->common;
    return 0;
}

static int s_device_close(hw_device_t* device)
{
    free(device);
    return 0;
}

// ----------------------------------------------------------------------------

static const int DEFAULT_SAMPLE_RATE = ALSA_DEFAULT_SAMPLE_RATE;

static const char *devicePrefix[SND_PCM_STREAM_LAST + 1] = {
        /* SND_PCM_STREAM_PLAYBACK : */"AndroidPlayback",
        /* SND_PCM_STREAM_CAPTURE  : */"AndroidCapture",
};

static alsa_handle_t _defaultsOut = {
    module      : 0,
    devices     : AudioSystem::DEVICE_OUT_ALL,
    curDev      : 0,
    curMode     : 0,
    handle      : 0,
    format      : SND_PCM_FORMAT_S16_LE, // AudioSystem::PCM_16_BIT
    channels    : 2,
    sampleRate  : DEFAULT_SAMPLE_RATE,
    latency     : 100000, // Desired Delay in usec
    bufferSize  : 2048, // Desired Number of samples
    mLock       : PTHREAD_MUTEX_INITIALIZER,
    modPrivate  : 0,
};
static const char* builtinAudio = "builtin-audio";

static alsa_handle_t _defaultsIn = {
    module      : 0,
    devices     : AudioSystem::DEVICE_IN_ALL,
    curDev      : 0,
    curMode     : 0,
    handle      : 0,
    format      : SND_PCM_FORMAT_S16_LE, // AudioSystem::PCM_16_BIT
    channels    : 2,
    sampleRate  : DEFAULT_SAMPLE_RATE,	//AudioRecord::DEFAULT_SAMPLE_RATE,
    latency     : 100000, // Desired Delay in usec
    bufferSize  : DEFAULT_SAMPLE_RATE/10, // Desired Number of samples
    mLock       : PTHREAD_MUTEX_INITIALIZER,
    modPrivate  : 0,
};

static const char* usbAudio = "usb-audio";

static alsa_handle_t _defaultsUSBIn = {
    module      : 0,
    devices     : AudioSystem::DEVICE_IN_ALL,
    curDev      : 0,
    curMode     : 0,
    handle      : 0,
    format      : SND_PCM_FORMAT_S16_LE, // AudioSystem::PCM_16_BIT
    channels    : 1,
    sampleRate  : DEFAULT_SAMPLE_RATE,	//AudioRecord::DEFAULT_SAMPLE_RATE,
    latency     : 100000, // Desired Delay in usec
    bufferSize  : DEFAULT_SAMPLE_RATE/10, // Desired Number of samples
    mLock       : PTHREAD_MUTEX_INITIALIZER,
    modPrivate  : 0,
};

struct device_suffix_t {
    const AudioSystem::audio_devices device;
    const char *suffix;
};

/* The following table(s) need to match in order of the route bits
 */
static const device_suffix_t deviceSuffix[] = {
        {AudioSystem::DEVICE_OUT_EARPIECE,       "_Earpiece"},
        {AudioSystem::DEVICE_OUT_SPEAKER,        "_Speaker"},
        {AudioSystem::DEVICE_OUT_BLUETOOTH_SCO,  "_Bluetooth"},
        {AudioSystem::DEVICE_OUT_WIRED_HEADSET,  "_Headset"},
        {AudioSystem::DEVICE_OUT_BLUETOOTH_A2DP, "_Bluetooth-A2DP"},
};

static const int deviceSuffixLen = (sizeof(deviceSuffix)
        / sizeof(device_suffix_t));

// ----------------------------------------------------------------------------

snd_pcm_stream_t direction(alsa_handle_t *handle)
{
    return (handle->devices & AudioSystem::DEVICE_OUT_ALL) ? SND_PCM_STREAM_PLAYBACK
            : SND_PCM_STREAM_CAPTURE;
}

const char *deviceName(alsa_handle_t *handle, uint32_t device, int mode)
{
    static char devString[ALSA_NAME_MAX];
    int hasDevExt = 0;

    strcpy(devString, devicePrefix[direction(handle)]);

    for (int dev = 0; device && dev < deviceSuffixLen; dev++)
        if (device & deviceSuffix[dev].device) {
            ALSA_STRCAT (devString, deviceSuffix[dev].suffix);
            device &= ~deviceSuffix[dev].device;
            hasDevExt = 1;
        }

    if (hasDevExt) switch (mode) {
    case AudioSystem::MODE_NORMAL:
        ALSA_STRCAT (devString, "_normal")
        ;
        break;
    case AudioSystem::MODE_RINGTONE:
        ALSA_STRCAT (devString, "_ringtone")
        ;
        break;
    case AudioSystem::MODE_IN_CALL:
        ALSA_STRCAT (devString, "_incall")
        ;
        break;
    };

    return devString;
}

const char *streamName(alsa_handle_t *handle)
{
    return snd_pcm_stream_name(direction(handle));
}

static int getDeviceNum(snd_pcm_stream_t stream, char* card_name)
{
	int card = -1, amlcard = -1,dev,err;
	snd_ctl_t *handle;
	snd_ctl_card_info_t *info;
	snd_pcm_info_t *pcminfo;
	int default_card = -1;
	char prop[20];

	if (stream == SND_PCM_STREAM_CAPTURE)
		property_get("snd.card.default.card.capture", prop, "null");
	else  if (stream == SND_PCM_STREAM_PLAYBACK)
		property_get("snd.card.default.card.playback", prop, "null");
	
	if (strcmp(prop, "null") != 0)
		default_card = strtol(prop, NULL, 0);	
	ALOGE("prop =  %s, default_card = %d", prop, default_card);
	
	snd_ctl_card_info_alloca(&info);
	snd_pcm_info_alloca(&pcminfo);
	if (snd_card_next(&card) < 0 || card < 0) {
		ALOGE("no soundcards found...");
		return -1;
	}
	while (card >= 0) {
		char name[32];
		sprintf(name, "hw:%d", card);
		if ((err = snd_ctl_open(&handle, name, 0)) < 0) {
			ALOGE("control open (%i): %s", card, snd_strerror(err));
			goto next_card;
		}
		if ((err = snd_ctl_card_info(handle, info)) < 0) {
			ALOGE("control hardware info (%i): %s", card, snd_strerror(err));
			snd_ctl_close(handle);
			goto next_card;
		}
		dev = -1;
		while (1) {
			if (snd_ctl_pcm_next_device(handle, &dev)<0)
				ALOGE("snd_ctl_pcm_next_device");
			if (dev < 0){
				ALOGE("(dev < 0)");
				break;
			}
			snd_pcm_info_set_device(pcminfo, dev);
			snd_pcm_info_set_subdevice(pcminfo, 0);
			snd_pcm_info_set_stream(pcminfo, stream);
			if ((err = snd_ctl_pcm_info(handle, pcminfo)) < 0) {
				if (err != -ENOENT)
					ALOGE("control digital audio info (%i): %s", card, snd_strerror(err));
				continue;
			}
			/*
			* if default_card >= 0 , or same as the current found card, return it.
			* if default_card <  0 , enable USB audio first, if found
			* else, enable builtin-audio
			*/
			ALOGE("heming add snd_ctl_card_info_get_id=%s, default_card=%d, card=%d",snd_ctl_card_info_get_id(info), default_card, card);
			// save card name
			strcpy(card_name, snd_ctl_card_info_get_id(info));
			ALOGD("saved card name: %s\n", card_name);
			if ((default_card>=0) && (default_card==card))
				return card;
			else if ((strncmp(snd_ctl_card_info_get_id(info),"AML",3)==0) && (stream == SND_PCM_STREAM_PLAYBACK)&&(default_card==-1)) //set aml as default playback
				return card;
			else if ((strncmp(snd_ctl_card_info_get_id(info),"AML",3)!=0) && (stream == SND_PCM_STREAM_CAPTURE)&&(default_card==-1)) //set usb as default capture
				return card;
			else
				amlcard = card;
		}
		snd_ctl_close(handle);
		next_card:
		if (snd_card_next(&card) < 0) {
			ALOGE("snd_card_next");
			break;
		}
	}
	return amlcard;
}

status_t setHardwareParams(alsa_handle_t *handle)
{
    snd_pcm_hw_params_t *hardwareParams;
    status_t err;

    snd_pcm_uframes_t bufferSize = handle->bufferSize;
    unsigned int requestedRate = handle->sampleRate;
    unsigned int latency = handle->latency;

    // snd_pcm_format_description() and snd_pcm_format_name() do not perform
    // proper bounds checking.
    bool validFormat = (static_cast<int> (handle->format)
            > SND_PCM_FORMAT_UNKNOWN) && (static_cast<int> (handle->format)
            <= SND_PCM_FORMAT_LAST);
    const char *formatDesc = validFormat ? snd_pcm_format_description(
            handle->format) : "Invalid Format";
    const char *formatName = validFormat ? snd_pcm_format_name(handle->format)
            : "UNKNOWN";

    if (snd_pcm_hw_params_malloc(&hardwareParams) < 0) {
        LOG_ALWAYS_FATAL("Failed to allocate ALSA hardware parameters!");
        return NO_INIT;
    }

    err = snd_pcm_hw_params_any(handle->handle, hardwareParams);
    if (err < 0) {
        ALOGE("Unable to configure hardware: %s", snd_strerror(err));
        goto done;
    }

    // Set the interleaved read and write format.
    err = snd_pcm_hw_params_set_access(handle->handle, hardwareParams,
            SND_PCM_ACCESS_RW_INTERLEAVED);
    if (err < 0) {
        ALOGE("Unable to configure PCM read/write format: %s",
                snd_strerror(err));
        goto done;
    }

    err = snd_pcm_hw_params_set_format(handle->handle, hardwareParams,
            handle->format);
    if (err < 0) {
        ALOGE("Unable to configure PCM format %s (%s): %s",
                formatName, formatDesc, snd_strerror(err));
        goto done;
    }

    ALOGW("Set %s PCM format to %s (%s)", streamName(handle), formatName, formatDesc);

    err = snd_pcm_hw_params_set_channels(handle->handle, hardwareParams,
            handle->channels);
    if (err < 0) {
        ALOGE("Unable to set channel count to %i: %s",
                handle->channels, snd_strerror(err));
        goto done;
    }

    ALOGW("Using %i %s for %s.", handle->channels,
            handle->channels == 1 ? "channel" : "channels", streamName(handle));

    ALOGW("requestedRate=%d\n", requestedRate);
    err = snd_pcm_hw_params_set_rate_near(handle->handle, hardwareParams,
            &requestedRate, 0);
    ALOGW("returned Rate=%d, handle->rate=%d\n", requestedRate, handle->sampleRate);
    if (err < 0)
        ALOGE("Unable to set %s sample rate to %u: %s",
                streamName(handle), handle->sampleRate, snd_strerror(err));
    else if (requestedRate != handle->sampleRate)
        // Some devices have a fixed sample rate, and can not be changed.
        // This may cause resampling problems; i.e. PCM playback will be too
        // slow or fast.
        ALOGW("Requested rate (%u HZ) does not match actual rate (%u HZ)",
                handle->sampleRate, requestedRate);
    else
        ALOGI("Set %s sample rate to %u HZ", streamName(handle), requestedRate);

#ifdef DISABLE_HARWARE_RESAMPLING
    // Disable hardware re-sampling.
    err = snd_pcm_hw_params_set_rate_resample(handle->handle,
            hardwareParams,
            static_cast<int>(resample));
    if (err < 0) {
        ALOGE("Unable to %s hardware resampling: %s",
                resample ? "enable" : "disable",
                snd_strerror(err));
        goto done;
    }
#endif

    // Make sure we have at least the size we originally wanted
    err = snd_pcm_hw_params_set_buffer_size_near(handle->handle, hardwareParams,
            &bufferSize);

    if (err < 0) {
        ALOGE("Unable to set buffer size to %d:  %s",
                (int)bufferSize, snd_strerror(err));
        goto done;
    }

    // Setup buffers for latency
    err = snd_pcm_hw_params_set_buffer_time_near(handle->handle,
            hardwareParams, &latency, NULL);
    if (err < 0) {
        /* That didn't work, set the period instead */
        unsigned int periodTime = latency / 4;
        err = snd_pcm_hw_params_set_period_time_near(handle->handle,
                hardwareParams, &periodTime, NULL);
        if (err < 0) {
            ALOGE("Unable to set the period time for latency: %s", snd_strerror(err));
            goto done;
        }
        snd_pcm_uframes_t periodSize;
        err = snd_pcm_hw_params_get_period_size(hardwareParams, &periodSize,
                NULL);
        if (err < 0) {
            ALOGE("Unable to get the period size for latency: %s", snd_strerror(err));
            goto done;
        }
        bufferSize = periodSize * 4;
        if (bufferSize < handle->bufferSize) bufferSize = handle->bufferSize;
        err = snd_pcm_hw_params_set_buffer_size_near(handle->handle,
                hardwareParams, &bufferSize);
        if (err < 0) {
            ALOGE("Unable to set the buffer size for latency: %s", snd_strerror(err));
            goto done;
        }
    } else {
        // OK, we got buffer time near what we expect. See what that did for bufferSize.
        err = snd_pcm_hw_params_get_buffer_size(hardwareParams, &bufferSize);
        if (err < 0) {
            ALOGE("Unable to get the buffer size for latency: %s", snd_strerror(err));
            goto done;
        }
        // Does set_buffer_time_near change the passed value? It should.
        err = snd_pcm_hw_params_get_buffer_time(hardwareParams, &latency, NULL);
        if (err < 0) {
            ALOGE("Unable to get the buffer time for latency: %s", snd_strerror(err));
            goto done;
        }
        unsigned int periodTime = latency / 4;
        err = snd_pcm_hw_params_set_period_time_near(handle->handle,
                hardwareParams, &periodTime, NULL);
        if (err < 0) {
            ALOGE("Unable to set the period time for latency: %s", snd_strerror(err));
            goto done;
        }
    }

    ALOGI("Buffer size: %d", (int)bufferSize);
    ALOGI("Latency: %d", (int)latency);

    handle->bufferSize = bufferSize;
    handle->latency = latency;

    // Commit the hardware parameters back to the device.
    err = snd_pcm_hw_params(handle->handle, hardwareParams);
    if (err < 0) ALOGE("Unable to set hardware parameters: %s", snd_strerror(err));

    done:
    snd_pcm_hw_params_free(hardwareParams);

    return err;
}

status_t setSoftwareParams(alsa_handle_t *handle)
{
    snd_pcm_sw_params_t * softwareParams;
    int err;

    snd_pcm_uframes_t bufferSize = 0;
    snd_pcm_uframes_t periodSize = 0;
    snd_pcm_uframes_t startThreshold, stopThreshold;

    if (snd_pcm_sw_params_malloc(&softwareParams) < 0) {
        LOG_ALWAYS_FATAL("Failed to allocate ALSA software parameters!");
        return NO_INIT;
    }

    // Get the current software parameters
    err = snd_pcm_sw_params_current(handle->handle, softwareParams);
    if (err < 0) {
        ALOGE("Unable to get software parameters: %s", snd_strerror(err));
        goto done;
    }

    // Configure ALSA to start the transfer when the buffer is almost full.
    snd_pcm_get_params(handle->handle, &bufferSize, &periodSize);

    if (handle->devices & AudioSystem::DEVICE_OUT_ALL) {
        // For playback, configure ALSA to start the transfer when the
        // buffer is full.
        startThreshold = bufferSize - 1;
        stopThreshold = bufferSize;
    } else {
        // For recording, configure ALSA to start the transfer on the
        // first frame.
        startThreshold = 1;
        stopThreshold = bufferSize;
    }

    err = snd_pcm_sw_params_set_start_threshold(handle->handle, softwareParams,
            startThreshold);
    if (err < 0) {
        ALOGE("Unable to set start threshold to %lu frames: %s",
                startThreshold, snd_strerror(err));
        goto done;
    }

    err = snd_pcm_sw_params_set_stop_threshold(handle->handle, softwareParams,
            stopThreshold);
    if (err < 0) {
        ALOGE("Unable to set stop threshold to %lu frames: %s",
                stopThreshold, snd_strerror(err));
        goto done;
    }

    // Allow the transfer to start when at least periodSize samples can be
    // processed.
    err = snd_pcm_sw_params_set_avail_min(handle->handle, softwareParams,
            periodSize);
    if (err < 0) {
        ALOGE("Unable to configure available minimum to %lu: %s",
                periodSize, snd_strerror(err));
        goto done;
    }

    // Commit the software parameters back to the device.
    err = snd_pcm_sw_params(handle->handle, softwareParams);
    if (err < 0) ALOGE("Unable to configure software parameters: %s",
            snd_strerror(err));

    done:
    snd_pcm_sw_params_free(softwareParams);

    return err;
}

// ----------------------------------------------------------------------------

static status_t s_init(alsa_device_t *module, ALSAHandleList &list)
{
    list.clear();

    snd_pcm_uframes_t bufferSize = _defaultsOut.bufferSize;

    for (size_t i = 1; (bufferSize & ~i) != 0; i <<= 1)
        bufferSize &= ~i;

    _defaultsOut.module = module;
    _defaultsOut.bufferSize = bufferSize;

    list.push_back(_defaultsOut);
    
    bufferSize = _defaultsUSBIn.bufferSize;

	    for (size_t i = 1; (bufferSize & ~i) != 0; i <<= 1)
	        bufferSize &= ~i;

	    _defaultsUSBIn.module = module;
	    _defaultsUSBIn.bufferSize = bufferSize;
        _defaultsUSBIn.modPrivate = (void*)usbAudio;
	    list.push_back(_defaultsUSBIn);
        ALOGW("use USB audio in as default");


	    bufferSize = _defaultsIn.bufferSize;

	    for (size_t i = 1; (bufferSize & ~i) != 0; i <<= 1)
	        bufferSize &= ~i;

	    _defaultsIn.module = module;
	    _defaultsIn.bufferSize = bufferSize;
        _defaultsIn.modPrivate = (void*)builtinAudio;
	    list.push_back(_defaultsIn);
        ALOGW("use AML audio in as default");

	        return NO_ERROR;
}

static status_t s_open(alsa_handle_t *handle, uint32_t devices, int mode)
{
	// Close off previously opened device.
	// It would be nice to determine if the underlying device actually
	// changes, but we might be recovering from an error or manipulating
    // mixer settings (see asound.conf).
    //
    ALOGD("open called for devices %08x in mode %d...", devices, mode);
    if( devices == 0 ){
    	return BAD_VALUE;
	}
    s_close(handle);

    pthread_mutex_lock(&handle->mLock);

    const char *stream = streamName(handle);
    const char *devName = deviceName(handle, devices, mode);
    int err,card;
    char prop[20],dev_Name[20],card_name[32]; 
 ALOGD("input handle: %s, devName = %s \n", (char*)handle->modPrivate, devName);
#if 1 
    if ((direction(handle) == SND_PCM_STREAM_CAPTURE)/*||(direction(handle) == SND_PCM_STREAM_PLAYBACK)*/){
        card = getDeviceNum(direction(handle), card_name);
	ALOGD("card : %d\n", card);

        if(card >= 0){
           // sprintf(dev_Name,"plug:SLAVE='hw:%d,0'",card);
            sprintf(dev_Name, "hw:%d", card);
            devName = dev_Name;
        }
        // if we want usb-audio, but returned builtin-audio, return error.
        // audiopolicymanager should try next card
        ALOGD("card name: %s\n", card_name);
        ALOGD("devName: %s\n", devName);
        if(strncmp(card_name,"AML", 3) == 0 && strcmp((char*)handle->modPrivate, "usb-audio") == 0){
          
          pthread_mutex_unlock(&handle->mLock);
          ALOGD("You are request usb-audio with usb's params, but returned builtin-audio card\n");
          return NO_INIT;
        }
   }
    if(direction(handle) == SND_PCM_STREAM_PLAYBACK){
		card = snd_card_get_aml_card();
		ALOGD("SND_PCM_STREAM_PLAYBACK  card : %d\n", card);

		sprintf(dev_Name, "hw:%d", card);
		devName = dev_Name;
    }
#else

	if(direction(handle) == SND_PCM_STREAM_CAPTURE){
		sprintf(dev_Name, "hw:0");
		devName = dev_Name;
    	}

#endif	
    for (;;) {
        // The PCM stream is opened in blocking mode, per ALSA defaults.  The
        // AudioFlinger seems to assume blocking mode too, so asynchronous mode
        // should not be used.
         ALOGD("---- devName = %s \n", devName);

        err = snd_pcm_open(&handle->handle, devName, direction(handle),
                SND_PCM_ASYNC);
        if (err == 0) break;

        // See if there is a less specific name we can try.
        // Note: We are changing the contents of a const char * here.
        char *tail = strrchr(devName, '_');
        if (!tail) break;
        *tail = 0;
    }

    if (err < 0) {
        // None of the Android defined audio devices exist. Open a generic one.
        devName = "default";
         ALOGD("-r-- devName = %s \n", devName);

        err = snd_pcm_open(&handle->handle, devName, direction(handle), 0);
    }

    if (err < 0) {
        ALOGE("Failed to Initialize any ALSA %s device: %s",
                stream, strerror(err));
	 pthread_mutex_unlock(&handle->mLock);
        return NO_INIT;
    }

    err = setHardwareParams(handle);

    if (err == NO_ERROR) err = setSoftwareParams(handle);

    ALOGI("Initialized ALSA %s device %s", stream, devName);

    handle->curDev = devices;
    handle->curMode = mode;

    pthread_mutex_unlock(&handle->mLock);
    return err;
}

static status_t s_close(alsa_handle_t *handle)
{
    pthread_mutex_lock(&handle->mLock);

    status_t err = NO_ERROR;
    snd_pcm_t *h = handle->handle;
    handle->handle = 0;
    handle->curDev = 0;
    handle->curMode = 0;
    if (h) {
        snd_pcm_drain(h);
        err = snd_pcm_close(h);
    }

    pthread_mutex_unlock(&handle->mLock);
    return err;
}

static status_t s_route(alsa_handle_t *handle, uint32_t devices, int mode)
{
    ALOGD("route called for devices %08x in mode %d...", devices, mode);

    if (handle->handle && handle->curDev == devices && handle->curMode == mode) return NO_ERROR;

    return s_open(handle, devices, mode);
}

}
