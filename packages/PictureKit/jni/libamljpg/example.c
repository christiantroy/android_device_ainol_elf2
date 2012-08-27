#include "amljpeg.h"
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
//#include <malloc.h>
int test_dec_from_file(int argc, const char *argv[])
{
    int width, height ,mode,flag,i;    
    char* outputdata;
    aml_image_info_t* image_info;
    if(argc < 5){        
        printf("Amlogic jpeg decoder API \n");
        printf("usage: output [filename] [width] [height] [mode] [flag]\n");
        printf("options :\n");
        printf("         filename : jpeg url in your root fs\n");
        printf("         width    : output width\n");
        printf("         height   : output height\n");
        printf("         mode     : 0/keep ratio  1/crop image 2/stretch image\n");
        printf("         flag     : 0/disable display 1/antiflicking disable&enable display  2/antiflicking enable&enable display \n ");
        return -1;    
    }else{
        printf("%s\n", argv[1]);
    }  
    width =  atoi(argv[2]);
    if((width <1)||(width > 1920)){
        printf("invalid width \n");
        return -1;    
    }
    height = atoi(argv[3]);
    if((height <1)||(height > 1080)){
        printf("invalid height \n");
        return -1;    
    }    
    mode  =  atoi(argv[4]);
    flag  =  atoi(argv[5]);    
    if((mode <0)||(mode > 2)){
        printf("invalid mode \n");
        return -1;    
    }    
    printf("url    is %s ;\n", argv[1]);
    printf("width  is %d ;\n", width);
    printf("height is %d ;\n", height);
    printf("mode   is %d ;\n", mode);
    if(amljpeg_init()<0 ) {
		printf("fucking cmem initing error, decode exited\n");
		exit(1);
	}
    image_info = read_jpeg_image((char*)argv[1],width,height,mode,flag,flag,0);
    if(image_info){
        printf("output image width is %d\n", image_info->width);
        printf("output image height is %d\n", image_info->height);
        printf("output image depth is %d\n", image_info->depth);
        printf("output image bytes_per_line is %d\n", image_info->bytes_per_line);
        printf("output image nbytes   is %d\n", image_info->nbytes);
    }
    if(image_info){
        free(image_info);    
        image_info = NULL;
    }
    amljpeg_exit();      
    return 0;  
}

