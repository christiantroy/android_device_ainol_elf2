# Inherit device configuration for N70.
$(call inherit-product, device/ainol/elf2/full_elf2.mk)

# Inherit some common cyanogenmod stuff.
$(call inherit-product, vendor/cm/config/common_full_tablet_wifionly.mk)
$(call inherit-product, vendor/cm/config/gsm.mk)

#
# Setup device specific product configuration.
#
PRODUCT_NAME := cm_elf2
PRODUCT_BRAND := ainol
PRODUCT_DEVICE := elf2
PRODUCT_MODEL := ELF-II
PRODUCT_MANUFACTURER := Ainol
PRODUCT_BUILD_PROP_OVERRIDES += PRODUCT_NAME=GT-I9100 PRODUCT_BRAND=samsung BUILD_ID=IML74K BUILD_FINGERPRINT=samsung/GT-I9100/GT-I9100:4.0.3/IML74K/BGLP8:user/release-keys PRIVATE_BUILD_DESC="GT-I9100-user 4.0.3 IML74K BGLP8 release-keys" BUILD_NUMBER=BGLP8
