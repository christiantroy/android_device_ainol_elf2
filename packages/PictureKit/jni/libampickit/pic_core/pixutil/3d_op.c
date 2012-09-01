#include <config.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <string.h>
#include <libaml/aml_common.h>
#include <pixutil.h>
#include <arm_neon.h>

aml_image_info_t* insert_lr_frame(aml_dec_para_t* para,aml_image_info_t* input_image_info,
							aml_image_info_t* output_image_info,int r_strip_flag) {
	int i;
	int j=0;
	int dest_y;
	char* input_data;
	char* output_data;
	int alignedwidth;
	
	int cnt_left,cnt_right;
	int left_start,right_start;
	int left_width,right_width,min_width=0;
	int ldelta=0,rdelta=0;
	int extrabits;
	
	/* parse appear rate for left right part */
	if(para->image_3d_lr_offset&0x8000) { 
		left_start= (para->image_3d_lr_offset>>9)&0x3f;
		left_width=(input_image_info->dest_w>>1)-left_start;
	} else {
		left_start=0;
		ldelta=(para->image_3d_lr_offset>>9)&0x3f;
		left_width=(input_image_info->dest_w>>1)-ldelta;
	}
	if(para->image_3d_lr_offset&0x80) { 
		right_start= (para->image_3d_lr_offset>>1)&0x3f;
		right_width=(input_image_info->dest_w>>1)-right_start;
	} else {
		right_start=0;
		rdelta=(para->image_3d_lr_offset>>1)&0x3f;
		right_width=(input_image_info->dest_w>>1)-rdelta;
	}
	//ALOGD("-ls:%d,rs:%d,lw:%d,rw:%d\n",left_start,right_start,left_width,right_width);	
	
	if(ldelta>rdelta) {
		ldelta=ldelta-rdelta;
		right_start+=ldelta;
		right_width-=ldelta;
	} else if(ldelta<rdelta){
		rdelta=rdelta-ldelta;
		left_start+=ldelta;
		left_width-=ldelta;
	}
	//ALOGD("-ls:%d,rs:%d,lw:%d,rw:%d\n",left_start,right_start,left_width,right_width);
	
	min_width=left_width<right_width?left_width:right_width;
	//ALOGD("-ls:%d,rs:%d,mw:%d\n",left_start,right_start,min_width);
	
	/* start to copy. */
	left_start+= input_image_info->dest_x;
	if(r_strip_flag)
		right_start+=(input_image_info->width>>1)+input_image_info->dest_x;
	else
		right_start+=(input_image_info->width>>1);
	
	if(min_width<=0) {
		//ALOGD("========fail========\n");
		min_width = input_image_info->dest_w>>1;
		left_start=0;
		right_start= min_width;
	}
	
	//ALOGD("-ls:%d,rs:%d,mw:%d\n",left_start,right_start,min_width);
	
	output_image_info->width = min_width*2;
	output_image_info->bytes_per_line = ((output_image_info->width * output_image_info->depth + 31) >> 5 ) << 2 ;
	output_image_info->nbytes = output_image_info->bytes_per_line * para->height;
	//ALOGD("===%d===\n",para->height);
	output_image_info->data  = malloc(output_image_info->nbytes);
    //ALOGD("--ii---ls:%d,rs:%d,mw:%d\n",left_start,right_start,min_width);
	if(!output_image_info->data){
		ALOGD("err alloc output_image_info->data\n");
		free(output_image_info);
		return NULL;   
	}

	left_start=left_start*4;
	right_start=right_start*4;

	alignedwidth=min_width&(~0x3);
	extrabits=min_width&0x3;
	//ALOGD("====alignedwidth:%d==extrabits:%d==\n",alignedwidth,extrabits);
	for(i = input_image_info->dest_y,dest_y=input_image_info->dest_y; i < input_image_info->height ; i++,dest_y++) {
		input_data =   scan_line(input_image_info , i);
		output_data =  scan_line(output_image_info , dest_y); 
        //if(i>718) ALOGD("===i:%x===o:%x\n",input_data,output_data);
		cnt_left=left_start;
		cnt_right= right_start;
		asm volatile(
			"mov r4,#0\n\t"
			"1:\n\t"
			"vld1.32 {d0,d1},[%0]!\n\t"
			"vld1.32 {d2,d3},[%1]!\n\t"
			"vzip.32 q0,q1\n\t"
			"vst1.32 {d0,d1,d2,d3},[%2]!\n\t"
			"add r4,r4,#4\n\t"
			"cmp r4,%3\n\t"
			"blt 1b\n\t"
			"nop\n\t"
		:
		:"r"(input_data+cnt_left),"r"(input_data+cnt_right),"r"(output_data),"r"(alignedwidth)
		:"d0","d1","d2","d3","cc", "memory","r4");
		/* copy extra data witch is smaller than 4 pixels. */
		
		j=alignedwidth<<3;
		cnt_left=left_start+(alignedwidth<<2);
		cnt_right= right_start+(alignedwidth<<2);
		/*if(i>718) {
            ALOGD("h:%dj:%d,cl:%d,cr:%d\n",i,j/4,cnt_left/4,cnt_right/4);
            ALOGD("===i:%x===o:%x\n",input_data,output_data);
        }*/
		if(extrabits) {
			input_data =   scan_line(input_image_info , i);
			output_data =  scan_line(output_image_info , dest_y); 
			switch(extrabits) {
			case 3:
				memcpy(output_data+j,input_data+cnt_left,4);
				cnt_left+=4;
				j+=4;
				memcpy(output_data+j,input_data+cnt_right,4);
				cnt_right+=4;
				j+=4;
			case 2:
				memcpy(output_data+j,input_data+cnt_left,4);
				cnt_left+=4;
				j+=4;
				memcpy(output_data+j,input_data+cnt_right,4);
				cnt_right+=4;
				j+=4;
			case 1:
				memcpy((output_data+j),(input_data+cnt_left),4);
				cnt_left+=4;
				j+=4;
				memcpy(output_data+j,input_data+cnt_right,4);
				cnt_right+=4;
				j+=4;
			}
		}

	}
	return output_image_info;
}

