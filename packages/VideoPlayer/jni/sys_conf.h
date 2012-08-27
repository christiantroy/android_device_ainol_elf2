#ifndef _SYS_CONF_H_
#define _SYS_CONF_H_

#ifdef __cplusplus
extern "C" {
#endif

#define PPMGR_IOC_MAGIC         'P'
#define PPMGR_IOC_ENABLE_PP     _IOW(PPMGR_IOC_MAGIC,0X01,unsigned int)

#define PPMGR_IOC_VIEW_MODE  _IOW(PPMGR_IOC_MAGIC,0X03,unsigned int)
#define PPMGR_IOC_HOR_VER_DOUBLE  _IOW(PPMGR_IOC_MAGIC,0X04,unsigned int)
#define PPMGR_IOC_SWITCHMODE  _IOW(PPMGR_IOC_MAGIC,0X05,unsigned int)


#define MODE_3D_DISABLE         0x00000000
#define MODE_3D_AUTO            0x00000001
#define MODE_3D_AUTO_SWITCH     0x00000401
#define MODE_3D_LR              0x00000101
#define MODE_3D_LR_SWITCH       0x00000501
#define MODE_3D_BT              0x00000201
#define MODE_3D_BT_SWITCH      0x00000601

#define MODE_3D_TO_2D_AUTO_1   0x00000002
#define MODE_3D_TO_2D_AUTO_2    0x00000802
#define MODE_3D_TO_2D_L         0x00000102
#define MODE_3D_TO_2D_R         0x00000902
#define MODE_3D_TO_2D_T         0x00000202
#define MODE_3D_TO_2D_B         0x00000a02

#define MODE_2D_TO_3D           0x00000003
#define MODE_FIELD_DEPTH        0x00010003

typedef enum _3D_MODE_SET{
	SYS_3D_DISABLE = 0,
	SYS_3D_AUTO,
	SYS_3D_LR,
	SYS_3D_BT,
	SYS_3D_TO_2D_L,
	SYS_3D_TO_2D_R,
	SYS_3D_TO_2D_T,
	SYS_3D_TO_2D_B,
	SYS_3D_TO_2D_AUTO_1,
	SYS_3D_TO_2D_AUTO_2,	
	SYS_2D_TO_3D,
	SYS_3D_FIELD_DEPTH,	
	SYS_3D_AUTO_SWITCH,
	SYS_3D_LR_SWITCH,
	SYS_3D_BT_SWITCH,
	
}SYS_3D_MODE_SET;

typedef enum _3D_VIEWMODE_SET{
	SYS_3D_VIEW_NORMAL=0,
	SYS_3D_VIEW_FULL,
	SYS_3D_VIEW_4_3,
	SYS_3D_VIEW_16_9,
	SYS_3D_VIEW_NO_SCALEUP,
}SYS_3D_VIEW_MODE_SET;


/*
Description: disable fb0 layer      
Comments:  disable osd display.          
*/
int SYS_disable_osd0(void);
/*
Description: enable fb0 layer      
Comments:  enable osd display.          
*/
int SYS_enable_osd0(void);


/*
Description:set colorkey      
Comments: refer to rgb565 as current color space.  
*/
int SYS_enable_colorkey(short key_rgb565);

/*
Description:disable colorkey      
Comments: clearall coloreky. 
*/
int SYS_disable_colorkey(void);   

int SYS_set_black_policy(int blackout);

int SYS_get_black_policy();

int SYS_set_tsync_enable(int enable);

int SYS_set_global_alpha(int alpha);
int SYS_get_osdbpp();

int SYS_set_video_preview_win(int x,int y,int w,int h);
int SYS_set_3D_mode(SYS_3D_MODE_SET mode);


int SYS_set_3D_view_mode(SYS_3D_VIEW_MODE_SET vmode);
int SYS_set_3D_switch(int isOn);

/*
 * aspect:function disable if aspect is 0,1 means L/R,2 means T/B.
 * 
*/
int SYS_set_3D_aspect_full(int aspect);


int SYS_set_3D_grating(int isOn);

/*
Description:set fullscreen
Comments: default 1280*720
*/
int SYS_set_video_fullscreen();


#ifdef __cplusplus
}
#endif

#endif //_SYS_CONF_H_

