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
#include <dirent.h>
#include <sys/stat.h>
#include <sys/types.h>
//#include <sys/mount.h>

#define LOG_TAG "DevListener"

#include <cutils/log.h>
#include <cutils/properties.h>

#include <sysutils/NetlinkEvent.h>
#include "NetlinkHandler.h"

typedef struct {
	int vid;
	int pid;
} VidPid;

static VidPid KnownModems[] = {
    /*vid,   ,pid,    */
    //ZTE MU351 dongle
    { 0x19d2, 0x0003 },
    //ZTE MF633,MF637U,MF110 dongle
    { 0x19d2, 0x0031 },
    //ZTE-T A355
    { 0x19d2, 0x0079},
    //ZTE AD3812 & MG3732 module
    { 0x19d2, 0xFFEB },
    //ZTE MC8630 module
    { 0x19d2, 0xFFFE },
    //ZTE MC2700 & MC2716 module
    { 0x19d2, 0xFFED },
    //ZTE MC2718 module
    { 0x19d2, 0xFFE8 },
    //ZTE AC682
    { 0x19d2, 0xFFDD },
    //ZTE MC2816 module
    { 0x19d2, 0xFFF1 },
    //ZTE MF210 module
    { 0x19d2, 0x0117 },
    //ZTE MF226 module
    { 0x19d2, 0x0144 },
    // ZTE AC580
    { 0x19d2, 0x0152 },  
    //LONGCHEER U6300/U6100 module
    { 0x1C9E, 0x9603 },

    //HUAWEI E1750/E620 module
    { 0x12d1, 0x1001 },	
    //HUAWEI E173s module
    { 0x12d1, 0x1c05 },	
    { 0x12d1, 0x1003 },
    { 0x12d1, 0x1402 },
    //HUAWEI EW770W module
    { 0x12d1, 0x1404 },		    
    { 0x12d1, 0x1406 },	
    { 0x12d1, 0x1407 },	
    { 0x12d1, 0x140A },	
    { 0x12d1, 0x140B },	
    //HUAWEI E173u module
    { 0x12d1, 0x140C },
    { 0x12d1, 0x1411 },
    { 0x12d1, 0x1412 },
    { 0x12d1, 0x1413 },
    { 0x12d1, 0x1414 },
    { 0x12d1, 0x1416 },
    { 0x12d1, 0x1417 },
    { 0x12d1, 0x1418 },
    { 0x12d1, 0x1419 },
    { 0x12d1, 0x141A },
    { 0x12d1, 0x141B },
    { 0x12d1, 0x141E },
    { 0x12d1, 0x1420 },
    { 0x12d1, 0x1422 },
    { 0x12d1, 0x1427 },
    { 0x12d1, 0x1428 },
    { 0x12d1, 0x1429 },
    { 0x12d1, 0x142A },
    { 0x12d1, 0x142B },
    { 0x12d1, 0x1433 },
    { 0x12d1, 0x1434 },
    //HUAWEI E173 module
    { 0x12d1, 0x1436 },
    { 0x12d1, 0x1438 },
    { 0x12d1, 0x1439 },
    { 0x12d1, 0x143A },
    { 0x12d1, 0x143B },
    { 0x12d1, 0x143E },
    { 0x12d1, 0x143F },
    { 0x12d1, 0x1448 },
    { 0x12d1, 0x144A },
    { 0x12d1, 0x144B },
    { 0x12d1, 0x144C },
    { 0x12d1, 0x143D },
    { 0x12d1, 0x144F },
    //HUAWEI K3765 dongle
    { 0x12d1, 0x1465 },
    //HUAWEI E1820 module
    { 0x12d1, 0x14ac },	
    //Vodafone (Huawei) K3770
    { 0x12d1, 0x14c9 },
    //MU733
    { 0x12d1, 0x1506 },
    { 0x12d1, 0x15FF },
    //Huawei ET188
    { 0x12d1, 0x1d09 },
    //Huawei MT509
    { 0x12d1, 0x1d50 },
    //ruisibo WH700G		
    { 0x05c6, 0x6000 },
    //alcatel X220D
    { 0x1bbb, 0x0017 },
    //LINKTOP
    { 0x230d, 0x000d },
    //Philips Semiconductors
    { 0x04cc, 0x225a },
    //A-LINK 3GU
    { 0x1e0e, 0x9200 },
    //ThinkWill MI900
    { 0x19f5, 0x9013 },
    //USI MT6229
    { 0x0e8d, 0x00a2 },
    //Longcheer SU9800
    { 0x1c9e, 0x9800},
    //Longcheer SU7300U
    { 0x1c9e, 0x9e00},
    //Advan Jetz XL
    { 0x19f5, 0x9013}
};

