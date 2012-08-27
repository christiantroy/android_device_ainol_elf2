package com.amlogic.OOBE;

import com.amlogic.OOBE.R;
import com.amlogic.OOBE.ethernet.EthernetConfigDialog;
import com.amlogic.OOBE.ethernet.EthernetEnabler;

import android.content.Intent;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;

public class EthernetSettings extends PreferenceActivity {
    private static final String KEY_TOGGLE_ETH = "toggle_eth";
    private static final String KEY_CONF_ETH = "eth_config";
    private static final String KEY_BACK_ETH = "eth_back";
    private EthernetEnabler mEthEnabler;
    private EthernetConfigDialog mEthConfigDialog;
    private Preference mEthConfigPref;
    private Preference mEthConfigBack;

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        if (preference == mEthConfigPref) {
            mEthConfigDialog.show();
        }
        
        if (preference == mEthConfigBack) {
        	Intent intent = new Intent();
        	intent.setClass(EthernetSettings.this,EthConfigurationSetting.class);
        	startActivity(intent);
        	EthernetSettings.this.finish();
        }        
        
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ethernet_settings);
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        mEthConfigPref = preferenceScreen.findPreference(KEY_CONF_ETH);
        mEthConfigBack = preferenceScreen.findPreference(KEY_BACK_ETH);
        /*
         * TO DO:
         * Add new perference screen for Etherenet Configuration
         */

        initToggles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEthEnabler.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEthEnabler.pause();
    }

	public boolean onKeyDown(int keyCode, KeyEvent msg) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {  
	    	Intent intent = new Intent();
			intent.setClass(EthernetSettings.this, EthConfigurationSetting.class);
			startActivity(intent);
			EthernetSettings.this.finish();
		}
		return true;
	}
    
    private void initToggles() {
        mEthEnabler = new EthernetEnabler(this,
                (EthernetManager) getSystemService(ETH_SERVICE),
                (CheckBoxPreference) findPreference(KEY_TOGGLE_ETH));
        mEthConfigDialog = new EthernetConfigDialog(this, mEthEnabler);
        mEthEnabler.setConfigDialog(mEthConfigDialog);
    }
}
