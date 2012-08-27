package com.amlogic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.amlogic.ContentProvider.UserContentProvider;
import com.amlogic.ContentProvider.MusicPreference;

public class MenuDataBase {
	
	private UserContentProvider ucp=null;
	private MusicPreference myPreference =null;
	
	public MenuDataBase(Context context) {		
		ucp=new UserContentProvider(context);
		myPreference = new MusicPreference(context);
	}
	
	public String Getvideoplaymode(){
		if(ucp!=null)
			return ucp.getParams("VidRepeatMode");		

		return null;
	}
	
	public UserContentProvider GetUcpInstance(){		
		return ucp;
	}
	
	public List<String> InitBalancer() {
		
		if(ucp==null){
			Log.d("uart", "........ InitBalancer ucp==null .........\n");
			return null;
		}
		List<String> balance_data=new ArrayList<String>(); 
		balance_data.add(ucp.getParams("SoundMode_STD_EQ_100"));
		balance_data.add(ucp.getParams("SoundMode_STD_EQ_300"));
		balance_data.add(ucp.getParams("SoundMode_STD_EQ_1K"));
		balance_data.add(ucp.getParams("SoundMode_STD_EQ_3K"));
		balance_data.add(ucp.getParams("SoundMode_STD_EQ_10K"));
		
		balance_data.add(ucp.getParams("SoundMode_MUSIC_EQ_100"));
		balance_data.add(ucp.getParams("SoundMode_MUSIC_EQ_300"));
		balance_data.add(ucp.getParams("SoundMode_MUSIC_EQ_1K"));
		balance_data.add(ucp.getParams("SoundMode_MUSIC_EQ_3K"));
		balance_data.add(ucp.getParams("SoundMode_MUSIC_EQ_10K"));
		
		balance_data.add(ucp.getParams("SoundMode_NEWS_EQ_100"));
		balance_data.add(ucp.getParams("SoundMode_NEWS_EQ_300"));
		balance_data.add(ucp.getParams("SoundMode_NEWS_EQ_1K"));
		balance_data.add(ucp.getParams("SoundMode_NEWS_EQ_3K"));
		balance_data.add(ucp.getParams("SoundMode_NEWS_EQ_10K"));
		
		balance_data.add(ucp.getParams("SoundMode_THEATER_EQ_100"));
		balance_data.add(ucp.getParams("SoundMode_THEATER_EQ_300"));
		balance_data.add(ucp.getParams("SoundMode_THEATER_EQ_1K"));
		balance_data.add(ucp.getParams("SoundMode_THEATER_EQ_3K"));
		balance_data.add(ucp.getParams("SoundMode_THEATER_EQ_10K"));
		
		balance_data.add(ucp.getParams("SoundMode_USER_EQ_100"));
		balance_data.add(ucp.getParams("SoundMode_USER_EQ_300"));
		balance_data.add(ucp.getParams("SoundMode_USER_EQ_1K"));
		balance_data.add(ucp.getParams("SoundMode_USER_EQ_3K"));
		balance_data.add(ucp.getParams("SoundMode_USER_EQ_10K"));	
				
		
		balance_data.add(ucp.getParams("SoundMode"));
		
		return balance_data;		
	}

