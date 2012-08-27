/* config.h.  Generated from config.h.in by configure.  */
/* config.h.in.  Generated from configure.ac by autoheader.  */

/* default framebuffer */
/* #undef DEFAULT_FRAMEBUFFER */

/* Define to 1 if you have the <fcntl.h> header file. */
#define HAVE_FCNTL_H 1

/* Define to 1 if you have the <gif_lib.h> header file. */
/* #undef HAVE_GIF_LIB_H */

/* Define to 1 if you have the <GLES2/gl2.h> header file. */
/* #undef HAVE_GLES2_GL2_H */

/* Define to 1 if you have the <inttypes.h> header file. */
/* #undef HAVE_INTTYPES_H */

/* Define to 1 if you have the <jpeglib.h> header file. */
#define HAVE_JPEGLIB_H 1

/* Define to 1 if you have the <memory.h> header file. */
/* #undef HAVE_MEMORY_H */

/* Define to 1 if you have the <png.h> header file. */
#define HAVE_PNG_H 1

/* Define to 1 if you have the <stdint.h> header file. */
/* #undef HAVE_STDINT_H */

/* Define to 1 if you have the <stdlib.h> header file. */
#define HAVE_STDLIB_H 1

/* Define to 1 if you have the <strings.h> header file. */
/* #undef HAVE_STRINGS_H */

/* Define to 1 if you have the <string.h> header file. */
#define HAVE_STRING_H 1

/* Define to 1 if you have the <sys/ioctl.h> header file. */
#define HAVE_SYS_IOCTL_H 1

/* Define to 1 if you have the <sys/stat.h> header file. */
/* #undef HAVE_SYS_STAT_H */

/* Define to 1 if you have the <sys/time.h> header file. */
#define HAVE_SYS_TIME_H 1

/* Define to 1 if you have the <sys/types.h> header file. */
/* #undef HAVE_SYS_TYPES_H */

/* Define to 1 if you have the <termios.h> header file. */
#define HAVE_TERMIOS_H 1

/* Define to 1 if you have the <unistd.h> header file. */
#define HAVE_UNISTD_H 1

/* Name of package */
#define PACKAGE "ampicplayer"

/* Define to the address where bug reports for this package should be sent. */
#define PACKAGE_BUGREPORT "kasin.li@amlogic.com"

/* Define to the full name of this package. */
#define PACKAGE_NAME "ampicplayer"

/* Define to the full name and version of this package. */
#define PACKAGE_STRING "ampicplayer 0.9"

/* Define to the one symbol short name of this package. */
#define PACKAGE_TARNAME "ampicplayer"

/* Define to the home page for this package. */
#define PACKAGE_URL ""

/* Define to the version of this package. */
#define PACKAGE_VERSION "0.9"

/* Define to 1 if you have the ANSI C header files. */
/* #undef STDC_HEADERS */

/* Define if supporting amlogic jpeg */
#define USE_AMLJPEG 1

/* Define if supporting BMP */
#define USE_BMP 1

/* Define if supporting Framebuffer */
/* #undef USE_FB */

/* Define if supporting GIF */
/* #undef USE_GIF */

/* Define if supporting Framebuffer */
/* #undef USE_GLES */

/* Define if supporting jpeg */
#define USE_JPEG 1

/* Define if supporting png */
#define USE_PNG 1 

/* Version number of package */
#define VERSION "0.9"

/* runned in windows 32 mode */
/* #undef WIN32 */

/* Define to `__inline__' or `__inline' if that's what the C compiler
   calls it, or to nothing if 'inline' is not supported under any name.  */
#ifndef __cplusplus
/* #undef inline */
#ifndef _LINUX
#include <cutils/log.h>
#else
#define LOGD printf
#endif
#endif
