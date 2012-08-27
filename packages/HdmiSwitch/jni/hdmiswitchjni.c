#include <string.h>
#include <jni.h>
#include <linux/fb.h>
#include <fcntl.h>
#include <stdlib.h>

#include <cutils/log.h>

#include "hdmiswitchjni.h"

#define  FBIOPUT_OSD_FREE_SCALE_ENABLE	0x4504
#define  FBIOPUT_OSD_FREE_SCALE_WIDTH	0x4505
#define  FBIOPUT_OSD_FREE_SCALE_HEIGHT	0x4506

struct fb_var_screeninfo vinfo;
char daxis_str[32];
char vaxis_str[80];

int freeScale(int mode) {
	int fd0 = -1, fd1 = -1;
	int fd_daxis = -1, fd_vaxis = -1;
	int fd_fb = -1;
	int fd_video = -1;
	int fd_ppmgr = -1;
        int fd_ppmgr_rect = -1;
	int osd_width = 0, osd_height = 0;	
	int ret = -1;
	int x = 0, y = 0, w = 0, h = 0;
	int find_flag = 0;
	
	//LOGI("freeScale: mode=%d", mode);
	if((fd0 = open("/dev/graphics/fb0", O_RDWR)) < 0) {
		LOGI("open /dev/graphics/fb0 fail.");
		goto exit;
	}
	if((fd1 = open("/dev/graphics/fb1", O_RDWR)) < 0) {
		LOGI("open /dev/graphics/fb1 fail.");
		goto exit;		
	}
	if((fd_vaxis = open("/sys/class/video/axis", O_RDWR)) < 0) {
		LOGI("open /sys/class/video/axis fail.");
		goto exit;	
	}
		
	if((fd_daxis = open("/sys/class/display/axis", O_RDWR)) < 0) {
		LOGI("open /sys/class/display/axis fail.");
		goto exit;	
	}	

	if((fd_fb = open("/dev/graphics/fb0", O_RDWR)) < 0) {
		LOGI("open /dev/graphics/fb0 fail.");
		goto exit;
	}
	
	if((fd_video = open("/sys/class/video/disable_video", O_RDWR)) < 0) {
		LOGI("open /sys/class/video/disable_video fail.");
	}	
		
	if((fd_ppmgr = open("/sys/class/ppmgr/ppscaler", O_RDWR)) < 0) {
		LOGI("open /sys/class/ppmgr/ppscaler fail.");	
	}

	if((fd_ppmgr_rect = open("/sys/class/ppmgr/ppscaler_rect", O_RDWR)) < 0) {
		LOGI("open /sys/class/ppmgr/ppscaler_rect fail.");	
	}

	memset(vaxis_str,0,80);	
	if(fd_vaxis>=0){
		int ret_len = read(fd_vaxis, vaxis_str, sizeof(vaxis_str));
		if(ret_len>0){
			if(sscanf(vaxis_str,"%d %d %d %d",&x,&y,&w,&h)>0){
				w = w -x + 1;
				h = h -y + 1;
				find_flag = 1;	
				LOGI("set mode: vaxis: x:%d, y:%d, w:%d, h:%d.",x,y,w,h);
			}
		}
	}

	memset(daxis_str,0,32);	
	if(ioctl(fd_fb, FBIOGET_VSCREENINFO, &vinfo) == 0) {
		osd_width = vinfo.xres;
		osd_height = vinfo.yres;
		sprintf(daxis_str, "0 0 %d %d 0 0 18 18", vinfo.xres, vinfo.yres);
																									
		//LOGI("osd_width = %d", osd_width);
		//LOGI("osd_height = %d", osd_height);
	} else {
		LOGI("get FBIOGET_VSCREENINFO fail.");
		goto exit;
	}
			
	switch(mode) {
		//LOGI("set mid mode=%d", mode);

		case 0:	//panel
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "0", strlen("0"));
			//if (fd_video >= 0) 	write(fd_video, "1", strlen("1"));	
			write(fd_daxis, daxis_str, strlen(daxis_str));				
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);		
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);	
			if((fd_ppmgr >= 0)&&(find_flag)){
				write(fd_vaxis, vaxis_str, strlen(vaxis_str));
			}
			//if (fd_video >= 0) 	write(fd_video, "2", strlen("2"));
			ret = 0;
			break;	
		case 1: //480p
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "0", strlen("0"));
			if (fd_video >= 0) 	write(fd_video, "1", strlen("1"));
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "1", strlen("1"));
			if(fd_ppmgr_rect >= 0)
				write(fd_ppmgr_rect, "20 10 700 470 0", strlen("20 10 700 470 0"));
			else if(fd_vaxis >= 0)
				write(fd_vaxis, "20 10 700 470", strlen("20 10 700 470"));
			write(fd_daxis, daxis_str, strlen(daxis_str));
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height); 
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);	
			if((fd_ppmgr >= 0)&&(find_flag)){
				write(fd_vaxis, vaxis_str, strlen(vaxis_str));
			}
			if ((fd_video >= 0)&&(fd_ppmgr >= 0)) 	write(fd_video, "2", strlen("2"));		
			ret = 0;
			break;
		case 2: //720p
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "0", strlen("0"));
			if (fd_video >= 0) 	write(fd_video, "1", strlen("1"));
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "1", strlen("1"));
			if(fd_ppmgr_rect >= 0)
				write(fd_ppmgr_rect, "40 15 1240 705 0", strlen("40 15 1240 705 0"));
			else if(fd_vaxis >= 0)
				write(fd_vaxis, "40 15 1240 705", strlen("40 15 1240 705"));
			write(fd_daxis, daxis_str, strlen(daxis_str));
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height);
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "1", strlen("1"));
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);	
			if((fd_ppmgr >= 0)&&(find_flag)){
				write(fd_vaxis, vaxis_str, strlen(vaxis_str));
			}
			if ((fd_video >= 0)&&(fd_ppmgr >= 0)) 	write(fd_video, "2", strlen("2"));			
			ret = 0;
			break;
		case 3: //1080i			
		case 4: //1080p
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "0", strlen("0"));
			if (fd_video >= 0) 	write(fd_video, "1", strlen("1"));
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "1", strlen("1"));
			if(fd_ppmgr_rect >= 0)
				write(fd_ppmgr_rect, "40 20 1880 1060 0", strlen("40 20 1880 1060 0"));
			else if(fd_vaxis >= 0)
				write(fd_vaxis, "40 20 1880 1060", strlen("40 20 1880 1060"));
			write(fd_daxis, daxis_str, strlen(daxis_str));
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);	
			if((fd_ppmgr >= 0)&&(find_flag)){
				write(fd_vaxis, vaxis_str, strlen(vaxis_str));
			}
			if ((fd_video >= 0)&&(fd_ppmgr >= 0)) 	write(fd_video, "2", strlen("2"));			
			ret = 0;
			break;	
		default:			
			break;		
			
	}
	
