/****************************************
 * file: sub_set_sys.c
 * description: set sys attr when
****************************************/
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <log_print.h>

int set_subtitle_enable(int enable)
{
    int fd;
    char *path = "/sys/class/subtitle/enable";    
	char  bcmd[16];
	fd=open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);
	if(fd>=0)
	{
    	sprintf(bcmd,"%d",enable);
    	write(fd,bcmd,strlen(bcmd));
    	close(fd);
    	return 0;
	}
	return -1;
    
}

int get_subtitle_enable()
{
    int fd;
	int subtitle_enable = 0;
    char *path = "/sys/class/subtitle/enable";    
	char  bcmd[16];
	fd=open(path, O_RDONLY);
	if(fd>=0)	{    	
    	read(fd,bcmd,sizeof(bcmd));       
        subtitle_enable = strtol(bcmd, NULL, 16);       
        subtitle_enable &= 0x1;
    	close(fd);    	
	}
	return subtitle_enable;   
}

int get_subtitle_num()
{
    int fd;
	int subtitle_num = 0;
    char *path = "/sys/class/subtitle/total";    
	char  bcmd[16];
	fd=open(path, O_RDONLY);
	if(fd>=0)	{    	
    	read(fd,bcmd,sizeof(bcmd)); 
		sscanf(bcmd, "%d", &subtitle_num);
    	close(fd);    	
	}
	return subtitle_num;   
}

int set_subtitle_curr(int curr)
{
    int fd;
    char *path = "/sys/class/subtitle/curr";    
	char  bcmd[16];
	fd=open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);
	if(fd>=0)
	{
    	sprintf(bcmd,"%d",curr);
    	write(fd,bcmd,strlen(bcmd));
    	close(fd);
    	return 0;
	}
	return -1;
    
}

int get_subtitle_curr()
{
    int fd;
	int subtitle_cur = 0;
    char *path = "/sys/class/subtitle/curr";    
	char  bcmd[16];
	fd=open(path, O_RDONLY);
	if(fd>=0)	{    	
    	read(fd,bcmd,sizeof(bcmd)); 
		sscanf(bcmd, "%d", &subtitle_cur);
    	close(fd);    	
	}
	return subtitle_cur;   
}

int set_subtitle_size(int size)
{
    int fd;
    char *path = "/sys/class/subtitle/size";    
	char  bcmd[16];
	fd=open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);
	if(fd>=0)
	{
    	sprintf(bcmd,"%d",size);
    	write(fd,bcmd,strlen(bcmd));
    	close(fd);
    	return 0;
	}
	return -1;
    
}

int get_subtitle_size()
{
    int fd;
	int subtitle_size = 0;
    char *path = "/sys/class/subtitle/size";    
	char  bcmd[16];
	fd=open(path, O_RDONLY);
	if(fd>=0)
	{    	
    	read(fd,bcmd,sizeof(bcmd));       
        subtitle_size = strtol(bcmd, NULL, 16);       
        //subtitle_size &= 0x1;
    	close(fd);    	
	}
	return subtitle_size;   
}

int set_subtitle_data(int data)
{
    int fd;
    char *path = "/sys/class/subtitle/data";    
	char  bcmd[16];
	fd=open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);
	if(fd>=0)
	{
    	sprintf(bcmd,"%d",data);
    	write(fd,bcmd,strlen(bcmd));
    	close(fd);
    	return 0;
	}
	return -1;
    
}

int get_subtitle_data()
{
    int fd;
	int subtitle_data = 0;
    char *path = "/sys/class/subtitle/data";    
	char  bcmd[16];
	fd=open(path, O_RDONLY);
	if(fd>=0)
	{    	
    	read(fd,bcmd,sizeof(bcmd));       
        subtitle_data = strtol(bcmd, NULL, 16);       
        //subtitle_cur &= 0x1;
    	close(fd);    	
	}
	return subtitle_data;   
}

int get_subtitle_startpts()
{
    int fd;
	int subtitle_pts = 0;
    char *path = "/sys/class/subtitle/startpts";    
	char  bcmd[16];
	fd=open(path, O_RDONLY);
	if(fd>=0)	{    	
    	read(fd,bcmd,sizeof(bcmd)); 
		sscanf(bcmd, "%d", &subtitle_pts);
    	close(fd);    	
	}
	return subtitle_pts;   
}

int get_subtitle_fps()
{
    int fd;
	int subtitle_fps = 0;
    char *path = "/sys/class/subtitle/fps";    
	char  bcmd[16];
	fd=open(path, O_RDONLY);
	if(fd>=0)	{    	
    	read(fd,bcmd,sizeof(bcmd)); 
		sscanf(bcmd, "%d", &subtitle_fps);
    	close(fd);    	
	}
	return subtitle_fps;   
}

int get_subtitle_subtype()
{
    int fd;
	int subtitle_subtype = 0;
    char *path = "/sys/class/subtitle/subtype";    
	char  bcmd[16];
	fd=open(path, O_RDONLY);
	if(fd>=0)	{    	
    	read(fd,bcmd,sizeof(bcmd)); 
		sscanf(bcmd, "%d", &subtitle_subtype);
    	close(fd);    	
	}
	return subtitle_subtype;   
}

