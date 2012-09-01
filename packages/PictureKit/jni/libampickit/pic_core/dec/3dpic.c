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
#include <stdio.h>
#include <string.h>
#include <sys/stat.h>

/**
*
* function to get thep information of picture.
*
**/

#define PIC_3D_IMAGE_MAX_FRAME 4
#define _3D_FILE_PARSER_BUFF_LEN 8192
#define MAGIC_3DG 0x676433
//#define MAGIC_3DM 0x6D6433
#define _3DG_FILE_HEAD_LEN 0x20
typedef struct
{
    unsigned magic;
    unsigned char type;
    unsigned char style;
    unsigned short width;
    unsigned short height;
    unsigned filesize;
    unsigned offset1;
    unsigned offset2;
}_3dg_header_t;

static int ParserNormal3DFile(aml_dec_para_t* para, char* ext)
{
    int ret = 0;
    int read_num = 0;
    unsigned file_offset = 0;
    unsigned char find_flag = 0;
    unsigned char last_char = 0xe1;
    int i = 0;
    FILE* fd=NULL;   
    
    if(!ext)
        return ret;
    if(strcasecmp(ext,".mpo") == 0)
        last_char = 0xe1;
    else if(strcasecmp(ext,".3dp") == 0)
        last_char = 0xe2;
    else
        return ret;
    memset(&(para->image_3d_info),0,sizeof(image_3d_info_t));
    unsigned char *buff = malloc(_3D_FILE_PARSER_BUFF_LEN+1);
    if(!buff)  goto exit;
        
	fd=fopen(para->fn,"r");
	if(!fd) goto exit;
	
    do{
        read_num = fread(buff,1, _3D_FILE_PARSER_BUFF_LEN,fd);
        if(read_num>0){
            buff[read_num] = 0; 
            while(i<read_num){
                if(!find_flag){
                    if (buff[i]==0xff){
                        if(i<read_num-3){
                            if((buff[i+1]==0xd8)&&(buff[i+2]==0xff)&&(buff[i+3]==last_char))
                                para->image_3d_info.frame[para->image_3d_info.total_frame++] = file_offset+i;
                        }else if(i<read_num-2){
                            if((buff[i+1]==0xd8)&&(buff[i+2]==0xff)){
                                find_flag = 3;
                                i = i+2;
                            }
                        }else if(i<read_num-1){
                            if(buff[i+1]==0xd8){
                                find_flag = 2;
                                i = i+1;
                            }
                        }else{
                            find_flag = 1;
                        }
                    }
                }else if((find_flag==1)&&(read_num>=3)){
                    if ((buff[i]==0xd8)&&(buff[i+1]==0xff)&&(buff[i+2]==last_char)&&(i==0))
                        para->image_3d_info.frame[para->image_3d_info.total_frame++] = file_offset+i-1;
                    find_flag = 0;
                }else if((find_flag==2)&&(read_num>=2)){
                    if ((buff[i]==0xff)&&(buff[i+1]==last_char)&&(i==0))
                        para->image_3d_info.frame[para->image_3d_info.total_frame++] = file_offset+i-2;
                    find_flag = 0;
                }else if(find_flag==3){
                    if ((buff[i]==last_char)&&(i==0))
                        para->image_3d_info.frame[para->image_3d_info.total_frame++]= file_offset+i-3;
                    find_flag = 0;
                }
                i++;
                if(para->image_3d_info.total_frame>=PIC_3D_IMAGE_MAX_FRAME)
                    break;
            }
            i = 0;
            file_offset+=read_num;
            if(para->image_3d_info.total_frame>=PIC_3D_IMAGE_MAX_FRAME)
                break;
        }
    }while(read_num>0);
    
    if(para->image_3d_info.total_frame>1){
        ret =1;
        para->image_3d_info.type = 2;
        para->image_3d_info.style = 3;
    }
exit:
	if(fd)
		fclose(fd);
    if(buff)
        free(buff);
    return ret;    
}

