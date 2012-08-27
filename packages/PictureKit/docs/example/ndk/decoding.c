#include <pic_app.h>
#include <pic_uop.h>

int main() {
	aml_dec_para_t dec_info;
	aml_image_info_t image_info;
	
	memset(&dec_info,0,sizeof(aml_dec_para_t));
	memset(&image_info,0,sizeof(aml_image_info_t));
	dec_info.fn="test.jpeg";
	if(dec_info.width<=0) dec_info.width=1280;
	if(dec_info.height<=0) dec_info.height=720;
	dec_info.colormode = COLOR_S32_ABGR;
	
	if(get_pic_info(&dec_info)==FH_ERROR_OK) {
		if(load_pic(&dec_info,&image_info)==FH_ERROR_OK) {
			/* data of picture is in image_info.data. */
			
			if(image_info.data) free(image_info.data);
		}
	}
}
