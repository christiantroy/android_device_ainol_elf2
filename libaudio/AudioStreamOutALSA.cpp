/* AudioStreamOutALSA.cpp
 **
 ** Copyright 2008-2009 Wind River Systems
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

//#define LOG_NDEBUG 0
#define LOG_TAG "AudioHardwareALSA"
#include <utils/Log.h>
#include <utils/String8.h>

#include <cutils/properties.h>
#include <media/AudioRecord.h>
#include <hardware_legacy/power.h>

#include "AudioHardwareALSA.h"

#ifndef ALSA_DEFAULT_SAMPLE_RATE
#define ALSA_DEFAULT_SAMPLE_RATE 44100 // in Hz
#endif

namespace android_audio_legacy
{

// ----------------------------------------------------------------------------

static const int DEFAULT_SAMPLE_RATE = ALSA_DEFAULT_SAMPLE_RATE;

// ----------------------------------------------------------------------------

AudioStreamOutALSA::AudioStreamOutALSA(AudioHardwareALSA *parent, alsa_handle_t *handle) :
    ALSAStreamOps(parent, handle),
    mFrameCount(0)
{
}

AudioStreamOutALSA::~AudioStreamOutALSA()
{
    close();
}

uint32_t AudioStreamOutALSA::channels() const
{
    int c = ALSAStreamOps::channels();
    return c;
}

status_t AudioStreamOutALSA::setVolume(float left, float right)
{
    return mixer()->setVolume (mHandle->curDev, left, right);
}

ssize_t AudioStreamOutALSA::write(const void *buffer, size_t bytes)
{
    Mutex::Autolock lock(mLock);
    
    if (mHandle->handle == NULL) {
	mHandle->module->open(mHandle, mHandle->curDev, mHandle->curMode);
    }
    
    if(mHandle->handle == NULL) {
	ALOGE("write:: device open failed");
	Mutex::Autolock unlock(mLock);
	return 0;
    }

    if (!mPowerLock) {
        acquire_wake_lock (PARTIAL_WAKE_LOCK, "AudioOutLock");
        mPowerLock = true;
    }

    acoustic_device_t *aDev = acoustics();

    // For output, we will pass the data on to the acoustics module, but the actual
    // data is expected to be sent to the audio device directly as well.
    if (aDev && aDev->write)
        aDev->write(aDev, buffer, bytes);

    snd_pcm_sframes_t n;
    size_t            sent = 0;
    status_t          err;

    while (mHandle->handle && sent < bytes) {
        n = snd_pcm_writei(mHandle->handle,
                           (char *)buffer + sent,
                           snd_pcm_bytes_to_frames(mHandle->handle, bytes - sent));
        if (n == -EBADFD) {
            // Somehow the stream is in a bad state. The driver probably
            // has a bug and snd_pcm_recover() doesn't seem to handle this.
            mHandle->module->open(mHandle, mHandle->curDev, mHandle->curMode);

            if (aDev && aDev->recover) aDev->recover(aDev, n);
        }
        else if (n < 0) {
            if (mHandle->handle) {
                // snd_pcm_recover() will return 0 if successful in recovering from
                // an error, or -errno if the error was unrecoverable.
                n = snd_pcm_recover(mHandle->handle, n, 1);

                if (aDev && aDev->recover) aDev->recover(aDev, n);

                if (n) return static_cast<ssize_t>(n);
            }
        }
        else {
            if (mHandle->handle) {
                mFrameCount += n;
                sent += static_cast<ssize_t>(snd_pcm_frames_to_bytes(mHandle->handle, n));
            }
        }

    } 

    return sent;
}

status_t AudioStreamOutALSA::dump(int fd, const Vector<String16>& args)
{
    return NO_ERROR;
}

status_t AudioStreamOutALSA::open(int mode)
{
    Mutex::Autolock lock(mLock);

    return ALSAStreamOps::open(mode);
}

status_t AudioStreamOutALSA::close()
{
    Mutex::Autolock lock(mLock);

    snd_pcm_drain (mHandle->handle);
    ALSAStreamOps::close();

    if (mPowerLock) {
        release_wake_lock ("AudioOutLock");
        mPowerLock = false;
    }

    return NO_ERROR;
}

status_t AudioStreamOutALSA::standby()
{
    Mutex::Autolock lock(mLock);

    if (mHandle->module->standby)
    // allow hw specific modules to imlement unique standby
    // if needed
        mHandle->module->standby(mHandle);
    else
        snd_pcm_drain (mHandle->handle);

    if (mPowerLock) {
        release_wake_lock ("AudioOutLock");
        mPowerLock = false;
    }

    mFrameCount = 0;

    return NO_ERROR;
}

#define USEC_TO_MSEC(x) ((x + 999) / 1000)

uint32_t AudioStreamOutALSA::latency() const
{
    int err;
    int t;
    snd_pcm_status_t *status;

	snd_pcm_status_alloca(&status);

    if(mHandle->handle == NULL) {
        ALOGV("handle is null, error !");
        return 0;
    }
	
	if ((err = snd_pcm_status(mHandle->handle, status)) < 0) {
	 ALOGV("stream status error :%s\n", snd_strerror(err));
        return USEC_TO_MSEC (mHandle->latency);
	}

    t = snd_pcm_status_get_delay(status);
    ALOGV("snd_pcm_status_get_delay = %d", t);
    ALOGV("AudioStreamOutALSA::latency = %d, sampleRate = %d", 
        (t * 1000) / sampleRate(),
        sampleRate());
    return (t * 1000) / sampleRate();
}

// return the number of audio frames written by the audio dsp to DAC since
// the output has exited standby
status_t AudioStreamOutALSA::getRenderPosition(uint32_t *dspFrames)
{
    *dspFrames = mFrameCount;
    return NO_ERROR;
}

}       // namespace android
