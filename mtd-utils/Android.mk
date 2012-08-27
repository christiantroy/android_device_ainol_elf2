# Copyright 2011 The AMLOGIC Android Open Source Project
# Author:  Frank Chen <Frank.Chen@amlogic.com>

BUILD_STATIC_TOOLS := false

LOCAL_PATH:= $(call my-dir)

#################################################################
# flash_erase
#
include $(CLEAR_VARS)

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_FORCE_STATIC_EXECUTABLE := true
endif

LOCAL_SRC_FILES := \
	./flash_erase.c \
	./lib/libmtd.c \
	./lib/libmtd_legacy.c \
	./lib/libcrc32.c \
	./lib/libfec.c

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/include/ \
	$(KERNEL_HEADERS) \
	$(LOCAL_PATH)/android/

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_STATIC_LIBRARIES := libc libcutils
else
LOCAL_SHARED_LIBRARIES := libc libcutils
endif

LOCAL_MODULE := flash_erase
LOCAL_MODULE_TAGS := eng
include $(BUILD_EXECUTABLE)

#################################################################
include $(LOCAL_PATH)/ubi-utils/Android.mk
