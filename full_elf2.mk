# Copyright (C) 2012 The Android Open Source Project
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

$(call inherit-product, $(SRC_TARGET_DIR)/product/languages_full.mk)

DEVICE_PACKAGE_OVERLAYS := device/ainol/elf2/overlay

# Ramdisk
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/ramdisk/mali.ko:root/boot/mali.ko \
    $(LOCAL_PATH)/ramdisk/ump.ko:root/boot/ump.ko \
    $(LOCAL_PATH)/ramdisk/init.rc:root/init.rc \
    $(LOCAL_PATH)/ramdisk/init:root/init \
    $(LOCAL_PATH)/ramdisk/ueventd.rc:root/ueventd.rc

# Prebuilt boot animation & PPP Widget
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/prebuilt/bootanimation.zip:system/media/bootanimation.zip

# init.d scripts
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/init.d/02modules:system/etc/init.d/02modules \
    $(LOCAL_PATH)/init.d/03kernel:system/etc/init.d/03kernel \
    $(LOCAL_PATH)/init.d/06fixpermissions:system/etc/init.d/06fixpermissions \
    $(LOCAL_PATH)/init.d/10devlistener:system/etc/init.d/10devlistener \
    $(LOCAL_PATH)/init.d/11link2sd:system/etc/init.d/11link2sd

# Prebuilt configs
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/alarm_blacklist.txt:/system/etc/alarm_blacklist.txt \
    $(LOCAL_PATH)/configs/asound.conf:system/etc/asound.conf \
    $(LOCAL_PATH)/configs/asound.state:system/etc/asound.state \
    $(LOCAL_PATH)/configs/calibration.sh:system/bin/calibration.sh \
    $(LOCAL_PATH)/configs/ft5x06.idc:system/usr/idc/ft5x06.idc \
    $(LOCAL_PATH)/configs/media_profiles.xml:system/etc/media_profiles.xml \
    $(LOCAL_PATH)/configs/remote.conf:system/etc/remote.conf \
    $(LOCAL_PATH)/configs/set_camera_clk.sh:system/bin/set_camera_clk.sh \
    $(LOCAL_PATH)/configs/set_display_mode.sh:system/bin/set_display_mode.sh \
    $(LOCAL_PATH)/configs/Vendor_0001_Product_0001.kl:/system/usr/keylayout/Vendor_0001_Product_0001.kl \
    $(LOCAL_PATH)/configs/vold.fstab:system/etc/vold.fstab \
    $(LOCAL_PATH)/configs/wififix.sh:system/bin/wififix.sh \
    $(LOCAL_PATH)/configs/media_codecs.xml:system/etc/media_codecs.xml \
    $(LOCAL_PATH)/configs/audio_policy.conf:system/etc/audio_policy.conf \
    $(LOCAL_PATH)/configs/mcli-cdma:system/etc/ppp/peers/mcli-cdma \
    $(LOCAL_PATH)/configs/mcli-gsm:system/etc/ppp/peers/mcli-gsm

# USB ModeSwitch
PRODUCT_COPY_FILES += \
	$(call find-copy-subdir-files,*,device/ainol/elf2/configs/usb_modeswitch.d,system/etc/usb_modeswitch.d)

# Prebuilt kernel modules
PRODUCT_COPY_FILES += \
	$(call find-copy-subdir-files,*,device/ainol/elf2/modules,system/lib/modules)

# Prebuilt proprietary stuff
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/proprietary/wifi-config/wpa_supplicant.conf:system/etc/wifi/wpa_supplicant.conf \
    $(LOCAL_PATH)/proprietary/android_I2C_Calibrate_V1_0:system/bin/android_I2C_Calibrate_V1_0 \
    $(LOCAL_PATH)/proprietary/gralloc.default.so:system/lib/hw/gralloc.default.so \
    $(LOCAL_PATH)/proprietary/battery/0.rot270.bmp:system/resource/battery_pic/0.bmp \
    $(LOCAL_PATH)/proprietary/battery/1.rot270.bmp:system/resource/battery_pic/1.bmp \
    $(LOCAL_PATH)/proprietary/battery/2.rot270.bmp:system/resource/battery_pic/2.bmp \
    $(LOCAL_PATH)/proprietary/battery/3.rot270.bmp:system/resource/battery_pic/3.bmp \
    $(LOCAL_PATH)/proprietary/battery/4.rot270.bmp:system/resource/battery_pic/4.bmp \
    $(LOCAL_PATH)/proprietary/battery/power_low.rot270.bmp:system/resource/battery_pic/power_low.bmp \
    $(LOCAL_PATH)/proprietary/vold/fsck.exfat:system/bin/fsck.exfat \
    $(LOCAL_PATH)/proprietary/vold/mkfs.exfat:system/bin/mkfs.exfat \
    $(LOCAL_PATH)/proprietary/vold/mount.exfat:system/bin/mount.exfat \
    $(LOCAL_PATH)/proprietary/vold/ntfs-3g:system/bin/ntfs-3g \
    $(LOCAL_PATH)/proprietary/vold/ntfs-3g.probe:system/bin/ntfs-3g.probe \
    $(LOCAL_PATH)/proprietary/vold/mkntfs:system/bin/mkntfs \
    $(LOCAL_PATH)/proprietary/fbset:system/xbin/fbset

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/tablet_core_hardware.xml:system/etc/permissions/tablet_core_hardware.xml \
    frameworks/native/data/etc/android.hardware.camera.xml:system/etc/permissions/android.hardware.camera.xml \
    frameworks/native/data/etc/android.hardware.camera.front.xml:system/etc/permissions/android.hardware.camera.front.xml \
    frameworks/native/data/etc/android.hardware.location.xml:system/etc/permissions/android.hardware.location.xml \
    frameworks/native/data/etc/android.hardware.location.gps.xml:system/etc/permissions/android.hardware.location.gps.xml \
    frameworks/native/data/etc/android.hardware.wifi.xml:system/etc/permissions/android.hardware.wifi.xml \
    frameworks/native/data/etc/android.hardware.sensor.proximity.xml:system/etc/permissions/android.hardware.sensor.proximity.xml \
    frameworks/native/data/etc/android.hardware.sensor.light.xml:system/etc/permissions/android.hardware.sensor.light.xml \
    frameworks/native/data/etc/android.hardware.sensor.gyroscope.xml:system/etc/permissions/android.hardware.sensor.gyroscope.xml \
    frameworks/native/data/etc/android.hardware.sensor.accelerometer.xml:system/etc/permissions/android.hardware.sensor.accelerometer.xml \
    frameworks/native/data/etc/android.hardware.touchscreen.multitouch.jazzhand.xml:system/etc/permissions/android.hardware.touchscreen.multitouch.jazzhand.xml \
    frameworks/native/data/etc/android.software.sip.voip.xml:system/etc/permissions/android.software.sip.voip.xml \
    frameworks/native/data/etc/android.hardware.usb.accessory.xml:system/etc/permissions/android.hardware.usb.accessory.xml \
    frameworks/native/data/etc/android.hardware.usb.host.xml:system/etc/permissions/android.hardware.usb.host.xml \
    packages/wallpapers/LivePicker/android.software.live_wallpaper.xml:system/etc/permissions/android.software.live_wallpaper.xml

