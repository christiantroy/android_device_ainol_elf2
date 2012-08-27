LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PRELINK_MODULE:= false
# measurements show that the ARM version of ZLib is about x1.17 faster
# than the thumb one...

LOCAL_ARM_MODE := arm
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := amljpeg.c
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../libcmem $(LOCAL_PATH)/libaml
LOCAL_STATIC_LIBRARIES :=libcmem libcutils

LOCAL_COPY_HEADERS_TO := libamljpg
LOCAL_COPY_HEADERS := amljpeg.h

LOCAL_MODULE := libamljpg
include $(BUILD_SHARED_LIBRARY)

#=============================================================
include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := example.c amljpeg.c
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../libcmem $(LOCAL_PATH)/../libamljpg/libaml
LOCAL_STATIC_LIBRARIES :=libcmem libcutils
LOCAL_SHARED_LIBRARIES := libamljpg

LOCAL_CFLAGS :=-g
LOCAL_CPPFLAGS := -g

LOCAL_MODULE := amljpgexp
include $(BUILD_EXECUTABLE)
