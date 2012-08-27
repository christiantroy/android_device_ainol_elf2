LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional 

LOCAL_SRC_FILES := $(call all-subdir-java-files) \
		src/org/geometerplus/android/fbreader/network/BookDownloaderInterface.aidl \
		src/com/amlogic/pmt/IGLMusicService.aidl \

#LOCAL_JAVA_LIBRARIES := AmlogicMenu amlogicCoocaa amlpictureKit

LOCAL_PACKAGE_NAME := MusicPlayer
LOCAL_CERTIFICATE := platform

LOCAL_STATIC_JAVA_LIBRARIES := amlpictureKit

#LOCAL_JNI_SHARED_LIBRARIES := libDeflatingDecompressor libLineBreak

include $(BUILD_PACKAGE)

include $(LOCAL_PATH)/jni/Android.mk
