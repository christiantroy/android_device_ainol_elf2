#ifndef SCALER_H_
#define SCALER_H_
typedef enum {
    PIC_DEC_DIR_0 = 0,
    PIC_DEC_DIR_90,
    PIC_DEC_DIR_180,
    PIC_DEC_DIR_270
} pic_dir_t;


typedef struct {
    unsigned        pic_width;
    unsigned        pic_height;
    
	unsigned        frame_width;
	unsigned 		frame_height;

	
    unsigned        scaled_top;
    unsigned        scaled_left;
    unsigned        scaled_width;
    unsigned        scaled_height;
	int bpp;
	unsigned  char  mode;
	unsigned char*    output;        	/* output buffer */
    pic_dir_t       rotation;           /* picture output rotation */
	
} scaler_input_t;
#define INTI_FRAME_WIDTH 1280
#define INIT_FRAME_HEIGHT 720

#define INTI_SCREEN_WIDTH 4
#define INIT_SCREEN_HEIGHT 3
typedef struct simp_scaler_s {
    scaler_input_t  input;
    void (*pixel_write)(struct simp_scaler_s *scaler, unsigned char r,unsigned char g, 
                            unsigned char b,unsigned char a, int image_x, int image_y);
    unsigned short *map_x;
    unsigned short *map_y;
} simp_scaler_t;

int     simp_scaler_init(simp_scaler_t *scaler);
void    simp_scaler_output_pixel(   simp_scaler_t *scaler, unsigned char r, unsigned char g,
                                    unsigned char b, unsigned char a ,int image_x, int image_y);
void    simp_scaler_flush(simp_scaler_t *scaler);
void    simp_scaler_destroy(simp_scaler_t *scaler);
#endif
