/*
    fbv  --  simple image viewer for the linux framebuffer
    Copyright (C) 2002  Tomasz Sterna

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
#include "pic_app.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#define BMP_TORASTER_OFFSET	10
#define BMP_SIZE_OFFSET		18
#define BMP_BPP_OFFSET		28
#define BMP_RLE_OFFSET		30
#define BMP_COLOR_OFFSET	54

#define fill4B(a)	( ( 4 - ( (a) % 4 ) ) & 0x03)

struct color {
	unsigned char red;
	unsigned char green;
	unsigned char blue;
};

typedef enum {
  BI_RGB = 0,
  BI_RLE8,
  BI_RLE4,
  BI_BITFIELDS,
  BI_JPEG,
  BI_PNG,
} bmp_compression_method_t;

int fh_bmp_id(aml_dec_para_t* para)
{
	int fd;
	char id[2];
	
	fd = open(para->fn, O_RDONLY);
	if (fd == -1) {
		return(0);
	}
	
	read(fd, id, 2);
	close(fd);
	if ( id[0]=='B' && id[1]=='M' ) {
		return(1);
	}
	return(0);
}

void fetch_pallete(int fd, struct color pallete[], int count)
{
	unsigned char buff[4];
	int i;

	lseek(fd, BMP_COLOR_OFFSET, SEEK_SET);
	for (i=0; i<count; i++) {
		read(fd, buff, 4);
		pallete[i].red = buff[2];
		pallete[i].green = buff[1];
		pallete[i].blue = buff[0];
	}
	return;
}

//int fh_bmp_load(char *name,unsigned char *buffer, unsigned char **alpha, int x,int y)
int fh_bmp_load(aml_dec_para_t* para , aml_image_info_t* image)
{
	int fd, bpp, raster, i, j, k, skip;
	int ihsize;	/* the size of header of bmp information. */    
	unsigned short pix;
	unsigned char buff[4];
	
	struct color pallete[256];
    char *p,*q;
    int buf_len;
    char* name;
    char* buffer = NULL;
    char* alpha = NULL;
    int x,y;    
    name = para->fn;
    buffer = (char*)malloc(para->iwidth * para->iheight * 4);
    if(!buffer){
        return FH_ERROR_MEM_FAIL;
    }
    buf_len = para->iwidth * para->iheight;
    x = para->iwidth;
    y = para->iheight;
    unsigned char *wr_buffer = buffer + x*(y-1)*3;
	fd = open(name, O_RDONLY);
	if (fd == -1) {
		return(FH_ERROR_FILE);
	}

	if (lseek(fd, BMP_TORASTER_OFFSET, SEEK_SET) == -1) {
		return(FH_ERROR_FORMAT);
	}
	read(fd, buff, 4);
	raster = buff[0] + (buff[1]<<8) + (buff[2]<<16) + (buff[3]<<24);  \
	
	read(fd, buff, 4);  
	ihsize = buff[0] + (buff[1]<<8) + (buff[2]<<16) + (buff[3]<<24);
	if((ihsize+14)>raster) return FH_ERROR_FILE;
	switch(ihsize){
		case  40: // windib v3
		case  64: // OS/2 v2
		case 108: // windib v4
		case 124: // windib v5
			break;
		case  12: // OS/2 v1   
		default:
			return FH_ERROR_FORMAT;
    }

	if (lseek(fd, BMP_BPP_OFFSET, SEEK_SET) == -1) {
		return(FH_ERROR_FORMAT);
	}
	read(fd, buff, 2);
	bpp = buff[0] + (buff[1]<<8);
	
	read(fd, buff, 4);
	bmp_compression_method_t compress_method= buff[0] + (buff[1]<<8) + (buff[1]<<16)+ (buff[1]<<24);
	
	switch (bpp){
		case 1: /* monochrome */
			skip = fill4B(x/8+(x%8?1:0));
			lseek(fd, raster, SEEK_SET);
			for (i=0; i<y; i++) {
				for (j=0; j<x/8; j++) {
					read(fd, buff, 1);
					for (k=0; k<8; k++) {
						if (buff[0] & 0x80) {
							*wr_buffer++ = 0xff;
							*wr_buffer++ = 0xff;
							*wr_buffer++ = 0xff;
						} else {
							*wr_buffer++ = 0x00;
							*wr_buffer++ = 0x00;
							*wr_buffer++ = 0x00;
						}
						buff[0] = buff[0]<<1;
					}
					
				}
				if (x%8) {
					read(fd, buff, 1);
					for (k=0; k<x%8; k++) {
						if (buff[0] & 0x80) {
							*wr_buffer++ = 0xff;
							*wr_buffer++ = 0xff;
							*wr_buffer++ = 0xff;
						} else {
							*wr_buffer++ = 0x00;
							*wr_buffer++ = 0x00;
							*wr_buffer++ = 0x00;
						}
						buff[0] = buff[0]<<1;
					}
					
				}
				if (skip) {
					read(fd, buff, skip);
				}
				wr_buffer -= x*6; /* backoff 2 lines - x*2 *3 */
			}
			break;
		case 4: /* 4bit palletized */
			skip = fill4B(x/2+x%2);
			fetch_pallete(fd, pallete, 16);
			lseek(fd, raster, SEEK_SET);
			for (i=0; i<y; i++) {
				for (j=0; j<x/2; j++) {
					read(fd, buff, 1);
					buff[1] = buff[0]>>4;
					buff[2] = buff[0] & 0x0f;
					*wr_buffer++ = pallete[buff[1]].red;
					*wr_buffer++ = pallete[buff[1]].green;
					*wr_buffer++ = pallete[buff[1]].blue;
					*wr_buffer++ = pallete[buff[2]].red;
					*wr_buffer++ = pallete[buff[2]].green;
					*wr_buffer++ = pallete[buff[2]].blue;
				}
				if (x%2) {
					read(fd, buff, 1);
					buff[1] = buff[0]>>4;
					*wr_buffer++ = pallete[buff[1]].red;
					*wr_buffer++ = pallete[buff[1]].green;
					*wr_buffer++ = pallete[buff[1]].blue;
				}
				if (skip) {
					read(fd, buff, skip);
				}
				wr_buffer -= x*6; /* backoff 2 lines - x*2 *3 */
			}
			break;
		case 8: /* 8bit palletized */
			skip = fill4B(x);
			fetch_pallete(fd, pallete, 256);
			lseek(fd, raster, SEEK_SET);
			for (i=0; i<y; i++) {
				for (j=0; j<x; j++) {
					read(fd, buff, 1);
					*wr_buffer++ = pallete[buff[0]].red;
					*wr_buffer++ = pallete[buff[0]].green;
					*wr_buffer++ = pallete[buff[0]].blue;
				}
				if (skip) {
					read(fd, buff, skip);
				}
				wr_buffer -= x*6; /* backoff 2 lines - x*2 *3 */
			}
			break;
		case 16: /* 16bit RGB */
			skip = fill4B(x*3);
			lseek(fd, raster, SEEK_SET);
			if(compress_method==BI_RGB) {
				for (i=0; i<y; i++) {
					for (j=0; j<x; j++) {
						read(fd, (char*)&pix, 2);
						*wr_buffer++ = (pix>>7)&0xf8;
						*wr_buffer++ = (pix>>2)&0xf8;
						*wr_buffer++ = (pix&0x1f)<<3;
					}
					if (skip) {
						read(fd, buff, skip);
					}
					wr_buffer -= x*6; /* backoff 2 lines - x*2 *3 */
				}
			} else if(compress_method==BI_BITFIELDS) {
				for (i=0; i<y; i++) {
					for (j=0; j<x; j++) {
						read(fd, (char*)&pix, 2);
						*wr_buffer++ = buff[2];
						*wr_buffer++ = buff[1];
						*wr_buffer++ = buff[0];
					}
					if (skip) {
						read(fd, buff, skip);
					}
					wr_buffer -= x*6; /* backoff 2 lines - x*2 *3 */
				}
			} else 
				return(FH_ERROR_FORMAT);
			break;
		case 24: /* 24bit RGB */
			skip = fill4B(x*3);
			lseek(fd, raster, SEEK_SET);
			for (i=0; i<y; i++) {
				for (j=0; j<x; j++) {
					read(fd, buff, 3);
					*wr_buffer++ = buff[2];
					*wr_buffer++ = buff[1];
					*wr_buffer++ = buff[0];
				}
				if (skip) {
					read(fd, buff, skip);
				}
				wr_buffer -= x*6; /* backoff 2 lines - x*2 *3 */
			}
			break;
		case 32:
			lseek(fd, raster, SEEK_SET);
			for (i=0; i<y; i++) {
				for (j=0; j<x; j++) {
					read(fd, buff, 4);
					*wr_buffer++ = buff[2];
					*wr_buffer++ = buff[1];
					*wr_buffer++ = buff[0];
				}
				wr_buffer -= x*6; /* backoff 2 lines - x*2 *3 */
			}
			break;
		default:
			return(FH_ERROR_FORMAT);
	}
    p = buffer;
    q = alpha ;   
    if(para->colormode==2) { 
        for(i = buf_len ; i >0 ;i--){
            if(!q){
                p[4*i - 1] = 0xff; 
            }else{
                p[4*i - 1] = q[i-1];         //a
            }
            p[4*i - 4] = p[3*i - 3];     //r
            p[4*i - 3] = p[3*i - 2];     //g
            p[4*i - 2] = p[3*i - 1];     //b
        }  
    } else {
        for(i = buf_len ; i >0 ;i--){
            if(!q){
                p[4*i - 1] = 0xff; 
            }else{
                p[4*i - 1] = q[i-1];         //a
            }
            p[4*i - 2] = p[3*i - 3];     //r
            p[4*i - 3] = p[3*i - 2];     //g
            p[4*i - 4] = p[3*i - 1];     //b
        } 
    }
     

    image->data = buffer;
    image->width = x;
    image->height = y;
    image->depth = 32;
    image->bytes_per_line = x << 2 ;
    image->nbytes = image->bytes_per_line * y;    
	close(fd);
    image->dest_x=0;
    image->dest_y=0;
    image->dest_w=x;
    image->dest_h=y;
    
	return(FH_ERROR_OK);
}
int fh_bmp_getsize(char *name,int *x,int *y)
{
	int fd;
	unsigned char size[4];

	fd = open(name, O_RDONLY);
	if (fd == -1) {
		return(FH_ERROR_FILE);
	}
	if (lseek(fd, BMP_SIZE_OFFSET, SEEK_SET) == -1) {
		return(FH_ERROR_FORMAT);
	}
	
	read(fd, size, 4);
	*x = size[0] + (size[1]<<8) + (size[2]<<16) + (size[3]<<24);
//	*x-=1;
	read(fd, size, 4);
	*y = size[0] + (size[1]<<8) + (size[2]<<16) + (size[3]<<24);
	
	close(fd);
	return(FH_ERROR_OK);
}
