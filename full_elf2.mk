# Inherit device configuration for ELF2.
$(call inherit-product, device/ainol/elf2/device.mk)

# Inherit some common cyanogenmod stuff.
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base.mk)
$(call inherit-product, vendor/aokp/configs/common_tablet_small.mk)
$(call inherit-product, vendor/aokp/configs/gsm.mk)

#
# Setup device specific product configuration.
#
PRODUCT_NAME := aokp_elf2
PRODUCT_BRAND := Google
PRODUCT_DEVICE := elf2
PRODUCT_MODEL := Nexus 7
PRODUCT_MANUFACTURER := Asus
PRODUCT_BUILD_PROP_OVERRIDES += PRODUCT_NAME=nakasi TARGET_DEVICE=grouper BUILD_FINGERPRINT="google/nakasi/grouper:4.1.2/JZO54K/485486:user/release-keys" PRIVATE_BUILD_DESC="nakasi-user 4.1.2 JZO54K 485486 release-keys"
