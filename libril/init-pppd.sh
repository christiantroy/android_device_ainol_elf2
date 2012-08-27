#!/system/bin/sh
# An unforunate wrapper script 
# so that the exit code of pppd may be retrieved


# this is a workaround for issue #651747
#trap "/system/bin/sleep 1;exit 0" TERM

SCRIPT=`/system/bin/getprop ril.ppp.script`
USER=`/system/bin/getprop ril.ppp.user`
PWD=`/system/bin/getprop ril.ppp.pwd`
APNNAME=`/system/bin/getprop ril.ppp.apn_name`

CHATSCRIBE="/system/bin/chat ABORT '\nBUSY\r' ABORT '\nNO ANSWER\r' ABORT '\nRINGING\r' TIMEOUT 30  '' \rAT  OK AT+CGDCONT=1,\"IP\",\"$APNNAME\"  '' ATDT*99***1# CONNECT  '' "

/system/bin/log -t pppd "init-pppd.sh SCRIPT=$SCRIPT"
/system/bin/log -t pppd "init-pppd.sh user=$USER"
/system/bin/log -t pppd "init-pppd.sh pwd=$PWD"
/system/bin/log -t pppd "init-pppd.sh apn_name=$APNNAME"

if busybox [ "$SCRIPT" = "mcli-cdma" ] ; then
	CHATSCRIBE="/system/bin/chat -s -S TIMEOUT 25 ABORT 'BUSY' ABORT 'ERROR' ABORT '+CME ERROR:' '' AT '' ATH0 ''  ATDT#777 CONNECT"
elif busybox [ "$SCRIPT" = "mcli-tdscdma" ] ; then
	CHATSCRIBE="/system/bin/chat TIMEOUT 25 '' AT+CGDCONT=1,\"IP\",\"$APNNAME\" '' ATDT*99***1# CONNECT "
	SCRIPT=mcli-gsm
fi

/system/bin/log -t pppd "Starting pppd $CHATSCRIBE"

#/system/bin/pppd file /system/etc/ppp/peers/mcli
/system/bin/pppd call $SCRIPT user $USER password $PWD connect "$CHATSCRIBE" 


/system/bin/log -t pppd "init-pppd.sh exited "


exit $PPPD_EXIT
