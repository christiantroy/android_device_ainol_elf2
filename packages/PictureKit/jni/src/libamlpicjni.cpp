/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/* JNI code for calling of amlpickit.
 */
#include "com_amlogic_graphics_PictureKit.h" 
#include <string.h>
#include <cutils/log.h>
#include <assert.h>
#include <android_runtime/AndroidRuntime.h>

#ifndef ANDROID
#include <version.h>
#endif

#include <pic_app.h>
#include <pic_uop.h>

#include <dlfcn.h>
#include "gjavaField.h"
#include "jnibitmap.h"

static bool hasException(JNIEnv *env) {
    if (env->ExceptionCheck() != 0) {
        LOGE("*** Uncaught exception returned from Java call!\n");
        env->ExceptionDescribe();
        return true;
    }
    return false;
}

JNIEXPORT jstring JNICALL Java_com_amlogic_graphics_PictureKit_PictureKitVersion
  (JNIEnv * env, jclass thiz)
{
    return env->NewStringUTF("amlogic JNI 1.0!");
}

JNIEXPORT jobject JNICALL Java_com_amlogic_graphics_PictureKit_getPictureInfoNative
  (JNIEnv* env, jclass thiz, jstring filename) {
	const char* str=env->GetStringUTFChars(filename,NULL);
	aml_dec_para_t dec_info;
	jobject obj;
	
	dec_info.fn=(char*)str;
	if(get_pic_info(&dec_info)==FH_ERROR_OK) {
		obj = env->AllocObject(gdecoderInfo_class);
		if(obj) {
			env->SetIntField(obj, gdecoderInfo_imageWidthID,dec_info.iwidth);
			env->SetIntField(obj, gdecoderInfo_imageHeightID,dec_info.iheight);
			env->SetIntField(obj, gdecoderInfo_widthToDecoderID,0);
			env->SetIntField(obj, gdecoderInfo_HeightToDecoderID,0);
			env->SetIntField(obj, gdecoderInfo_colorModeID,0);
			env->SetIntField(obj, gdecoderInfo_pictureTypeID,dec_info.pic_type);
			if (hasException(env)) {
				obj = NULL;
			}	
		}
	}
	env->ReleaseStringUTFChars(filename,str);
	return obj;
}

JNIEXPORT jobject JNICALL Java_com_amlogic_graphics_PictureKit_loadPictureNative
  (JNIEnv* env, jclass thiz, jstring filename, jobject dec_obj) {
		
	aml_dec_para_t dec_info = {0};
	aml_image_info_t image_info = {0};
	const char* str=env->GetStringUTFChars(filename,NULL);
	jobject obj;
	dec_info.fn=(char*)str;
	dec_info.iwidth  = env->GetIntField(dec_obj, gdecoderInfo_imageWidthID);
	dec_info.iheight = env->GetIntField(dec_obj, gdecoderInfo_imageHeightID);
	dec_info.width   = env->GetIntField(dec_obj, gdecoderInfo_widthToDecoderID);
	dec_info.height  = env->GetIntField(dec_obj, gdecoderInfo_HeightToDecoderID);
	dec_info.mode 	 = env->GetIntField(dec_obj, gdecoderInfo_decodeModeID);
	dec_info.thumbpref = env->GetIntField(dec_obj,gdecoderInfo_thumbPreferID);
	dec_info.pic_type= env->GetIntField(dec_obj, gdecoderInfo_pictureTypeID);
	dec_info.colormode = COLOR_S32_ABGR;
	
	if(get_pic_info(&dec_info)==FH_ERROR_OK) {
		env->SetIntField(dec_obj, gdecoderInfo_imageWidthID,dec_info.iwidth);
		env->SetIntField(dec_obj, gdecoderInfo_imageHeightID,dec_info.iheight);
		env->SetIntField(dec_obj, gdecoderInfo_widthToDecoderID,0);
		env->SetIntField(dec_obj, gdecoderInfo_HeightToDecoderID,0);
		env->SetIntField(dec_obj, gdecoderInfo_colorModeID,0);
		env->SetIntField(dec_obj, gdecoderInfo_pictureTypeID,dec_info.pic_type);
		if(load_pic(&dec_info,&image_info)!=FH_ERROR_OK)  {
			obj=NULL;
		} else {
			obj = env->AllocObject(gImageInfo_class);
			if(obj) {
				env->SetIntField(obj, gdecoderInfo_originImageWidthID,dec_info.iwidth);
				env->SetIntField(obj, gdecoderInfo_originImageHeightID,dec_info.iheight);
				env->SetIntField(obj, gdecoderInfo_outImageWidthID,image_info.width);
				env->SetIntField(obj, gdecoderInfo_outImageHeightID,image_info.height);
				if (hasException(env)) {
					obj = NULL;
				}	
			}
		}
	}
	env->ReleaseStringUTFChars(filename,str);
	return obj;
}

