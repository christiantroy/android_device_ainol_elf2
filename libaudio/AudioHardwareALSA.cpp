/* AudioHardwareALSA.cpp
 **
 ** Copyright 2008-2010 Wind River Systems
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

#include <errno.h>
#include <stdarg.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <dlfcn.h>

#define LOG_TAG "AudioHardwareALSA"
#include <utils/Log.h>
#include <utils/String8.h>

#include <cutils/properties.h>
#include <media/AudioRecord.h>
#include <hardware_legacy/power.h>

#include "AudioHardwareALSA.h"

bool mMicMute = 0;

extern "C"
{
    //
    // Function for dlsym() to look up for creating a new AudioHardwareInterface.
    //
    android_audio_legacy::AudioHardwareInterface *createAudioHardware(void) {
        return android_audio_legacy::AudioHardwareALSA::create();
    }
}         // extern "C"

namespace android_audio_legacy
{

// ----------------------------------------------------------------------------

static void ALSAErrorHandler(const char *file,
                             int line,
                             const char *function,
                             int err,
                             const char *fmt,
                             ...)
{
    char buf[BUFSIZ];
    va_list arg;
    int l;

    va_start(arg, fmt);
    l = snprintf(buf, BUFSIZ, "%s:%i:(%s) ", file, line, function);
    vsnprintf(buf + l, BUFSIZ - l, fmt, arg);
    buf[BUFSIZ-1] = '\0';
    ALOGE("ALSALib %s", buf);
    va_end(arg);
}

AudioHardwareInterface *AudioHardwareALSA::create() {
    return new AudioHardwareALSA();
}

AudioHardwareALSA::AudioHardwareALSA() :
    mALSADevice(0),
    mAcousticDevice(0)
{
    snd_lib_error_set_handler(&ALSAErrorHandler);
    mMixer = new ALSAMixer;

    hw_module_t *module;
    int err = hw_get_module(ALSA_HARDWARE_MODULE_ID,
            (hw_module_t const**)&module);

    if (err == 0) {
        hw_device_t* device;
        err = module->methods->open(module, ALSA_HARDWARE_NAME, &device);
        if (err == 0) {
            mALSADevice = (alsa_device_t *)device;
            mALSADevice->init(mALSADevice, mDeviceList);
        } else
            ALOGE("ALSA Module could not be opened!!!");
    } else
        ALOGE("ALSA Module not found!!!");

    err = hw_get_module(ACOUSTICS_HARDWARE_MODULE_ID,
            (hw_module_t const**)&module);

    if (err == 0) {
        hw_device_t* device;
        err = module->methods->open(module, ACOUSTICS_HARDWARE_NAME, &device);
        if (err == 0)
            mAcousticDevice = (acoustic_device_t *)device;
        else
            ALOGE("Acoustics Module not found.");
    }
}

AudioHardwareALSA::~AudioHardwareALSA()
{
    if (mMixer) delete mMixer;
    if (mALSADevice)
        mALSADevice->common.close(&mALSADevice->common);
    if (mAcousticDevice)
        mAcousticDevice->common.close(&mAcousticDevice->common);
}

status_t AudioHardwareALSA::initCheck()
{
    if (!mALSADevice)
        return NO_INIT;

    if (!mMixer || !mMixer->isValid())
        ALOGW("ALSA Mixer is not valid. AudioFlinger will do software volume control.");

    return NO_ERROR;
}

status_t AudioHardwareALSA::setVoiceVolume(float volume)
{
    // The voice volume is used by the VOICE_CALL audio stream.
    if (mMixer)
        return mMixer->setVolume(AudioSystem::DEVICE_OUT_EARPIECE, volume, volume);
    else
        return INVALID_OPERATION;
}

status_t AudioHardwareALSA::setMasterVolume(float volume)
{
    if (mMixer)
        return mMixer->setMasterVolume(volume);
    else
        return INVALID_OPERATION;
}

status_t AudioHardwareALSA::setMode(int mode)
{
    status_t status = NO_ERROR;

    if (mode != mMode) {
        status = AudioHardwareBase::setMode(mode);

        if (status == NO_ERROR) {
            // take care of mode change.
            for(ALSAHandleList::iterator it = mDeviceList.begin();
                it != mDeviceList.end(); ++it)
                if (it->curDev) {
                    status = mALSADevice->route(&(*it), it->curDev, mode);
                    if (status != NO_ERROR){
                        // if usb-audio not exist or open error, try builtin-audio
                        if(strcmp((char*)it->modPrivate, "usb-audio") == 0)
                          continue;
                        // other case
                        break;
                    }
                }
        }
    }

    return status;
}

AudioStreamOut *
AudioHardwareALSA::openOutputStream(uint32_t devices,
                                    int *format,
                                    uint32_t *channels,
                                    uint32_t *sampleRate,
                                    status_t *status)
{
    ALOGD("openOutputStream called for devices: 0x%08x", devices);

    status_t err = BAD_VALUE;
    AudioStreamOutALSA *out = 0;

    if (devices & (devices - 1)) {
        if (status) *status = err;
        ALOGD("openOutputStream called with bad devices");
        return out;
    }

    // Find the appropriate alsa device
    for(ALSAHandleList::iterator it = mDeviceList.begin();
        it != mDeviceList.end(); ++it)
        if (it->devices & devices) {
            err = mALSADevice->open(&(*it), devices, mode());
            if (err) break;
            out = new AudioStreamOutALSA(this, &(*it));
            err = out->set(format, channels, sampleRate);
            break;
        }

    if (status) *status = err;
    return out;
}

void
AudioHardwareALSA::closeOutputStream(AudioStreamOut* out)
{
    delete out;
}

AudioStreamIn *
AudioHardwareALSA::openInputStream(uint32_t devices,
                                   int *format,
                                   uint32_t *channels,
                                   uint32_t *sampleRate,
                                   status_t *status,
                                   AudioSystem::audio_in_acoustics acoustics)
{
    status_t err = BAD_VALUE;
    AudioStreamInALSA *in = 0;

    if (devices & (devices - 1)) {
        if (status) *status = err;
        return in;
    }

    // Find the appropriate alsa device
    // Open usb-audio first, if not avaible, try builtin-audio
    for(ALSAHandleList::iterator it = mDeviceList.begin();
        it != mDeviceList.end(); ++it)
        if (it->devices & devices) {
            err = mALSADevice->open(&(*it), devices, mode());
            if (err) {
              // check if usb-audio
              if(strcmp((char*)it->modPrivate, "usb-audio") == 0){
                ALOGE("open usb-audio error, to try builtin-audio\n");
                continue;
              }

              break;
            }
            in = new AudioStreamInALSA(this, &(*it), acoustics);
            err = in->set(format, channels, sampleRate);
            break;
        }

    if (status) *status = err;
    return in;
}

void
AudioHardwareALSA::closeInputStream(AudioStreamIn* in)
{
    delete in;
}

status_t AudioHardwareALSA::setMicMute(bool state)
{	
	//replace DEVICE_OUT_EARPIECE by DEVICE_IN_BUILTIN_MIC,junliang add mic control
    //if (mMixer)
        //return mMixer->setCaptureMuteState(AudioSystem::DEVICE_IN_BUILTIN_MIC, state);

	mMicMute = state;

    return NO_ERROR;
}

status_t AudioHardwareALSA::getMicMute(bool *state)
{
	//replace DEVICE_OUT_EARPIECE by DEVICE_IN_BUILTIN_MIC,junliang add mic control
    //if (mMixer)
        //return mMixer->getCaptureMuteState(AudioSystem::DEVICE_IN_BUILTIN_MIC, state);

	*state = mMicMute;

    return NO_ERROR;
}

status_t AudioHardwareALSA::dump(int fd, const Vector<String16>& args)
{
    return NO_ERROR;
}


size_t AudioHardwareALSA::getInputBufferSize(uint32_t sampleRate, int format, int channelCount)
{
    if (sampleRate != 8000 && sampleRate!= 11025&&
    	sampleRate!= 12000 &&sampleRate!=16000 &&
    	sampleRate!=22050&&sampleRate!=24000&&
    	sampleRate!=32000&&sampleRate!=44100&&sampleRate!=48000) {
        ALOGW("getInputBufferSize bad sampling rate: %d", sampleRate);
        return 0;
    }
    if (format != AudioSystem::PCM_16_BIT) {
        ALOGW("getInputBufferSize bad format: %d", format);
        return 0;
    }
    if (channelCount != 1 && channelCount!=2) {
        ALOGW("getInputBufferSize bad channel count: %d", channelCount);
        return 0;
    }

	int minbufsize= 4096;

	if(mDeviceList.empty() == false)
	{
		int devicebuffersize = 0;
		int devicesampelrate = 0;
		List<alsa_handle_t>::iterator iter = mDeviceList.begin();
		do
		{
          // we'd better to try to find the current active input device
            if(iter->handle == 0){

            }
            ALOGD("audio-in handle=%p", iter->handle);
			if(iter->devices == AudioSystem::DEVICE_IN_ALL)
			{
				devicebuffersize = iter->bufferSize;
				devicesampelrate = iter->sampleRate;
				ALOGD("devicebuffersize = %d,devicesampelrate=%d",devicebuffersize,devicesampelrate);
				break;
			}
			iter ++;
		}while(iter.getNode()!=NULL);

		const int samplesize = 2;
		minbufsize = (devicebuffersize*sampleRate/devicesampelrate+1)*samplesize*channelCount;
		ALOGD("minbufsize %d",minbufsize);
	}
	else
		ALOGE("AudioHardwareALSA device list is null!");
    return minbufsize;}

}       // namespace android
