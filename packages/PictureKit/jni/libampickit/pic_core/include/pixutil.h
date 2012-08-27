
#ifndef __PIXUTIL_H__
#define __PIXUTIL_H__

/* 
 * used for 3d image inserting. 
 */
extern aml_image_info_t* insert_lr_frame(aml_dec_para_t* para,aml_image_info_t* input_image_info,
							aml_image_info_t* output_image_info,int r_strip_flag);
extern aml_image_info_t* insert_tb_frame(aml_dec_para_t* para,aml_image_info_t* input_image_info,
							aml_image_info_t* output_image_info);
extern aml_image_info_t* insert_lr_frame2(aml_dec_para_t* para,aml_image_info_t* input_image_info,int r_strip_flag);
extern aml_image_info_t* insert_tb_frame2(aml_dec_para_t* para,aml_image_info_t* input_image_info);
extern char* scan_line(aml_image_info_t* info , int i);


#endif /* __PIXUTIL_H__ */
