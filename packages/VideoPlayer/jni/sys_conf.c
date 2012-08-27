#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <fcntl.h>
#include  <linux/fb.h>
#include <errno.h>
#include "sys_conf.h"
#include <cutils/log.h>
#include <dlfcn.h> 
#include <sys/ioctl.h>
#include <fcntl.h>

static int set_fb0_blank(int blank)
{
    int fd;
    char *path = "/sys/class/graphics/fb0/blank" ;   
    char  bcmd[16];
    memset(bcmd,0,16);
    fd=open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);
    if(fd>=0){
        sprintf(bcmd,"%d",blank);
        write(fd,bcmd,strlen(bcmd));
        close(fd);
        return 0;
    }
    return -1;                  
}

int SYS_disable_osd0(void)
{
    set_fb0_blank(1);   
    return 0;
}

int SYS_enable_osd0(void)
{
    set_fb0_blank(0);
    return 0;
}
//============================================
//about colorkey

#ifndef FBIOPUT_OSD_SRCCOLORKEY
#define  FBIOPUT_OSD_SRCCOLORKEY    0x46fb
#endif

#ifndef FBIOPUT_OSD_SRCKEY_ENABLE
#define  FBIOPUT_OSD_SRCKEY_ENABLE  0x46fa
#endif


#ifndef FBIOPUT_OSD_SET_GBL_ALPHA
#define  FBIOPUT_OSD_SET_GBL_ALPHA  0x4500
#endif

int SYS_enable_colorkey(short key_rgb565)
{
    int ret = -1;
    int fd_fb0 = open("/dev/graphics/fb0", O_RDWR);
    if (fd_fb0 >= 0) {
        uint32_t myKeyColor = key_rgb565;
        uint32_t myKeyColor_en = 1;
        printf("enablecolorkey color=%#x\n", myKeyColor);
        ret = ioctl(fd_fb0, FBIOPUT_OSD_SRCCOLORKEY, &myKeyColor);
        ret += ioctl(fd_fb0, FBIOPUT_OSD_SRCKEY_ENABLE, &myKeyColor_en);
        close(fd_fb0);
    }
    return ret;
}
int SYS_disable_colorkey(void)
{
    int ret = -1;
    int fd_fb0 = open("/dev/graphics/fb0", O_RDWR);
    if (fd_fb0 >= 0) {
        uint32_t myKeyColor_en = 0;
        ret = ioctl(fd_fb0, FBIOPUT_OSD_SRCKEY_ENABLE, &myKeyColor_en);
        close(fd_fb0);
    }
    return ret;

}


int SYS_set_black_policy(int blackout)
{
    int fd;
    int bytes;
    char *path = "/sys/class/video/blackout_policy";
    char  bcmd[16];
    fd=open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);
    if(fd>=0)
    {
        sprintf(bcmd,"%d",blackout);
        bytes = write(fd,bcmd,strlen(bcmd));
        close(fd);
        return 0;
    }
    return -1;
    
}

int SYS_get_black_policy()
{
    int fd;
    int black_out = 0;
    char *path = "/sys/class/video/blackout_policy";
    char  bcmd[16];
    fd=open(path, O_RDONLY);
    if(fd>=0)
    {       
        read(fd,bcmd,sizeof(bcmd));       
        black_out = strtol(bcmd, NULL, 16);       
        black_out &= 0x1;
        close(fd);      
    }
    return black_out;

}

int SYS_set_tsync_enable(int enable)
{
    int fd;
    char *path = "/sys/class/tsync/enable";    
    char  bcmd[16];
    fd=open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);
    if(fd>=0)
    {
        sprintf(bcmd,"%d",enable);
        write(fd,bcmd,strlen(bcmd));
        close(fd);
        return 0;
    }
    return -1;
    
}

int SYS_set_global_alpha(int alpha){
    int ret = -1;   
    int fd_fb0 = open("/dev/graphics/fb0", O_RDWR); 
    if (fd_fb0 >= 0) {   
        uint32_t myAlpha = alpha;  
        ret = ioctl(fd_fb0, FBIOPUT_OSD_SET_GBL_ALPHA, &myAlpha);    
        close(fd_fb0);   

    }   
    return ret;
}
int SYS_get_osdbpp(){
       int ret = 16;   
       int fd_fb0 = open("/dev/graphics/fb0", O_RDWR);   
       if (fd_fb0 >= 0) {     
            struct fb_var_screeninfo vinfo; 
            ioctl(fd_fb0, FBIOGET_VSCREENINFO, &vinfo);  
            close(fd_fb0);      
            ret = vinfo.bits_per_pixel;
       }   
       return ret;
}
//===========================================
int SYS_set_video_preview_win(int x,int y,int w,int h)
{
    int fd;
    char *path = "/sys/class/video/axis";    
    char  bcmd[32];

    memset(bcmd,0,sizeof(bcmd));
    
    fd=open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);
    if(fd>=0)
    {
        
        sprintf(bcmd,"%d %d %d %d",x,y,w,h);
        write(fd,bcmd,strlen(bcmd));
        close(fd);
        return 0;
    }
    return -1;    
}

