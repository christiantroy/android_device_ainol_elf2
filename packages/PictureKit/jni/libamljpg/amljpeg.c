/*
 * Copyright (c) 2009, Texas Instruments Incorporated
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * *  Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * *  Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * *  Neither the name of Texas Instruments Incorporated nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <string.h>
#include <sys/mman.h>
#include <ge2d.h>
#include <amstream.h>
#include <vformat.h>
#include "amljpeg.h"
#include <jpegdec.h>
#include <cutils/log.h>
#include <sys/time.h>
#include <time.h>

#define JPEG_DBG 
//#undef JPEG_DBG

//#define LOGD printf

#include <cutils/log.h>
#define   FILE_NAME_AMPORT    "/dev/amstream_vbuf"
#define   FILE_NAME_JPEGDEC	  "/dev/amjpegdec"
#define   FILE_NAME_GE2D		  "/dev/ge2d"
#define   MAX_BUFFER_SIZE		  (32*1024)
#define   HEADER_SIZE				  (2048)	
#define CANVAS_ALIGNED(x)	(((x) + 7) & ~7)

enum {
	DEC_STAT_INFOCONFIG=0,
	DEC_STAT_INFO,
	DEC_STAT_DECCONFIG,
	DEC_STAT_RUN,
	DEC_STAT_MAX
};
enum{
    KEEP_SIZE       =0 ,   
    KEEPASPECTRATIO  ,  	/* keep aspect ratio */
    KEEPASPECTRATIOWITHCROP,	/* keep aspect ratio with crop. */
	SCRETCHFULLSCREEN		/* scretch full screen. */
};

typedef  struct{
	int  fd_amport;
	jpegdec_info_t info;
	char buffer[MAX_BUFFER_SIZE];
}jpeg_data_t;
static jpegdec_mem_info_t jpeg_dec_mem;

#define Format_RGB32 0
#define Format_RGB16 1
int fd_amport = -1;
int scale_x;
int scale_y;
int scale_w;
int scale_h;	
unsigned char *planes[4];
unsigned decoder_opt;
int sMode;

#ifndef JPEG_DBG
#define LOGD
#endif

//to get a valid image for jpeg.
inline int nextMulOf8(int n)
{
    return ((n + 7) & 0xfffffff8);
}

int amljpeg_init()
{
    int i = 0;  
    LOGD("last fd_amport is (%d).\n",fd_amport);  
    fd_amport = open(FILE_NAME_AMPORT, O_WRONLY);
	if(fd_amport<0) {    
		LOGD("error:hwjpeg initing,can't access %s.\n",FILE_NAME_AMPORT);
		LOGD(" error no %d\n",errno);
		return -1;
	}

	
    //init amport for write device data.	
    ioctl(fd_amport, AMSTREAM_IOC_VB_SIZE, 1024*1024);
    ioctl(fd_amport, AMSTREAM_IOC_VFORMAT, VFORMAT_JPEG); 
    ioctl(fd_amport, AMSTREAM_IOC_PORT_INIT);
    while(access(FILE_NAME_JPEGDEC, R_OK|W_OK)) {	 //waitting for device created.
      	usleep(10000);
      	i++;
      	if(i>20)
      	{   	    
			LOGD("hw jpeg init decoder error---hw jpeg device access error\n");	
			return -1;
      	}
    }
    return 0;
}

