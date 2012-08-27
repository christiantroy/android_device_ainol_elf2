DEVICE_PACKAGE_OVERLAYS := device/ainol/elf2/overlay

# AML HAL
PRODUCT_PACKAGES += \
    camera.amlogic \
    sensors.amlogic \
    lights.amlogic \
    gralloc.amlogic \
    hwcomposer.amlogic

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
    VideoPlayer \
    HdmiSwitch \
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

# libemoji for Webkit
PRODUCT_PACKAGES += libemoji
    
PRODUCT_PACKAGES += \
    HoloSpiralWallpaper \
    LiveWallpapersPicker \
    VisualizationWallpapers \

PRODUCT_PACKAGES += \
    Camera

PRODUCT_PACKAGES += \
    librs_jni \
    com.android.future.usb.accessory

PRODUCT_PROPERTY_OVERRIDES := \
    service.adb.root=1 \
    ro.secure=0 \
    ro.allow.mock.location=1 \
    ro.debuggable=1

PRODUCT_PROPERTY_OVERRIDES += \
    ro.com.google.locationfeatures=1 \
    ro.setupwizard.enable_bypass=1 \
    dalvik.vm.execution-mode=int:jit \
    dalvik.vm.lockprof.threshold=500 \
    dalvik.vm.dexopt-flags=m=y

PRODUCT_PROPERTY_OVERRIDES += \
    persist.sys.timezone=Europe/Rome \
    persist.sys.language=en \
    persist.sys.country=US \
    persist.sys.use_dithering=0 \
    persist.sys.purgeable_assets=0 \
    windowsmgr.max_events_per_sec=240 \
    view.touch_slop=2 \
    view.minimum_fling_velocity=25 \
    ro.additionalmounts=/mnt/external_sdcard \
    ro.vold.switchablepair=/mnt/sdcard,/mnt/external_sdcard \
    persist.sys.vold.switchexternal=0

  