JNIEXPORT jobject JNICALL Java_com_amlogic_graphics_PictureKit_loadPicture2BmNative
  (JNIEnv* env , jclass thiz, jstring filename, jobject dec_obj) {	
	aml_dec_para_t dec_info;
	aml_image_info_t image_info;
	const char* str=env->GetStringUTFChars(filename,NULL);
	memset(&dec_info,0,sizeof(aml_dec_para_t));
	memset(&image_info,0,sizeof(aml_image_info_t));
	jobject obj=NULL;
	dec_info.fn=(char*)str;
	dec_info.iwidth  = env->GetIntField(dec_obj, gdecoderInfo_imageWidthID);
	dec_info.iheight = env->GetIntField(dec_obj, gdecoderInfo_imageHeightID);
	dec_info.width   = env->GetIntField(dec_obj, gdecoderInfo_widthToDecoderID);
	dec_info.height  = env->GetIntField(dec_obj, gdecoderInfo_HeightToDecoderID);
	if(dec_info.width<=0) dec_info.width=1280;
	if(dec_info.height<=0) dec_info.height=720;
	dec_info.mode 	 = env->GetIntField(dec_obj, gdecoderInfo_decodeModeID);
	
	dec_info.thumbpref = env->GetIntField(dec_obj,gdecoderInfo_thumbPreferID);
	dec_info.pic_type= env->GetIntField(dec_obj, gdecoderInfo_pictureTypeID);
	dec_info.colormode = COLOR_S32_ABGR;
	dec_info.image_3d_mode_pref = env->GetIntField(dec_obj, gdecoderInfo_3DPrefID);
	dec_info.image_3d_lr_offset = env->GetIntField(dec_obj, gdecoderInfo_3DParam1ID);
	dec_info.image_3d_preserv = env->GetIntField(dec_obj, gdecoderInfo_3DParam2ID);
	/*LOGD("%d===%d==%d=======\n",dec_info.image_3d_mode_pref,
			dec_info.image_3d_info.l_or_r_first,
			dec_info.image_3d_info.image_offset);*/
	
	if(get_pic_info(&dec_info)==FH_ERROR_OK) {
		env->SetIntField(dec_obj, gdecoderInfo_imageWidthID,dec_info.iwidth);
		env->SetIntField(dec_obj, gdecoderInfo_imageHeightID,dec_info.iheight);
		env->SetIntField(dec_obj, gdecoderInfo_widthToDecoderID,0);
		env->SetIntField(dec_obj, gdecoderInfo_HeightToDecoderID,0);
		env->SetIntField(dec_obj, gdecoderInfo_colorModeID,0);
		env->SetIntField(dec_obj, gdecoderInfo_pictureTypeID,dec_info.pic_type);
		if(load_pic(&dec_info,&image_info)!=FH_ERROR_OK)  obj=NULL; 
		else if(image_info.width<=0||image_info.height<=0) obj=NULL;
		else obj = create_java_bitmap(env,image_info.data,image_info.width,image_info.height,32);
		if(image_info.data) free(image_info.data);
	}
	env->ReleaseStringUTFChars(filename,str);
	return obj;
}