void amljpeg_exit()
{
	if(fd_amport>=0)
	{
		close(fd_amport);	
		LOGD("fd_amport (%d) closed.\n",fd_amport);
		fd_amport=-1;
	}
}
unsigned int  ImgFormat2Ge2dFormat(int img_format)
{
	unsigned int format=0xffffffff;
	
	 switch (img_format) {
	/* 32 bpp */
    case COLOR_S32_ARGB:
		format = GE2D_FORMAT_S32_ARGB;
		break;
	/* 16 bpp */
    case COLOR_S24_RGB:
		format = GE2D_FORMAT_S24_RGB;
	    break;
    case COLOR_S16_RGB:
		format = GE2D_FORMAT_S16_RGB_565;
	    break;
	case COLOR_S32_ABGR:
        format = GE2D_FORMAT_S32_ABGR;
        break;
	default:
    	LOGD("blit_32(): Image format %d not supported!", img_format);
        format = GE2D_FORMAT_S32_ARGB;
        break;	
    	}
	 return format;

}
int clear_plane(int index,jpegdec_config_t *config,int format)
{
	int fd_ge2d=-1;
	config_para_t ge2d_config;
    ge2d_op_para_t op_para;
    memset((char*)&ge2d_config,0,sizeof(config_para_t));
    fd_ge2d= open(FILE_NAME_GE2D, O_RDWR);
	if(fd_ge2d<0)
	{    
		LOGD("can't open framebuffer device" );  		
		goto exit;
	}
	LOGD("start clean plane buffer %d!!!!!\n", index);
	ge2d_config.src_dst_type = ALLOC_ALLOC;

//	qCritical("planes[3]addr : 0x%x-0x%x" ,planes[3],ge2d_config.dst_planes[0].addr);		
    
    switch(index){
        case 0:
        op_para.color=0x008080ff;
        ge2d_config.src_format = GE2D_FORMAT_S8_Y;
	    ge2d_config.dst_format = GE2D_FORMAT_S8_Y;
    	ge2d_config.dst_planes[0].addr = config->addr_y;
    	ge2d_config.dst_planes[0].w = config->canvas_width;
    	ge2d_config.dst_planes[0].h = config->dec_h;     
    	op_para.src1_rect.x = config->dec_x;
    	op_para.src1_rect.y = config->dec_y;
    	op_para.src1_rect.w = config->dec_w;
    	op_para.src1_rect.h = config->dec_h;      	   
    	op_para.dst_rect.x = config->dec_x;
    	op_para.dst_rect.y = config->dec_y;
    	op_para.dst_rect.w = config->dec_w;
    	op_para.dst_rect.h = config->dec_h;        	
        break;
        case 1:
        op_para.color=0x008080ff;
        ge2d_config.src_format = GE2D_FORMAT_S8_CB;
	    ge2d_config.dst_format = GE2D_FORMAT_S8_CB;
    	ge2d_config.dst_planes[0].addr = config->addr_u;
    	ge2d_config.dst_planes[0].w = config->canvas_width/2;
    	ge2d_config.dst_planes[0].h = config->dec_h / 2;      
    	op_para.src1_rect.x = config->dec_x/2;
    	op_para.src1_rect.y = config->dec_y/2;
    	op_para.src1_rect.w = config->dec_w/2;
    	op_para.src1_rect.h = config->dec_h/2;       	
    	op_para.dst_rect.x = config->dec_x/2;
    	op_para.dst_rect.y = config->dec_y/2;
    	op_para.dst_rect.w = config->dec_w/2;
    	op_para.dst_rect.h = config->dec_h/2;        	
        break;
        case 2:
        op_para.color=0x008080ff;
        ge2d_config.src_format = GE2D_FORMAT_S8_CR;
	    ge2d_config.dst_format = GE2D_FORMAT_S8_CR;    
    	ge2d_config.dst_planes[0].addr = config->addr_v;
    	ge2d_config.dst_planes[0].w = config->canvas_width/2;
    	ge2d_config.dst_planes[0].h = config->dec_h / 2;
    	op_para.src1_rect.x = config->dec_x/2;    
    	op_para.src1_rect.y = config->dec_y/2;    
    	op_para.src1_rect.w = config->dec_w/2;    
    	op_para.src1_rect.h = config->dec_h/2;    
    	op_para.dst_rect.x = config->dec_x/2;
    	op_para.dst_rect.y = config->dec_y/2;
    	op_para.dst_rect.w = config->dec_w/2;
    	op_para.dst_rect.h = config->dec_h/2;        		    
        break;
        case 3:
        op_para.color=0x000000ff;
        ge2d_config.src_format = ImgFormat2Ge2dFormat(format);
        ge2d_config.dst_format = ImgFormat2Ge2dFormat(format);
    	ge2d_config.dst_planes[0].addr=planes[3];
    	ge2d_config.dst_planes[0].w= scale_w;
    	ge2d_config.dst_planes[0].h = scale_h;    
    	op_para.src1_rect.x = scale_x; 
    	op_para.src1_rect.y = scale_y; 
    	op_para.src1_rect.w = scale_w; 
    	op_para.src1_rect.h = scale_h;       	
    	op_para.dst_rect.x = scale_x;  
    	op_para.dst_rect.y = scale_y;  
    	op_para.dst_rect.w = scale_w;  
    	op_para.dst_rect.h = scale_h;    	    
        break;
        case 4:
        ge2d_config.src_dst_type = OSD0_OSD0;
        op_para.color=0x000000ff;
        ge2d_config.src_format = ImgFormat2Ge2dFormat(format);
        ge2d_config.dst_format = ImgFormat2Ge2dFormat(format);
    	op_para.src1_rect.x = scale_x; 
    	op_para.src1_rect.y = scale_y; 
    	op_para.src1_rect.w = scale_w; 
    	op_para.src1_rect.h = scale_h;       	
    	op_para.dst_rect.x = scale_x;  
    	op_para.dst_rect.y = scale_y;  
    	op_para.dst_rect.w = scale_w;  
    	op_para.dst_rect.h = scale_h;            
        break;
        default:
        break;                            
    }
	ioctl(fd_ge2d, FBIOPUT_GE2D_CONFIG, &ge2d_config);
   ioctl(fd_ge2d, FBIOPUT_GE2D_FILLRECTANGLE, &op_para);     
exit:       
	if(fd_ge2d >=0){
	    close(fd_ge2d);		
	    fd_ge2d = -1;
    }       
    LOGD("finish clean plane buffer %d!!!!!\n", index);
    return 0;     
}


int compute_keep_ratio(jpeg_data_t  *jpeg_data,jpegdec_config_t *config)
{

    int image_width , image_height;
    int frame_left , frame_top ,frame_width, frame_height;
    int target_width , target_height;
    if(config->angle & 1){
        image_width = jpeg_data->info.height ;
        image_height = jpeg_data->info.width ;         
    }else{
        image_width = jpeg_data->info.width ;
        image_height = jpeg_data->info.height;          
    }
    frame_left = scale_x;
    frame_top =  scale_y;
    frame_width = scale_w;
    frame_height = scale_h ; 
    
    if((image_width * frame_height) > (image_height * frame_width)){
/*according with width*/        
        target_width = frame_width ;
        target_height = (float)(image_height*frame_width)/image_width;       
    }else{
        target_height = frame_height ;
        target_width = (float)(image_width*frame_height)/image_height;   
    }
    config->dec_x = 0 ; 
    config->dec_y = 0;
    config->dec_w = target_width ;
    config->dec_h = target_height ;
    if((image_width < frame_width)&&(image_height < frame_height)){        
        config->dec_w = image_width ;
        config->dec_h = image_height ;        
    }
    return 0;    
}

int compute_keep_ratio_by_expanding(jpeg_data_t  *jpeg_data,jpegdec_config_t *config)
{
    int image_width , image_height;
    int frame_left , frame_top ,frame_width, frame_height;
    int target_width , target_height;
    int w_limit ,h_limit;
    if(config->angle & 1){
        image_width = jpeg_data->info.height ;
        image_height = jpeg_data->info.width ;         
    }else{
        image_width = jpeg_data->info.width ;
        image_height = jpeg_data->info.height;          
    }
    frame_left = scale_x;
    frame_top =  scale_y;
    frame_width = scale_w;
    frame_height = scale_h ; 
    
    if((frame_width * image_height) > (frame_height * image_width)){
/*adjust height*/        
        target_width = frame_width ;
        target_height = (float)(image_height*frame_width)/image_width;       
    }else{
        target_height = frame_height ;
        target_width = (float)(image_width*frame_height)/image_height;   
    }    
    
    config->dec_x = 0 ; 
    config->dec_y = 0;
    config->dec_w = target_width ;
    config->dec_h = target_height ;
    if(config->angle & 1){
        w_limit = 1920;
        h_limit = 1920;   
    }else{
        w_limit = 1920;
        h_limit = 1920;      
    }
    if((target_width > w_limit)||(target_height > h_limit)){       
        LOGD("crop area exceed %d*%d!!!!!\n", target_width, target_height);       
        return -1;       
    }
    if((image_width <= frame_width)&&(image_height <= frame_height)){        
        config->dec_w = image_width ;
        config->dec_h = image_height ;        
    }else if((image_width < frame_width)||(image_height < frame_height)){    
        LOGD("crop function disable %d*%d!!!!!\n", image_width, image_height);          
        return -1;    
    }    
    return 0;      
}
int rebuild_jpg_config_para(jpeg_data_t  *jpeg_data,jpegdec_config_t *config)
{
	int ret = 0;	
	switch(sMode){
	    case KEEP_SIZE:
	    case KEEPASPECTRATIO:
		case SCRETCHFULLSCREEN:
			ret = compute_keep_ratio(jpeg_data,config);
			break;
	    break;
	    case KEEPASPECTRATIOWITHCROP:
	    ret = compute_keep_ratio_by_expanding(jpeg_data,config);
	    if(ret < 0){
	        ret = compute_keep_ratio(jpeg_data,config);    
	    }
	    break;
	    default:
	    break;    
	}  
	if(config->dec_h<2) {
		LOGD("too small to decode with hwjpeg decoder.\n");
		return -1;
	}	
	return 0;
}

