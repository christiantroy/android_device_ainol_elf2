# Copyright 2011 The AMLOGIC Android Open Source Project
# Author:  Frank Chen <Frank.Chen@amlogic.com>

LOCAL_PATH:= $(call my-dir)

#################################################################
# libubi shared library
#
include $(CLEAR_VARS)

LOCAL_PRELINK_MODULE := false

LOCAL_SRC_FILES := \
	./src/ubiutils-common.c \
	./src/libubi.c 

LOCAL_C_INCLUDES += \
	$(LOCAL_PATH)/include/ \
	$(LOCAL_PATH)/../include/ 

LOCAL_MODULE := libubi
LOCAL_MODULE_TAGS := optional
LOCAL_CFLAGS += -Wall

include $(BUILD_SHARED_LIBRARY)

#################################################################
# libubi static library
#
include $(CLEAR_VARS)
LOCAL_SRC_FILES := \
	./src/ubiutils-common.c \
	./src/libubi.c 

LOCAL_C_INCLUDES += \
	$(LOCAL_PATH)/include/ \
	$(LOCAL_PATH)/../include/ 

LOCAL_MODULE := libubi
LOCAL_MODULE_TAGS := optional
LOCAL_CFLAGS += -Wall

include $(BUILD_STATIC_LIBRARY)

#################################################################
# ubiattach
#
include $(CLEAR_VARS)

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_FORCE_STATIC_EXECUTABLE := true
endif

LOCAL_SRC_FILES := \
	./src/ubiattach.c 

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/include/ \
	$(LOCAL_PATH)/../include/
	
ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_STATIC_LIBRARIES := libubi libc libcutils
else
LOCAL_SHARED_LIBRARIES := libubi libc libcutils
endif

LOCAL_MODULE := ubiattach
LOCAL_MODULE_TAGS := eng
include $(BUILD_EXECUTABLE)

#################################################################
# ubidetach
#
include $(CLEAR_VARS)

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_FORCE_STATIC_EXECUTABLE := true
endif

LOCAL_SRC_FILES := \
	./src/ubidetach.c

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/include/ \
	$(LOCAL_PATH)/../include/
	
ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_STATIC_LIBRARIES := libubi libc libcutils
else
LOCAL_SHARED_LIBRARIES := libubi libc libcutils
endif

LOCAL_MODULE := ubidetach
LOCAL_MODULE_TAGS := eng
include $(BUILD_EXECUTABLE)

#################################################################
# ubiformat
#
include $(CLEAR_VARS)

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_FORCE_STATIC_EXECUTABLE := true
endif

LOCAL_SRC_FILES := \
	./src/ubiformat.c \
	./src/libscan.c \
	./src/libubigen.c \
	../lib/libmtd.c \
	../lib/libmtd_legacy.c \
	../lib/libcrc32.c \
	../lib/libfec.c

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/../include/ \
	$(KERNEL_HEADERS) \
	$(LOCAL_PATH)/include/

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_STATIC_LIBRARIES := libubi libc libcutils
else
LOCAL_SHARED_LIBRARIES := libubi libc libcutils
endif

LOCAL_MODULE := ubiformat
LOCAL_MODULE_TAGS := eng
include $(BUILD_EXECUTABLE)

#################################################################
# ubinfo
#
include $(CLEAR_VARS)

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_FORCE_STATIC_EXECUTABLE := true
endif

LOCAL_SRC_FILES := \
	./src/ubinfo.c 

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/include/ \
	$(LOCAL_PATH)/../include/

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_STATIC_LIBRARIES := libubi libc libcutils
else
LOCAL_SHARED_LIBRARIES := libubi libc libcutils
endif

LOCAL_MODULE := ubinfo
LOCAL_MODULE_TAGS := eng
include $(BUILD_EXECUTABLE)

#################################################################
# ubirename
#
include $(CLEAR_VARS)

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_FORCE_STATIC_EXECUTABLE := true
endif

LOCAL_SRC_FILES := \
	./src/ubirename.c

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/include/ \
	$(LOCAL_PATH)/../include/
	
ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_STATIC_LIBRARIES := libubi libc libcutils
else
LOCAL_SHARED_LIBRARIES := libubi libc libcutils
endif

LOCAL_MODULE := ubirename
LOCAL_MODULE_TAGS := eng
include $(BUILD_EXECUTABLE)
#################################################################
# ubiupdatevol
#
include $(CLEAR_VARS)

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_FORCE_STATIC_EXECUTABLE := true
endif

LOCAL_SRC_FILES := \
	./src/ubiupdatevol.c
	
LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/include/ \
	$(LOCAL_PATH)/../include/
	
ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_STATIC_LIBRARIES := libubi libc libcutils
else
LOCAL_SHARED_LIBRARIES := libubi libc libcutils
endif

LOCAL_MODULE := ubiupdatevol

LOCAL_MODULE_TAGS := eng
include $(BUILD_EXECUTABLE)

#################################################################
# ubimkvol
#
include $(CLEAR_VARS)

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_FORCE_STATIC_EXECUTABLE := true
endif

LOCAL_SRC_FILES := \
	./src/ubimkvol.c

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/include/ \
	$(LOCAL_PATH)/../include/

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_STATIC_LIBRARIES := libubi libc libcutils
else
LOCAL_SHARED_LIBRARIES := libubi libc libcutils
endif

LOCAL_MODULE := ubimkvol
LOCAL_MODULE_TAGS := eng
include $(BUILD_EXECUTABLE)

#################################################################
# ubirmvol
#
include $(CLEAR_VARS)

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_FORCE_STATIC_EXECUTABLE := true
endif

LOCAL_SRC_FILES := ./src/ubirmvol.c

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/include/ \
	$(LOCAL_PATH)/../include/

ifeq ($(BUILD_STATIC_TOOLS),true)
LOCAL_STATIC_LIBRARIES := libubi libc libcutils
else
LOCAL_SHARED_LIBRARIES := libubi libc libcutils
endif

LOCAL_MODULE := ubirmvol
LOCAL_MODULE_TAGS := eng
include $(BUILD_EXECUTABLE)
