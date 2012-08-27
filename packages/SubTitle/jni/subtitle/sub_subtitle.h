#ifndef SUBTITLE_H
#define SUBTITLE_H


#define VOB_SUB_SIZE 1440*1080/4

#define SUBTITLE_VOB      1
#define SUBTITLE_PGS      2
#define SUBTITLE_MKV_STR  3
#define SUBTITLE_MKV_VOB  4
#define SUBTITLE_SSA  5     //add yjf

typedef struct 
{
 unsigned sync_bytes;
 unsigned buffer_size;
 unsigned pid;	
 unsigned pts;
 unsigned m_delay;
 unsigned char *spu_data;
 unsigned short cmd_offset;
 unsigned short length; 

 unsigned r_pt;
 unsigned frame_rdy;
 	
 unsigned short spu_color; 
 unsigned short spu_alpha; 
 unsigned short spu_start_x;
 unsigned short spu_start_y;
 unsigned short spu_width;
 unsigned short spu_height;
 unsigned short top_pxd_addr;  
 unsigned short bottom_pxd_addr; 

 unsigned disp_colcon_addr;  
 unsigned char display_pending;
 unsigned char displaying;
 unsigned char subtitle_type;
 unsigned char reser[2];   

 unsigned rgba_enable;	
 unsigned rgba_background;
 unsigned rgba_pattern1;
 unsigned rgba_pattern2;
 unsigned rgba_pattern3; 
} AML_SPUVAR;


int get_spu(AML_SPUVAR *spu, int sub_fd);
int release_spu(AML_SPUVAR *spu);
int get_inter_spu();
unsigned char spu_fill_pixel(unsigned short *pixelIn, char *pixelOut, AML_SPUVAR *sub_frame, int n);
int add_pgs_end_time(int end_time);

#endif