int set_jpeg_dec_mem(jpeg_data_t *jpeg_data,jpegdec_config_t *config) {
	config->canvas_width = CANVAS_ALIGNED(config->dec_w);	
	planes[0] = (unsigned char *)jpeg_dec_mem.canv_addr;
	planes[1] = planes[0] + CANVAS_ALIGNED(config->canvas_width) *config->dec_h;
	planes[2] = planes[1] + CANVAS_ALIGNED(config->canvas_width/2) * config->dec_h/2;
	planes[3] = planes[2] + CANVAS_ALIGNED(config->canvas_width/2) * config->dec_h/2;
	planes[3] = ((unsigned long) planes[3] +0xffff)&0xffff0000; 
	if ((planes[3]+CANVAS_ALIGNED(config->canvas_width) *config->dec_h*4) > 
		(jpeg_dec_mem.canv_addr+jpeg_dec_mem.canv_len)) {
		LOGD("Not enough system memory\n");
		return -1;
	}
	config->addr_y = planes[0];
	config->addr_u = planes[1];
	config->addr_v = planes[2];	
	
	if(config->dec_w==0 ||config->dec_h==0)
	{
		config->dec_w= jpeg_data->info.width;
		config->dec_h= jpeg_data->info.height;
	}
//	scaleSize(config->dec_w, config->dec_h, jpeg_data->info.width, jpeg_data->info.height, (Qt::AspectRatioMode)config->opt);
	config->opt = 0;
	config->dec_x = 0;
	config->dec_y = 0;
	config->angle = CLKWISE_0;
	clear_plane(0,config,0);
	clear_plane(1,config,0);
	clear_plane(2,config,0);
	return 0;	
}
unsigned int  get_decoder_state(int  handle)
{
	if(handle>0)
	{
		return ioctl(handle, JPEGDEC_IOC_STAT);
	}
	return JPEGDEC_STAT_ERROR;
}

char* scan_line(aml_image_info_t* info , int i)
{
    if((!info)||(!info->data)||(i > 1080)){
        return NULL;    
    }
    return (info->data + i*info->bytes_per_line);
}

int read_jpeg_data(int fd,jpeg_data_t  *jpeg_data,int op_max,jpegdec_config_t *config,int thumbprefor,int fd_jpegdec)
{
    struct am_io_param vb_info;
	int  read_num;
	unsigned int decState;
	int fd_amport=jpeg_data->fd_amport;
	
	lseek(fd ,0, SEEK_SET);  //seek device head .
	if(jpeg_data->buffer==NULL||fd_amport<0) return 0;
  	
	int  op_step=DEC_STAT_INFOCONFIG ;
	int  result=0;;
	int  read_unit=HEADER_SIZE;
	int total_size =0 ;
	int file_read_end = 0;
//	QDateTime time1;
//	QDateTime time2;			
	int wait_info_count  =0 ;
	int wait_timeout = 0 ;
//    time1= QDateTime::currentDateTime();  
    int time_start = time((time_t*)NULL) ;
    int time_cur;
    int time_poll;
    
	LOGD("decoder start\n");
	while(op_step < op_max)
	{
		decState=get_decoder_state( fd_jpegdec);
		result = decState;
		if (decState & JPEGDEC_STAT_ERROR) {	    
			LOGD("jpegdec error\n");
			break;
		}

		if (decState & JPEGDEC_STAT_UNSUPPORT) {	    
			LOGD("jpegdec unsupported format\n");	
			break;
		}

		if (decState & JPEGDEC_STAT_DONE) {	    
			LOGD("jpegdec done\n");	
			break;
		}
		ioctl(fd_amport, AMSTREAM_IOC_VB_STATUS,&vb_info);		
		if((!file_read_end)&&(vb_info.status.data_len < ((4*vb_info.status.size)/5))&&(decState & JPEGDEC_STAT_WAIT_DATA)){
			if(total_size == 0 ){
				memset(jpeg_data->buffer,0,HEADER_SIZE);
				 write( fd_amport, jpeg_data->buffer, HEADER_SIZE); 				
			}			
			read_num=read(fd,jpeg_data->buffer,read_unit );
			total_size += read_num;
			if(read_num<0)
			{		    
				LOGD("can't read data from jpeg device");  	
				result= 0;
				break;
			}
			read_unit=MAX_BUFFER_SIZE;//expand buffer size to read real data.
			if(read_num==0) //file end then fill padding data into buffer.
			{
			    file_read_end = 1;
				read_num=read_unit=HEADER_SIZE;
				memset(jpeg_data->buffer,0,read_unit);
			}
			int ret = write( fd_amport, jpeg_data->buffer, read_num); 
			
		}	
		switch(op_step)
		{
			case DEC_STAT_INFOCONFIG:
			ioctl( fd_jpegdec, JPEGDEC_IOC_INFOCONFIG, decoder_opt);
			op_step=DEC_STAT_INFO;
			break;
			case DEC_STAT_INFO:
			if (decState & JPEGDEC_STAT_INFO_READY) {
				ioctl( fd_jpegdec, JPEGDEC_IOC_INFO, &jpeg_data->info);			
				LOGD("++jpeg informations:w:%d,h:%d\r\n",jpeg_data->info.width,jpeg_data->info.height);			
				op_step=DEC_STAT_DECCONFIG;
				
			}else{
			    wait_info_count++;
			    if(wait_info_count > 100){
			        wait_info_count = 0 ;		        	
                    time_cur = time((time_t*)NULL) ;
                    wait_timeout = time_cur - time_start;			        	        	
			        LOGD("current timeout is %d!!!\n",wait_timeout);	        		        		        	        		        
			    }
			    if(wait_timeout > 1){
			        op_step = op_max;  			        
			        LOGD("timeout for get jpeg info!!!\n");		        
			        result =0;
			        break;  			        
			    }		    
				LOGD("in jpeg decoding process\n");			
				result =0;
			}
			break;
			case DEC_STAT_DECCONFIG:
			if(config)
			{
				if (rebuild_jpg_config_para(jpeg_data,config)<0) {
					LOGD("get mem info failed\n");
					op_step = op_max;
					result =0;
					continue;
				}
				jpeg_dec_mem.angle=config->angle;
				jpeg_dec_mem.dec_w=config->dec_w;
				jpeg_dec_mem.dec_h=config->dec_h;
				if(ioctl(fd_jpegdec, JPEGDEC_G_MEM_INFO, &jpeg_dec_mem)<0) {
					LOGD("get mem info failed\n");
					op_step = op_max;
					result =0;
					continue;
				}
				if (set_jpeg_dec_mem(jpeg_data,config)<0||ioctl(fd_jpegdec, JPEGDEC_IOC_DECCONFIG, config)<0) {			    
					LOGD("decoder config failed\n");			
					op_step = op_max;
					result =0;
					continue;
				}
				
			}
			op_step =DEC_STAT_RUN;
			break;
			default:
			break;	
		}
		time_poll = time((time_t*)NULL) ;
		if((time_poll - time_start) > 8){
			op_step = op_max;  			        
			LOGD("it's a corrupted jpeg\n");		        
			result =0;  			
		}
	}
	LOGD("decoder exit\n");
	LOGD("total read bytes is %d",total_size);
	
	return result;
		
}

