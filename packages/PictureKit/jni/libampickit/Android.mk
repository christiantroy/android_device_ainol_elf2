LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PRELINK_MODULE:= false
# measurements show that the ARM version of ZLib is about x1.17 faster
# than the thumb one...

LOCAL_ARM_MODE := arm
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
	pic_core/dec/amljpeg.c 	\
	pic_core/dec/libamljpg/amljpeg.c \
	pic_core/dec/jpeg.c 	\
	pic_core/dec/bmp.c \
	pic_core/dec/png.c \
	pic_core/dec/3dpic.c \
	pic_core/pixutil/transforms.c \
	pic_core/pixutil/3d_op.c \
	pic_core/vo/vo.c \
	pic_core/pic_app.c \
	pic_core/dec/scaler.c 
	
LOCAL_C_INCLUDES += \
	$(LOCAL_PATH)/include \
	$(LOCAL_PATH)/pic_core/include \
	$(LOCAL_PATH)/pic_core/include/libaml \
	$(LOCAL_PATH)/pic_core/dec/libamljpg \
	external/jpeg \
	external/libpng \
	external/zlib \
	
LOCAL_SHARED_LIBRARIES :=libjpeg libz
LOCAL_STATIC_LIBRARIES :=libcutils libpng 

LOCAL_COPY_HEADERS_TO := libpic_core
LOCAL_COPY_HEADERS := include/pic_app.h include/pic_uop.h

LOCAL_MODULE := libpickit
include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_STATIC_LIBRARY)
#=============================================================
#include $(CLEAR_VARS)

#LOCAL_ARM_MODE := arm
#LOCAL_MODULE_TAGS := optional

#LOCAL_SRC_FILES := player/test.c
#LOCAL_C_INCLUDES += \
#	include \
#	pic_core/include \
#	$(LOCAL_PATH)/../libamljpg/libaml \
#	$(LOCAL_PATH)/../libamljpg \
#	external/jpeg
#	
#LOCAL_SHARED_LIBRARIES :=libpickit

#LOCAL_MODULE := amltstply
#include $(BUILD_EXECUTABLE)