NetlinkHandler::NetlinkHandler(int listenerSocket) :
                NetlinkListener(listenerSocket) {
	isEnabled = 0;
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

#if 0
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
#endif

int NetlinkHandler::isKnownModem(int vid, int pid) {
    for(int i = 0; i < sizeof(KnownModems) / sizeof(VidPid); i++) {
        if((vid == KnownModems[i].vid) && (pid == KnownModems[i].pid))
            return i;
    }
    return -1;
}

int NetlinkHandler::checkModem(void) {
    DIR *pDevDir = NULL;
    struct dirent *dirp = NULL;
    int cnt = 0;
    int isttyACM = MODEM_TTYUSB;
 
    pDevDir = opendir("/dev");
    
    if (pDevDir == NULL) {
        SLOGE("Failed to opendir(/dev), err:%s", strerror(errno));
        return -1;
    }
    
    while((dirp=readdir(pDevDir)) != NULL) {
      
        if(strstr(dirp->d_name, "ttyUSB") != NULL)
            cnt++;
	
        if(strstr(dirp->d_name, "ttyACM") != NULL) {
            isttyACM = MODEM_TTYACM ;
            cnt++;
        }
    }
    
    closedir(pDevDir);
    
    if (cnt >= 2)
        return isttyACM ;
    
    return -1 ;
}

void NetlinkHandler::call_rild(int vid, int pid) {
    char vidpid[28];
    const char *RIL_FIFO = "/data/system/RIL_FIFO" ;
    snprintf(vidpid, sizeof(vidpid), "%x/%x/", vid, pid);
    int fd = open(RIL_FIFO, O_NONBLOCK|O_WRONLY);
    if (fd != -1) {
	if (0 > write(fd, vidpid, strlen(vidpid)))
	    SLOGE("Can't write to RIL_FIFO file");
	close(fd);
    } else {
	SLOGE("Can't open RIL_FIFO file");
	close(fd);
    }
}

int NetlinkHandler::switchMode(int vid, int pid) {
    int mType = 0;
    char supported[128] = {0};
    char callModeSwitch[256] = {0};
    int i = 0;
    snprintf(supported, sizeof(supported), "/system/etc/usb_modeswitch.d/%.4x_%.4x", vid, pid);
    if (access(supported, F_OK) > 0) {
	SLOGD("%s doesn't exist\n", supported);
	mType = checkModem();
	if (0 <= isKnownModem(vid,pid) && (isEnabled == 1 || mType == MODEM_TTYACM)) {
	    SLOGW("%x_%x is already switched, let's call rild\n", vid, pid);
	    goto END;
	}
	SLOGD("%.4x:%.4x device doesn't require usb_modeswitch", vid, pid);
	return -1;
    }
    SLOGD("%.4x:%.4x modem requires usb_modeswith", vid, pid);
    
    for (i = 0; i < 5; i++) {
	SLOGD("usb_modeswitch %d call", i+1);
	strncpy(callModeSwitch, "/system/bin/usb_modeswitch -I -W -c ", sizeof(callModeSwitch));
	strcat(callModeSwitch, supported);
	system(callModeSwitch);
	if (0 <= (mType = checkModem())) {
	    SLOGD("usb_modeswitch succeeded");
	    break;
	}
	sleep(2);
    }
    
    if (i == 5) {
	SLOGE("usb_modeswitch failed");
	return -2;
    }
    
END:
    if (MODEM_TTYACM == mType) {
	if (isKnownModem(vid,pid) < 0) {
	    SLOGE("%.4x:%.4x device is not a known modem", vid, pid);
	    return -1;
	}
	
	SLOGD("%.4x:%.4x modem recognized", vid, pid);
	char value[PROPERTY_VALUE_MAX];
	snprintf(value, sizeof(value), "%d", vid);
	property_set(RIL_PROP_USBMODEM_VID, value);
	snprintf(value, sizeof(value), "%d", pid);
	property_set(RIL_PROP_USBMODEM_PID, value);
	
	isEnabled = 1;
	
	call_rild(vid,pid);
	
	return 0;
    }
    
    return 0;
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
	    switchMode(vid,pid);
	}
    }
    
    if (productStr != NULL && !strcmp(devtype, "usb_interface") && NetlinkEvent::NlActionRemove == evt->getAction()) {
	if (devpath != NULL) {
	    if (isEnabled) {
		SLOGD("3G dongle disconnected");
		isEnabled = 0;
		property_set(RIL_PROP_USBMODEM_VID, "0");
		property_set(RIL_PROP_USBMODEM_PID, "0");
	    }
	}
    }
}