LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
         $(call all-subdir-java-files) \
         com/amlogic/inputmethod/remote/IPinyinDecoderService.aidl

LOCAL_MODULE := com.amlogic.inputmethod.remote.lib

include $(BUILD_STATIC_JAVA_LIBRARY)