aml_image_info_t* insert_tb_frame(aml_dec_para_t* para,aml_image_info_t* input_image_info,
							aml_image_info_t* output_image_info) {
	int i;
	int left_start,right_start;
	char* input_data;
	char* output_data;

	output_image_info->width = input_image_info->width;
	output_image_info->bytes_per_line = ((output_image_info->width * output_image_info->depth + 31) >> 5 ) << 2 ;
	output_image_info->nbytes = output_image_info->bytes_per_line * para->height;
	output_image_info->data  = malloc(output_image_info->nbytes);
	if(!output_image_info->data){
		ALOGD("err alloc output_image_info->data\n");
		free(output_image_info);
		return NULL;   
	}

	left_start= input_image_info->dest_y;
	right_start= left_start+(input_image_info->dest_h>>1);
	ALOGD("d_y:%d,d_h:%d,o_h:%d\n",input_image_info->dest_y,input_image_info->dest_h,input_image_info->height);
#if 1
	for(i = input_image_info->dest_y ; i < input_image_info->dest_h ; i+=2,right_start++,left_start++) {
		input_data =   scan_line(input_image_info , left_start);
		output_data =  scan_line(output_image_info , i); 
		memcpy(output_data,input_data,output_image_info->bytes_per_line);
		input_data =   scan_line(input_image_info , right_start);
		output_data =  scan_line(output_image_info , i+1); 
		memcpy(output_data,input_data,output_image_info->bytes_per_line);
	}
#else 
	for(i = 0 ; i < output_image_info->height ; i+=1,right_start++,left_start++) {
		input_data =   scan_line(input_image_info , i);
		output_data =  scan_line(output_image_info , i); 
		memcpy(output_data,input_data,output_image_info->bytes_per_line);
	}
#endif
	return output_image_info;
}

