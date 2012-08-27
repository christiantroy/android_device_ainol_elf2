/*! \file pic_app.h
\brief APIs to process pictures in C/C++.
APIs of C/C++ to process pictures.
*/
/*! \mainpage amlpickit main page.

APIs in C/C++ for picture process. 
<br>Amlogic sh.

*/
/*
    ampicplayer

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

#ifndef __PIC_APP__H__
#define __PIC_APP__H__

#ifdef __cplusplus
extern "C" {
#endif

#ifndef _LINUX
#include <cutils/log.h>
#else
#define LOGD printf
#endif


#include <aml_common.h>

/*! \def FH_ERROR_OK
 * \brief decoder return successfully. */
#define FH_ERROR_OK 0

/*! \def FH_ERROR_FILE
 * \brief read/access error. */
#define FH_ERROR_FILE 1

/*! \def FH_ERROR_FORMAT
 * \brief this type of picture is not currect or supported by decoder. */ 
#define FH_ERROR_FORMAT 2

/*! \def FH_ERROR_OTHER
 * \brief other error. */ 
#define FH_ERROR_OTHER 3

/*! \def FH_ERROR_HWDEC_FAIL
 * \brief error in calling amlogic hw jpeg decoder. */ 
#define FH_ERROR_HWDEC_FAIL 4

/*! \def FH_ERROR_BAD_INFO
 * \brief error in getting picture infomation. */ 
#define FH_ERROR_BAD_INFO 5

/*! \def FH_ERROR_MEM_FAIL
 * \brief memory error. */ 
#define FH_ERROR_MEM_FAIL 6

typedef struct s_image_t
{
	int width, height;
	unsigned char *rgb;
	unsigned char *alpha;
	int do_free;
	char* fn;
	
	unsigned pic_type;		/* pointer to load function. */
}image_t;

/*!
 * \fn int get_pic_info(aml_dec_para_t* para).
 *  git the information of pictures.
 * @param para	Object to set or return information of pictures
 * @return		-1 will be returned if failed.
 * @see aml_dec_para_t
 */ 
extern int get_pic_info(aml_dec_para_t* para);

/*!
 * \fn int load_pic(aml_dec_para_t* para , aml_image_info_t* image).
 *  decoding Pictures.
 * @param para	Object to set or return information of pictures.
 * @param image	Objects if decoded pictures returned by decoder. 
 * @return		-1 will be returned if failed.
 * @see aml_dec_para_t.
 * @see aml_image_info_t.
 */ 
extern int load_pic(aml_dec_para_t* para , aml_image_info_t* image);
extern int show_pic(aml_image_info_t* image,int flag);

#ifdef __cplusplus
}
#endif

#endif /* __PIC_APP__H__ */
