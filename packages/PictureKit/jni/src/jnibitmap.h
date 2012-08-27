#ifndef _JNIBMP_INCLUDE__
#define _JNIBMP_INCLUDE__

extern jobject create_java_bitmap(JNIEnv* env,char* addr,int width,int height,int color_mode);
extern int register_android_graphics_Graphics(JNIEnv* env);
extern int unregister_android_graphics_Graphics(JNIEnv* env);
#endif /* _JNIBMP_INCLUDE__ */