aml_image_info_t* read_jpeg_image(const char* url , int width, int height,int mode , int flag,
    int thumbpref,int colormode)
{  
    char* outimage = NULL; 
    aml_image_info_t* input_image_info;
    aml_image_info_t* output_image_info;   
    int input_image_width;    
    char* input_data;
    char* output_data;
    int fd_jpegdec;
    int i;

	sMode=mode;
	if(mode>SCRETCHFULLSCREEN) sMode=KEEPASPECTRATIO;
    if((width <= 0)||(height <=0)){
    	 width = 1280;
    	 height = 720;		
	}
	
	input_image_info = (aml_image_info_t*)malloc(sizeof(aml_image_info_t));
    output_image_info = (aml_image_info_t*)malloc(sizeof(aml_image_info_t));
    memset((char*)output_image_info , 0 , sizeof(aml_image_info_t));	
     int wait_timeout = 0 ;
     int fd = open(url,O_RDONLY ) ; 
     if(fd < 0){
		LOGD("amljpeg:source file:%s open error\n",url);
        goto exit;   
     }
	//we need some step to decompress jpeg image to output 
	// 1  request yuv space from cmem to store decompressed data
	// 2  config and decompress jpg picture.
	// 3	 request  new rgb space from cmem to store output rgb data.
	// 4  ge2d move yuv data to rgb space.
	// 5  release request mem to cmem module.
	jpegdec_config_t  config={0};
	jpeg_data_t  jpeg_data;
	int fd_ge2d=-1;
	config_para_t ge2d_config;
	int format=colormode;
    ge2d_op_para_t op_para;
	int bpl 	 ;

	memset((char*)&ge2d_config,0,sizeof(config_para_t));	
	scale_x = 0;
	scale_y = 0;
	scale_w = width;
	scale_h = height;	
/*default value for no scaler input*/	
	if(thumbpref!=0)  {
		decoder_opt=JPEGDEC_OPT_THUMBNAIL_PREFERED;
	} else {
		decoder_opt=0;
	}
	if((scale_w == 0)||(scale_h ==0)){
	    scale_w  = 160 ;
	    scale_h  = 100;     
	}
	config.opt=(unsigned)sMode ;
	jpeg_data.fd_amport=fd_amport;
	
	fd_jpegdec= open(FILE_NAME_JPEGDEC, O_RDWR); 
    if(fd_jpegdec <0 ){
        perror("open amjpec device error\r\n")	; 	
        return 0;
    }

	if(!(JPEGDEC_STAT_DONE&read_jpeg_data(fd,&jpeg_data,DEC_STAT_MAX,&config,thumbpref,fd_jpegdec)))
	{    
		LOGD("can't decode jpg pic\n");			
		goto exit;
	} 

	LOGD("deocde jpg pic completed\n");
    input_image_width = CANVAS_ALIGNED(scale_w);
	
	//open fb device to handle ge2d op FILE_NAME_GE2D
    fd_ge2d= open(FILE_NAME_GE2D, O_RDWR); 
	if(fd_ge2d<0)
	{    
		LOGD("can't open framebuffer device\n" );  			
		goto exit;
	}
/*antiflicking setting*/	
    if(flag){
	    ioctl(fd_ge2d,FBIOPUT_GE2D_ANTIFLICKER_ENABLE,1);    
	}else{
	    ioctl(fd_ge2d,FBIOPUT_GE2D_ANTIFLICKER_ENABLE,0);   
	}
	if(format==0) {
	    LOGD("default data!\n" );
	    if(jpeg_data.info.comp_num==3 ||jpeg_data.info.comp_num==4)
	    {
		    format = COLOR_S32_ARGB;
	    } else {
		    LOGD("unsupported color format\n" );  		
		    goto exit;
	    }
	} 
	clear_plane(3,&config,format);
	LOGD("start ge2d image format convert!!!!!\n");
	ge2d_config.src_dst_type = ALLOC_ALLOC;
//    ge2d_config.src_dst_type = ALLOC_OSD1;        //only for test
	ge2d_config.alu_const_color=0xff0000ff;
	ge2d_config.src_format = GE2D_FORMAT_M24_YUV420;
	ge2d_config.dst_format = ImgFormat2Ge2dFormat(format);
	if(0xffffffff==ge2d_config.dst_format)
	{	    
		LOGD("can't get proper ge2d format\n" );  			
		goto exit;
	}

	ge2d_config.src_planes[0].addr = config.addr_y;
	ge2d_config.src_planes[0].w =    config.canvas_width;
	ge2d_config.src_planes[0].h =    config.dec_h;
	ge2d_config.src_planes[1].addr = config.addr_u;
	ge2d_config.src_planes[1].w =    config.canvas_width/2;
	ge2d_config.src_planes[1].h =    config.dec_h / 2;

	ge2d_config.src_planes[2].addr = config.addr_v;
	ge2d_config.src_planes[2].w = config.canvas_width/2;
	ge2d_config.src_planes[2].h = config.dec_h / 2;
	ge2d_config.dst_planes[0].addr=planes[3];	
//	ge2d_config.dst_planes[0].w=  scale_w;
    ge2d_config.dst_planes[0].w= input_image_width ;
	ge2d_config.dst_planes[0].h = scale_h;
//#ifdef JPEG_DBG 	
//	LOGD("planes[3]addr : 0x%x-0x%x" ,planes[3],ge2d_config.dst_planes[0].addr);		
//#endif	
	ioctl(fd_ge2d, FBIOPUT_GE2D_CONFIG, &ge2d_config);
/*crop case*/
	
    switch(mode){
        case  KEEP_SIZE:
    		op_para.src1_rect.x = 0;
    		op_para.src1_rect.y = 0;
    		op_para.src1_rect.w = config.dec_w;
    		op_para.src1_rect.h = config.dec_h;
    		op_para.dst_rect.x = 0;
    		op_para.dst_rect.y = 0;
    		op_para.dst_rect.w = config.dec_w;
    		op_para.dst_rect.h = config.dec_h;	  
    		scale_w = config.dec_w;  
    		scale_h = config.dec_h ;  
            break;
        case  KEEPASPECTRATIO:
        case  KEEPASPECTRATIOWITHCROP:
    	    if((config.dec_w > scale_w )||(config.dec_h > scale_h)){
            	op_para.src1_rect.x = (config.dec_w - scale_w)>>1;
            	op_para.src1_rect.y = (config.dec_h - scale_h)>>1;
            	op_para.src1_rect.w = scale_w;
            	op_para.src1_rect.h = scale_h;
            	op_para.dst_rect.x = 0;
            	op_para.dst_rect.y = 0;
            	op_para.dst_rect.w = scale_w;
            	op_para.dst_rect.h = scale_h;		    
    	    }else{	
            	op_para.src1_rect.x = config.dec_x;
            	op_para.src1_rect.y = config.dec_y;
            	op_para.src1_rect.w = config.dec_w;
            	op_para.src1_rect.h = config.dec_h;
            	op_para.dst_rect.x = (scale_w - config.dec_w )>>1;
            	op_para.dst_rect.y = (scale_h - config.dec_h )>>1;
            	op_para.dst_rect.w = config.dec_w;
            	op_para.dst_rect.h = config.dec_h;
            }         
            break;
        case  SCRETCHFULLSCREEN:
    		LOGD("====================full screen mode. \n");
    		op_para.src1_rect.x = 0;
    		op_para.src1_rect.y = 0;
    		op_para.src1_rect.w = config.dec_w;
    		op_para.src1_rect.h = config.dec_h;
    		op_para.dst_rect.x = 0;
    		op_para.dst_rect.y = 0;
    		op_para.dst_rect.w = scale_w;
    		op_para.dst_rect.h = scale_h;	        
            break;
        default:
    		op_para.src1_rect.x = 0;
    		op_para.src1_rect.y = 0;
    		op_para.src1_rect.w = config.dec_w;
    		op_para.src1_rect.h = config.dec_h;
    		op_para.dst_rect.x = 0;
    		op_para.dst_rect.y = 0;
    		op_para.dst_rect.w = config.dec_w;
    		op_para.dst_rect.h = config.dec_h;	  
    		scale_w = config.dec_w;  
    		scale_h = config.dec_h  ; 
            break;        
    }

   LOGD("alloc_alloc:srcx :%d  : srcy :%d srcw :%d srch :%d\n" ,op_para.src1_rect.x,op_para.src1_rect.y,op_para.src1_rect.w,op_para.src1_rect.h);	
   LOGD("alloc_alloc:dstx :%d  : dsty :%d dstw :%d dsth :%d\n" ,op_para.dst_rect.x,op_para.dst_rect.y,op_para.dst_rect.w,op_para.dst_rect.h);	
    ioctl(fd_ge2d, FBIOPUT_GE2D_STRETCHBLIT_NOALPHA, &op_para); 
//    bpl = nextMulOf8(bytesPerPixel(format) *scale_w);

    LOGD("start generate output image\n");
    
    char* hwjpeg_mmap_p= mmap(0,jpeg_dec_mem.canv_len, 
			PROT_READ , MAP_PRIVATE, fd_jpegdec, 0);
    if ((int)hwjpeg_mmap_p == -1) {
        LOGD("Error: failed to map framebuffer device to memory.\n");
        goto exit;
    }
    input_image_info->data= hwjpeg_mmap_p+(planes[3]-planes[0]); 
    
    input_image_info->width = scale_w;
    input_image_info->height = scale_h;
    input_image_info->depth = 32;
    input_image_info->bytes_per_line = input_image_width << 2 ;
    input_image_info->nbytes = input_image_info->bytes_per_line * height;
    
    output_image_info->width = scale_w;
    output_image_info->height = scale_h;
    output_image_info->depth = 32;
    output_image_info->bytes_per_line = ((output_image_info->width * output_image_info->depth + 31) >> 5 ) << 2 ;
    output_image_info->nbytes = output_image_info->bytes_per_line * height;
    output_image_info->data  = malloc(output_image_info->nbytes);
    
    if(!output_image_info->data){
		LOGD("err alloc output_image_info->data\n");
		if(munmap(hwjpeg_mmap_p,jpeg_dec_mem.canv_len)<0) {
			LOGD("#####%d######\n",errno);
		}
        goto exit;    
    }else{
        for(i = 0 ; i < output_image_info->height ; i++){
            input_data =   scan_line(input_image_info , i);
            output_data =  scan_line(output_image_info , i);       
            memcpy(output_data, input_data , 4* output_image_info->width );
        }                
    }
    if(munmap(hwjpeg_mmap_p,jpeg_dec_mem.canv_len)<0) {
		LOGD("#####%d######\n",errno);
	}
exit:	
	if(fd_jpegdec>=0)
	{
		close(fd_jpegdec);
	}
	if(fd_ge2d>=0){
	    close(fd_ge2d);		
	    fd_ge2d = -1;
    }
    if(fd >=0){
        close(fd);    
    }
    
    if(!output_image_info->data){
        if(output_image_info){
            free(output_image_info);   
            output_image_info = NULL; 
        } 
    }
    if(input_image_info){
        free(input_image_info);   
        input_image_info = NULL;         
    }
	return output_image_info;
}