static jclass make_globalref(JNIEnv* env, const char classname[])
{
    jclass c = env->FindClass(classname);
    assert(c);
    return (jclass)env->NewGlobalRef(c);
}

static jfieldID getFieldIDCheck(JNIEnv* env, jclass clazz,
                                const char fieldname[], const char type[])
{
    jfieldID id = env->GetFieldID(clazz, fieldname, type);
    assert(id);
    return id;
}

jclass   gBitmap_class;
jfieldID gBitmap_nativeInstanceID;
jmethodID gBitmap_constructorMethodID;
jmethodID gBitmap_allocBufferMethodID;

int register_amlogic_graphics(JNIEnv* env)
{
    jmethodID m;
    jclass c;

#if 0
    gBitmap_class = make_globalref(env, "android/graphics/Bitmap");
    gBitmap_nativeInstanceID = getFieldIDCheck(env, gBitmap_class, "mNativeBitmap", "I");    
    gBitmap_constructorMethodID = env->GetMethodID(gBitmap_class, "<init>",
                                            "(IZ[BI)V");
    gBitmapConfig_class = make_globalref(env, "android/graphics/Bitmap$Config");
    gBitmapConfig_nativeInstanceID = getFieldIDCheck(env, gBitmapConfig_class,
                                                     "nativeInt", "I");       
#endif

	gdecoderInfo_class 				= make_globalref(env, "com/amlogic/graphics/DecoderInfo");
    gdecoderInfo_imageWidthID 		= getFieldIDCheck(env, gdecoderInfo_class, "imageWidth", "I");
    gdecoderInfo_imageHeightID 		= getFieldIDCheck(env, gdecoderInfo_class, "imageHeight", "I");
    gdecoderInfo_widthToDecoderID 	= getFieldIDCheck(env, gdecoderInfo_class, "widthToDecoder", "I");
    gdecoderInfo_HeightToDecoderID 	= getFieldIDCheck(env, gdecoderInfo_class, "heightToDecoder", "I");
    gdecoderInfo_colorModeID 		= getFieldIDCheck(env, gdecoderInfo_class, "colorMode", "I");
	gdecoderInfo_decodeModeID 		= getFieldIDCheck(env, gdecoderInfo_class, "decodemode", "I");
    gdecoderInfo_pictureTypeID 		= getFieldIDCheck(env, gdecoderInfo_class, "pictureType", "I");
    gdecoderInfo_thumbPreferID      = getFieldIDCheck(env, gdecoderInfo_class, "thumbprefered", "I");
    gdecoderInfo_3DPrefID	 		= getFieldIDCheck(env, gdecoderInfo_class, "image3DPref", "I");
    gdecoderInfo_3DParam1ID 		= getFieldIDCheck(env, gdecoderInfo_class, "image3DParam1", "I");
    gdecoderInfo_3DParam2ID 		= getFieldIDCheck(env, gdecoderInfo_class, "image3DParam2", "I");
                                                                               
	gImageInfo_class 				= make_globalref(env, "com/amlogic/graphics/ImageInfo");
    gdecoderInfo_originImageWidthID = getFieldIDCheck(env, gImageInfo_class, "originImageWidth", "I");
    gdecoderInfo_originImageHeightID= getFieldIDCheck(env, gImageInfo_class, "originImageHeight", "I");
    gdecoderInfo_outImageWidthID 	= getFieldIDCheck(env, gImageInfo_class, "outImageWidth", "I");
    gdecoderInfo_outImageHeightID 	= getFieldIDCheck(env, gImageInfo_class, "outImageHeight", "I");
	
	// Get the VMRuntime class.
    c = env->FindClass("dalvik/system/VMRuntime");
    assert(c);
    // Look up VMRuntime.getRuntime().
    m = env->GetStaticMethodID(c, "getRuntime", "()Ldalvik/system/VMRuntime;");
    assert(m);
    // Call VMRuntime.getRuntime() and hold onto its result.
    gVMRuntime_singleton = env->CallStaticObjectMethod(c, m);
    assert(gVMRuntime_singleton);
    gVMRuntime_singleton = (jobject)env->NewGlobalRef(gVMRuntime_singleton);
    // Look up the VMRuntime methods we'll be using.
    gVMRuntime_trackExternalAllocationMethodID =
                        env->GetMethodID(c, "trackExternalAllocation", "(J)Z");
    gVMRuntime_trackExternalFreeMethodID =
                            env->GetMethodID(c, "trackExternalFree", "(J)V");
    return 0;
}

