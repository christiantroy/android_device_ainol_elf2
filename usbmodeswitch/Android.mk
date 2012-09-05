# Copyright (C) 2011 Amlogic Inc.
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

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES:=  \
		usb_modeswitch.c

LOCAL_MODULE:= usb_modeswitch

LOCAL_MODULE_TAGS := optional

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../libusb/libusb-0.1.12/

LOCAL_CFLAGS := 

LOCAL_SHARED_LIBRARIES := \
	libusb   

include $(BUILD_EXECUTABLE)

##############################################################
include $(CLEAR_VARS)

copy_from :=                  \
    0421_060c                 \
    0421_0610                 \
    0421_0622                 \
    0421_0627                 \
    0471_1210_uMa=Philips     \
    0471_1210_uMa=Wisue       \
    0471_1237                 \
    0482_024d                 \
    04e8_689a                 \
    04e8_f000                 \
    057c_84ff                 \
    05c6_1000_sVe=Option      \
    05c6_1000_uMa=AnyDATA     \
    05c6_1000_uMa=Option      \
    05c6_1000_uMa=SAMSUNG     \
    05c6_1000_uMa=SSE         \
    05c6_1000_uMa=Vertex      \
    05c6_2000                 \
    05c6_2001                 \
    05c6_f000                 \
    05c7_1000                 \
    072f_100d                 \
    07d1_a800                 \
    0930_0d46                 \
    0ace_2011                 \
    0ace_20ff                 \
    0af0_6711                 \
    0af0_6731                 \
    0af0_6751                 \
    0af0_6771                 \
    0af0_6791                 \
    0af0_6811                 \
    0af0_6911                 \
    0af0_6951                 \
    0af0_6971                 \
    0af0_7011                 \
    0af0_7031                 \
    0af0_7051                 \
    0af0_7071                 \
    0af0_7111                 \
    0af0_7211                 \
    0af0_7251                 \
    0af0_7271                 \
    0af0_7301                 \
    0af0_7311                 \
    0af0_7361                 \
    0af0_7381                 \
    0af0_7401                 \
    0af0_7501                 \
    0af0_7601                 \
    0af0_7701                 \
    0af0_7801                 \
    0af0_7901                 \
    0af0_8200                 \
    0af0_8201                 \
    0af0_8300                 \
    0af0_8302                 \
    0af0_8304                 \
    0af0_8400                 \
    0af0_c031                 \
    0af0_c100                 \
    0af0_d013                 \
    0af0_d031                 \
    0af0_d033                 \
    0af0_d035                 \
    0af0_d055                 \
    0af0_d057                 \
    0af0_d058                 \
    0af0_d155                 \
    0af0_d157                 \
    0af0_d255                 \
    0af0_d257                 \
    0af0_d357                 \
    0b3c_c700                 \
    0b3c_f000                 \
    0cf3_20ff                 \
    0d46_45a1                 \
    0d46_45a5                 \
    0e8d_7109                 \
    0fce_d0cf                 \
    0fce_d0e1                 \
    0fce_d103                 \
    0fd1_1000                 \
    1004_1000                 \
    1004_607f                 \
    1004_613a                 \
    1004_613f                 \
    1004_6190                 \
    1033_0035                 \
    106c_3b03                 \
    106c_3b05                 \
    106c_3b06                 \
    1076_7f40                 \
    1199_0fff                 \
    1266_1000                 \
    12d1_1001                 \
    12d1_1003                 \
    12d1_1009                 \
    12d1_101e                 \
    12d1_1031                 \
    12d1_1414                 \
    12d1_1446                 \
    12d1_1449                 \
    12d1_14ad                 \
    12d1_14c1                 \
    12d1_1520                 \
    12d1_1521                 \
    12d1_1523                 \
    12d1_1553                 \
    12d1_1557                 \
    12d1_1c0b                 \
    12d1_1da1                 \
    12d1_380b                 \
    1410_5010                 \
    1410_5020                 \
    1410_5030                 \
    1410_5031                 \
    1410_5041                 \
    148f_2578                 \
    16d8_6281                 \
    16d8_6803                 \
    16d8_6803__               \
    16d8_700a                 \
    16d8_f000                 \
    198f_bccd                 \
    19d2_0003                 \
    19d2_0026                 \
    19d2_0040                 \
    19d2_0053                 \
    19d2_0083                 \
    19d2_0101                 \
    19d2_0103                 \
    19d2_0110                 \
    19d2_0115                 \
    19d2_1001                 \
    19d2_1007                 \
    19d2_1009                 \
    19d2_1013                 \
    19d2_2000                 \
    19d2_fff5                 \
    19d2_fff6                 \
    1a8d_1000                 \
    1ab7_5700                 \
    1b7d_0700                 \
    1bbb_f000                 \
    1c9e_1001                 \
    1c9e_6061                 \
    1c9e_9200                 \
    1c9e_9e00                 \
    1c9e_f000                 \
    1dd6_1000                 \
    1e0e_f000                 \
    1edf_6003                 \
    1ee8_0009                 \
    1ee8_0013                 \
    1ee8_0040                 \
    1f28_0021                 \
    1fac_0032                 \
    1fac_0130                 \
    201e_2009                 \
    230d_0001                 \
    8888_6500                 \
    12d1_14d1                 \
    12d1_1505                 \
    230d_000d                 \
    19d2_0120                 \
    04cc_225a

#copy_from = $(wildcard usb_modeswitch.d/*)

#copy_to := $(addprefix $(TARGET_OUT)/etc/usb_modeswitch.d/,$(copy_from))
#copy_from := $(addprefix $(LOCAL_PATH)/usb_modeswitch.d/,$(copy_from))
#$(copy_to) : $(TARGET_OUT)/etc/usb_modeswitch.d/% : $(LOCAL_PATH)/usb_modeswitch.d/% | $(ACP)
#	   $(transform-prebuilt-to-target)

#ALL_PREBUILT += $(copy_to)


