#include <string.h>
#include <jni.h>
#include <linux/fb.h>
#include <fcntl.h>
#include <stdlib.h>

#include <cutils/log.h>

#include "setppmjni.h"

int setPpmDisp(int width, int height) {

	int fb_disp;
	char ppmDisp_str[32];

	LOGI("setPpmDisp: width=%d height=%d", width, height);
	if((fb_disp = open("/sys/class/ppmgr/disp", O_RDWR)) < 0) {
		LOGI("open /sys/class/ppmgr/disp fail.");
	}
	
	memset(ppmDisp_str,0,32);	
	sprintf(ppmDisp_str, "%d %d", width, height);
	write(fb_disp, ppmDisp_str, strlen(ppmDisp_str));
	
	return 0;
}

JNIEXPORT jint JNICALL Java_com_amlogic_OOBE_SetPpm_setPpmDispJni( JNIEnv * env,
																									jobject thiz, jint width, jint height)
{	
		return setPpmDisp(width, height);
}