static int ParserThird3DFile(aml_dec_para_t* para)
{
    int ret =0;
    char buff[_3DG_FILE_HEAD_LEN];
    _3dg_header_t info;
    int read_num = 0;
	
	FILE* fd=fopen(para->fn,"r");
	if(!fd) return  ret;
	
    memset(&(para->image_3d_info),0,sizeof(image_3d_info_t));
    memset(&info,0,sizeof(_3dg_header_t));
    memset(buff,0,sizeof(buff));

    read_num = fread(buff,1, _3DG_FILE_HEAD_LEN,fd);
    if(read_num == _3DG_FILE_HEAD_LEN){
        int i = 0;
        info.magic = buff[i++];
        info.magic |= buff[i++]<<8;
        info.magic |= buff[i++]<<16;
        info.magic |= buff[i++]<<24;

        info.type = buff[i++];
        info.style = buff[i++];
        info.width = buff[i++];
        info.width |= buff[i++]<<8;
        info.height= buff[i++];
        info.height |= buff[i++]<<8;

        info.filesize= buff[i++];
        info.filesize |= buff[i++]<<8;
        info.filesize |= buff[i++]<<16;
        info.filesize |= buff[i++]<<24;

        info.offset1= buff[i++];
        info.offset1 |= buff[i++]<<8;
        info.offset1 |= buff[i++]<<16;
        info.offset1 |= buff[i++]<<24;

        info.offset2= buff[i++];
        info.offset2 |= buff[i++]<<8;
        info.offset2 |= buff[i++]<<16;
        info.offset2 |= buff[i++]<<24;

        if(info.magic == MAGIC_3DG){ 
            para->image_3d_info.type = info.type;
            para->image_3d_info.style= info.style;
            if(info.type == 2){
                if(info.style ==IMAGE_3D_STYLE_MIXED){
                    para->image_3d_info.total_frame = 2;
                    para->image_3d_info.frame[0] = info.offset1;
                    para->image_3d_info.frame[1] = info.offset2;
                }else if(info.style==IMAGE_3D_STYLE_MIXED){ //mixed
                    para->image_3d_info.total_frame = 1;
                    para->image_3d_info.frame[0] = _3DG_FILE_HEAD_LEN;
                }else if(info.style==IMAGE_3D_STYLE_LR){ // l & r
                    para->image_3d_info.total_frame = 1;
                    para->image_3d_info.frame[0] = _3DG_FILE_HEAD_LEN;
                }else if(info.style==IMAGE_3D_STYLE_TB){ // top & bottom
                    para->image_3d_info.total_frame = 1;
                    para->image_3d_info.frame[0] = _3DG_FILE_HEAD_LEN;
                }
            }
        }
    }
    if(para->image_3d_info.total_frame>=1)
        ret =1;
    fclose (fd);
    return ret;    
}


/* parse [3D][FULL] OR [3D][HALF] */
int parse_ThirdParty_3d_jpeg(aml_dec_para_t* para) {
	if(para->image_3d_mode_pref>2) {
		ALOGD("not initalized yet!\n");
		return 0;
	}
	char *jpegname = basename(para->fn);
	if(strcasestr(jpegname,"[3D][FULL]")!=NULL) {
		para->image_3d_info.type=2;
		para->image_3d_info.style=IMAGE_3D_STYLE_LR;
		para->image_3d_info.total_frame=1;
		para->image_3d_info.frame[0]=0;
		return 1;
	} else if (strcasestr(jpegname,"[3D][HALF]")!=NULL) {
		para->image_3d_info.type=2;	/* half height mode. */
		para->image_3d_info.style=IMAGE_3D_STYLE_H_HEIGHT;
		para->image_3d_info.total_frame=1;
		para->image_3d_info.frame[0]=0;
		return 1;
	}
	return 0;
}

int fh_3d_getinfo(aml_dec_para_t* para)
{
    int ret = 0;
    int read_num = 0;
    unsigned file_offset = 0;
    unsigned char find_flag = 0;
    unsigned char last_char = 0xe1;
    char* ext;
    
    /* test if dest is a file. */
    #if 0
	struct  stat  buf; 
	if(lstat(para->fn,&buf)<0||S_ISDIR(buf.st_mode))
		return 0;
	else
	if(para->fn[strlen(para->fn)-1]=='/') return 0;
	#endif
	
    
    ext=strrchr(para->fn,'.');  
    if(!ext&&(*ext)==0)
        return 0;
    if((strcasecmp(ext,".mpo") == 0)||(strcasecmp(ext,".3dp") == 0))
        ret = ParserNormal3DFile(para,ext);
    else if(strcasecmp(ext,".3dg") == 0)
	 ret = ParserThird3DFile(para);
	else if(strcasecmp(ext,".jpeg")==0||strcasecmp(ext,".jpg")==0) {
		ret = parse_ThirdParty_3d_jpeg(para);
	}
    return ret;    
}

/**
*
* function to load a picture to pbuff.
*
**/
int fh_3d_load(aml_dec_para_t* para , aml_image_info_t* image) {
	int ret=FH_ERROR_OTHER;
    aml_image_info_t* output_image=NULL;
    
    if(para->image_3d_info.type!=2) {
		ALOGD("wrong file format!\n");
		goto exit;
	}
    
    if(amljpeg_init()<0) {
		ret= FH_ERROR_HWDEC_FAIL;
		goto exit;
    }

	if(para->image_3d_info.style==IMAGE_3D_STYLE_MIXED) { /* mixed. */
		output_image = read_jpeg_image(para);
	} else if(para->image_3d_info.style==IMAGE_3D_STYLE_LR
				||para->image_3d_info.style==IMAGE_3D_STYLE_H_HEIGHT) {  /* l & r */
		output_image = read_lr_in_one_image(para);
	} else if(para->image_3d_info.style==IMAGE_3D_STYLE_TB) { /* top & bottom. */
		ALOGD("not initalized yet!\n");
		goto exit;
	} else if(para->image_3d_info.style==IMAGE_3D_STYLE_2FRAME) {  /* two jpeg stream mode. */
		output_image = read_2_frame_image(para);
	}
	if(!output_image) {
		ALOGD("decoding failed#1.\n");
		goto exit;
	}
	memcpy((char*)image , (char*)output_image, sizeof(aml_image_info_t)) ;
	ret= FH_ERROR_OK;
	
exit:  
	if(output_image) free(output_image);
    amljpeg_exit();  
    return ret;
}

int fh_3d_getsize(char *filename,int *x,int *y)
{
    return(FH_ERROR_OK);
}

