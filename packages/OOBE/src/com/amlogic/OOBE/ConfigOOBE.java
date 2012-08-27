package com.amlogic.OOBE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContextWrapper;
import android.util.Log;

public class ConfigOOBE {
	
	private final String TAG = "ConfigOOBE:";
	
	public String strNextconfig = "";
	public String strPrevconfig = "";
	private ContextWrapper cwRes ;
	
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	
	public ConfigOOBE(Activity res){
		Map<String, Object> map;
		cwRes = new ContextWrapper(res);
		
		map = new HashMap<String, Object>();
		map.put("item_name", "Language");
		map.put("item_value", cwRes.getResources().getString(R.string.cfg_language));
		list.add(map);
		Log.d(TAG, "list init R.string.cfg_language:"+cwRes.getResources().getString(R.string.cfg_language));

		map = new HashMap<String, Object>();
		map.put("item_name", "DateAndTimeSetting");
		map.put("item_value", cwRes.getResources().getString(R.string.cfg_datetime));
		list.add(map);
		Log.d(TAG, "list init R.string.cfg_datetime:"+cwRes.getResources().getString(R.string.cfg_datetime));

		map = new HashMap<String, Object>();
		map.put("item_name", "UserLicenseAgree");
		map.put("item_value", cwRes.getResources().getString(R.string.cfg_userlicence));
		list.add(map);
		Log.d(TAG, "list init R.string.cfg_userlicence:"+cwRes.getResources().getString(R.string.cfg_userlicence));
		
		map = new HashMap<String, Object>();
		map.put("item_name", "Calibrationoobe");
		map.put("item_value", cwRes.getResources().getString(R.string.cfg_calibration));
		list.add(map);
		Log.d(TAG, "list init R.string.cfg_calibration:"+cwRes.getResources().getString(R.string.cfg_calibration));
		
		map = new HashMap<String, Object>();
		map.put("item_name", "LayoutSetting");
		map.put("item_value", cwRes.getResources().getString(R.string.cfg_layout));
		list.add(map);
		Log.d(TAG, "list init R.string.cfg_layout:"+cwRes.getResources().getString(R.string.cfg_layout));
		
		map = new HashMap<String, Object>();
		map.put("item_name", "EthConfigurationSetting");
		map.put("item_value", cwRes.getResources().getString(R.string.cfg_ethnetconfig));
		list.add(map);
		Log.d(TAG, "list init R.string.cfg_ethnetconfig:"+cwRes.getResources().getString(R.string.cfg_ethnetconfig));

		map = new HashMap<String, Object>();
		map.put("item_name", "WifiConfigurationSetting");
		map.put("item_value", cwRes.getResources().getString(R.string.cfg_wificonfig));
		list.add(map);
		Log.d(TAG, "list init R.string.cfg_wificonfig:"+cwRes.getResources().getString(R.string.cfg_wificonfig));
		
		Log.d(TAG, "list init finish.");
	}
	
	public String getFirstActivityName(){
		strNextconfig = "";
	
		for(int i=0;i<list.size();i++){
			if(list.get(i).get("item_value").toString().equals("true") == true){
				strNextconfig = list.get(i).get("item_name").toString();
				break;
			}
		}
		
		Log.d(TAG, "getFirstActivityName:" + strNextconfig);
		return strNextconfig;
	}

	public String getLastActivityName(){
		strPrevconfig = "";
		
		for(int i=list.size()-1;i>0;i--){
			if(list.get(i).get("item_value").toString().equals("true") == true){
				strPrevconfig = list.get(i).get("item_name").toString();
				break;
			}
		}
		
		Log.d(TAG, "getLastActivityName:" + strPrevconfig);
		return strPrevconfig;
	}
	
	public String getNextActivityName(String str){
		strNextconfig = "";
		
		for(int i=0;i<list.size();i++){
			if(str.equals(list.get(i).get("item_name").toString()) == true){
				for(int j=i+1;j<list.size();j++){
					if(list.get(j).get("item_value").toString().equals("true") == true){
						strNextconfig = list.get(j).get("item_name").toString();
						break;
					}
				}
				break;
			}
		}
		
		Log.d(TAG, "getNextActivityName:" + strNextconfig);
		return strNextconfig;
	}
	
	public String getPrevActivityName(String str){
		strPrevconfig = "";
		
		for(int i=0;i<list.size();i++){
			if(str.equals(list.get(i).get("item_name").toString()) == true){
				for(int j=i-1;j>=0;j--){
					if(list.get(j).get("item_value").toString().equals("true") == true){
						strPrevconfig = list.get(j).get("item_name").toString();
						break;
					}
				}
				break;
			}
		}

		Log.d(TAG, "getPrevActivityName:" + strPrevconfig);
		return strPrevconfig;
	}
	
	public Object getActivity(String str){
		if(str.equals("Language") == true){
			Log.d(TAG, "getActivity:Language");
			return Language.class;
		}
		
		if(str.equals("DateAndTimeSetting") == true){
			Log.d(TAG, "getActivity:DateAndTimeSetting");
			return DateAndTimeSetting.class;		
		}
		
		if(str.equals("UserLicenseAgree") == true){
			Log.d(TAG, "getActivity:UserLicenseAgree");
			return UserLicenseAgree.class;
		}
		
		if(str.equals("Calibrationoobe") == true){
			Log.d(TAG, "getActivity:Calibrationoobe");
			return Calibrationoobe.class;
		}
		
		if(str.equals("LayoutSetting") == true){
			Log.d(TAG, "getActivity:LayoutSetting");
			return LayoutSetting.class;
		}
		
		if(str.equals("EthConfigurationSetting") == true){
			Log.d(TAG, "getActivity:EthConfigurationSetting");
			return EthConfigurationSetting.class;
		}
		
		if(str.equals("WifiConfigurationSetting") == true){
			Log.d(TAG, "getActivity:WifiConfigurationSetting");
			return WifiConfigurationSetting.class;
		}
		
		return str;
	}
}
