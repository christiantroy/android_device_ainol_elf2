/*
    ampicplayer.
    Copyright (C) 2010 amlogic.

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
#include <pic_app.h>
#include <pic_load_impl.h>
#include <vo.h>       
/**
*
* function to get thep information of picture.
*
**/

int get_pic_info(aml_dec_para_t* para) {
	int i;
	 
	/* check validility. */
	if(!para) return FH_ERROR_OTHER;
	if(configure_number_of_decoder<=1) return FH_ERROR_FORMAT;
	/* try to detect the picture type. */
	for(i=1;i<configure_number_of_decoder;i++) {
		if(decoder_funs[i].fd_id(para)) {
			if(decoder_funs[i].fh_getsize(para->fn, &para->iwidth, &para->iheight)
					== FH_ERROR_OK) {
				para->pic_type=i;
				return FH_ERROR_OK;
			}
		}
	}
	return FH_ERROR_FORMAT;
}

/**
*
* function to load a picture to pbuff.
*
**/
int load_pic(aml_dec_para_t* para , aml_image_info_t* image) {
    int ret = 0;
    
	if(para->pic_type<1 || para->pic_type>=configure_number_of_decoder){
	    ret = FH_ERROR_FORMAT;	
	    return ret ;  
	}
	ret = decoder_funs[para->pic_type].fh_load(para,image);
	if(ret==FH_ERROR_HWDEC_FAIL){
        if(decoder_funs[para->pic_type+1].fh_getsize(para->fn, &para->iwidth, &para->iheight)== FH_ERROR_OK) {
			para->pic_type++;
            ret = decoder_funs[para->pic_type].fh_load(para,image);
        }        
	}
	if(ret==FH_ERROR_OK&&para->pic_type>2) {
		if(para->image_3d_mode_pref>0&&para->image_3d_mode_pref<3) {
			insert_lr_frame2(para,image,0);
		} else if (para->image_3d_mode_pref>=3) {
			insert_tb_frame2(para,image);
		}
	}
	
    return ret ; 	
}

int show_pic(aml_image_info_t* image,int flag)
{
    int x_size, y_size, screen_width, screen_height;
	int x_pan, y_pan, x_offs, y_offs;
    static video_out_t vo={0};
	char* name;
	int reset_vo = 0;
	if(!image->data){
	    return -1;       
	}
    switch(flag){
        case 0: 
        name="fb";        
        break;
        case 1:
        name="gles";    
        break ;
        default:
        name="fb"; 
        break;    
    }	     
	if(vo.name == 0){
		vo.name = name ;
		if(vo_cfg(&vo)!=VO_ERROR_OK) {
			printf("video out device invalid\n");
			return -1;
		}		
		vo_preinit(&vo);	
	}else{
		if(!strcmp(vo.name, name)) {   /*match last vo*/
			reset_vo = 0;
		}else{
			reset_vo = 1;
			vo_uninit(&vo);	
			vo.name = name ;
			if(vo_cfg(&vo)!=VO_ERROR_OK) {
				printf("video out device invalid\n");
				return -1;
			}		
			vo_preinit(&vo);				
		}
	}
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
	return 0;
}
