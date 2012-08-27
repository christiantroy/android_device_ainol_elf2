/*
    fbv  --  simple image viewer for the linux framebuffer
    Copyright (C) 2000, 2001, 2003  Mateusz Golicz

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
#include <config.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <jpeglib.h>
#include <setjmp.h>
#include <unistd.h>
#include <string.h>
#include "pic_app.h"
#include "scaler.h"

struct r_jpeg_error_mgr
{
    struct jpeg_error_mgr pub;
    jmp_buf envbuffer;
};


int fh_jpeg_id(aml_dec_para_t* para)
{
    int fd;
    unsigned char id[10];
    fd=open(para->fn,O_RDONLY); if(fd==-1) return(0);
    read(fd,id,10);
    close(fd);
    if(id[6]=='J' && id[7]=='F' && id[8]=='I' && id[9]=='F') return(1);
    if(id[0]==0xff && id[1]==0xd8 && id[2]==0xff) return(1);
    return(0);
}
			    

void jpeg_cb_error_exit(j_common_ptr cinfo)
{
    struct r_jpeg_error_mgr *mptr;
    mptr=(struct r_jpeg_error_mgr*) cinfo->err;
    (*cinfo->err->output_message) (cinfo);
    longjmp(mptr->envbuffer,1);
}

static simp_scaler_t simp_scaler;

void jpeg_scaler_init(aml_dec_para_t* para , unsigned char* buffer)
{
	simp_scaler.input.pic_width = para->iwidth;
    simp_scaler.input.pic_height = para->iheight;
    simp_scaler.input.frame_width = para->width;
    simp_scaler.input.frame_height = para->height;

    simp_scaler.input.output = buffer;
    simp_scaler.input.mode = para->mode;
    simp_scaler.input.bpp = ((simp_scaler.input.frame_width * 32 + 31) >> 5 ) << 2 ;
    simp_scaler.input.rotation = 0;
	simp_scaler_init(&simp_scaler);
	para->dest_x = simp_scaler.input.scaled_left;
	para->dest_y = simp_scaler.input.scaled_top;
	para->dest_w = simp_scaler.input.scaled_width;
	para->dest_h = simp_scaler.input.scaled_height;
}

void jpeg_scaler_exit()
{
	simp_scaler_destroy(&simp_scaler);
}

static void line_outputbgr(char* line_buffer , int line ,int c)
{
	int i;
	unsigned char *data = line_buffer;
/*sequence b---g---r*/
	for (i= 0 ; i< simp_scaler.input.pic_width; i++) {
		simp_scaler_output_pixel(&simp_scaler, data[2], data[1],data[0],0xff, i, line);		
		data+=c;
	}
}

static void line_outputrgb(char* line_buffer , int line ,int c)
{
	int i;
	unsigned char *data = line_buffer;
/*sequence b---g---r*/
	for (i= 0 ; i< simp_scaler.input.pic_width; i++) {
		simp_scaler_output_pixel(&simp_scaler, data[0], data[1],data[2],0xff, i, line);		
		data+=c;
	}
}
static struct jpeg_decompress_struct cinfo;
static struct jpeg_decompress_struct *ciptr = NULL;
static struct r_jpeg_error_mgr emgr;