	public int InitSelectFrameValue(String MenuItemName, String mediaType) {
		
		if(ucp==null){
			Log.d("uart", "........ InitSelectFrameValue ucp==null .........\n");
			return -1;
		}
		
		String channel = ucp.getParams("CHANNEL");		
		
		// ................音乐图片视频播放设置.....................
		if(MenuItemName.equals("shortcut_common_playmode_")){
			String ct;
			if(mediaType.equals("music"))
				ct=myPreference.getMyParam("MusRepeatMode","");
			else if(mediaType.equals("picture"))
				ct=ucp.getParams("PicRepeatMode");
			else
				ct=ucp.getParams("VidRepeatMode");
			if(ct.equals("FOLDER"))
				return 1;
			else if(ct.equals("RANDOM"))
				return 2;
			else
				return 0;
		}
		
		//......................图片播放设置........................
		 if (MenuItemName.equals("shortcut_picture_switch_time_")) {
				String ct=ucp.getParams("PicPlayMode");
				if(ct.equals("3S"))
					return 0;
				else if(ct.equals("5S"))
					return 1;
				else if(ct.equals("10S"))		
					return 2;
				else
					return 3;
		 }
		 else  if (MenuItemName.equals("shortcut_picture_switch_mode_")) {
			 String ct=ucp.getParams("PicSwitchMode");
				if(ct.equals("Normal"))
					return 0;
				else			//Random
					return 1;
		 }	
		 
		//......................文本播放设置........................
		 if (MenuItemName.equals("shortcut_txt_turn_page_")) {
			 String ct=ucp.getParams("TxtTurnNum");
				if(ct.equals("10"))
					return 0;
				else if(ct.equals("20"))
					return 1;
				else if(ct.equals("50"))
					return 2;	
				else if(ct.equals("-10"))
					return 3;
				else if(ct.equals("-20"))
					return 4;
				else if(ct.equals("-50"))
					return 5;
				else
					return 0;
		 }
		 
			//......................聊天设置........................
		 if(MenuItemName.equals("shortcut_videochat_local_video_"))
		 { 
			 String ct=ucp.getParams("LocalVideoOn");
			 Log.e("amlogicmenu", ct);
				if(ct.equals("on"))										
					return 0;					
				else 					
					return 1;
			
		 }
		 
		 if(MenuItemName.equals("shortcut_videochat_remote_video_"))
		 { 
			 String ct=ucp.getParams("RemoteVideoOn");
			 Log.e("amlogicmenu", ct);
				if(ct.equals("on"))										
					return 0;					
				else 					
					return 1;
			
		 }
		 
		 if(MenuItemName.equals("shortcut_videochat_fullscr_"))
		 { 
			 String ct=ucp.getParams("FullScreenOn");
			 Log.e("amlogicmenu", ct);
				if(ct.equals("on"))										
					return 0;					
				else 					
					return 1;
			
		 }
		
		
		// ......................AmlogicPicSetup........................	
		if (MenuItemName.equals("shortcut_setup_video_temperature_")) {
			String ct=ucp.getParams("ColorTemp");
			if(ct.equals("COLD"))
				return 0;
			else if(ct.equals("STD"))
				return 1;
			else
				return 2;			
		} else if (MenuItemName.equals("shortcut_setup_video_dnr_")) {
			String ct=ucp.getParams(channel + "_NR");
			if(ct.equals("OFF"))
				return 0;
			else if(ct.equals("LOW"))
				return 1;
			else if(ct.equals("MID"))
				return 2;
			else if(ct.equals("HIGH"))
				return 3;
			else
				return 4;
		
		} else if (MenuItemName.equals("shortcut_setup_video_picture_mode_")) {
			String ct=ucp.getParams("PictureMode");
			if(ct.equals("STD"))
				return 0;
			else if(ct.equals("VIVID"))
				return 1;
			else if(ct.equals("SOFT"))
				return 2;
			else
				return 3;
		} else if (MenuItemName.equals("shortcut_setup_video_display_mode_")) {
			String ct=ucp.getParams(channel + "_DisplayMode");
			if(ct.equals("169"))
				return 0;
			else if(ct.equals("PERSON"))
				return 1;
			else if(ct.equals("THEATER"))
				return 2;
			else if(ct.equals("SUBTITLE"))
				return 3;
			else if(ct.equals("4:3"))
				return 4;
			else
				return 5;
		}
		
		// ......................AmlogicSoundSetup........................
		if (MenuItemName.equals("shortcut_setup_audio_srs_")) {
			String ct=ucp.getParams("SRS");
			if(ct.equals("ON"))
				return 1;
			else 
				return 0;
		} else if (MenuItemName.equals("shortcut_setup_audio_voice_")) {
			String ct=ucp.getParams("VOICE");
			if(ct.equals("ON"))
				return 1;
			else 
				return 0;
		} else if (MenuItemName.equals("shortcut_setup_audio_increase_bass_")) {
			String ct=ucp.getParams("INCREASE_BASS");
			if(ct.equals("ON"))
				return 1;
			else 
				return 0;
		}else if (MenuItemName.equals("shortcut_setup_audio_sound_mode_")) {
			String ct=ucp.getParams("SoundMode");
			if(ct.equals("STD"))
				return 0;
			else if(ct.equals("MUSIC"))
				return 1;
			else if(ct.equals("NEWS"))
				return 2;
			else if(ct.equals("THEATER"))
				return 3;
			else
				return 4;
		}
		
		
		
		//......................SysSetupMenu........................			
		if (MenuItemName.equals("shortcut_setup_sys_poweron_source_")) {
			String ct=ucp.getParams("PowerOnSource");
			if(ct.equals("MEMORY"))
				return 0;
			else if(ct.equals("COOCAA"))
				return 1;
			else if(ct.equals("TV"))
				return 2;
			else if(ct.equals("AV"))
				return 3;
			else if(ct.equals("YUV"))
				return 4;
			else if(ct.equals("HDMI1"))
				return 5;
			else if(ct.equals("HDMI2"))
				return 6;
			else if(ct.equals("HDMI3"))
				return 7;
			else if(ct.equals("VGA"))
				return 8;
			else 
				return 1;
		} else if (MenuItemName.equals("shortcut_setup_sys_sleep_time_")) {
			String ct=ucp.getParams("SleepTime");
			if(ct.equals("0"))
				return 0;
			else if(ct.equals("5"))
				return 1;
			else if(ct.equals("15"))
				return 2;
			else if(ct.equals("30"))
				return 3;
			else if(ct.equals("60"))
				return 4;
			else if(ct.equals("90"))
				return 5;
			else
				return 6;
		} else if (MenuItemName.equals("shortcut_setup_sys_six_color_")) {
			String ct=ucp.getParams("SixColor");
			if(ct.equals("OFF"))
				return 0;
			else if(ct.equals("OPTI"))
				return 1;
			else if(ct.equals("ENHANCE"))
				return 2;
//				else
//					return 3;
		} 
		else if (MenuItemName.equals("shortcut_setup_sys_matrix_")) {
			String ct=ucp.getParams("LocalDimming");
			if(ct.equals("OFF"))
				return 0;
			else if(ct.equals("ON"))
				return 1;
//				else
//					return 2;
		} 
		else if (MenuItemName.equals("shortcut_setup_sys_dream_panel_")) {
			String ct=ucp.getParams("DreamPanel");
			if(ct.equals("OFF"))
				return 0;
			else if(ct.equals("SENSOR"))
				return 1;
			else if(ct.equals("SCENE"))
				return 2;
			else if(ct.equals("ALL"))
				return 3;
//				else
//					return 4;
		}
		else if (MenuItemName.equals("shortcut_setup_sys_memc_")) {
			String ct=ucp.getParams("MEMC");
			if(ct.equals("OFF"))
				return 0;
			else if(ct.equals("WEAK"))
				return 1;
			else if(ct.equals("MID"))
				return 2;
			else if(ct.equals("STRONG"))
				return 3;		
		}
		else if (MenuItemName.equals("shortcut_setup_sys_woofer_switch_")) {
			String ct=ucp.getParams("SubwooferSwitch");
			if(ct.equals("OFF"))
				return 0;
			else 
				return 1;
		}
		else if (MenuItemName.equals("shortcut_setup_sys_poweron_music_")) {
			String ct=ucp.getParams("PowerOnMusic");
			if(ct.equals("OFF"))
				return 0;
			else 
				return 1;
		}
		else if (MenuItemName.equals("shortcut_setup_sys_wall_effects_")) {
			String ct=ucp.getParams("WallEffects");
			if(ct.equals("OFF"))
				return 0;
			else 
				return 1;
		}
		else if (MenuItemName.equals("shortcut_setup_sys_filelist_mode_")) {
			String ct=ucp.getParams("ListMode");
			if(ct.equals("FILE"))
				return 0;
			else 
				return 1;
		}
		
		//......................shortcut_common_source.....................
		else if (MenuItemName.equals("shortcut_common_source_")) {
			String ct = ucp.getParams("CHANNEL");
			if(ct.equals("TV"))
				return 0;
			else if(ct.equals("AV"))
				return 1;
			else if(ct.equals("YUV"))
				return 2;
			else if(ct.equals("HDMI1"))
				return 3;
			else if(ct.equals("HDMI2"))
				return 4;
			else if(ct.equals("HDMI3"))
				return 5;
			else if(ct.equals("VGA"))
				return 6;
			else 
				return 0;
		}	
				
		return -1;
	}
	
	
	public String InitParamValue(String MenuItemName) {
		
		if(ucp==null){
			Log.d("uart", "........ InitParamValue ucp==null .........\n");
			return "";
		}
		
		String picmode = ucp.getParams("PictureMode");

		//picmode
		if(!picmode.equals(""))	{			
			
			if (MenuItemName.equals("shortcut_setup_video_brightness_")) {
				return ucp.getParams("Brightness_" + picmode);
			} else if (MenuItemName.equals("shortcut_setup_video_contrast_")) {
				return ucp.getParams("Contrast_" + picmode);
			} else if (MenuItemName.equals("shortcut_setup_video_color_")) {
				return ucp.getParams("Color_" + picmode);
			} else if (MenuItemName.equals("shortcut_setup_video_sharpness_")) {
				return ucp.getParams("Sharpness_" + picmode);
			}
		}

		// ......................AmlogicSoundSetup........................
		if (MenuItemName.equals("shortcut_common_vol_")) {
			return ucp.getParams("Volumn");
		} 

		else if (MenuItemName.equals("shortcut_setup_audio_balance_")) {
			return ucp.getParams("Balance");
		}
		
		//......................SysSetupMenu........................
		if (MenuItemName.equals("shortcut_setup_sys_back_light_")) {
			return ucp.getParams("BackLight");
		} else if (MenuItemName.equals("shortcut_setup_sys_woofer_vol_")) {
			return ucp.getParams("SubwooferVol");
		}
		
		return "";		
	}	
	
}
