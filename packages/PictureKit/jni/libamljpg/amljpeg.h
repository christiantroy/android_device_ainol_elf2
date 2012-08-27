#if defined (__cplusplus)
extern "C" {
#endif
#include "libaml/aml_common.h"
//typedef struct aml_image{
//    int width;
//    int height;
//    int depth;
//    int bytes_per_line;
//    int nbytes;  
//    char* data;  
//}aml_image_info_t;
int amljpeg_init();
void amljpeg_exit();
aml_image_info_t* read_jpeg_image(const char* url , int width, int height,int mode , int flag,
																		int thumbpref,int colormode);  
aml_image_info_t* read_mem_jpeg(char* img_buf,unsigned int img_len,int width, int height,
													int mode , int flag,int thumbpref,int colormode);
#if defined (__cplusplus)
}
#endif
