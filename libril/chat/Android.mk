# Copyright 2006 The Android Open Source Project

# XXX using libutils for simulator build only...
#
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= chat.c

LOCAL_SHARED_LIBRARIES := \
    libcutils libutils 

# for asprinf
LOCAL_CFLAGS := -D_GNU_SOURCE

LOCAL_CFLAGS += -pipe

LOCAL_C_INCLUDES := $(KERNEL_HEADERS)

ifeq ($(TARGET_DEVICE),sooner)
  LOCAL_CFLAGS += -DOMAP_CSMI_POWER_CONTROL -DUSE_TI_COMMANDS
endif

ifeq ($(TARGET_DEVICE),surf)
  LOCAL_CFLAGS += -DPOLL_CALL_STATE -DUSE_QMI
endif

ifeq ($(TARGET_DEVICE),dream)
  LOCAL_CFLAGS += -DPOLL_CALL_STATE -DUSE_QMI
endif

LOCAL_CFLAGS += -DTERMIOS -UNO_SLEEP -DFNDELAY=O_NDELAY 


#build shared library
LOCAL_PRELINK_MODULE := false
LOCAL_SHARED_LIBRARIES += \
    libcutils libutils
LOCAL_LDLIBS += -lpthread
LOCAL_MODULE:= chat
LOCAL_MODULE_TAGS := optional
include $(BUILD_EXECUTABLE)

