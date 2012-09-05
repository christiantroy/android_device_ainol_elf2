LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE:= libusb
LOCAL_SRC_FILES := linux.c \
	error.c \
	usb.c \
	descriptors.c 

LOCAL_MODULE_TAGS := optional

LOCAL_C_INCLUDES += ./

LOCAL_SHARED_LIBRARIES += libc

LOCAL_CFLAGS += -DOS_LINUX

LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)


