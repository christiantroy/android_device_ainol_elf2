/*! \file aml_common.h
\brief  Objects for decoding pictures.
Objects for decoding pictures.
*/  

#ifndef AML_COMMON_H_
#define AML_COMMON_H_

/*!
 * \enum
 * \brief color format of data to be decoded to 
 */
typedef enum {
    COLOR_S32_ARGB=1,
    COLOR_S32_ABGR,
    COLOR_S16_RGB
}colormode_t;

#define IMAGE_3D_STYLE_MIXED 	0
#define IMAGE_3D_STYLE_LR	 	1
#define IMAGE_3D_STYLE_TB	 	2 /* not realized. */
#define IMAGE_3D_STYLE_2FRAME 	3
#define IMAGE_3D_STYLE_H_HEIGHT	10 

typedef struct {
	unsigned char type;
	unsigned char style;
	unsigned total_frame; 
	int frame[2];
} image_3d_info_t;

/*!
 * \struct aml_dec_para.
 * \brief Object of picture setting used by decoder.
 * */
typedef struct aml_dec_para{
    char* fn;           ///< Name of picture to be decoded.
    int width;          ///< scaled image width info. 
    int height;        ///< scaled image height info. 
    int iwidth;         ///< original image width info.
    int iheight;        ///< original image height info.
    
    ///< image position in buffer
    int dest_x;
    int dest_y;
    int dest_w;
    int dest_h;
    
    
    int flag;
    int mode;    
	unsigned char *rgb;
	unsigned char *alpha;    
    unsigned pic_type;		/* pointer to load function. */
    int thumbpref;		///< Set it to 1 to prefer thumbnail data of jpeg.
    int colormode;      ///< Color mode to be decoded to. @see colormode_t.
    
    ///< decode mode for 3d. 0:no 3d, 1:auto lr 3d, 2:force lr 3d,3:auto tb 3d, 4:force tb 3d.
    char image_3d_mode_pref;
    int image_3d_lr_offset; ///< bit 0-6 right offset from 0 to 100, bit 7 move left or right.
							///< bit 8~14 left offset  from 0 to 100, bit 15 move left or right. 
	int image_3d_preserv; 
	image_3d_info_t  image_3d_info;

}aml_dec_para_t;

/*!
 * \struct aml_image.
 * \brief Object returned by decoder.
 * */
typedef struct aml_image{
    int width;          ///< scaled image width info.
    int height;         ///< scaled image height info.
    int iwidth;         ///< original image width info.
    int iheight;        ///< original image height info.
    
    ///< image position in buffer
    int dest_x;
    int dest_y;
    int dest_w;
    int dest_h;
    
    int depth;
    int bytes_per_line;
    int nbytes;  
    int do_free; 
	unsigned char *rgb;
	unsigned char *alpha;      
    char* data;         ///< data of decoded picture.
}aml_image_info_t;
#endif
