LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_MODULE                  := libDeflatingDecompressor
LOCAL_SRC_FILES               := DeflatingDecompressor/DeflatingDecompressor.cpp
LOCAL_SHARED_LIBRARIES := \
        libz

LOCAL_C_INCLUDES +=  \
	$(JNI_H_INCLUDE) \
	 external/zlib
LOCAL_PRELINK_MODULE := false
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_MODULE                  := libLineBreak
LOCAL_SRC_FILES               := LineBreak/LineBreaker.cpp LineBreak/liblinebreak-2.0/linebreak.c LineBreak/liblinebreak-2.0/linebreakdata.c LineBreak/liblinebreak-2.0/linebreakdef.c
LOCAL_C_INCLUDES +=  \
	$(JNI_H_INCLUDE) \
	 external/zlib

LOCAL_PRELINK_MODULE := false
include $(BUILD_SHARED_LIBRARY)
