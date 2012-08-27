
ifeq ($(strip $(BOARD_USES_ALSA_AUDIO)),true)
ifeq ($(strip $(BUILD_WITH_ALSA_UTILS)),true)

LOCAL_PATH:= $(call my-dir)

#
# Build aplay command
#

include $(CLEAR_VARS)

LOCAL_CFLAGS := \
	-fPIC -D_POSIX_SOURCE \
	-DALSA_CONFIG_DIR=\"/system/usr/share/alsa\" \
	-DALSA_PLUGIN_DIR=\"/system/usr/lib/alsa-lib\" \
	-DALSA_DEVICE_DIRECTORY=\"/dev/snd/\"

LOCAL_C_INCLUDES:= \
	$(LOCAL_PATH)/include \
	$(LOCAL_PATH)/android \
	device/ainol/elf2/alsa-lib/include

LOCAL_SRC_FILES := \
	aplay/aplay.c

LOCAL_MODULE_TAGS := optional
LOCAL_MODULE := alsa_aplay

LOCAL_SHARED_LIBRARIES := \
	libasound \
	libc

include $(BUILD_EXECUTABLE)

#
# Build alsactl command
#

include $(CLEAR_VARS)

LOCAL_CFLAGS := \
	-fPIC -D_POSIX_SOURCE \
	-DALSA_CONFIG_DIR=\"/system/usr/share/alsa\" \
	-DALSA_PLUGIN_DIR=\"/system/usr/lib/alsa-lib\" \
	-DALSA_DEVICE_DIRECTORY=\"/dev/snd/\"

LOCAL_C_INCLUDES:= \
	$(LOCAL_PATH)/include \
	$(LOCAL_PATH)/android \
	device/ainol/elf2/alsa-lib/include

LOCAL_SRC_FILES := \
	alsactl/alsactl.c \
	alsactl/init_parse.c \
	alsactl/state.c \
	alsactl/utils.c

LOCAL_MODULE_TAGS := optional
LOCAL_MODULE := alsa_ctl

LOCAL_SHARED_LIBRARIES := \
	libasound \
	libc

include $(BUILD_EXECUTABLE)

#
# Build amixer command
#

include $(CLEAR_VARS)

LOCAL_CFLAGS := \
	-fPIC -D_POSIX_SOURCE \
	-DALSA_CONFIG_DIR=\"/system/usr/share/alsa\" \
	-DALSA_PLUGIN_DIR=\"/system/usr/lib/alsa-lib\" \
	-DALSA_DEVICE_DIRECTORY=\"/dev/snd/\"

LOCAL_C_INCLUDES:= \
	$(LOCAL_PATH)/include \
	$(LOCAL_PATH)/android \
	device/ainol/elf2/alsa-lib/include

LOCAL_SRC_FILES := \
	amixer/amixer.c

LOCAL_MODULE_TAGS := optional
LOCAL_MODULE := alsa_amixer

LOCAL_SHARED_LIBRARIES := \
	libasound \
	libc

include $(BUILD_EXECUTABLE)

include $(CLEAR_VARS)

ALSAINIT_DIR := $(TARGET_OUT)/usr/share/alsa/init
LOCAL_ALSAINIT_DIR := alsactl/init


include $(CLEAR_VARS)
LOCAL_MODULE := alsainit-00main
LOCAL_MODULE_STEM := 00main
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(ALSAINIT_DIR)
LOCAL_SRC_FILES := $(LOCAL_ALSAINIT_DIR)/00main
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := alsainit-default
LOCAL_MODULE_STEM := default
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(ALSAINIT_DIR)
LOCAL_SRC_FILES := $(LOCAL_ALSAINIT_DIR)/default
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := alsainit-hda
LOCAL_MODULE_STEM := hda
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(ALSAINIT_DIR)
LOCAL_SRC_FILES := $(LOCAL_ALSAINIT_DIR)/hda
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := alsainit-help
LOCAL_MODULE_STEM := help
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(ALSAINIT_DIR)
LOCAL_SRC_FILES := $(LOCAL_ALSAINIT_DIR)/help
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := alsainit-info
LOCAL_MODULE_STEM := info
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(ALSAINIT_DIR)
LOCAL_SRC_FILES := $(LOCAL_ALSAINIT_DIR)/info
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := alsainit-test
LOCAL_MODULE_STEM := test
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(ALSAINIT_DIR)
LOCAL_SRC_FILES := $(LOCAL_ALSAINIT_DIR)/test
include $(BUILD_PREBUILT)

endif
endif
