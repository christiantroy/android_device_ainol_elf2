ifneq (foo,foo)

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE:= libusb
LOCAL_SRC_FILES := os/linux_usbfs.c \
	core.c \
	descriptor.c \
	io.c \
	sync.c

LOCAL_MODULE_TAGS := eng

LOCAL_C_INCLUDES += os libc/kernel/common/linux

LOCAL_SHARED_LIBRARIES += libc

LOCAL_CFLAGS += -DOS_LINUX

LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)

endif