aml_image_info_t* insert_lr_frame2(aml_dec_para_t* para,aml_image_info_t* input_image_info,
							int r_strip_flag) {
	int i;
	int j=0;
	int dest_y;
	char* input_data;
	char* output_data;
	int alignedwidth;
	
	int cnt_left,cnt_right;
	int left_start,right_start;
	int left_width,right_width,min_width=0;
	int ldelta=0,rdelta=0;
	int extrabits;
	
	/* parse appear rate for left right part */
	if(para->image_3d_lr_offset&0x8000) { 
		left_start= (para->image_3d_lr_offset>>9)&0x3f;
		left_width=(input_image_info->dest_w>>1)-left_start;
	} else {
		left_start=0;
		ldelta=(para->image_3d_lr_offset>>9)&0x3f;
		left_width=(input_image_info->dest_w>>1)-ldelta;
	}
	if(para->image_3d_lr_offset&0x80) { 
		right_start= (para->image_3d_lr_offset>>1)&0x3f;
		right_width=(input_image_info->dest_w>>1)-right_start;
	} else {
		right_start=0;
		rdelta=(para->image_3d_lr_offset>>1)&0x3f;
		right_width=(input_image_info->dest_w>>1)-rdelta;
	}
	ALOGD("-ls:%d,rs:%d,lw:%d,rw:%d\n",left_start,right_start,left_width,right_width);	
	
	if(ldelta>rdelta) {
		ldelta=ldelta-rdelta;
		right_start+=ldelta;
		right_width-=ldelta;
	} else if(ldelta<rdelta){
		rdelta=rdelta-ldelta;
		left_start+=ldelta;
		left_width-=ldelta;
	}
	ALOGD("-ls:%d,rs:%d,lw:%d,rw:%d\n",left_start,right_start,left_width,right_width);
	
	min_width=left_width<right_width?left_width:right_width;
	ALOGD("-ls:%d,rs:%d,mw:%d\n",left_start,right_start,min_width);
	
	/* start to copy. */
	left_start+= input_image_info->dest_x;
	if(r_strip_flag)
		right_start+=(input_image_info->width>>1)+input_image_info->dest_x;
	else
		right_start+=(input_image_info->width>>1);
	
	if(min_width<=0) {
		//ALOGD("========fail========\n");
		min_width = input_image_info->dest_w>>1;
		left_start=0;
		right_start= min_width;
	}
	
	ALOGD("-ls:%d,rs:%d,mw:%d\n",left_start,right_start,min_width);
	
	char* out_img  = malloc(input_image_info->nbytes);
    ALOGD("--ii---ls:%d,rs:%d,mw:%d\n",left_start,right_start,min_width);
	if(!out_img){
		ALOGD("err alloc output_image_info->data\n");
		return NULL;   
	}

	left_start=left_start*4;
	right_start=right_start*4;

	alignedwidth=min_width&(~0x3);
	extrabits=min_width&0x3;
	ALOGD("====alignedwidth:%d==extrabits:%d==\n",alignedwidth,extrabits);
	for(i = input_image_info->dest_y,dest_y=input_image_info->dest_y; i < input_image_info->height ; i++,dest_y++) {
		input_data =   scan_line(input_image_info , i);
		output_data =  out_img+input_image_info->bytes_per_line*dest_y;
		output_data += input_image_info->dest_x*4; 
        //if(i>718) ALOGD("===i:%x===o:%x\n",input_data,output_data);
		cnt_left=left_start;
		cnt_right= right_start;
		asm volatile(
			"mov r4,#0\n\t"
			"1:\n\t"
			"vld1.32 {d0,d1},[%0]!\n\t"
			"vld1.32 {d2,d3},[%1]!\n\t"
			"vzip.32 q0,q1\n\t"
			"vst1.32 {d0,d1,d2,d3},[%2]!\n\t"
			"add r4,r4,#4\n\t"
			"cmp r4,%3\n\t"
			"blt 1b\n\t"
			"nop\n\t"
		:
		:"r"(input_data+cnt_left),"r"(input_data+cnt_right),"r"(output_data),"r"(alignedwidth)
		:"d0","d1","d2","d3","cc", "memory","r4");
		/* copy extra data witch is smaller than 4 pixels. */
		
		j=alignedwidth<<3;
		cnt_left=left_start+(alignedwidth<<2);
		cnt_right= right_start+(alignedwidth<<2);
		/*if(i>718) {
            ALOGD("h:%dj:%d,cl:%d,cr:%d\n",i,j/4,cnt_left/4,cnt_right/4);
            ALOGD("===i:%x===o:%x\n",input_data,output_data);
        }*/
		if(extrabits) {
			input_data =   scan_line(input_image_info , i);
			output_data =  out_img+input_image_info->bytes_per_line*dest_y; 
			output_data += input_image_info->dest_x*4;
			switch(extrabits) {
			case 3:
				memcpy(output_data+j,input_data+cnt_left,4);
				cnt_left+=4;
				j+=4;
				memcpy(output_data+j,input_data+cnt_right,4);
				cnt_right+=4;
				j+=4;
			case 2:
				memcpy(output_data+j,input_data+cnt_left,4);
				cnt_left+=4;
				j+=4;
				memcpy(output_data+j,input_data+cnt_right,4);
				cnt_right+=4;
				j+=4;
			case 1:
				memcpy((output_data+j),(input_data+cnt_left),4);
				cnt_left+=4;
				j+=4;
				memcpy(output_data+j,input_data+cnt_right,4);
				cnt_right+=4;
				j+=4;
			}
		}

	}
	free(input_image_info->data);
	input_image_info->data=out_img;
	return input_image_info;
}

aml_image_info_t* insert_tb_frame2(aml_dec_para_t* para,aml_image_info_t* input_image_info
							) {
	int i;
	int left_start,right_start;
	char* input_data;
	char* output_data;

	char* out_img  = malloc(input_image_info->nbytes);
	if(!out_img){
		ALOGD("err alloc output_image_info->data\n");
		return NULL;   
	}

	left_start= input_image_info->dest_y;
	right_start= left_start+(input_image_info->dest_h>>1);
	ALOGD("d_y:%d,d_h:%d,o_h:%d\n",input_image_info->dest_y,input_image_info->dest_h,input_image_info->height);

	for(i = input_image_info->dest_y ; i < input_image_info->dest_h ; i+=2,right_start++,left_start++) {
		input_data =   scan_line(input_image_info , left_start);
		output_data =  out_img+input_image_info->bytes_per_line*i; 
		memcpy(output_data,input_data,input_image_info->bytes_per_line);
		input_data =   scan_line(input_image_info , right_start);
		output_data =  out_img+input_image_info->bytes_per_line*(i+1); 
		memcpy(output_data,input_data,input_image_info->bytes_per_line);
	}

	free(input_image_info->data);
	input_image_info->data=out_img;
	return input_image_info;
}

char* scan_line(aml_image_info_t* info , int i)
{
    if((!info)||(!info->data)||(i > 1080)){
        return NULL;    
    }
    return (info->data + i*info->bytes_per_line);
}
