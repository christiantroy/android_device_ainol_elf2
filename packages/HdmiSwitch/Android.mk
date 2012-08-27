ifeq ($(BOARD_USE_DEFAULT_HDMISWITCH),true)
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_PACKAGE_NAME := HdmiSwitch
LOCAL_CERTIFICATE := platform

LOCAL_REQUIRED_MODULES := libhdmiswitchjni
include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
endif
