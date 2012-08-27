LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files) \
	src/org/geometerplus/android/fbreader/network/BookDownloaderInterface.aidl \

#LOCAL_JAVA_LIBRARIES := SkyworthMenu skyworthCoocaa amlpictureKit

LOCAL_PACKAGE_NAME := PicturePlayer
LOCAL_CERTIFICATE := platform

LOCAL_STATIC_JAVA_LIBRARIES := amlpictureKit
LOCAL_REQUIRED_MODULES := libamlpicjni
#LOCAL_JNI_SHARED_LIBRARIES := libDeflatingDecompressor libLineBreak
LOCAL_PROGUARD_FLAGS := -include $(LOCAL_PATH)/proguard.flags

include $(BUILD_PACKAGE)

