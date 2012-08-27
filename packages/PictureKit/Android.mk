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

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := src/com/amlogic/graphics/PictureKit.java \
				src/com/amlogic/graphics/DecoderInfo.java \
				src/com/amlogic/graphics/ImageInfo.java

LOCAL_MODULE := amlpictureKit

LOCAL_SDK_VERSION := current

LOCAL_JNI_SHARED_LIBRARIES := libamlpicjni

#LOCAL_PROGUARD_FLAGS := -include $(LOCAL_PATH)/proguard.flags

LOCAL_CERTIFICATE := platform
#include $(BUILD_PACKAGE)
#include $(BUILD_JAVA_LIBRARY)
include $(BUILD_STATIC_JAVA_LIBRARY)
##################################################

#include $(CLEAR_VARS)
#LOCAL_MODULE_TAGS := optional
#LOCAL_SRC_FILES := src/com/amlogic/picplayer/PicPlayer.java

#LOCAL_SDK_VERSION := current

#LOCAL_STATIC_JAVA_LIBRARIES := amlpictureKit

#LOCAL_PACKAGE_NAME := amlpicplayer

#LOCAL_CERTIFICATE := platform
#LOCAL_PROGUARD_FLAGS := -include $(LOCAL_PATH)/proguard.flags

#include $(BUILD_PACKAGE)
###################################################

include $(call all-makefiles-under,$(LOCAL_PATH))
