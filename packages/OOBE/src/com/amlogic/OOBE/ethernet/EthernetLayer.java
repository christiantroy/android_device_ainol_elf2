package com.amlogic.OOBE.ethernet;

import static android.net.ethernet.EthernetManager.ETH_DEVICE_SCAN_RESULT_READY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.ethernet.EthernetManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EthernetLayer {
    private static final String TAG = "EthernetLayer";

    private EthernetManager mEthManager;
    private String[] mDevList;
    private EthernetConfigDialog mDialog;

    EthernetLayer (EthernetConfigDialog configdialog) {
        mDialog = configdialog;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(EthernetManager.ETH_DEVICE_SCAN_RESULT_READY)) {
                handleDevListChanges();
            }
        }
    };

    private void handleDevListChanges() {
        mDevList = mEthManager.getDeviceNameList();
        mDialog.updateDevNameList(mDevList);
    }
}