int read_mem_jpeg_data(char* img_buf,unsigned int img_len,jpeg_data_t  *jpeg_data,int op_max,
												jpegdec_config_t *config,int thumbprefor,int fd_jpegdec)
{
    struct am_io_param vb_info;
	int  read_num;
	unsigned int decState;
	int fd_amport=jpeg_data->fd_amport;
	char* cur_img_ptr;
	cur_img_ptr=img_buf;
	
	if(jpeg_data->buffer==NULL||fd_amport<0) return 0;
  	
	int  op_step=DEC_STAT_INFOCONFIG ;
	int  result=0;;
	int  read_unit=HEADER_SIZE;
	int total_size =0 ;
	int file_read_end = 0;			
	int wait_info_count  =0 ;
	int wait_timeout = 0 ; 
    int time_start = time((time_t*)NULL) ;
    int time_cur;
    int time_poll;
    
	LOGD("decoder start\n");
	while(op_step < op_max)
	{
		decState=get_decoder_state( fd_jpegdec);
		result = decState;
		if (decState & JPEGDEC_STAT_ERROR) {	    
			LOGD("jpegdec error\n");
			break;
		}

		if (decState & JPEGDEC_STAT_UNSUPPORT) {	    
			LOGD("jpegdec unsupported format\n");	
			break;
		}

		if (decState & JPEGDEC_STAT_DONE) {	    
			LOGD("jpegdec done\n");	
			break;
		}
		ioctl(fd_amport, AMSTREAM_IOC_VB_STATUS,&vb_info);		
		if((!file_read_end)&&(vb_info.status.data_len < ((4*vb_info.status.size)/5))&&(decState & JPEGDEC_STAT_WAIT_DATA)){
			if(total_size == 0 ){
				memset(jpeg_data->buffer,0,HEADER_SIZE);
				write( fd_amport, jpeg_data->buffer, HEADER_SIZE);
			} 
			if (total_size>img_len) {		    
				LOGD("can't read data from jpeg device\n");  	
				result= 0;
				break;
			} 
			if((img_len-total_size)>read_unit) 
			{ 
				read_num= read_unit;
			}
			else 
			{
				read_num= img_len-total_size;
			}
			total_size += read_num;
			read_unit=MAX_BUFFER_SIZE;//expand buffer size to read real data.
			if(read_num<=0) //file end then fill padding data into buffer.
			{
				file_read_end = 1;
				write( fd_amport, jpeg_data->buffer, HEADER_SIZE);
			} 
			else 
				write( fd_amport, cur_img_ptr, read_num); 
			cur_img_ptr+= read_num;
		}	
		switch(op_step)
		{
			case DEC_STAT_INFOCONFIG:
			ioctl( fd_jpegdec, JPEGDEC_IOC_INFOCONFIG, decoder_opt);
			op_step=DEC_STAT_INFO;
			break;
			case DEC_STAT_INFO:
			if (decState & JPEGDEC_STAT_INFO_READY) {
				ioctl( fd_jpegdec, JPEGDEC_IOC_INFO, &jpeg_data->info);			
				LOGD("++jpeg informations:w:%d,h:%d\r\n",jpeg_data->info.width,jpeg_data->info.height);			
				op_step=DEC_STAT_DECCONFIG;
				
			}else{
			    wait_info_count++;
			    if(wait_info_count > 100){
			        wait_info_count = 0 ;		        	
                    time_cur = time((time_t*)NULL) ;
                    wait_timeout = time_cur - time_start;			        	        	
			        //LOGD("current timeout is %d!!!\n",wait_timeout);	        		        		        	        		        
			    }
			    #if 0
			    if(wait_timeout > 1){
			        op_step = op_max;  			        
			        LOGD("timeout for get jpeg info!!!\n");		        
			        result =0;
			        break;  			        
			    }
				LOGD("in jpeg decoding process\n");
				#endif
				result =0;
			}
			break;
			case DEC_STAT_DECCONFIG:
			if(config)
			{
				if (rebuild_jpg_config_para(jpeg_data,config)<0) {
					LOGD("get mem info failed\n");
					op_step = op_max;
					result =0;
					continue;
				}
				jpeg_dec_mem.angle=config->angle;
				jpeg_dec_mem.dec_w=config->dec_w;
				jpeg_dec_mem.dec_h=config->dec_h;
				if(ioctl(fd_jpegdec, JPEGDEC_G_MEM_INFO, &jpeg_dec_mem)<0) {
					LOGD("get mem info failed\n");
					op_step = op_max;
					result =0;
					continue;
				}
				if (set_jpeg_dec_mem(jpeg_data,config)<0||ioctl(fd_jpegdec, JPEGDEC_IOC_DECCONFIG, config)<0) {			    
					LOGD("decoder config failed\n");			
					op_step = op_max;
					result =0;
					continue;
				}
				
			}
			op_step =DEC_STAT_RUN;
			break;
			default:
			break;	
		}
		time_poll = time((time_t*)NULL) ;
		if((time_poll - time_start) > 8){
			op_step = op_max;  			        
			LOGD("it's a corrupted jpeg\n");		        
			result =0;  			
		}
	}
	LOGD("decoder exit\n");
	LOGD("total read bytes is %d\n",total_size);
	
	return result;
		
}

