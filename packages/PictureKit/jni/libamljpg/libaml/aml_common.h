#ifndef AML_COMMON_H_
#define AML_COMMON_H_

/**
 * color format of data to be decoded to 
 */
typedef enum {
    COLOR_S32_ARGB=1,
    COLOR_S32_ABGR,
    COLOR_S24_RGB,
    COLOR_S16_RGB
}colormode_t;

typedef struct aml_dec_para{
    char* fn;           ///< Name of picture to be decoded.
    int width;          ///< scaled image width info. 
    int height ;        ///< scaled image height info. 
    int iwidth;         ///< original image width info.
    int iheight;        ///< original image height info.
    int flag;
    int mode;    
	unsigned char *rgb;   
	unsigned char *alpha;    
    unsigned pic_type;		/* pointer to load function. */
    int thumbpref;		///< Set it to 1 to prefer thumbnail data of jpeg.
    int colormode;      ///< Color mode to be decoded to. @see colormode_t.
}aml_dec_para_t;

typedef struct aml_image{
    int width;          ///< scaled image width info.
    int height;         ///< scaled image height info.
    int iwidth;         ///< original image width info.
    int iheight;        ///< original image height info.
    int depth;
    int bytes_per_line;
    int nbytes;  
    int do_free; 
	unsigned char *rgb;
	unsigned char *alpha;      
    char* data;         ///< data of decoded picture.
}aml_image_info_t;
#endif
