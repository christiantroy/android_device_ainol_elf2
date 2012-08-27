/*******************************************************************
 * 
 *  Copyright C 2005 by Amlogic, Inc. All Rights Reserved.
 *
 *  Description: 
 *
 *  Author: Amlogic Software
 *  Created: Fri Nov 11 00:01:32 2005
 *
 *******************************************************************/
#include <stdlib.h>
//#include "scaler_common.h"
#include "scaler.h"

#define _max(a,b) (a)>(b)?(a):(b)

int compute_full_image(simp_scaler_t *scaler,float* ratio_x,float* ratio_y)
{
    float image_aspect_ratio;
	float frame_aspect_ratio;
	int image_width ,image_height;
	int frame_width , frame_height;
	image_width = scaler->input.pic_width;
	image_height = scaler->input.pic_height;
	
	frame_width = scaler->input.frame_width;
	frame_height = scaler->input.frame_height;
	
    image_aspect_ratio = ((float) image_height) / ((float) image_width);
	frame_aspect_ratio = ((float) frame_height) / ((float) frame_width);
	switch(scaler->input.mode){
	    case 0:
	    case 1:
	    case 2:
        if (image_aspect_ratio >= frame_aspect_ratio){
            // Image is taller than display
            *ratio_x  = *ratio_y = (float)image_height/frame_height;
        }else{
            // Image is wider than display
            *ratio_y = *ratio_x =  (float)image_width/frame_width ;
        } 	    
	    break;  
	    case 3:
	    *ratio_x  = (float)image_width/frame_width ;
	    *ratio_y = (float)image_height/frame_height ;
	    break;
	    default:
	    break;
	      
	}
	
	


    return 0;
}

void draw_pixel(simp_scaler_t *scaler , int  x, int y,unsigned char a,unsigned char r,unsigned char g,unsigned char b)
{

    int offset = y * scaler->input.bpp + x*4;
    unsigned char* p = &scaler->input.output[offset];
//    p[0] = a;
//    p[1] = r;
//    p[2] = g;
//    p[3] = b;
    p[0] = b;
    p[1] = g;
    p[2] = r;
    p[3] = a;
}

static void
build_scale_map(simp_scaler_t *scaler)
{

	int i;
	float ratio_x, ratio_y;
	
    compute_full_image(scaler, &ratio_x, &ratio_y);
    
    //ratio
    ratio_x = 1 / ratio_x;
    ratio_y = 1 / ratio_y;
    
    /* build X map */
    for (i=0;i<scaler->input.pic_width+1;i++){
        scaler->map_x[2*i+1] = (unsigned)(i * ratio_x + 0.5);       /* target X for each input */
    }

    for (i=0;i<scaler->input.pic_width;i++){
        scaler->map_x[2*i] = scaler->map_x[2*i+3] - scaler->map_x[2*i+1];
    }

    /* build Y map */
    for (i=0;i<scaler->input.pic_height+1;i++){
        scaler->map_y[2*i+1] = (unsigned)(i * ratio_y + 0.5);       /* target X for each input */
    }

    for (i=0;i<scaler->input.pic_height;i++){
        scaler->map_y[2*i] = scaler->map_y[2*i+3] - scaler->map_y[2*i+1];
    }

    /* note:
     * ratio_x/ratio_y is in the same space with the input image, so (x,y) works for the input
     * and the map_x/map_y also works for the input(x,y), and scaled_width/scaled_height
     * is same. The width/height is the same direction as the input, but scaled_top/scaled_left
     * is the screen based position.
     */
    scaler->input.scaled_width  =  scaler->input.pic_width  * ratio_x;
    scaler->input.scaled_height =  scaler->input.pic_height * ratio_y;
#if 0
	printf("pic_width is %d \n",scaler->input.pic_width) ;
	printf("pic_height is %d \n",scaler->input.pic_height) ;	
	printf("ratio_x is %f \n",ratio_x) ;
	printf("ratio_y is %f \n",ratio_y) ;
	
	printf("scaled_width is %d \n",scaler->input.scaled_width) ;
	printf("scaled_height is %d \n",scaler->input.scaled_height) ;
	while(1);
#endif	
	scaler->input.scaled_left =   (scaler->input.frame_width - scaler->input.scaled_width) >>1;	
	scaler->input.scaled_top =   (scaler->input.frame_height - scaler->input.scaled_height) >>1;
	
	scaler->input.scaled_top  = (scaler->input.scaled_top >> 1) << 1;
	scaler->input.scaled_left = (scaler->input.scaled_left >> 1) << 1;

}

static void
pixel_write( simp_scaler_t *scaler, unsigned char r, 
                    unsigned char g,unsigned char b, unsigned char a , int image_x, int image_y)
{
    int x,y,temp_x, temp_y;  
    for (y=0; y<scaler->map_y[2*(image_y)]; y++){

        unsigned screen_y = scaler->map_y[2*(image_y)+1] + y;
        for (x=0; x<scaler->map_x[2*(image_x)]; x++){

            unsigned screen_x = scaler->map_x[2*(image_x)+1] + x;
            unsigned char *p;

			switch (scaler->input.rotation) {
				default:
				case PIC_DEC_DIR_0:
					temp_x = screen_x;
					temp_y = screen_y;
					break;

				case PIC_DEC_DIR_90:
					temp_x = scaler->input.scaled_height - screen_y - 1;
					temp_y = screen_x;
					break;

				case PIC_DEC_DIR_180:
					temp_x = scaler->input.scaled_width - screen_x - 1;
					temp_y = scaler->input.scaled_height - screen_y - 1;
					break;

				case PIC_DEC_DIR_270:
					temp_x = screen_y;
					temp_y = scaler->input.scaled_width - screen_x - 1;
					break;
			}

			temp_x += scaler->input.scaled_left;
			temp_y += scaler->input.scaled_top;
			
			if(temp_x >= scaler->input.frame_width){
				temp_x = scaler->input.frame_width - 1;
			}
			if(temp_y >= scaler->input.frame_height){
				temp_y = scaler->input.frame_height - 1;
			}			
			draw_pixel(scaler ,temp_x,temp_y,255,r,g,b);
        }
    }
}

int
simp_scaler_init(simp_scaler_t *scaler)
{
    unsigned size;

    size = _max(scaler->input.pic_width, scaler->input.pic_height);
    scaler->map_x = (unsigned short *)malloc((size * 2 + 2) * 2);
    if (!scaler->map_x) {
        return -1;
    }

    scaler->map_y = (unsigned short *)malloc((size * 2 + 2) * 2);
    if (!scaler->map_y) {
        free(scaler->map_x);
        scaler->map_x = NULL;
        return -1;
    }

    build_scale_map(scaler);

	scaler->pixel_write = pixel_write;    
	return 0;    
}

void
simp_scaler_output_pixel(   simp_scaler_t *scaler, unsigned char r, unsigned char g,
                                    unsigned char b, unsigned char a , int image_x, int image_y)
{
    scaler->pixel_write(scaler, r,g,b, a ,image_x, image_y);
}

void
simp_scaler_flush(simp_scaler_t *scaler)
{
    return;
}

void
simp_scaler_destroy(simp_scaler_t *scaler)
{
    if (scaler->map_x) {
        free(scaler->map_x);
        scaler->map_x = NULL;
    }
    if (scaler->map_y) {
        free(scaler->map_y);
        scaler->map_y = NULL;
    }
}

