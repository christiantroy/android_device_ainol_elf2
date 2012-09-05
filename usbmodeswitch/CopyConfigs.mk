#
# CopyConfigs.mk 
# Copy usb_modeswitch configs to out
#
#
#

#LOCAL_PATH := external/libusb/usbmodeswitch

allfiles := $(wildcard external/libusb/usbmodeswitch/usb_modeswitch.d/*)                   
src_file := $(notdir $(allfiles))


PRODUCT_COPY_FILES += $(foreach file,$(src_file), \
	external/libusb/usbmodeswitch/usb_modeswitch.d/$(file):system/etc/usb_modeswitch.d/$(file))