#define VIDEO_SCREEN_W 1280
#define VIDEO_SCREEN_H 720

int SYS_set_video_fullscreen(){
    int fd;
    char *path = "/sys/class/video/axis";    
    char  bcmd[32];

    memset(bcmd,0,sizeof(bcmd));
    
    fd=open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);
    if(fd>=0)
    {
        
        sprintf(bcmd,"%d %d %d %d",0,0,VIDEO_SCREEN_W,VIDEO_SCREEN_H);
        write(fd,bcmd,strlen(bcmd));
        close(fd);
        return 0;
    }
    return -1;      
}

static int AddVfmPath(char *path)
{
    FILE * fp;
    
    fp = fopen("/sys/class/vfm/map", "w");
    
    if(fp != NULL) {
        fprintf(fp, "%s", path);
    } else {
        LOGE("VideoPlayer open /sys/class/vfm/map ERROR(%s)!!\n", strerror(errno));
        return -1;
    }

    fclose(fp);
    return 0;
}
static int RmVfmDefPath(void)
{
	int fd, ret;
	char str[]="rm default";

	fd = open("/sys/class/vfm/map", O_RDWR);

	if(fd < 0) {
		LOGE("VideoPlayer open /sys/class/vfm/map ERROR(%s)!!\n",strerror(errno));
		close(fd);
		return -1;
	} else {
	    	ret = write(fd, str, sizeof(str));
	}
	close(fd);
	return ret;
}

static int Open3DPpmgr(SYS_3D_MODE_SET commd)
{
	int ppmgrfd = open("/dev/ppmgr", O_RDWR);

	if(ppmgrfd < 0) {

		LOGE("VideoPlayer open ppmgr, error (%s)\n", strerror(errno));
		return ppmgrfd;
	}
	int ret = -1;
	switch(commd)
	{
		case SYS_3D_DISABLE:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_DISABLE);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D fucntion (0: Disalbe!!)\n");
#endif
			break;
		case SYS_3D_AUTO:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_AUTO);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D fucntion (1: AUTO!!)\n");
#endif
			break;
		case SYS_3D_LR:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_LR);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D fucntion (2 L/R!!)\n");
#endif
			break;
		case SYS_3D_BT:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_BT);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D fucntion (3: B/T!)\n");
#endif
			break;
		case SYS_3D_TO_2D_L:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_TO_2D_L);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D function (4: 3D_TO_2D_L!!)\n");
#endif
			break;
		case SYS_3D_TO_2D_R:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_TO_2D_R);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D function (5: 3D_TO_2D_R!!)\n");
#endif
			break;
		case SYS_3D_TO_2D_T:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_TO_2D_T);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D function (6: 3D_TO_2D_T!!)\n");
#endif		 	
			break;
		case SYS_3D_TO_2D_B:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_TO_2D_B);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D function (7: 3D_TO_2D_B!!)\n");
#endif		 	
			break;
		case SYS_3D_TO_2D_AUTO_1:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_TO_2D_AUTO_1);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D function (8: 3D_TO_2D_AUTO_1!!)\n");
#endif		 	
			break;
		case SYS_3D_TO_2D_AUTO_2:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_TO_2D_AUTO_2);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D function (9: 3D_TO_2D_AUTO_2!!)\n");
#endif		 	
			break;
		case SYS_2D_TO_3D:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_2D_TO_3D);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D fucntion (10: 2D->3D!!)\n");
#endif
			break;
		case SYS_3D_FIELD_DEPTH:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_FIELD_DEPTH);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D function (11: FIELD_DEPTH!!)\n");
#endif
			break;
		case SYS_3D_AUTO_SWITCH:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_AUTO_SWITCH);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D function (12: 3D_AUTO_SWITCH!!)\n");
#endif
			break;
		case SYS_3D_LR_SWITCH:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_LR_SWITCH);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D function (13: 3D_LR_SWITCH!!)\n");
#endif
			break;
		case SYS_3D_BT_SWITCH:
			ret = ioctl(ppmgrfd, PPMGR_IOC_ENABLE_PP, MODE_3D_BT_SWITCH);
#ifdef LOGD_3D_FUNCTION
			LOGD("VideoPlayer 3D function (14: 3D_BT_SWITCH!!)\n");
#endif
			break;
	}

	if(ret < 0)
		LOGE("VideoPlayer set 3D function error");
	close(ppmgrfd);
	return ret;
}

