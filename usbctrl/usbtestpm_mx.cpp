/* main.c */  
/********************************************
this file because m6 usb power control is defferent with other
usbA and usbB por is connected.
*********************************************/
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <fcntl.h>
#include <dirent.h>

#define LOG_TAG "USBtestpm_mx"

#include "cutils/log.h"
#include  "cutils/properties.h"

extern "C" int logwrap(int argc, const char **argv, int background);

static char USBPOWER_PATH[] = "/system/bin/usbpower";

#define USB_IDX_A	0
#define USB_IDX_B	1
#define USB_IDX_MAX	2

#define USB_CMD_ON	0
#define USB_CMD_OFF	1
#define USB_CMD_IF	2
#define USB_CMD_MAX	3

char usb_index_str[USB_IDX_MAX][2]=
{
	"A","B"
};

char usb_state_str[USB_CMD_MAX][8]=
{
	"on","off","if"
};


int usbcheck(int index) 
{
	bool rw = true;
    if (access(USBPOWER_PATH, X_OK)) {
        SLOGW("No USBPOWER_PATH file\n");
        return -1;
    }

    int rc = 0;
    do {
        const char *args[4];
        args[0] = USBPOWER_PATH;
        args[1] = usb_index_str[index];
        args[2] = usb_state_str[USB_CMD_IF];
        args[3] = NULL;

        rc = logwrap(3, args, 1);

    } while (0);

    return rc;
}

int usbset(int index,int cmd) 
{
	bool rw = true;
    if (access(USBPOWER_PATH, X_OK)) {
        SLOGW("No USBPOWER_PATH file\n");
        return -1;
    }

    int rc = 0;
    do {
        const char *args[4];
        args[0] = USBPOWER_PATH;
        args[1] = usb_index_str[index];
        args[2] = usb_state_str[cmd];
        args[3] = NULL;

        rc = logwrap(3, args, 1);

    } while (0);

    return rc;
}

static const char USBA_DEVICE_PATH[] = "/sys/devices/lm0";
static const char USBB_DEVICE_PATH[] = "/sys/devices/lm1";

int check_usb_devices_exists(int port)
{
	if (port == 0) {
	    if (access(USBA_DEVICE_PATH, R_OK) == 0) {    		
	        return 0;
	    } else {    		  
	        return -1;
	    }
	} else {
	    if (access(USBB_DEVICE_PATH, R_OK) == 0) {    		
	        return 0;
	    } else {    		  
	        return -1;
	    }	
	}
		
}

int main()
{   
	int usb_a_exist = 0;
	int usb_b_exist = 0;
	int on_flag;
	int checkflag;
	
	if (check_usb_devices_exists(0) == 0)
	{
		usb_a_exist = 1;
		on_flag = 1;
	}
	else
	{
		usb_a_exist = 0;
		on_flag = 0;
	}
	
	if (check_usb_devices_exists(1) == 0)
	{
		usb_b_exist = 1;
		on_flag = 1;
	}
	else
	{
	  usb_b_exist = 0;
		on_flag = 0;
	}
	
	if (usb_a_exist || usb_b_exist) {
		while(1) {
				if(on_flag == 0)
				{
					usbset(0,USB_CMD_ON);
					on_flag = 1;
					usleep(500000);	
				}
				else
				{
					checkflag = 0;
					if(usb_a_exist)
						checkflag |= usbcheck(0);
					if(usb_b_exist)
						checkflag |= usbcheck(1);
					if (checkflag == 0)
					{ 
						usbset(0,USB_CMD_OFF);
						on_flag = 0;
					}	
					sleep(1);
				}					
					
		}
	}
	else
	{
		usbset(0,USB_CMD_OFF);
	}
	
}
	













	
