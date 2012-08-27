# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
ifneq ($(IGNOR_COMMON_AMLOGIC_PICKIT),true)

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PRELINK_MODULE:= false

LOCAL_MODULE    := libamlpicjni
LOCAL_SRC_FILES := src/libamlpicjni.cpp src/jnibitmap.cpp src/Graphics.cpp

LOCAL_ARM_MODE := arm
LOCAL_MODULE_TAGS := optional

LOCAL_C_INCLUDES := $(JNI_H_INCLUDE) \
					$(LOCAL_PATH)/libampickit/include \
					$(LOCAL_PATH)/libampickit/pic_core/dec/libamljpg \
					$(LOCAL_PATH)/libampickit/pic_core/include/libaml \
					external/skia/include/core \
					external/skia/include/effects \
					external/skia/include/images \
					external/skia/src/ports \
					external/skia/include/utils \

LOCAL_SHARED_LIBRARIES := libpickit  libskia  libandroid_runtime libnativehelper
					
LOCAL_STATIC_LIBRARIES := libcutils  

include $(BUILD_SHARED_LIBRARY)
###################################################
include $(call all-makefiles-under,$(LOCAL_PATH))

endif
