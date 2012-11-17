#
# Copyright (C) 2012 The Android Open-Source Project
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

DEVICE_PACKAGE_OVERLAYS += $(LOCAL_PATH)/overlay

# Use the non-open-source parts, if they're present
-include vendor/ainol/elf2/BoardConfigVendor.mk

# Alsa
BOARD_USES_ALSA_AUDIO := true
BUILD_WITH_ALSA_UTILS := true

# Bluetooth
BOARD_HAVE_BLUETOOTH := true

# Sensors
BOARD_USES_SENSOR_BMA250 :=true
BOARD_USES_LIGHT_SENSOR := false
BOARD_HAVE_COMPASS := false

# Camera
USE_CAMERA_STUB := false
BOARD_HAVE_FRONT_CAM :=true
BOARD_HAVE_BACK_CAM :=false

# Touchscreen
TARGET_TOUCH_CALIBRATION_METHOD := none

# Amlogic stuff
BUILD_WITH_AMLOGIC_PLAYER := true
BOARD_USE_DEFAULT_APPINSTALL := true
BOARD_USE_DEFAULT_HDMISWITCH := true
BOARD_USE_AML_STANDARD_RIL := true
BOARD_USES_USB_PM := true
BOARD_PROVIDES_MALI := true
TARGET_USE_AMLOGIC_MKYAFFS_TOOL := true
TARGET_AMLOGIC_MKYAFFSIMG_TOOL := mkyaffsimage4K.dat

# Wifi
WIFI_DRIVER := bcm40181
WIFI_DRIVER_MODULE_PATH := /system/lib/modules/dhd.ko
WIFI_DRIVER_MODULE_NAME := dhd
WIFI_DRIVER_MODULE_ARG  := "firmware_path=/etc/wifi/40181/fw_bcm40181a2.bin nvram_path=/etc/wifi/40181/nvram.txt"
WIFI_DRIVER_FW_PATH_STA :=/etc/wifi/40181/fw_bcm40181a2.bin
WIFI_DRIVER_FW_PATH_AP  :=/etc/wifi/40181/fw_bcm40181a2_apsta.bin
#WIFI_DRIVER_FW_PATH_P2P :=/etc/wifi/40181/fw_bcm40181a2_p2p.bin
WPA_SUPPLICANT_VERSION := VER_0_8_X
BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_nl80211
BOARD_WPA_SUPPLICANT_DRIVER := NL80211
#BOARD_HOSTAPD_DRIVER_RTL :=true

TARGET_CPU_ABI := armeabi-v7a
TARGET_CPU_ABI2 := armeabi
TARGET_CPU_SMP := true
TARGET_ARCH_VARIANT := armv7-a-neon
ARCH_ARM_HAVE_VFP := true
TARGET_ARCH_VARIANT_CPU := cortex-a9
TARGET_ARCH_VARIANT_FPU := neon
ARCH_ARM_HAVE_NEON := true
ARCH_ARM_HAVE_TLS_REGISTER := true
ARCH_ARM_HAVE_ARMV7A := true
TARGET_GLOBAL_CFLAGS += -mtune=cortex-a9 -mfpu=neon -mfloat-abi=softfp
TARGET_GLOBAL_CPPFLAGS += -mtune=cortex-a9 -mfpu=neon -mfloat-abi=softfp

TARGET_BOARD_PLATFORM := meson6
TARGET_BOOTLOADER_BOARD_NAME := g06ref
TARGET_NO_BOOTLOADER := true
#TARGET_NO_KERNEL := true
TARGET_PREBUILT_KERNEL := device/ainol/elf2/kernel
BOARD_KERNEL_BASE := 0x40000000
BOARD_KERNEL_CMDLINE := console=ttyS0,115200 rw init=/init loglevel=8
TARGET_NO_RADIOIMAGE := true
TARGET_SIMULATOR := false
TARGET_PROVIDES_INIT_RC := true

BOARD_EGL_CFG := device/ainol/elf2/egl.cfg
USE_OPENGL_RENDERER := true
ENABLE_WEBGL := true
BOARD_USE_SKIA_LCDTEXT := true

#TARGET_RECOVERY_INITRC := device/ainol/elf2/recovery.init.rc
BOARD_HAS_NO_SELECT_BUTTON := true
BOARD_UMS_LUNFILE := "/sys/class/android_usb/android0/f_mass_storage/lun0/file"

TARGET_BOOTANIMATION_PRELOAD := true

TARGET_USE_CUSTOM_LUN_FILE_PATH := "/sys/class/android_usb/android0/f_mass_storage/lun%d/file"

COMMON_GLOBAL_CFLAGS += -DICS_CAMERA_BLOB

TARGET_RELEASETOOL_OTA_FROM_TARGET_SCRIPT := device/ainol/elf2/releasetools/amlogic_ota_from_target_files