exit:	
	close(fd0);
	close(fd1);
	close(fd_vaxis);
	close(fd_daxis);	
	close(fd_fb);
	close(fd_video);
	close(fd_ppmgr);
	close(fd_ppmgr_rect);
	return ret;
}
	

JNIEXPORT jint JNICALL Java_com_amlogic_HdmiSwitch_HdmiSwitch_freeScaleSetModeJni( JNIEnv * env,
																									jobject thiz, jint mode )
{	
		return freeScale(mode);
}						

int DisableFreeScale(int mode) {
	int fd0 = -1, fd1 = -1;
	int fd_daxis = -1, fd_vaxis = -1;
	int fd_fb = -1;	
	int fd_ppmgr = -1, fd_video = -1;
	int osd_width = 0, osd_height = 0;	
	int ret = -1;
	int x = 0, y = 0, w = 0, h = 0;
	int find_flag = 0;
	
	//LOGI("DisableFreeScale: mode=%d", mode);	
	if(mode == 0) return 0;		
		
	if((fd0 = open("/dev/graphics/fb0", O_RDWR)) < 0) {
		LOGI("open /dev/graphics/fb0 fail.");
		goto exit;
	}
	if((fd1 = open("/dev/graphics/fb1", O_RDWR)) < 0) {
		LOGI("open /dev/graphics/fb1 fail.");
		goto exit;	
	}
	if((fd_vaxis = open("/sys/class/video/axis", O_RDWR)) < 0) {
		LOGI("open /sys/class/video/axis fail.");
		goto exit;	
	}
		
	if((fd_daxis = open("/sys/class/display/axis", O_RDWR)) < 0) {
		LOGI("open /sys/class/display/axis fail.");
		goto exit;
	}	
	
	if((fd_fb = open("/dev/graphics/fb0", O_RDWR)) < 0) {
		LOGI("open /dev/graphics/fb0 fail.");
		goto exit;
	}		

	if((fd_video = open("/sys/class/video/disable_video", O_RDWR)) < 0) {
		LOGI("open /sys/class/video/disable_video fail.");
	}

	if((fd_ppmgr = open("/sys/class/ppmgr/ppscaler", O_RDWR)) < 0) {
		LOGI("open /sys/class/ppmgr/ppscaler fail.");	
	}


	memset(vaxis_str,0,80);	
	if(fd_vaxis>=0){
		int ret_len = read(fd_vaxis, vaxis_str, sizeof(vaxis_str));
		if(ret_len>0){
			if(sscanf(vaxis_str,"%d %d %d %d",&x,&y,&w,&h)>0){
				w = w -x + 1;
				h = h -y + 1;
				find_flag = 1;	
				LOGI("disable mode: vaxis: x:%d, y:%d, w:%d, h:%d.",x,y,w,h);
			}
		}
	}

	memset(daxis_str,0,32);	
	if(ioctl(fd_fb, FBIOGET_VSCREENINFO, &vinfo) == 0) {
		osd_width = vinfo.xres;
		osd_height = vinfo.yres;

		//LOGI("osd_width = %d", osd_width);
		//LOGI("osd_height = %d", osd_height);
	} else {
		LOGI("get FBIOGET_VSCREENINFO fail.");
		goto exit;
	}
		
	switch(mode) {
		//LOGI("set mid mode=%d", mode);

		case 0:	//panel			
			ret = 0;
			break;	
		case 1: //480p	
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "0", strlen("0"));
			if (fd_video >= 0) 	write(fd_video, "1", strlen("1"));		
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);			
			sprintf(daxis_str, "0 0 %d %d 0 0 18 18", vinfo.xres, vinfo.yres);
			write(fd_daxis, daxis_str, strlen(daxis_str));		
			if((fd_ppmgr >= 0)&&(find_flag)){
				write(fd_vaxis, vaxis_str, strlen(vaxis_str));
			}
			if (fd_video >= 0)	write(fd_video, "2", strlen("2"));
			ret = 0;
			break;
		case 2: //720p
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "0", strlen("0"));
			if (fd_video >= 0) 	write(fd_video, "1", strlen("1"));	
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			sprintf(daxis_str, "%d %d %d %d %d %d 18 18", (1280 - vinfo.xres)/2, 
																										(720-vinfo.yres)/2,
																										vinfo.xres, 
																										vinfo.yres,
																										(1280 - vinfo.xres)/2,
																										(720-vinfo.yres)/2);
			write(fd_daxis, daxis_str, strlen(daxis_str));
			if((fd_ppmgr >= 0)&&(find_flag)){
				write(fd_vaxis, vaxis_str, strlen(vaxis_str));
			}
			if (fd_video >= 0)	write(fd_video, "2", strlen("2"));
			ret = 0;
			break;
		case 3: //1080i			
		case 4: //1080p
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "0", strlen("0"));
			if (fd_video >= 0) 	write(fd_video, "1", strlen("1"));
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			sprintf(daxis_str, "%d %d %d %d %d %d 18 18", (1920 - vinfo.xres)/2, 
																										(1080-vinfo.yres)/2,
																										vinfo.xres, 
																										vinfo.yres,
																										(1920 - vinfo.xres)/2,
																										(1080-vinfo.yres)/2);
			write(fd_daxis, daxis_str, strlen(daxis_str));		
			if((fd_ppmgr >= 0)&&(find_flag)){
				write(fd_vaxis, vaxis_str, strlen(vaxis_str));
			}
			if (fd_video >= 0)	write(fd_video, "2", strlen("2"));
			ret = 0;
			break;	
		default:			
			break;					
	}	
	
