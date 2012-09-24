/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>

#include <string.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
//#include <sys/mount.h>

#define LOG_TAG "DevListener"

#include <cutils/log.h>

#include <sysutils/NetlinkEvent.h>
#include "NetlinkHandler.h"

NetlinkHandler::NetlinkHandler(int listenerSocket) :
                NetlinkListener(listenerSocket) {
}

NetlinkHandler::~NetlinkHandler() {
}

int NetlinkHandler::start() {
    return this->startListener();
}

int NetlinkHandler::stop() {
    return this->stopListener();
}

void NetlinkHandler::onEvent(NetlinkEvent *evt) {
    const char *subsys = evt->getSubsystem();

    if (!subsys) {
        SLOGW("No subsystem found in netlink event");
        return;
    }

    if (!strcmp(subsys, "rfkill")) {
        handleBluetoothEvent(evt);
    } else if (!strcmp(subsys, "input")) {
        handleInputEvent(evt);
    } else if (!strcmp(subsys, "usb")) {
      handleUsbEvent(evt);
    }
}

void NetlinkHandler::handleBluetoothEvent(NetlinkEvent *evt) {
    const char *devpath = evt->findParam("DEVPATH");
    const char *rfkillType = evt->findParam("RFKILL_TYPE");

    if (rfkillType != NULL && !strcmp(rfkillType, "bluetooth") && evt->getAction() == NetlinkEvent::NlActionAdd) {
        if (devpath != NULL) {
            char statepath[strlen(devpath)+14];
            strcpy(statepath, "/sys");
            strcat(statepath, devpath);
            strcat(statepath, "/state");
            
            int result = chmod(statepath, S_IROTH|S_IWOTH|S_IRGRP|S_IWGRP|S_IRUSR|S_IWUSR);
            if (result == 0) {
                SLOGD("Changed the permission of file %s to 666", statepath);
            }
        }
    }
}

void NetlinkHandler::handleInputEvent(NetlinkEvent *evt) {
    const char *path = evt->findParam("DEVPATH");
    const char *devname = evt->findParam("DEVNAME");

    if (devname != NULL && evt->getAction() == NetlinkEvent::NlActionAdd && !strncmp(devname, "input/js", 8)) {
        char devpath[strlen(devname)+10];
        strcpy(devpath, "/dev/");
        strcat(devpath, devname);
            
        usleep(2000);
        int result = chmod(devpath, S_IROTH|S_IWOTH|S_IRGRP|S_IWGRP|S_IRUSR|S_IWUSR);
        if (result == 0) {
            SLOGD("Changed the permission of file %s to 666", devpath);
        }
    }
}

void NetlinkHandler::handleUsbEvent(NetlinkEvent *evt) {
    const char *devpath = evt->findParam("DEVPATH");
    const char *devtype = evt->findParam("DEVTYPE");
    const char *productStr = evt->findParam("PRODUCT");
       
    if (productStr != NULL && !strcmp(devtype, "usb_interface") && NetlinkEvent::NlActionAdd == evt->getAction()) {
	if (devpath != NULL) {
	    int vid = 0;
	    int pid = 0;
	    sscanf(productStr, "%x/%x/", &vid, &pid);
	    char supported[128] = {0};
	    snprintf(supported, sizeof(supported), "/system/etc/usb_modeswitch.d/%.4x_%.4x", vid, pid);
	    if (access(supported, F_OK) == 0) {
		SLOGD("%.4x:%.4x modem requires usb_modeswith", vid, pid);
		char callModeSwitch[256] = {0};
		strncpy(callModeSwitch, "/system/bin/usb_modeswitch -I -W -c ", sizeof(callModeSwitch));
		strcat(callModeSwitch, supported);
		if (system(callModeSwitch) == 0) {
		    SLOGD("Called usb_modeswitch");
		    char vidpid[28];
		    const char *RIL_FIFO = "/data/system/RIL_FIFO" ;
		    snprintf(vidpid, sizeof(vidpid), "%x/%x/", vid, pid);
		    int fd = open(RIL_FIFO, O_NONBLOCK|O_WRONLY);
		    if (fd != -1) {
			write(fd, vidpid, strlen(vidpid));
			close(fd);
		    }
		} else {
		    SLOGE("Call usb_modeswitch FAILED");
		}
	    } else {
	      SLOGD("%.4x:%.4x device doesn't require usb_modeswitch", vid, pid);
	    }
	}
    }
}
