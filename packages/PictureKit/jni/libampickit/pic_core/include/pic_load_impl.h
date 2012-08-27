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

/* decoder function of kinds of picture type. */
#ifndef _PIC_LOAD_IMPL_H__
#define _PIC_LOAD_IMPL_H__

int fh_3d_getinfo(aml_dec_para_t* para);
int fh_3d_load(aml_dec_para_t* para , aml_image_info_t* image);
int fh_3d_getsize(char *filename,int *x,int *y);

int fh_bmp_id(aml_dec_para_t* para);
int fh_bmp_load(aml_dec_para_t* para , aml_image_info_t* image);
int fh_bmp_getsize(char *name,int *x,int *y);

int fh_amljpeg_id(aml_dec_para_t* para);
int fh_amljpeg_load(aml_dec_para_t* para , aml_image_info_t* image);
int fh_amljpeg_getsize(char *name,int *x,int *y);

int fh_jpeg_id(aml_dec_para_t* para);
int fh_jpeg_load(aml_dec_para_t* para , aml_image_info_t* image);
int fh_jpeg_getsize(char *name,int *x,int *y);

int fh_png_id(aml_dec_para_t* para);
int fh_png_load(aml_dec_para_t* para , aml_image_info_t* image);
int fh_png_getsize(char *name,int *x,int *y);

int fh_gif_id(aml_dec_para_t* para);
int fh_gif_load(aml_dec_para_t* para , aml_image_info_t* image);
int fh_gif_getsize(char *name,int *x,int *y);

#define CONFIGURE_MAXIMUM_DECODER   50

typedef int (*fh_id_t)(aml_dec_para_t*);
typedef int (*fh_load_t)(aml_dec_para_t*  , aml_image_info_t*);
typedef int (*fh_getinfo_t)(char*,int*,int*);

typedef struct s_decoder_fun_t {
	char*			name;
	fh_id_t 		fd_id;
	fh_load_t		fh_load;
	fh_getinfo_t	fh_getsize;
}decoder_fun_t;

static decoder_fun_t decoder_funs[] = {
	{(char*)0,(fh_id_t)0,(fh_load_t)0,(fh_getinfo_t)0},
	
    {"3dpic",fh_3d_getinfo,fh_3d_load,fh_3d_getsize},
	
#ifdef USE_AMLJPEG
    {"jpeg",fh_amljpeg_id,fh_amljpeg_load,fh_amljpeg_getsize},
#endif
#ifdef USE_JPEG
	{"jpeg",fh_jpeg_id,fh_jpeg_load,fh_jpeg_getsize},
#endif

#ifdef USE_PNG
	{"png",fh_png_id,fh_png_load,fh_png_getsize},
#endif

#ifdef USE_BMP
	{"bmp",fh_bmp_id,fh_bmp_load,fh_bmp_getsize},
#endif

#ifdef USE_GIF
	{"gif",fh_gif_id,fh_gif_load,fh_gif_getsize},
#endif
};

static const configure_number_of_decoder =
  (sizeof(decoder_funs) / sizeof(decoder_fun_t)); 

#endif /* _PIC_LOAD_IMPL_H__ */

