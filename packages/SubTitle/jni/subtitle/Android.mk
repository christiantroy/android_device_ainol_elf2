LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libsubjni
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := sub_jni.c sub_api.c log_print.c sub_subtitle.c sub_vob_sub.c sub_set_sys.c vob_sub.c sub_pgs_sub.c sub_control.c avi_sub.c
LOCAL_ARM_MODE := arm
LOCAL_C_INCLUDES := $(JNI_H_INCLUDE) 

LOCAL_SHARED_LIBRARIES += libutils libmedia

LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)
