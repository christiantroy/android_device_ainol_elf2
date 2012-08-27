/*
 * jpeg decoder with amlogic decoding modules
 * 2009 amlogic sh.
 */

#include <config.h>
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
#include <sys/time.h>
#include <time.h>
#include <pixutil.h>

#define JPEG_DBG 
//#undef JPEG_DBG
#define   FILE_NAME_AMPORT	"/dev/amstream_vbuf"
#define   FILE_NAME_JPEGDEC	"/dev/amjpegdec"
#define   FILE_NAME_GE2D	"/dev/ge2d"
#define   MAX_BUFFER_SIZE	(32*1024)
#define   HEADER_SIZE		(2048)	
#define CANVAS_ALIGNED(x)	(((x) + 7) & ~7)

enum {
	DEC_STAT_INFOCONFIG=0,
	DEC_STAT_INFO,
	DEC_STAT_DECCONFIG,
	DEC_STAT_RUN,
	DEC_STAT_MAX
};
enum{
	KEEP_SIZE = 0,
	KEEPASPECTRATIO,   /* keep aspect ratio */
	KEEPASPECTRATIOWITHCROP,  /* keep aspect ratio with crop. */
	SCRETCHFULLSCREEN,  /* scretch full screen. */
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
int fd_jpegdec = -1;
int scale_x;
int scale_y;
int scale_w;
int scale_h;	
unsigned char *planes[4];
char* hwjpeg_mmap_p;
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
	while(access(FILE_NAME_JPEGDEC, R_OK|W_OK)) { //waitting for device created.
		usleep(10000);
		i++;
		if(i>20)  {
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
	case COLOR_S32_ARGB: /* 32 bpp */
		format = GE2D_FORMAT_S32_ARGB;
		break;
	case COLOR_S16_RGB: /* 16 bpp */
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
	if(fd_ge2d<0) {
		LOGD("can't open framebuffer device" );
		goto exit;
	}
	LOGD("start clean plane buffer %d!!!!!\n", index);
	ge2d_config.src_dst_type = ALLOC_ALLOC;

	switch(index) {
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
		ge2d_config.dst_planes[0].addr=(unsigned int)planes[3];
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
		image_width = jpeg_data->info.height;
		image_height = jpeg_data->info.width;
	} else {
		image_width = jpeg_data->info.width;
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
		config->dec_w = image_width;
		config->dec_h = image_height;
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
	} else {
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
	} else {
		w_limit = 1920;
		h_limit = 1920;      
	}
	if((target_width > w_limit)||(target_height > h_limit)){       
		LOGD("crop area exceed %d*%d!!!!!\n", target_width, target_height);       
		return -1;       
	}
	if((image_width <= frame_width)&&(image_height <= frame_height)){        
		config->dec_w = image_width;
		config->dec_h = image_height;
	} else if ((image_width < frame_width)||(image_height < frame_height)){    
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
	case KEEPASPECTRATIOWITHCROP:
		ret = compute_keep_ratio_by_expanding(jpeg_data,config);
		if(ret < 0)
			ret = compute_keep_ratio(jpeg_data,config);    
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
		return ioctl(handle, JPEGDEC_IOC_STAT);
	return JPEGDEC_STAT_ERROR;
}

int read_jpeg_data(FILE* fd,jpeg_data_t  *jpeg_data,int op_max,jpegdec_config_t *config,int thumbprefor)
{
	struct am_io_param vb_info;
	int  read_num;
	unsigned int decState;
	int fd_amport=jpeg_data->fd_amport;
	
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
	while(op_step < op_max) {
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
			if(total_size == 0 ) {
				memset(jpeg_data->buffer,0,HEADER_SIZE);
				write( fd_amport, jpeg_data->buffer, HEADER_SIZE); 
			}
			read_num=fread(jpeg_data->buffer,1,read_unit,fd);
			total_size += read_num;
			if(read_num<0)
			{
				LOGD("can't read data from jpeg device");  
				result= 0;
				break;
			}
			read_unit=MAX_BUFFER_SIZE;//expand buffer size to read real data.
			if(read_num==0) {//file end then fill padding data into buffer.
				file_read_end = 1;
				read_num=read_unit=HEADER_SIZE;
				memset(jpeg_data->buffer,0,read_unit);
			}
			int ret = write( fd_amport, jpeg_data->buffer, read_num); 
		
		}
		switch(op_step) {
		case DEC_STAT_INFOCONFIG:
			ioctl( fd_jpegdec, JPEGDEC_IOC_INFOCONFIG, decoder_opt);
			op_step=DEC_STAT_INFO;
			break;
		case DEC_STAT_INFO:
			if (decState & JPEGDEC_STAT_INFO_READY) {
				ioctl( fd_jpegdec, JPEGDEC_IOC_INFO, &jpeg_data->info);
				LOGD("++jpeg informations:w:%d,h:%d\r\n",jpeg_data->info.width,jpeg_data->info.height);			
				op_step=DEC_STAT_DECCONFIG;
				
			} else {
				wait_info_count++;
				if(wait_info_count > 100){
					wait_info_count = 0;
					time_cur = time((time_t*)NULL) ;
					wait_timeout = time_cur - time_start;
					//LOGD("current timeout is %d!!!\n",wait_timeout);
				}
				if(wait_timeout > 1) {
					op_step = op_max;
					LOGD("timeout for get jpeg info!!!\n");	
					result =0;
					break;
				}
				//LOGD("in jpeg decoding process\n");
				result =0;
			}
			break;
		case DEC_STAT_DECCONFIG:
			if(config) {
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
		if((time_poll - time_start) > 3){
			op_step = op_max;
			LOGD("it's a corrupted jpeg\n");
			result =0;
		}
	}
	LOGD("decoder exit\n");
	LOGD("total read bytes is %d",total_size);
	
	return result;
}


/* 
 * param:
 * para: parameter for decoding.
 * fd: image file handle.
 * c_flag: clear dist buffer before decoding or not 1 is clear.
 * dist_area: 0 normal,1, left parts, 2 right, 3 top, 4 bottom.
 * 
 */
 static jpeg_data_t  jpeg_data;
aml_image_info_t* decode_jpeg(aml_dec_para_t* para,FILE* fd,int c_flag,
					int dist_area)
{  
	char* outimage = NULL; 
	aml_image_info_t* input_image_info=NULL;
	aml_image_info_t* output_image_info=NULL;   
	int input_image_width;

	sMode=para->mode;
	if(sMode>SCRETCHFULLSCREEN) 
		sMode=KEEPASPECTRATIO;
	if((para->width <= 0) || (para->height <=0)) {
		para->width = 1280;
		para->height = 720;
	}
	
	int wait_timeout = 0 ;
	//we need some step to decompress jpeg image to output 
	// 1  request yuv space from cmem to store decompressed data
	// 2  config and decompress jpg picture.
	// 3	 request  new rgb space from cmem to store output rgb data.
	// 4  ge2d move yuv data to rgb space.
	// 5  release request mem to cmem module.
	jpegdec_config_t  config;
	int fd_ge2d=-1;
	config_para_t ge2d_config;
	int format=para->colormode;
	ge2d_op_para_t op_para;
	int bpl 	 ;

	memset((char*)&config,0,sizeof(jpegdec_config_t));
	memset((char*)&ge2d_config,0,sizeof(config_para_t));	
	scale_x = 0;
	scale_y = 0;
	scale_w = para->width;
	scale_h = para->height;	
	
	/*default value for no scaler input*/	
	if(para->thumbpref!=0)  {
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

	if(!(JPEGDEC_STAT_DONE & 
			read_jpeg_data(fd, &jpeg_data, DEC_STAT_MAX, &config, para->thumbpref))) {    
		LOGD("can't decode jpg pic\n");
		goto exit;
	} 

	input_image_info = (aml_image_info_t*)malloc(sizeof(aml_image_info_t));
	
	if(!input_image_info) {
		LOGD("amljpeg: info mem get error\n");
		goto exit;
	}

	LOGD("deocde jpg pic completed\n");
	input_image_width = CANVAS_ALIGNED(scale_w);

	//open fb device to handle ge2d op FILE_NAME_GE2D
	fd_ge2d= open(FILE_NAME_GE2D, O_RDWR); 
	if(fd_ge2d<0) {
		LOGD("can't open framebuffer device\n" );  			
		goto exit;
	}

	/*antiflicking setting*/	
	if(para->flag)
		ioctl(fd_ge2d,FBIOPUT_GE2D_ANTIFLICKER_ENABLE,1);    
	else
		ioctl(fd_ge2d,FBIOPUT_GE2D_ANTIFLICKER_ENABLE,0);

	if(format == 0) {
		LOGD("default data!\n" );
		if(jpeg_data.info.comp_num==3 ||jpeg_data.info.comp_num==4)
			format = COLOR_S32_ARGB;
		else {
			LOGD("unsupported color format\n" );
			goto exit;
		}
	} 

	if(c_flag) 
		clear_plane(3,&config,format);

	LOGD("start ge2d image format convert!!!!!\n");
	ge2d_config.src_dst_type = ALLOC_ALLOC;
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
	ge2d_config.src_planes[2].h = config.dec_h/ 2;
	ge2d_config.dst_planes[0].addr=planes[3];	
	ge2d_config.dst_planes[0].w= input_image_width ;
	ge2d_config.dst_planes[0].h = scale_h;
	
	ioctl(fd_ge2d, FBIOPUT_GE2D_CONFIG, &ge2d_config);
	
	switch(sMode){
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
		LOGD("full screen mode. \n");
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

	para->dest_x=op_para.dst_rect.x;
	para->dest_y=op_para.dst_rect.y;
	para->dest_w=op_para.dst_rect.w;
	para->dest_h=op_para.dst_rect.h;

	/* for normal jpeg to 3D mode.*/
	if(para->image_3d_info.type==2 && 
			para->image_3d_info.style==IMAGE_3D_STYLE_LR && 
			(para->image_3d_mode_pref==0)) {
		op_para.src1_rect.w/=2;
	}

	/* for 3d mode for [3D][HALF] image. */
	if(para->image_3d_info.type==2&&para->image_3d_info.style==IMAGE_3D_STYLE_H_HEIGHT
							&&(para->image_3d_mode_pref)) {
		op_para.dst_rect.h*=2;
		if(scale_h<op_para.dst_rect.h)op_para.dst_rect.h=scale_h;
		para->dest_h= op_para.dst_rect.h;
	} 
	
	if(dist_area==1)
		op_para.dst_rect.w=op_para.dst_rect.w/2;
	else if(dist_area==2) {
		op_para.dst_rect.w=op_para.dst_rect.w/2;
		op_para.dst_rect.x= op_para.dst_rect.w+op_para.dst_rect.x;
	} else if(dist_area==3) {
		op_para.dst_rect.h=op_para.dst_rect.h/2;
	} else if(dist_area==4) {
		op_para.dst_rect.h=op_para.dst_rect.h/2;
		op_para.dst_rect.y= op_para.dst_rect.h+op_para.dst_rect.y;
	}

	LOGD("alloc_alloc:srcx :%d  : srcy :%d srcw :%d srch :%d\n" ,op_para.src1_rect.x,op_para.src1_rect.y,op_para.src1_rect.w,op_para.src1_rect.h);	
	LOGD("alloc_alloc:dstx :%d  : dsty :%d dstw :%d dsth :%d\n" ,op_para.dst_rect.x,op_para.dst_rect.y,op_para.dst_rect.w,op_para.dst_rect.h);	
	ioctl(fd_ge2d, FBIOPUT_GE2D_STRETCHBLIT_NOALPHA, &op_para); 
	//bpl = nextMulOf8(bytesPerPixel(format) *scale_w);

	LOGD("start generate output image\n");

	hwjpeg_mmap_p= mmap(0,jpeg_dec_mem.canv_len, 
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
	input_image_info->nbytes = input_image_info->bytes_per_line * para->height;
	input_image_info->dest_x=para->dest_x;
	input_image_info->dest_y=para->dest_y;
	input_image_info->dest_w=para->dest_w;
	input_image_info->dest_h=para->dest_h;
    
exit:
	if(fd_ge2d>=0){
		close(fd_ge2d);
		fd_ge2d = -1;
	}
	return input_image_info;
}

/*
 * decode baseline jpeg cross bitbld lr2tb or tb2lr(todo..). 
 * param:
 * para: parameter for decoding.
 * fd: image file handle.
 * c_flag: clear dist buffer before decoding or not 1 is clear.
 * whichparts: 0 lr2tb, 1 tb2lr(todo...).
 * 
 */
aml_image_info_t* decode_jpeg_cross(aml_dec_para_t* para,FILE* fd,int whichparts)
{  
	char* outimage = NULL; 
	aml_image_info_t* input_image_info=NULL;
	aml_image_info_t* output_image_info=NULL;   
	int input_image_width;
	int wait_timeout = 0 ;
	//we need some step to decompress jpeg image to output 
	// 1  request yuv space from cmem to store decompressed data
	// 2  config and decompress jpg picture.
	// 3	 request  new rgb space from cmem to store output rgb data.
	// 4  ge2d move yuv data to rgb space.
	// 5  release request mem to cmem module.
	jpegdec_config_t  config;
	int fd_ge2d=-1;
	config_para_t ge2d_config;
	int format = para->colormode;
	ge2d_op_para_t op_para;
	int bpl;

	sMode=para->mode;
	if(sMode>SCRETCHFULLSCREEN) sMode=KEEPASPECTRATIO;
	if((para->width <= 0)||(para->height <=0)){
		para->width = 1280;
		para->height = 720;
	}

	memset((char*)&config,0,sizeof(jpegdec_config_t));
	memset((char*)&ge2d_config,0,sizeof(config_para_t));	
	scale_x = 0;
	scale_y = 0;
	scale_w = para->width;
	scale_h = para->height;
	
	/*default value for no scaler input*/	
	if(para->thumbpref!=0)  {
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

	if(!(JPEGDEC_STAT_DONE & 
		read_jpeg_data(fd, &jpeg_data, DEC_STAT_MAX, &config,para->thumbpref)))
	{    
		LOGD("can't decode jpg pic\n");			
		goto exit;
	} 

	input_image_info = (aml_image_info_t*)malloc(sizeof(aml_image_info_t));

	if(!input_image_info) {
		LOGD("amljpeg: info mem get error\n");
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
	if(para->flag)
		ioctl(fd_ge2d,FBIOPUT_GE2D_ANTIFLICKER_ENABLE,1);    
	else
		ioctl(fd_ge2d,FBIOPUT_GE2D_ANTIFLICKER_ENABLE,0);
	
	if(format==0) {
		LOGD("default data!\n" );
		if(jpeg_data.info.comp_num==3 ||jpeg_data.info.comp_num==4)
			format = COLOR_S32_ARGB;
		else {
			LOGD("unsupported color format\n" );
			goto exit;
		}
	} 
	clear_plane(3,&config,format);
	LOGD("start ge2d image format convert!!!!!\n");
	ge2d_config.src_dst_type = ALLOC_ALLOC;
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
	ge2d_config.dst_planes[0].w= input_image_width ;
	ge2d_config.dst_planes[0].h = scale_h;

	ioctl(fd_ge2d, FBIOPUT_GE2D_CONFIG, &ge2d_config);

	switch(sMode){
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

	para->dest_x=op_para.dst_rect.x;
	para->dest_y=op_para.dst_rect.y;
	para->dest_w=op_para.dst_rect.w;
	para->dest_h=op_para.dst_rect.h;

	if(whichparts==0) {
		op_para.src1_rect.x = 0;
		op_para.src1_rect.y = 0;
		op_para.src1_rect.w = op_para.src1_rect.w/2;
		op_para.dst_rect.x = para->dest_x;
		op_para.dst_rect.y = para->dest_y;
		op_para.dst_rect.h = op_para.dst_rect.h/2;
		LOGD("alloc_alloc:srcx :%d  : srcy :%d srcw :%d srch :%d\n" ,op_para.src1_rect.x,op_para.src1_rect.y,op_para.src1_rect.w,op_para.src1_rect.h);	
		LOGD("alloc_alloc:dstx :%d  : dsty :%d dstw :%d dsth :%d\n" ,op_para.dst_rect.x,op_para.dst_rect.y,op_para.dst_rect.w,op_para.dst_rect.h);	
		ioctl(fd_ge2d, FBIOPUT_GE2D_STRETCHBLIT_NOALPHA, &op_para); 
		op_para.src1_rect.x = op_para.src1_rect.w;
		op_para.dst_rect.y = para->dest_y+op_para.dst_rect.h;
		LOGD("alloc_alloc:srcx :%d  : srcy :%d srcw :%d srch :%d\n" ,op_para.src1_rect.x,op_para.src1_rect.y,op_para.src1_rect.w,op_para.src1_rect.h);	
		LOGD("alloc_alloc:dstx :%d  : dsty :%d dstw :%d dsth :%d\n" ,op_para.dst_rect.x,op_para.dst_rect.y,op_para.dst_rect.w,op_para.dst_rect.h);	
		ioctl(fd_ge2d, FBIOPUT_GE2D_STRETCHBLIT_NOALPHA, &op_para); 
	} 

	LOGD("start generate output image\n");

	hwjpeg_mmap_p= mmap(0,jpeg_dec_mem.canv_len, 
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
	input_image_info->nbytes = input_image_info->bytes_per_line * para->height;

	input_image_info->dest_x=para->dest_x;
	input_image_info->dest_y=para->dest_y;
	input_image_info->dest_w=para->dest_w;
	input_image_info->dest_h=para->dest_h;
    
exit:
	if(fd_ge2d>=0) {
		close(fd_ge2d);
		fd_ge2d = -1;
	}
	return input_image_info;
}

aml_image_info_t* read_jpeg_image(aml_dec_para_t* para)
{
	aml_image_info_t* input_image_info=NULL;
	aml_image_info_t* output_image_info=NULL; 
	
	FILE* fd = fopen(para->fn,"rb") ; 
	if(fd == NULL) {
		LOGD("amljpeg:source file:%s open error\n",para->fn);
		goto exit;   
	}

	fd_jpegdec= open(FILE_NAME_JPEGDEC, O_RDWR); 
	if(fd_jpegdec <0 ){
		LOGD("open amjpec device error\r\n");
		goto exit; 
	}
	
	output_image_info = (aml_image_info_t*)malloc(sizeof(aml_image_info_t));
	if(!output_image_info) {
		LOGD("decoding error,mem error\n");
		goto exit;
	}
	if(para->image_3d_mode_pref<3) /* force 3d mode. */
		input_image_info=decode_jpeg(para,fd,1,0);
	else 
		input_image_info=decode_jpeg_cross(para,fd,0);
	memset((char*)output_image_info , 0 , sizeof(aml_image_info_t));
	if(!input_image_info)  goto exit;
	output_image_info->height = input_image_info->height;
	output_image_info->depth = 32;

	if(para->image_3d_mode_pref==0) /* normal jpeg decode mode. */
	{ 
		int i;
		int j=0; 
		char* input_data;
		char* output_data;
		
		output_image_info->width = input_image_info->width;
		output_image_info->bytes_per_line = ((output_image_info->width * output_image_info->depth + 31) >> 5 ) << 2 ;
		output_image_info->nbytes = output_image_info->bytes_per_line * para->height;
		output_image_info->data  = malloc(output_image_info->nbytes);
		if(!output_image_info->data){
			LOGD("err alloc output_image_info->data\n");
			goto umap_exit;    
		}
		
		for(i = 0 ; i < output_image_info->height ; i++){
			input_data =   scan_line(input_image_info , i);
			output_data =  scan_line(output_image_info , i);       
			memcpy(output_data, input_data , 4* output_image_info->width );
		}
	} 
	else if(para->image_3d_mode_pref==2)
		output_image_info=insert_lr_frame(para,input_image_info,output_image_info,0);
	else if(para->image_3d_mode_pref==4)
		output_image_info=insert_tb_frame(para,input_image_info,output_image_info);
umap_exit:
	if(munmap(hwjpeg_mmap_p,jpeg_dec_mem.canv_len)<0) {
		LOGD("#####%d######\n",errno);
	}
    
exit:
	if(input_image_info){
		free(input_image_info);
		input_image_info = NULL;
	} 
	if(fd_jpegdec>=0) 
		close(fd_jpegdec);
	fd_jpegdec=-1;
	if(fd != NULL) 
		fclose(fd);
	if(output_image_info&&!output_image_info->data) {
		free(output_image_info);   
		output_image_info = NULL; 
	}
	return  output_image_info;
}

aml_image_info_t* read_2_frame_image(aml_dec_para_t* para)
{
	int i,j;
	int start_step=1;
	aml_image_info_t* input_image_info=NULL;
	aml_image_info_t* output_image_info=NULL; 
	char* input_data;
	char* output_data;
	FILE* fd = fopen(para->fn,"rb" ) ; 
	if(!fd){
		LOGD("amljpeg:source file:%s open error\n",para->fn);
		goto exit;
	}
	
	/* decode the first frame. */
	if(fseek(fd,para->image_3d_info.frame[0],SEEK_SET)) {
		LOGD("amljpeg:source file:%s seek error\n",para->fn);
		goto exit;   
	}
	if(fd_jpegdec>=0) close(fd_jpegdec);
	fd_jpegdec= open(FILE_NAME_JPEGDEC, O_RDWR); 
	if(fd_jpegdec <0 ){
		LOGD("open amjpec device error %d \n",errno);
		goto exit; 
	}
	output_image_info = (aml_image_info_t*)malloc(sizeof(aml_image_info_t));
	if(!output_image_info) {
		LOGD("decoding error,mem error\n");
		goto exit;
	}
	para->thumbpref=0;
	if(para->image_3d_mode_pref==0) {
		input_image_info=decode_jpeg(para,fd,1,0);
		memset((char*)output_image_info , 0 , sizeof(aml_image_info_t));
		if(!input_image_info)  {
			LOGD("hwjpeg decod error\n");
			goto exit;
		}
		output_image_info->width = input_image_info->width;
		output_image_info->height = input_image_info->height;
		output_image_info->depth = 32;
		output_image_info->bytes_per_line = ((output_image_info->width * output_image_info->depth + 31) >> 5 ) << 2 ;
		output_image_info->nbytes = output_image_info->bytes_per_line * para->height;
		output_image_info->data  = malloc(output_image_info->nbytes);
		if(!output_image_info->data){
			LOGD("err alloc output_image_info->data\n");
		} else {
			for(i = 0 ; i < output_image_info->height ; i++){
				input_data =   scan_line(input_image_info , i);
				output_data =  scan_line(output_image_info , i);       
				memcpy(output_data, input_data , 4* output_image_info->width );
			}
		}
	} else {
		int decode_parts=0;
		if(para->image_3d_mode_pref<3) 
			decode_parts=1;
		else
			decode_parts=3;
		
		input_image_info=decode_jpeg(para,fd,1,decode_parts);
		if(!input_image_info)  {
			LOGD("hwjpeg decod error\n");
			goto exit;
		}
    
		/* clear content for decode the second frame. */
		if(munmap(hwjpeg_mmap_p,jpeg_dec_mem.canv_len)<0) {
			LOGD("#####%d######\n",errno);
			goto exit;
		}
		free(input_image_info);
		input_image_info=NULL;
		if(fd_jpegdec>=0) close(fd_jpegdec);
		fd_jpegdec=-1;
		amljpeg_exit(); 
		
		/* start to decode the second frame. */
		if(amljpeg_init()<0) goto exit;
		if(fseek(fd,para->image_3d_info.frame[1],SEEK_SET)) {
			LOGD("amljpeg:source file:%s seek error\n",para->fn);
			goto exit;   
		}
		fd_jpegdec= open(FILE_NAME_JPEGDEC, O_RDWR); 
		if(fd_jpegdec <0 ){
			LOGD("open amjpec device error\r\n");
			goto exit;
		}
		if(para->image_3d_mode_pref<3) 
			decode_parts=2;
		else
			decode_parts=4;
		input_image_info=decode_jpeg(para,fd,0,decode_parts);
		if(!input_image_info) { 
			goto exit;
		}
		
		memset((char*)output_image_info , 0 , sizeof(aml_image_info_t));
		output_image_info->height = input_image_info->height;
		output_image_info->depth = 32;

		//output_image_info=insert_lr_frame(para,input_image_info,output_image_info);
		if(para->image_3d_mode_pref<3) /* left right cross 3d. */
			output_image_info=insert_lr_frame(para,input_image_info,output_image_info,1);
		else if(para->image_3d_mode_pref<5)
			output_image_info=insert_tb_frame(para,input_image_info,output_image_info);
	}

umap_exit:
    if(munmap(hwjpeg_mmap_p,jpeg_dec_mem.canv_len)<0) {
		LOGD("#####%d######\n",errno);
	}
exit:
	if(input_image_info)
		free(input_image_info);  
	if(fd_jpegdec>=0) 
		close(fd_jpegdec);
	fd_jpegdec=-1;
	if(fd) 
		fclose(fd);
	if(output_image_info&&!output_image_info->data){
		free(output_image_info);   
		output_image_info = NULL; 
	}
	return  output_image_info;
}

aml_image_info_t* read_lr_in_one_image(aml_dec_para_t* para)
{
	aml_image_info_t* input_image_info=NULL;
	aml_image_info_t* output_image_info=NULL;
	
	FILE* fd = fopen(para->fn,"rb" ) ; 
	if(!fd){
		LOGD("amljpeg:source file:%s open error\n",para->fn);
		goto exit;   
	}
	
	if(fseek(fd,para->image_3d_info.frame[0],SEEK_SET)) {
		LOGD("amljpeg:source file:%s seek error\n",para->fn);
		goto exit;   
	}
	
	fd_jpegdec= open(FILE_NAME_JPEGDEC, O_RDWR); 
	if(fd_jpegdec <0 ){
		perror("open amjpec device error\r\n")	; 	
		goto exit; 
	}
	
	output_image_info = (aml_image_info_t*)malloc(sizeof(aml_image_info_t));
	if(!output_image_info) {
		LOGD("decoding error,mem error\n");
		goto exit;
	}
	para->thumbpref=0;
	if(para->image_3d_mode_pref<3)
		input_image_info=decode_jpeg(para,fd,1,0);
	else
		input_image_info=decode_jpeg_cross(para,fd,0);
	memset((char*)output_image_info , 0 , sizeof(aml_image_info_t));
	if(!input_image_info)  goto umap_exit;
	output_image_info->height = input_image_info->height;
	output_image_info->depth = 32;
    
	if(para->image_3d_mode_pref<1) {
		int i;
		int j=0; 
		char* input_data;
		char* output_data;
		
		output_image_info->width = input_image_info->width;
		output_image_info->bytes_per_line = ((output_image_info->width * output_image_info->depth + 31) >> 5 ) << 2 ;
		output_image_info->nbytes = output_image_info->bytes_per_line * para->height;
		output_image_info->data  = malloc(output_image_info->nbytes);
		if(!output_image_info->data){
			LOGD("err alloc output_image_info->data\n");
			goto umap_exit;    
		}
		
		for(i = 0 ; i < output_image_info->height ; i++){
			input_data =   scan_line(input_image_info , i);
			output_data =  scan_line(output_image_info , i);       
			memcpy(output_data, input_data , 4* output_image_info->width );
		}
	} 
	else if(para->image_3d_mode_pref<3) /* left right cross 3d. */
		output_image_info=insert_lr_frame(para,input_image_info,output_image_info,0);
	else if(para->image_3d_mode_pref<5)
		output_image_info=insert_tb_frame(para,input_image_info,output_image_info);
umap_exit:
	if(munmap(hwjpeg_mmap_p,jpeg_dec_mem.canv_len)<0) {
		LOGD("#####%d######\n",errno);
	}
exit:
	if(input_image_info){
		free(input_image_info);
		input_image_info = NULL;
	} 
	if(fd_jpegdec>=0) 
		close(fd_jpegdec);
	fd_jpegdec=-1;
	if(fd) 
		fclose(fd);
	if(output_image_info&&!output_image_info->data){
		free(output_image_info);   
		output_image_info = NULL; 
	}
	return  output_image_info;
}