DEVICE_PACKAGE_OVERLAYS := device/ainol/elf2/overlay

# AML HAL
PRODUCT_PACKAGES += \
    camera.amlogic.so \
    hwcomposer.amlogic \
    sensors.amlogic \
    lights.amlogic \
    power.amlogic

# Mali GPU OpenGL libraries
PRODUCT_PACKAGES += \
    libEGL_mali.so \
    libGLESv1_CM_mali.so \
    libGLESv2_mali.so \
    libGLESv2_mali.so \
    libMali.so \
    libUMP.so

# AML WiFi
PRODUCT_PACKAGES += \
    40181/nvram.txt \
    40181/fw_bcm40181a0.bin \
    40181/fw_bcm40181a0_apsta.bin \
    40181/fw_bcm40181a2.bin \
    40181/fw_bcm40181a2_apsta.bin \
    40181/fw_bcm40181a2_p2p.bin \
    wl \
    dhd

# AML RIL
PRODUCT_PACKAGES += \
    Phone \
    libaml-ril.so \
    init-pppd.sh \
    ip-up \
    chat

PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/libril/rild:system/bin/rild

# ALSA
PRODUCT_PACKAGES += \
    audio_policy.default \
    audio.primary.amlogic \
    alsa.default \
    acoustics.default \
    libasound \
    alsa_aplay \
    alsa_ctl \
    alsa_amixer \
    alsainit-00main \
    alsalib-alsaconf \
    alsalib-pcmdefaultconf \
    alsalib-cardsaliasesconf \
    audio_firmware \
    libaudiopolicy

# AML Misc
PRODUCT_PACKAGES += \
    HdmiSwitch \
    libamplayerjni \
    libsubjni \
    amlogic.subtitle.xml \
    amlogic.libplayer.xml \
    remotecfg

# AML USB tools
PRODUCT_PACKAGES += \
    usbtestpm \
    usbpower \
    usbtestpm_mx \
    usbtestpm_mx_iddq \
    usbpower_mx_iddq

# manage bluetooth permissions
PRODUCT_PACKAGES += \
    audio.a2dp.default \
    devlistener

# usb_modeswitch
PRODUCT_PACKAGES += \
    usb_modeswitch

# libemoji for Webkit
PRODUCT_PACKAGES += libemoji

PRODUCT_PACKAGES += Torch
    
PRODUCT_PROPERTY_OVERRIDES += \
    service.adb.root=1 \
    ro.secure=0 \
    ro.allow.mock.location=1 \
    ro.debuggable=1

PRODUCT_PROPERTY_OVERRIDES += \
    ro.com.google.locationfeatures=1 \
    ro.setupwizard.enable_bypass=1 \
    dalvik.vm.execution-mode=int:jit \
    dalvik.vm.lockprof.threshold=500 \
    dalvik.vm.verify-bytecode=false \
    dalvik.vm.dexopt-flags=v=n,o=v \
    dalvik.vm.dexopt-data-only=1

PRODUCT_PROPERTY_OVERRIDES += \
    persist.sys.timezone=Europe/Rome \
    persist.sys.language=en \
    persist.sys.country=US \
    persist.sys.use_dithering=0 \
    persist.sys.purgeable_assets=0 \
    windowsmgr.max_events_per_sec=240 \
    view.touch_slop=2 \
    view.minimum_fling_velocity=25 \
    ro.additionalmounts=/storage/sdcard1 \
    ro.vold.switchablepair=/storage/sdcard0,/storage/sdcard1 \
    persist.sys.vold.switchexternal=0
   
PRODUCT_AAPT_CONFIG := normal large tvdpi hdpi
PRODUCT_AAPT_PREF_CONFIG := mdpi

PRODUCT_CHARACTERISTICS := tablet

PRODUCT_TAGS += dalvik.gc.type-precise

TARGET_BOOTANIMATION_NAME := horizontal-1024x600

PRODUCT_BUILD_PROP_OVERRIDES += BUILD_UTC_DATE=0

$(call inherit-product, frameworks/native/build/tablet-7in-hdpi-1024-dalvik-heap.mk)
$(call inherit-product, build/target/product/full_base.mk)
$(call inherit-product-if-exists, vendor/ainol/elf2/elf2-vendor.mk)