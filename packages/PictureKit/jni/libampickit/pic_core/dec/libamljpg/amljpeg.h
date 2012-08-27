#if defined (__cplusplus)
extern "C" {
#endif
#include <libaml/aml_common.h>
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
aml_image_info_t* read_jpeg_image(aml_dec_para_t* para);
aml_image_info_t* read_2_frame_image(aml_dec_para_t* para);
aml_image_info_t* read_lr_in_one_image(aml_dec_para_t* para);
#if defined (__cplusplus)
}
#endif