exit:	
	close(fd0);
	close(fd1);
	close(fd_vaxis);
	close(fd_daxis);	
	close(fd_fb);
	close(fd_ppmgr);
	close(fd_video);
	return ret;

}

int EnableFreeScale(int mode) {
	int fd0 = -1, fd1 = -1;
	int fd_daxis = -1, fd_vaxis = -1;
	int fd_fb = -1;	
	int fd_ppmgr = -1,fd_ppmgr_rect = -1;
	int fd_video = -1;
 	int osd_width = 0, osd_height = 0;	
	int ret = -1;
	int x = 0, y = 0, w = 0, h = 0;
	int find_flag = 0;	

	//LOGI("EnableFreeScale: mode=%d", mode);	
	if(mode == 0) return 0;		
		
	if((fd0 = open("/dev/graphics/fb0", O_RDWR)) < 0) {
		LOGI("open /dev/graphics/fb0 fail.");
		goto exit;
	}
	if((fd1 = open("/dev/graphics/fb1", O_RDWR)) < 0) {
		LOGI("open /dev/graphics/fb1 fail.");
		goto exit;	
	}
	if((fd_vaxis = open("/sys/class/video/axis", O_RDWR)) < 0) {
		LOGI("open /sys/class/video/axis fail.");
		goto exit;		
	}
		
	if((fd_daxis = open("/sys/class/display/axis", O_RDWR)) < 0) {
		LOGI("open /sys/class/display/axis fail.");
		goto exit;
	}		
	
	if((fd_fb = open("/dev/graphics/fb0", O_RDWR)) < 0) {
		LOGI("open /dev/graphics/fb0 fail.");
		goto exit;
	}

	if((fd_video = open("/sys/class/video/disable_video", O_RDWR)) < 0) {
		LOGI("open /sys/class/video/disable_video fail.");
	}

	if((fd_ppmgr = open("/sys/class/ppmgr/ppscaler", O_RDWR)) < 0) {
		LOGI("open /sys/class/ppmgr/ppscaler fail.");	
	}
		
	if((fd_ppmgr_rect = open("/sys/class/ppmgr/ppscaler_rect", O_RDWR)) < 0) {
		LOGI("open /sys/class/ppmgr/ppscaler_rect fail.");	
	}

	memset(vaxis_str,0,80);	
	if(fd_vaxis>=0){
		int ret_len = read(fd_vaxis, vaxis_str, sizeof(vaxis_str));
		if(ret_len>0){
			if(sscanf(vaxis_str,"%d %d %d %d",&x,&y,&w,&h)>0){
				w = w -x + 1;
				h = h -y + 1;
				find_flag = 1;	
				LOGI("enable mode: vaxis: x:%d, y:%d, w:%d, h:%d.",x,y,w,h);
			}
		}
	}
	memset(daxis_str,0,32);	
	if(ioctl(fd_fb, FBIOGET_VSCREENINFO, &vinfo) == 0) {
		osd_width = vinfo.xres;
		osd_height = vinfo.yres;
		sprintf(daxis_str, "0 0 %d %d 0 0 18 18", vinfo.xres, vinfo.yres);
		
		//LOGI("osd_width = %d", osd_width);
		//LOGI("osd_height = %d", osd_height);
	} else {
		LOGI("get FBIOGET_VSCREENINFO fail.");
		goto exit;
	}
		
	switch(mode) {
		//LOGI("set mid mode=%d", mode);

		case 0:	//panel
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "0", strlen("0"));
			if (fd_video >= 0) 	write(fd_video, "1", strlen("1"));
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			write(fd_daxis, daxis_str, strlen(daxis_str));		
			if((fd_ppmgr >= 0)&&(find_flag)){
				write(fd_vaxis, vaxis_str, strlen(vaxis_str));
			}
			if (fd_video >= 0) 	write(fd_video, "2", strlen("2"));
			ret = 0;
			break;	
		case 1: //480p
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "0", strlen("0"));
			if (fd_video >= 0) 	write(fd_video, "1", strlen("1"));
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "1", strlen("1"));
			if(fd_ppmgr_rect >= 0)
				write(fd_ppmgr_rect, "20 10 700 470 0", strlen("20 10 700 470 0"));
			else if(fd_vaxis >= 0)
				write(fd_vaxis, "20 10 700 470", strlen("20 10 700 470"));
			write(fd_daxis, daxis_str, strlen(daxis_str));
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height); 
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height);	
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);	
			if((fd_ppmgr >= 0)&&(find_flag)){
				write(fd_vaxis, vaxis_str, strlen(vaxis_str));
			}
			if ((fd_video >= 0)&&(fd_ppmgr >= 0)) 	write(fd_video, "2", strlen("2"));			
			ret = 0;
			break;
		case 2: //720p
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "0", strlen("0"));
			if (fd_video >= 0) 	write(fd_video, "1", strlen("1"));
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "1", strlen("1"));
			if(fd_ppmgr_rect >= 0)
				write(fd_ppmgr_rect, "40 15 1240 705 0", strlen("40 15 1240 705 0"));
			else if(fd_vaxis >= 0)
				write(fd_vaxis, "40 15 1240 705", strlen("40 15 1240 705"));
			write(fd_daxis, daxis_str, strlen(daxis_str));
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height); 
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height);	
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);	
			if((fd_ppmgr >= 0)&&(find_flag)){
				write(fd_vaxis, vaxis_str, strlen(vaxis_str));
			}
			if ((fd_video >= 0)&&(fd_ppmgr >= 0)) 	write(fd_video, "2", strlen("2"));			
			ret = 0;
			break;
		case 3: //1080i			
		case 4: //1080p
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "0", strlen("0"));
			if (fd_video >= 0) 	write(fd_video, "1", strlen("1"));
			if (fd_ppmgr >= 0) 	write(fd_ppmgr, "1", strlen("1"));
			if(fd_ppmgr_rect >= 0)
				write(fd_ppmgr_rect, "40 20 1880 1060 0", strlen("40 20 1880 1060 0"));
			else if(fd_vaxis >= 0)
				write(fd_vaxis, "40 20 1880 1060", strlen("40 20 1880 1060"));
			write(fd_daxis, daxis_str, strlen(daxis_str));
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,0);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height); 
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_WIDTH,osd_width);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_HEIGHT,osd_height);	
			ioctl(fd0,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);
			ioctl(fd1,FBIOPUT_OSD_FREE_SCALE_ENABLE,1);	
			if((fd_ppmgr >= 0)&&(find_flag)){
				write(fd_vaxis, vaxis_str, strlen(vaxis_str));
			}
			if ((fd_video >= 0)&&(fd_ppmgr >= 0)) 	write(fd_video, "2", strlen("2"));			
			ret = 0;
			break;	
		default:			
			break;					
	}	

exit:	
	close(fd0);
	close(fd1);
	close(fd_vaxis);
	close(fd_daxis);	
	close(fd_fb);	
	close(fd_ppmgr);
	close(fd_video);
	close(fd_ppmgr_rect);
	return ret;

}
																			
JNIEXPORT jint JNICALL Java_com_amlogic_HdmiSwitch_HdmiSwitch_DisableFreeScaleJni( JNIEnv * env,
																									jobject thiz, jint mode )
{	
		return DisableFreeScale(mode);
}
JNIEXPORT jint JNICALL Java_com_amlogic_HdmiSwitch_HdmiSwitch_EnableFreeScaleJni( JNIEnv * env,
																									jobject thiz, jint mode )
{	
		return EnableFreeScale(mode);
}
