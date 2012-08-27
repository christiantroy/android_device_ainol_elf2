LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
         $(call all-subdir-java-files)

LOCAL_PACKAGE_NAME := RemoteIME

LOCAL_REQUIRED_MODULES := libjni_remoteime

LOCAL_STATIC_JAVA_LIBRARIES := com.amlogic.inputmethod.remote.lib

LOCAL_CERTIFICATE := shared

# Make sure our dictionary file is not compressed, so we can read it with
# a raw file descriptor.
LOCAL_AAPT_FLAGS := -0 .dat

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
