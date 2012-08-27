# Copyright (C) 2011 Amlogic
#
#

LOCAL_PATH := $(call my-dir)

# HAL module implemenation, not prelinked and stored in
# /system/lib/hw/hwcomposer.amlogic.so
include $(CLEAR_VARS)
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)/hw
LOCAL_SHARED_LIBRARIES := liblog libEGL libamavutils
LOCAL_SRC_FILES := hwcomposer.cpp

LOCAL_C_INCLUDES +=\
        device/ainol/elf2/packages/LibPlayer/amavutils/include \
	device/ainol/elf2/include

LOCAL_MODULE := hwcomposer.amlogic
LOCAL_CFLAGS:= -DLOG_TAG=\"hwcomposer\"
LOCAL_MODULE_TAGS := optional
include $(BUILD_SHARED_LIBRARY)