aml_image_info_t* read_mem_jpeg(char* img_buf,unsigned int img_len,int width, int height,
													int mode , int flag,int thumbpref,int colormode)
{  
    char* outimage = NULL; 
    aml_image_info_t* input_image_info;
    static aml_image_info_t* output_image_info;  
    char* hwjpeg_mmap_p; 
    int input_image_width;    
    char* input_data;
    char* output_data;
    int fd_jpegdec;
    int colornum=4;
    int i;

	sMode=mode;
	if(mode>SCRETCHFULLSCREEN) sMode=KEEPASPECTRATIO;
    if((width <= 0)||(height <=0)){
    	 width = 1280;
    	 height = 720;		
	}
	
	input_image_info = (aml_image_info_t*)malloc(sizeof(aml_image_info_t));
    output_image_info = (aml_image_info_t*)malloc(sizeof(aml_image_info_t));
    if(!output_image_info||!input_image_info) {
		LOGD("mem alloc error!\n");
		goto exit;
	}
    memset((char*)output_image_info , 0 , sizeof(aml_image_info_t));	
     int wait_timeout = 0 ;
	//we need some step to decompress jpeg image to output 
	// 1  request yuv space from cmem to store decompressed data
	// 2  config and decompress jpg picture.
	// 3	 request  new rgb space from cmem to store output rgb data.
	// 4  ge2d move yuv data to rgb space.
	// 5  release request mem to cmem module.
	jpegdec_config_t  config={0};
	jpeg_data_t  jpeg_data;
	int fd_ge2d=-1;
	config_para_t ge2d_config;
	int format=colormode;
    ge2d_op_para_t op_para;
	int bpl 	 ;

	memset((char*)&ge2d_config,0,sizeof(config_para_t));	
	scale_x = 0;
	scale_y = 0;
	scale_w = width;
	scale_h = height;	
/*default value for no scaler input*/	
	if(thumbpref!=0)  {
		decoder_opt=JPEGDEC_OPT_THUMBNAIL_PREFERED;
	} else {
		decoder_opt=0;
	}
	if((scale_w == 0)||(scale_h ==0)){
	    scale_w  = 160 ;
	    scale_h  = 100;     
	}
	config.opt=(unsigned)sMode ;
	jpeg_data.fd_amport=fd_amport;
	
	fd_jpegdec= open(FILE_NAME_JPEGDEC, O_RDWR); 
    if(fd_jpegdec <0 ){
        perror("open amjpec device error\r\n")	; 	
        return 0;
    }

	if(!(JPEGDEC_STAT_DONE&read_mem_jpeg_data(img_buf,img_len,&jpeg_data,DEC_STAT_MAX,&config,thumbpref,fd_jpegdec)))
	{    
		LOGD("can't decode jpg pic\n");			
		goto exit;
	} 

	LOGD("deocde jpg pic completed\n");
#if 0
	if(format==COLOR_YUV_420) {
		hwjpeg_mmap_p= mmap(0,jpeg_dec_mem.canv_len, 
				PROT_READ , MAP_PRIVATE, fd_jpegdec, 0);
		if ((int)hwjpeg_mmap_p == -1) {
			LOGD("Error: failed to map framebuffer device to memory.\n");
			goto exit;
		}
		output_image_info->width = config.dec_w;
		output_image_info->height = config.dec_h;
		output_image_info->depth = 12;
		output_image_info->bytes_per_line = 0;
		output_image_info->nbytes = (config.dec_w*config.dec_h*3)>>1;
		output_image_info->data  = malloc(output_image_info->nbytes);
		
		if(!output_image_info->data){
			LOGD("err alloc output_image_info->data\n");   
		}else{
			memcpy(output_image_info->data,hwjpeg_mmap_p,output_image_info->nbytes);                
		}
		if(munmap(hwjpeg_mmap_p,jpeg_dec_mem.canv_len)<0) {
			LOGD("#####%d######\n",errno);
		}
		goto exit;
	}
#endif
	
	/* output for RGB colorspace mode. */
    input_image_width = CANVAS_ALIGNED(scale_w);
    fd_ge2d= open(FILE_NAME_GE2D, O_RDWR); 
	if(fd_ge2d<0)
	{    
		LOGD("can't open framebuffer device\n" );  			
		goto exit;
	}
/*antiflicking setting*/	
    if(flag){
	    ioctl(fd_ge2d,FBIOPUT_GE2D_ANTIFLICKER_ENABLE,1);    
	}else{
	    ioctl(fd_ge2d,FBIOPUT_GE2D_ANTIFLICKER_ENABLE,0);   
	}
	if(format==0) {
	    LOGD("default data!\n" );
	    if(jpeg_data.info.comp_num==3 ||jpeg_data.info.comp_num==4)
	    {
		    format = COLOR_S32_ARGB;
	    } else {
		    LOGD("unsupported color format\n" );  		
		    goto exit;
	    }
	} 
	clear_plane(3,&config,format);
	LOGD("start ge2d image format convert!!!!!\n");
	ge2d_config.src_dst_type = ALLOC_ALLOC;
//    ge2d_config.src_dst_type = ALLOC_OSD1;        //only for test
	ge2d_config.alu_const_color=0xff0000ff;
	ge2d_config.src_format = GE2D_FORMAT_M24_YUV420;
	ge2d_config.dst_format = ImgFormat2Ge2dFormat(format);
	if(0xffffffff==ge2d_config.dst_format)
	{	    
		LOGD("can't get proper ge2d format\n" );  			
		goto exit;
	}

	ge2d_config.src_planes[0].addr = config.addr_y;
	ge2d_config.src_planes[0].w =    config.canvas_width;
	ge2d_config.src_planes[0].h =    config.dec_h;
	ge2d_config.src_planes[1].addr = config.addr_u;
	ge2d_config.src_planes[1].w =    config.canvas_width/2;
	ge2d_config.src_planes[1].h =    config.dec_h / 2;

	ge2d_config.src_planes[2].addr = config.addr_v;
	ge2d_config.src_planes[2].w = config.canvas_width/2;
	ge2d_config.src_planes[2].h = config.dec_h / 2;
	ge2d_config.dst_planes[0].addr=planes[3];	
//	ge2d_config.dst_planes[0].w=  scale_w;
    ge2d_config.dst_planes[0].w= input_image_width ;
	ge2d_config.dst_planes[0].h = scale_h;
//#ifdef JPEG_DBG 	
//	LOGD("planes[3]addr : 0x%x-0x%x" ,planes[3],ge2d_config.dst_planes[0].addr);		
//#endif	
	ioctl(fd_ge2d, FBIOPUT_GE2D_CONFIG, &ge2d_config);
/*crop case*/
	
    switch(mode){
        case  KEEP_SIZE:
    		op_para.src1_rect.x = 0;
    		op_para.src1_rect.y = 0;
    		op_para.src1_rect.w = config.dec_w;
    		op_para.src1_rect.h = config.dec_h;
    		op_para.dst_rect.x = 0;
    		op_para.dst_rect.y = 0;
    		op_para.dst_rect.w = config.dec_w;
    		op_para.dst_rect.h = config.dec_h;	  
    		scale_w = config.dec_w;  
    		scale_h = config.dec_h ;  
            break;
        case  KEEPASPECTRATIO:
        case  KEEPASPECTRATIOWITHCROP:
    	    if((config.dec_w > scale_w )||(config.dec_h > scale_h)){
            	op_para.src1_rect.x = (config.dec_w - scale_w)>>1;
            	op_para.src1_rect.y = (config.dec_h - scale_h)>>1;
            	op_para.src1_rect.w = scale_w;
            	op_para.src1_rect.h = scale_h;
            	op_para.dst_rect.x = 0;
            	op_para.dst_rect.y = 0;
            	op_para.dst_rect.w = scale_w;
            	op_para.dst_rect.h = scale_h;		    
    	    }else{	
            	op_para.src1_rect.x = config.dec_x;
            	op_para.src1_rect.y = config.dec_y;
            	op_para.src1_rect.w = config.dec_w;
            	op_para.src1_rect.h = config.dec_h;
            	op_para.dst_rect.x = (scale_w - config.dec_w )>>1;
            	op_para.dst_rect.y = (scale_h - config.dec_h )>>1;
            	op_para.dst_rect.w = config.dec_w;
            	op_para.dst_rect.h = config.dec_h;
            }         
            break;
        case  SCRETCHFULLSCREEN:
    		LOGD("====================full screen mode. \n");
    		op_para.src1_rect.x = 0;
    		op_para.src1_rect.y = 0;
    		op_para.src1_rect.w = config.dec_w;
    		op_para.src1_rect.h = config.dec_h;
    		op_para.dst_rect.x = 0;
    		op_para.dst_rect.y = 0;
    		op_para.dst_rect.w = scale_w;
    		op_para.dst_rect.h = scale_h;	        
            break;
        default:
    		op_para.src1_rect.x = 0;
    		op_para.src1_rect.y = 0;
    		op_para.src1_rect.w = config.dec_w;
    		op_para.src1_rect.h = config.dec_h;
    		op_para.dst_rect.x = 0;
    		op_para.dst_rect.y = 0;
    		op_para.dst_rect.w = config.dec_w;
    		op_para.dst_rect.h = config.dec_h;	  
    		scale_w = config.dec_w;  
    		scale_h = config.dec_h  ; 
            break;        
    }

   LOGD("alloc_alloc:srcx :%d  : srcy :%d srcw :%d srch :%d\n" ,op_para.src1_rect.x,op_para.src1_rect.y,op_para.src1_rect.w,op_para.src1_rect.h);	
   LOGD("alloc_alloc:dstx :%d  : dsty :%d dstw :%d dsth :%d\n" ,op_para.dst_rect.x,op_para.dst_rect.y,op_para.dst_rect.w,op_para.dst_rect.h);	
    ioctl(fd_ge2d, FBIOPUT_GE2D_STRETCHBLIT_NOALPHA, &op_para); 
//    bpl = nextMulOf8(bytesPerPixel(format) *scale_w);

    LOGD("start generate output image\n");
    
    hwjpeg_mmap_p= mmap(0,jpeg_dec_mem.canv_len, 
			PROT_READ , MAP_PRIVATE, fd_jpegdec, 0);
    if ((int)hwjpeg_mmap_p == -1) {
        LOGD("Error: failed to map framebuffer device to memory.\n");
        goto exit;
    }
    
	if(format==COLOR_S24_RGB) {
		input_image_info->depth = 24;
		output_image_info->depth = 24;
		colornum=3;
	}  
	else if(format==COLOR_S16_RGB) {
		input_image_info->depth = 16;
		output_image_info->depth = 16;
		colornum=2;
	}
	else  {
		input_image_info->depth = 32;
		output_image_info->depth = 32;
	}
    input_image_info->data= hwjpeg_mmap_p+(planes[3]-planes[0]); 
    input_image_info->width = scale_w;
    input_image_info->height = scale_h;
    input_image_info->bytes_per_line = input_image_width *colornum;
    input_image_info->nbytes = input_image_info->bytes_per_line * height;
    output_image_info->width = scale_w;
    output_image_info->height = scale_h;
    output_image_info->bytes_per_line = ((output_image_info->width * output_image_info->depth + 31) >> 5 ) << 2 ;
    output_image_info->nbytes = output_image_info->bytes_per_line * height;
    output_image_info->data  = malloc(output_image_info->nbytes);
    
    if(!output_image_info->data){
		LOGD("err alloc output_image_info->data\n");
		if(munmap(hwjpeg_mmap_p,jpeg_dec_mem.canv_len)<0) {
			LOGD("#####%d######\n",errno);
		}
        goto exit;    
    }else{
        for(i = 0 ; i < output_image_info->height ; i++){
            input_data =   scan_line(input_image_info , i);
            output_data =  scan_line(output_image_info , i);       
            memcpy(output_data, input_data , colornum* output_image_info->width );
        }                
    }
    if(munmap(hwjpeg_mmap_p,jpeg_dec_mem.canv_len)<0) {
		LOGD("#####%d######\n",errno);
	}
exit:	
	if(fd_jpegdec>=0)
	{
		close(fd_jpegdec);
	}
	if(fd_ge2d>=0){
	    close(fd_ge2d);		
	    fd_ge2d = -1;
    }
    
    if(!output_image_info->data){
        if(output_image_info){
            free(output_image_info);   
            output_image_info = NULL; 
        } 
    }
    if(input_image_info){
        free(input_image_info);   
        input_image_info = NULL;         
    }
	return output_image_info;
}

