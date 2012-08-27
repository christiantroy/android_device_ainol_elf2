#!/system/xbin/busybox sh
tag=0
if [ -f "/data/calibrationtag" ]; then
	echo "calibrationtag exists"
else
	touch "/data/calibrationtag"
	echo "0" > "/data/calibrationtag"
fi
cat "/data/calibrationtag" | while read tag
do 
	if [ "$tag" = "1" ]; then
		echo "unnecessary to calibrate!"
	else
		echo "1" > "/data/calibrationtag"
		/system/bin/android_I2C_Calibrate_V1_0
	fi
done

