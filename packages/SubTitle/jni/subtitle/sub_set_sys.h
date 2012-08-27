#ifndef _SUBTITLE_SET_DISP_H_
#define _SUBTITLE_SET_DISP_H_


int get_subtitle_enable();
int get_subtitle_num();
int get_subtitle_curr();
int set_subtitle_curr(int num);
int set_subtitle_enable(int num);
int set_subtitle_size(int num);
int get_subtitle_size();
int set_subtitle_data(int data);
int get_subtitle_data();
int get_subtitle_startpts();
int get_subtitle_fps();
int get_subtitle_subtype();

#endif