//int fh_jpeg_load(char *filename,unsigned char *buffer, unsigned char ** alpha, int x,int y)
int fh_jpeg_load(aml_dec_para_t* para , aml_image_info_t* image)
{

    unsigned char *bp;
    int px,py,c;
    FILE *fh;
    JSAMPLE *lb;
    char* filename;
    int i;
    char *p;
    int buf_len;
    char* buffer =NULL;
    int x,y;
    filename = para->fn;
    int ret= FH_ERROR_OTHER;
    
    
    int dx = para->width ;
	int dy = para->height ; 
	LOGD("dx is %d , dy is %d\n",  dx ,dy);   
    memset((char*)&simp_scaler ,0 , sizeof(simp_scaler_t));
    ciptr=&cinfo;
    if(!(fh=fopen(filename,"rb"))) return(FH_ERROR_FILE);
    ciptr->err=jpeg_std_error(&emgr.pub);
    emgr.pub.error_exit=jpeg_cb_error_exit;
    if(setjmp(emgr.envbuffer)==1)
    {
		// FATAL ERROR - Free the object and return...
		ret=FH_ERROR_FORMAT;
		goto exit;
    }
    
    jpeg_create_decompress(ciptr);
    jpeg_stdio_src(ciptr,fh);
    if(jpeg_read_header(ciptr,TRUE)!=JPEG_HEADER_OK) {
		ret=FH_ERROR_BAD_INFO;
		goto exit;
	}         
    
    ciptr->do_fancy_upsampling = 0;

    /* this gives another few percents */
    ciptr->do_block_smoothing = 0;
    
    ciptr->out_color_space=JCS_RGB;
    if(jpeg_start_decompress(ciptr)!=TRUE) goto exit;

    px=ciptr->output_width; py=ciptr->output_height;
    c=ciptr->output_components;
	para->iwidth  = px;
	para->iheight = py ;	
	
	buffer = (unsigned char*) malloc(para->width * para->height * 4);
    buf_len = para->iwidth * para->iheight;
	jpeg_scaler_init(para , buffer);

    if(c==3)
    {
		lb=(*ciptr->mem->alloc_small)((j_common_ptr) ciptr,JPOOL_PERMANENT,c*px);
		if(para->colormode == 2 ) {
			while (ciptr->output_scanline < ciptr->output_height) {
				jpeg_read_scanlines(ciptr, &lb, 1);			
				line_outputbgr((unsigned char*)lb,ciptr->output_scanline ,3);
			}
		} else {
			while (ciptr->output_scanline < ciptr->output_height) {
				jpeg_read_scanlines(ciptr, &lb, 1);			
				line_outputrgb((unsigned char*)lb,ciptr->output_scanline ,3);
			}
		}
    }
	if(jpeg_finish_decompress(ciptr)!=TRUE) goto exit;

	x =  dx ;
	y = dy;			
#if 0
    for(i =  dx*dy ; i >0 ;i--){
        p[4*i - 1] = 0xff; 
        p[4*i - 2] = p[3*i - 3];     //r
        p[4*i - 3] = p[3*i - 2];     //g
        p[4*i - 4] = p[3*i - 1];     //b
    }  	
    for(i = buf_len ; i >0 ;i--){
        p[4*i - 1] = 0xff; 
        p[4*i - 2] = p[3*i - 3];     //r
        p[4*i - 3] = p[3*i - 2];     //g
        p[4*i - 4] = p[3*i - 1];     //b
    }     
#endif	

	image->bytes_per_line = x *4;
    image->data =buffer;
    image->width = x;
    image->height = y;
    image->depth = 32;
//    image->bytes_per_line = x << 2 ;
	image->bytes_per_line =simp_scaler.input.bpp;
    image->nbytes = image->bytes_per_line * y;  

    image->dest_x=para->dest_x;
    image->dest_y=para->dest_y;
    image->dest_w=para->dest_w;
    image->dest_h=para->dest_h;
     
	jpeg_scaler_exit();
    ret= FH_ERROR_OK;        
exit:	
    if(ret!=FH_ERROR_OK){
        if(buffer){
    		free(buffer);
    		buffer = NULL;
	    }
	}
    jpeg_destroy_decompress(ciptr);
    fclose(fh);
    return ret;
}

int fh_jpeg_getsize(char *filename,int *x,int *y)
{
    struct jpeg_decompress_struct cinfo;
    struct jpeg_decompress_struct *ciptr;
    struct r_jpeg_error_mgr emgr;
    int px,py,c;
    FILE *fh;

    ciptr=&cinfo;
    if(!(fh=fopen(filename,"rb"))) return(FH_ERROR_FILE);
    
    ciptr->err=jpeg_std_error(&emgr.pub);
    emgr.pub.error_exit=jpeg_cb_error_exit;
    if(setjmp(emgr.envbuffer)==1)
    {
	// FATAL ERROR - Free the object and return...
	jpeg_destroy_decompress(ciptr);
	fclose(fh);
	return(FH_ERROR_FORMAT);
    }
    
    jpeg_create_decompress(ciptr);
    jpeg_stdio_src(ciptr,fh);
    jpeg_read_header(ciptr,TRUE);
    px=ciptr->output_width; py=ciptr->output_height;
    c=ciptr->output_components;
    *x=px; *y=py;
    jpeg_destroy_decompress(ciptr);
    fclose(fh);
    return(FH_ERROR_OK);
}
