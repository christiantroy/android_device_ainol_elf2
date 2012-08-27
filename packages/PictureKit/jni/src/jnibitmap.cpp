#include <string.h>
#include <jni.h>
#include <cutils/log.h>
#include <assert.h>
#include <android_runtime/AndroidRuntime.h>

#include "SkTemplates.h"
#include "SkUtils.h"
#include "SkImageDecoder.h"
#include "SkImageRef_ashmem.h"
#include "SkImageRef_GlobalPool.h"
#include "SkStream.h"
#include "SkPixelRef.h"
#include "GraphicsJNI.h"

#ifndef ANDROID
#include <version.h>
#endif

jobject create_java_bitmap(JNIEnv* env,char* addr,int width,int height,int color_mode) {
    if (width <= 0 || height <= 0) {
        doThrowIAE(env, "width and height must be > 0");
        return NULL;
    }

    SkBitmap bitmap;

    bitmap.setConfig(SkBitmap::kARGB_8888_Config, width, height);
    jbyteArray buff = GraphicsJNI::allocateJavaPixelRef(env, &bitmap, NULL);
    if (NULL == buff) {
        return NULL;
    }

	unsigned char* dst = (unsigned char*)bitmap.getAddr(0, 0);
	memcpy(dst,addr,width*height*4);
	
    return GraphicsJNI::createBitmap(env, new SkBitmap(bitmap), buff,true,
                                     NULL);
}