int test_dec_from_mem(int argc, const char *argv[])
{
    int width, height ,mode,flag,i;    
    char* outputdata;
    aml_image_info_t* image_info=NULL;
    char* img_buf=NULL;
    int img_len;
    FILE* fd;
    if(argc < 5){        
        printf("Amlogic jpeg decoder API \n");
        printf("usage: output [filename] [width] [height] [mode] [flag]\n");
        printf("options :\n");
        printf("         filename : jpeg url in your root fs\n");
        printf("         width    : output width\n");
        printf("         height   : output height\n");
        printf("         mode     : 0/keep ratio  1/crop image 2/stretch image\n");
        printf("         flag     : 0/disable display 1/antiflicking disable&enable display  2/antiflicking enable&enable display \n ");
        return -1;    
    }else{
        printf("%s\n", argv[1]);
    }  
    width =  atoi(argv[2]);
    if((width <1)||(width > 1920)){
        printf("invalid width \n");
        goto exit;    
    }
    height = atoi(argv[3]);
    if((height <1)||(height > 1080)){
        printf("invalid height \n");
        goto exit;    
    }    
    mode  =  atoi(argv[4]);
    flag  =  atoi(argv[5]);    
    if((mode <0)||(mode > 2)){
        printf("invalid mode \n");
        goto exit;  
    }    
    printf("url    is %s ;\n", argv[1]);
    printf("width  is %d ;\n", width);
    printf("height is %d ;\n", height);
    printf("mode   is %d ;\n", mode);
    if(amljpeg_init()<0 ) {
		printf("cmem initing error, decode exited\n");
		goto exit;
	}
	
	fd=fopen(argv[1],"rb+");
	if(!fd) {
		printf("can not open %s for decoding.\n",argv[1]);
		goto exit;
	}
	
	FILE* fd2=fopen("/sdcard/external_sdcard/fr3.dat","w+");
	if(!fd2) {
		printf("can not open /sdcard/external_sdcard/fr2.dat for out.\n");
		goto exit;
	}
	
	fseek(fd,0L,SEEK_END);
	img_len = ftell(fd);
	if(img_len<=0) {
		printf("empty file?\n");
		goto exit;
	}
	fseek(fd,0,SEEK_SET); 
	img_buf=malloc(img_len);
	if(!img_buf) {
		printf("alloc memory for %s error.\n",argv[1]);
		goto exit;
	}
	
	fread(fd,img_buf,img_len,fd);
	

    image_info = read_mem_jpeg(img_buf,img_len,width,height,mode,flag,flag,0);
    if(image_info){
        printf("output image width is %d\n", image_info->width);
        printf("output image height is %d\n", image_info->height);
        printf("output image depth is %d\n", image_info->depth);
        printf("output image bytes_per_line is %d\n", image_info->bytes_per_line);
        printf("output image nbytes   is %d\n", image_info->nbytes);
    }
    fwrite(image_info->data,1,image_info->nbytes,fd2);
exit:
	if(fd) fclose(fd);
	if(fd2) fclose(fd2);
	if(img_buf) free(img_buf);
    if(image_info){
        free(image_info);    
        image_info = NULL;
    }
    amljpeg_exit();      
    return 0;  
}

int test_dec_from_mem_simple(int argc, const char *argv[])
{
    int width, height ,mode,flag,i;    
    char* outputdata;
    aml_image_info_t* image_info=NULL;
    char* img_buf=NULL;
    static int img_len;
    FILE* fd=NULL; 
    width = 640;
    height = 480;    
    mode  =  0;
    flag  =  0;       

    if(amljpeg_init()<0 ) {
		printf("cmem initing error, decode exited\n");
		goto exit;
	}
	
	fd=fopen("/sdcard/external_sdcard/1.jpg","rb");
	if(!fd) {
		printf("can not open %s for decoding.\n",argv[1]);
		goto exit;
	}
	
	FILE* fd2=fopen("/sdcard/external_sdcard/fr3.dat","w+");
	if(!fd2) {
		printf("can not open /sdcard/external_sdcard/fr2.dat for out.\n");
		goto exit;
	}
	
	fseek(fd,0L,SEEK_END);
	img_len = ftell(fd);
	if(img_len<=0) {
		printf("empty file?\n");
		goto exit;
	}
	fseek(fd,0,SEEK_SET); 
	img_buf=malloc(img_len);
	if(!img_buf) {
		printf("alloc memory for %s error.\n",argv[1]);
		goto exit;
	}
	
	fread(img_buf,1,img_len,fd);

    image_info = read_mem_jpeg(img_buf,img_len,width,height,mode,flag,flag,3);
    if(image_info){
        printf("output image width is %d\n", image_info->width);
        printf("output image height is %d\n", image_info->height);
        printf("output image depth is %d\n", image_info->depth);
        printf("output image bytes_per_line is %d\n", image_info->bytes_per_line);
        printf("output image nbytes   is %d\n", image_info->nbytes);
        fwrite(image_info->data,1,image_info->nbytes,fd2);
    }
exit:
	if(fd) fclose(fd);
	if(fd2) fclose(fd2);
	if(img_buf) free(img_buf);
    if(image_info){
        free(image_info);    
        image_info = NULL;
    }
    amljpeg_exit();      
    return 0;  
}

int main(int argc, const char *argv[])
{
    if(1)
		return test_dec_from_mem_simple(argc,argv);
	else 
		return test_dec_from_file(argc,argv);
}