int unregister_amlogic_graphics(JNIEnv* env)
{
    jmethodID m;
    jclass c;

#if 0
    gBitmap_class = make_globalref(env, "android/graphics/Bitmap");
    gBitmapConfig_class = make_globalref(env, "android/graphics/Bitmap$Config");   
	env->DeleteGlobalRef(gBitmap_class);
	gBitmap_class=NULL;
#endif
	env->DeleteGlobalRef(gdecoderInfo_class);
	gdecoderInfo_class=NULL;
	env->DeleteGlobalRef(gImageInfo_class);
	gImageInfo_class=NULL;
	env->DeleteGlobalRef(gVMRuntime_singleton);
	gVMRuntime_singleton=NULL;
    return 0;
}

static JavaVM *gJavaVM ;

static JNINativeMethod gMethods[] = { 
    /* name, signature, funcPtr */
    { "PictureKitVersion", "()Ljava/lang/String;",
            (void*) Java_com_amlogic_graphics_PictureKit_PictureKitVersion},
    { "getPictureInfoNative", "(Ljava/lang/String;)Lcom/amlogic/graphics/DecoderInfo;",
            (void*) Java_com_amlogic_graphics_PictureKit_getPictureInfoNative }, 
    { "loadPictureNative", "(Ljava/lang/String;Lcom/amlogic/graphics/DecoderInfo;)Lcom/amlogic/graphics/ImageInfo;",
            (void*) Java_com_amlogic_graphics_PictureKit_loadPictureNative },
    { "loadPicture2BmNative", "(Ljava/lang/String;Lcom/amlogic/graphics/DecoderInfo;)Landroid/graphics/Bitmap;",
            (void*) Java_com_amlogic_graphics_PictureKit_loadPicture2BmNative }
};

/* Do not modify the follow codes, if need not to. */
//JNIHelp.h ????
#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

static int registerNativeMethods(JNIEnv* env, const char* className,
                                 const JNINativeMethod* methods, int numMethods)
{
    int rc;
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class '%s'\n", className);
        return -1;
    }
	rc = env->RegisterNatives(clazz, methods, numMethods);
    if (rc==JNI_ERR) {
        LOGE("RegisterNatives failed for '%s' %d\n", className, rc);
        return -1;
    }
    return 0;
}

JNIEXPORT jint
JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jclass * localClass;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("GetEnv failed!\n");
        return -1;
    }
 
    if (android::AndroidRuntime::registerNativeMethods(env, this_class_name,
                              gMethods, NELEM(gMethods)) < 0) {
		LOGE("register Native failed\n");
        return -1;
	}
	
    gJavaVM = vm;	
	register_amlogic_graphics(env);
	register_android_graphics_Graphics(env);
    return JNI_VERSION_1_4;
}

JNIEXPORT void
JNI_OnUnload(JavaVM* vm, void* reserved)
{
	JNIEnv* env = NULL;
    jclass clazz;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("GetEnv failed!\n");
        return;
    }
    clazz = env->FindClass(this_class_name);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class '%s'\n", this_class_name);
        return ;
    }
	env->UnregisterNatives(clazz);
	unregister_amlogic_graphics(env);
	unregister_android_graphics_Graphics(env);
}