int SYS_set_3D_mode(SYS_3D_MODE_SET mode)
{
	int ret = -1;
#if 0	
	ret = RmVfmDefPath();
	if(ret<0)	{
		return -1;
	}
#endif	
	switch(mode) {
		case 0:
#if 0			
			ret = AddVfmPath("add default decoder amvideo");
#endif	
			Open3DPpmgr(0);//disable
			break;

		default:
#if 0			
			ret = AddVfmPath("add default decoder ppmgr amvideo");
			usleep(100);
#endif			
			Open3DPpmgr(mode);
			break;
	}
	return ret;
}
int SYS_set_3D_view_mode(SYS_3D_VIEW_MODE_SET vmode){
	int ret = -1;
	int ppmgrfd = open("/dev/ppmgr", O_RDWR);
	if(ppmgrfd<0){
		LOGE("VideoPlayer open ppmgr, error (%s)\n", strerror(errno));
		return ppmgrfd;		
	}	
	switch(vmode){
		case SYS_3D_VIEW_NORMAL:
			ret = ioctl(ppmgrfd, PPMGR_IOC_VIEW_MODE, SYS_3D_VIEW_NORMAL);
			break;
		case SYS_3D_VIEW_FULL:
			ret = ioctl(ppmgrfd, PPMGR_IOC_VIEW_MODE, SYS_3D_VIEW_FULL);
			break;
		case SYS_3D_VIEW_4_3:
			ret = ioctl(ppmgrfd, PPMGR_IOC_VIEW_MODE, SYS_3D_VIEW_4_3);
			break;
		case SYS_3D_VIEW_16_9:
			ret = ioctl(ppmgrfd, PPMGR_IOC_VIEW_MODE, SYS_3D_VIEW_16_9);
			break;
		case SYS_3D_VIEW_NO_SCALEUP:
			ret = ioctl(ppmgrfd, PPMGR_IOC_VIEW_MODE,SYS_3D_VIEW_NO_SCALEUP);
			break;
		default:
			LOGE("invalid case,never see this line,vmode value:%d\n",vmode);
			break;
	}
	if(ret < 0)
		LOGE("VideoPlayer SYS_set_3D_view_mode function error");
			
	if(ppmgrfd>0){
		close(ppmgrfd);
	}
	return 0;
}
int SYS_set_3D_switch(int isOn){
	int ret = -1;
	if(isOn!=0 || isOn!=1){
		LOGE("invalid para for 3d switch\n");
		return -1;
	}
	int ppmgrfd = open("/dev/ppmgr", O_RDWR);
	if(ppmgrfd<0){
		LOGE("VideoPlayer open ppmgr, error (%s)\n", strerror(errno));
		return ppmgrfd;		
	}	
	
	ret = ioctl(ppmgrfd, PPMGR_IOC_SWITCHMODE,isOn);

	if(ret < 0)
		LOGE("VideoPlayer SYS_set_3D_view_mode function error");
			
	if(ppmgrfd>0){
		close(ppmgrfd);
	}	
	return 0;
}

int SYS_set_3D_aspect_full(int aspect){
	int ret = -1;
	int ppmgrfd = open("/dev/ppmgr", O_RDWR);
	if(ppmgrfd<0){
		LOGE("VideoPlayer open ppmgr, error (%s)\n", strerror(errno));
		return ppmgrfd;		
	}	
	LOGI("%s,para:%d\n",__FUNCTION__,aspect);
	switch(aspect){
		case 0:
			ret = ioctl(ppmgrfd, PPMGR_IOC_HOR_VER_DOUBLE, 0);
			break;
		case 1:
			ret = ioctl(ppmgrfd, PPMGR_IOC_HOR_VER_DOUBLE, 1);
			break;
		case 2:
			ret = ioctl(ppmgrfd, PPMGR_IOC_HOR_VER_DOUBLE, 2);
			break;
		default:
			LOGE("invalid case,never see this line,aspect value:%d\n",aspect);
			break;
	}
	if(ret < 0)
		LOGE("VideoPlayer SYS_set_3D_view_mode function error");
			
	if(ppmgrfd>0){
		close(ppmgrfd);
	}
	return 0;
}

int SYS_set_3D_grating(int isOn){	
    int fd;
    char *path = "/sys/class/enable3d/enable-3d" ;   
    char  bcmd[16];
    memset(bcmd,0,16);
    fd=open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);
    if(fd>=0){
		if(isOn >0){
			sprintf(bcmd,"%d",1);
		}else{
			sprintf(bcmd,"%d",0);
		}
        write(fd,bcmd,strlen(bcmd));
        close(fd);
        return 0;
    }
    return -1;   	
	
}
