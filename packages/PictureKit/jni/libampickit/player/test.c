/*
	fbv  --  simple image viewer for the linux framebuffer
	Copyright (C) 2000, 2001, 2003, 2004  Mateusz 'mteg' Golicz

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
#include <stdio.h>
#include <unistd.h>
#include <getopt.h>
#include <stdlib.h>
#include <string.h>

#include <pic_app.h>
#include <pic_uop.h>
#include <vo.h>
#include <aml_common.h>
#include "filelistop.h"

int opt_clear = 1,
	   opt_alpha = 0,
	   opt_hide_cursor = 1,
	   opt_image_info = 1,
	   opt_stretch = 0,
	   opt_delay = 0,
	   opt_enlarge = 0,
	   opt_ignore_aspect = 0;

extern int show_image(char *filename,video_out_t* vo);
extern void sighandler(int s);
extern void setup_console(int t);

void show_buffer(aml_image_info_t* image)
{
    int x_size, y_size, screen_width, screen_height;
	int x_pan, y_pan, x_offs, y_offs;
    video_out_t vo={0};
//	vo.name="gles";    
    vo.name="fb";  
	if(!image->data){
	    return;       
	}
    if(vo_cfg(&vo)!=VO_ERROR_OK) {
		printf("video out device invalid\n");
		exit(1);
	}
	vo_preinit(&vo);
	vo_getCurrentRes(&vo,&screen_width, &screen_height);
	if(image->width < screen_width)
		x_offs = (screen_width - image->width) / 2;
	else
		x_offs = 0;
	
	if(image->height < screen_height)
		y_offs = (screen_height - image->height) / 2;
	else
		y_offs = 0;
	x_pan = 0;
	y_pan= 0;
	vo_display(&vo, image, x_pan, y_pan, x_offs, y_offs);	
}

int main(int argc, char **argv)
{
    int width, height ,mode,flag;    
    char* outputdata;
    int ret =0 ;
    aml_image_info_t image_info = {0};
    aml_dec_para_t para;
    if(argc < 6){        
        printf("Amlogic jpeg decoder API \n");
        printf("usage: output [filename] [width] [height] [mode]\n");
        printf("options :\n");
        printf("         filename1 : jpeg url in your root fs\n");
        printf("         filename2 : jpeg url in your root fs\n");
        printf("         width    : output width\n");
        printf("         height   : output height\n");
        printf("         mode     : 0/keep ratio  1/crop image 2/stretch image\n");
        printf("         flag     : 0/antiflicking disable  1/antiflicking enable\n");
        return -1;    
    }else{
        printf("%s\n", argv[1]);
    }  
    width =  atoi(argv[3]);
    if((width <1)||(width > 1920)){
        printf("invalid width \n");
        return -1;    
    }
    height = atoi(argv[4]);
    if((height <1)||(height > 1080)){
        printf("invalid height \n");
        return -1;    
    }    
    mode  =  atoi(argv[5]);
    flag  =  atoi(argv[6]);    
    if((mode <0)||(mode > 2)){
        printf("invalid mode \n");
        return -1;    
    }    
    printf("url_1    is %s ;\n", argv[1]);
    printf("url_2    is %s ;\n", argv[1]);
    printf("width  is %d ;\n", width);
    printf("height is %d ;\n", height);
    printf("mode   is %d ;\n", mode);    
	int k = 0 ;
	while(1){	
		k^=1;
		if(k){
			para.fn = argv[1];;
		}else{
			para.fn = argv[2];;
		}
    //		para.fn = argv[1];
        para.width = width;   /* scale width ,it's pre-defined.*/
        para.height = height; /* scale height,it's pre-defined.*/
        para.iwidth =  0;     /* need got through get_pic_info function*/
        para.iheight = 0;     /* need got through get_pic_info function*/
        para.mode = mode ;    /* 0/keep ratio  1/crop image 2/stretch image*/
        para.flag = flag;     /* 0/disable display  1/enable display */
        get_pic_info(&para);
        printf("iwidth is %d ; iheight is %d\n", para.iwidth,para.iheight);
        ret = load_pic(&para,&image_info);
    //    show_buffer(&image_info);
        show_pic(&image_info,flag);
        if(!ret){
            printf("output image width is %d\n", image_info.width);
            printf("output image height is %d\n", image_info.height);
            printf("output image depth is %d\n", image_info.depth);
            printf("output image bytes_per_line is %d\n", image_info.bytes_per_line);
            printf("output image nbytes   is %d\n", image_info.nbytes);
            printf("bgra is %d-%d-%d-%d\n",image_info.data[0],image_info.data[1],image_info.data[2],image_info.data[3]);
            printf("decode succeed\n");    
            if(image_info.data){
                free(image_info.data);    
            }
        }else{
            printf("decode fail\n");    
        }
	}
    return 0;
}