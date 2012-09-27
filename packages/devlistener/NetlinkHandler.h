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

#ifndef _NETLINKHANDLER_H
#define _NETLINKHANDLER_H

#include <sysutils/NetlinkListener.h>

#define  VENDOR_SPEC_CLASS_ID "255"

#define RIL_PROP_USBMODEM_VID                 "ril.usbmodem.vid"
#define RIL_PROP_USBMODEM_PID                 "ril.usbmodem.pid"

#define MODEM_TTYUSB (0)
#define MODEM_TTYACM (1)

class NetlinkHandler: public NetlinkListener {

public:
    NetlinkHandler(int listenerSocket);
    virtual ~NetlinkHandler();

    void handleBluetoothEvent(NetlinkEvent *evt);
    void handleInputEvent(NetlinkEvent *evt);
    
    int isEnabled;
    int isKnownModem(int vid, int pid);
    int checkModem(void);
    void call_rild(int vid, int pid);
    int switchMode(int vid, int pid);
    void handleUsbEvent(NetlinkEvent *evt);

    int start(void);
    int stop(void);

protected:
    virtual void onEvent(NetlinkEvent *evt);
};
#endif
