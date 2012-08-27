package com.amlogic.OOBE;

import com.amlogic.OOBE.wifi.WifiEnabler;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.KeyEvent;

public class WirelessSettings extends PreferenceActivity {

//    private static final String KEY_TOGGLE_AIRPLANE = "toggle_airplane";
//    private static final String KEY_TOGGLE_BLUETOOTH = "toggle_bluetooth";
    private static final String KEY_TOGGLE_WIFI = "toggle_wifi";
    private static final String KEY_WIFI_SETTINGS = "wifi_settings";
//    private static final String KEY_BT_SETTINGS = "bt_settings";
//    private static final String KEY_VPN_SETTINGS = "vpn_settings";
//    private static final String KEY_NETWORK_SETTINGS = "network_settings";
    private static final String KEY_TETHER_SETTINGS = "tether_settings";
    private static final String KEY_WIFI_BACK = "wifi_back";
    public static final String EXIT_ECM_RESULT = "exit_ecm_result";
    public static final int REQUEST_CODE_EXIT_ECM = 1;

//    private AirplaneModeEnabler mAirplaneModeEnabler;
//    private CheckBoxPreference mAirplaneModePreference;
    private WifiEnabler mWifiEnabler;
//    private BluetoothEnabler mBtEnabler;
    private Preference mWifiConfigBack;

    /**
     * Invoked on each preference click in this hierarchy, overrides
     * PreferenceActivity's implementation.  Used to make sure we track the
     * preference click events.
     */
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
//        if (preference == mAirplaneModePreference && Boolean.parseBoolean(
//                SystemProperties.get(TelephonyProperties.PROPERTY_INECM_MODE))) {
            // In ECM mode launch ECM app dialog
//            startActivityForResult(
//                new Intent(TelephonyIntents.ACTION_SHOW_NOTICE_ECM_BLOCK_OTHERS, null),
//                REQUEST_CODE_EXIT_ECM);
//            return true;
//        }
        // Let the intents be launched by the Preference manager
        if (preference == mWifiConfigBack) {
        	Intent intent = new Intent();
        	intent.setClass(WirelessSettings.this,WifiConfigurationSetting.class);
        	startActivity(intent);
        	WirelessSettings.this.finish();
        }   
    	
        return false;
    }

    public static boolean isRadioAllowed(Context context, String type) {
//        if (!AirplaneModeEnabler.isAirplaneModeOn(context)) {
//            return true;
//        }
        // Here we use the same logic in onCreate().
        String toggleable = Settings.System.getString(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_TOGGLEABLE_RADIOS);
        return toggleable != null && toggleable.contains(type);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.wireless_settings);

//        CheckBoxPreference airplane = (CheckBoxPreference) findPreference(KEY_TOGGLE_AIRPLANE);
        CheckBoxPreference wifi = (CheckBoxPreference) findPreference(KEY_TOGGLE_WIFI);
//        CheckBoxPreference bt = (CheckBoxPreference) findPreference(KEY_TOGGLE_BLUETOOTH);
        mWifiConfigBack = findPreference(KEY_WIFI_BACK);
        
//        mAirplaneModeEnabler = new AirplaneModeEnabler(this, airplane);
//        mAirplaneModePreference = (CheckBoxPreference) findPreference(KEY_TOGGLE_AIRPLANE);
        mWifiEnabler = new WifiEnabler(this, wifi);
//        if(Utils.isBTEnabled())
//        	mBtEnabler = new BluetoothEnabler(this, bt);

//        String toggleable = Settings.System.getString(getContentResolver(),
//                Settings.System.AIRPLANE_MODE_TOGGLEABLE_RADIOS);

        // Manually set dependencies for Wifi when not toggleable.
//        if (toggleable == null || !toggleable.contains(Settings.System.RADIO_WIFI)) {
//		    if(Utils.isPhoneEnabled()){
//            wifi.setDependency(KEY_TOGGLE_AIRPLANE);
//            findPreference(KEY_WIFI_SETTINGS).setDependency(KEY_TOGGLE_AIRPLANE);
//            findPreference(KEY_VPN_SETTINGS).setDependency(KEY_TOGGLE_AIRPLANE);
//			}
//        }

        // Manually set dependencies for Bluetooth when not toggleable.
//        if (toggleable == null || !toggleable.contains(Settings.System.RADIO_BLUETOOTH)) {
//			if(Utils.isPhoneEnabled()){
//            bt.setDependency(KEY_TOGGLE_AIRPLANE);
//            findPreference(KEY_BT_SETTINGS).setDependency(KEY_TOGGLE_AIRPLANE);
//			}
//        }

        // Disable Bluetooth Settings if Bluetooth service is not available.
//        if(Utils.isBTEnabled()){
//	        if (ServiceManager.getService(BluetoothAdapter.BLUETOOTH_SERVICE) == null) {
//	            findPreference(KEY_BT_SETTINGS).setEnabled(false);
//	        }
//      	}


        // Disable Tethering if it's not allowed
//        ConnectivityManager cm =
//                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (!cm.isTetheringSupported()) {
//            getPreferenceScreen().removePreference(findPreference(KEY_TETHER_SETTINGS));
//        } else {
//            String[] usbRegexs = cm.getTetherableUsbRegexs();
//            String[] wifiRegexs = cm.getTetherableWifiRegexs();
//            Preference p = findPreference(KEY_TETHER_SETTINGS);
//            if (wifiRegexs.length == 0) {
//                p.setTitle(R.string.tether_settings_title_usb);
//                p.setSummary(R.string.tether_settings_summary_usb);
//            } else {
//                if (usbRegexs.length == 0) {
//                    p.setTitle(R.string.tether_settings_title_wifi);
//                    p.setSummary(R.string.tether_settings_summary_wifi);
//                } else {
//                    p.setTitle(R.string.tether_settings_title_both);
//                    p.setSummary(R.string.tether_settings_summary_both);
//                }
//            }
//        }
//        if(!Utils.isPhoneEnabled()){
//            getPreferenceScreen().removePreference(findPreference(KEY_NETWORK_SETTINGS));
//			getPreferenceScreen().removePreference(mAirplaneModePreference);
//			getPreferenceScreen().removePreference(findPreference(KEY_VPN_SETTINGS));
//        }
        
//        if(!Utils.isBTEnabled()){
//						getPreferenceScreen().removePreference(bt);
//						getPreferenceScreen().removePreference(findPreference(KEY_BT_SETTINGS));
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
//        mAirplaneModeEnabler.resume();
        mWifiEnabler.resume();
//        if (Utils.isBTEnabled())
//        	mBtEnabler.resume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
//        mAirplaneModeEnabler.pause();
        mWifiEnabler.pause();
//        if (Utils.isBTEnabled())
//        	mBtEnabler.pause();
    }
    
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {  
        	Intent intent = new Intent();
        	intent.setClass(WirelessSettings.this,WifiConfigurationSetting.class);
        	startActivity(intent);
        	WirelessSettings.this.finish();
		}
		return true;
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EXIT_ECM) {
            Boolean isChoiceYes = data.getBooleanExtra(EXIT_ECM_RESULT, false);
            // Set Airplane mode based on the return value and checkbox state
//            mAirplaneModeEnabler.setAirplaneModeInECM(isChoiceYes,mAirplaneModePreference.isChecked());
        }
    }
}

