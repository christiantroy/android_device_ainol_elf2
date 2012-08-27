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
#include <amljpeg.h>

#define JPEG_BASELINE 0x1
#define JPEG_PROP_PROG 0x2
#define JPEG_PROP_GRAYSCALE    0x4
#define JPEG_PROP_CYMK  0x8
#define UNKNOWN	0x10
int check_prog(int fd)
{
    unsigned char data[6];
    int r = 0;

    if (read(fd, data, 2) < 0) {
		r|= UNKNOWN;
        return r;
    }
    
    if ((data[0] != 0xff) || (data[1] != 0xd8)) {
		r|=UNKNOWN;
        return r;
    }
    
    /* check progressive jpeg */
    while (1) {
        if (read(fd, data, 2) < 0) {
			r|=UNKNOWN;
			return r;
        }

        if (data[0] != 0xff) {
            goto relocate;
        }

        if ((data[1] == 0xc0) || (data[1] == 0xc2)) {
            if (data[1] == 0xc2) {
                r |= JPEG_PROP_PROG;
            }  
            if(data[1] == 0xc0){
                r |= JPEG_BASELINE;    
            }          
//            if (read(fd, data, 6) < 0) {
//                return r;
//            }
//
//            if (data[5] == 1) {
//                r |= JPEG_PROP_GRAYSCALE;
//            }
//            else if (data[5] == 4) {
//                r |= JPEG_PROP_CYMK;
//            }
            return r;
        }
relocate:
        lseek(fd, -1, SEEK_CUR);
    }
    
    return 0;
}

int fh_amljpeg_id(aml_dec_para_t* para)
{
    int fd;
    int ret =0 ;
//    unsigned char id[10];
//    fd=open(name,O_RDONLY); if(fd==-1) return(0);
//    read(fd,id,10);
//    close(fd);
//    if(id[6]=='J' && id[7]=='F' && id[8]=='I' && id[9]=='F') return(1);
//    if(id[0]==0xff && id[1]==0xd8 && id[2]==0xff) return(1);
//    return(0);
	fd=open(para->fn,O_RDONLY); 
	if(fd==-1) return(0);
	ret = check_prog(fd);
	close(fd);
	if(ret&JPEG_BASELINE){
		return 1;
	}else{
		return 0;
	}
}
			    

int fh_amljpeg_load(aml_dec_para_t* para , aml_image_info_t* image)
{
	int ret=FH_ERROR_OK;
    aml_image_info_t* output_image;
    if(amljpeg_init()<0) 
		ret = FH_ERROR_HWDEC_FAIL;
	if(ret == FH_ERROR_OK) {
		output_image = read_jpeg_image(para);
		if(output_image){
			if(1){
				LOGD("output image width is %d\n", output_image->width);
				LOGD("output image height is %d\n", output_image->height);
				LOGD("output image depth is %d\n", output_image->depth);
				LOGD("output image bytes_per_line is %d\n", output_image->bytes_per_line);
				LOGD("output image nbytes   is %d\n", output_image->nbytes);
			}        
			memcpy((char*)image , (char*)output_image, sizeof(aml_image_info_t)) ;
			free(output_image);   
		} else {
			LOGD("decoding error\n");
			ret = FH_ERROR_HWDEC_FAIL;
		}
	}
    amljpeg_exit();  
    
    return ret;
}

int fh_amljpeg_getsize(char *filename,int *x,int *y)
{
    return(FH_ERROR_OK);
}
