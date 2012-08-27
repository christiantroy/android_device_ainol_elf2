package com.amlogic;

import java.util.ArrayList;
import java.util.List;

import android.os.RemoteException;
import android.util.Log;

import com.amlogic.serialport.Iuartservice;

public class InitMenuState {

	public String GetInitType(String type)
	{
		if(type.equals("AnalogTV"))
		{
			return "tv_set";
		}
		else
		if(type.equals("Music"))
		{
			return "music";
		}
		else
		if(type.equals("Picture"))
		{
			return "picture";
		}
		else
		if(type.equals("Txt"))
		{
			return "txt";
		}
		else
		if(type.equals("MusPic"))
		{
			return "music";
		}
		else
		if(type.equals("MusT"))
		{
			return "music";
		}
		else
		if(type.equals("PicT"))
		{
			return "picture";
		}
		else
		if(type.equals("MusPT"))
		{
			return "music";
		}
		else				
			return "";
	}
	
	
	private Iuartservice mIuartservice = null;
	public void setIuartService(Iuartservice uartservice) {		
		mIuartservice=uartservice;
	}
	
	//state "0" disable tv vol+-
	//state	"1" enable tv vol+-
	//state	"2" show tv volumn menu and enable tv vol+-
	public void UartSendVolumeOsd(String state) {
		if (mIuartservice != null)
			try {
				mIuartservice.UartSend("VolumeOsd", state);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
	}	
	
	public String Getvideoplaymode(){
		try {
			if(mIuartservice==null){
				Log.d("uart", "........ GetVideoPlaymode mIuartservice==null .........\n");
			}
			else{
				return mIuartservice.GetParam("VidRepeatMode");		
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void DialogSetDefault(){
		try {
			if(mIuartservice==null){
				Log.d("uart", "........ DialogSetDefault mIuartservice==null .........\n");
			}
			else{
				mIuartservice.UartSend("setdefault", "1");
				mIuartservice.SetDefault();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}	
	}
	
	public void BalancerKeyProcess(boolean doit, int high,byte low, int[] a){
		try {
			if(mIuartservice==null){
				Log.d("uart", "........ BalancerKeyProcess mIuartservice==null .........\n");
			}
			else if(doit)
			{
				mIuartservice.UartSend("eq100", (((high<<8)& 0xFFff)+low)+"");
				if(a[5]!=4)
					mIuartservice.SaveParam("SoundMode", "USER");
				switch(high){
				case 0:
					mIuartservice.SaveParam("SoundMode_USER_EQ_100", low+"");
					break;
				case 1:
					mIuartservice.SaveParam("SoundMode_USER_EQ_300", low+"");
					break;
				case 2:
					mIuartservice.SaveParam("SoundMode_USER_EQ_1K", low+"");
					break;
				case 3:
					mIuartservice.SaveParam("SoundMode_USER_EQ_3K", low+"");
					break;
				case 4:
					mIuartservice.SaveParam("SoundMode_USER_EQ_10K", low+"");
					break;
				}
			}
			else
			{
				mIuartservice.SaveParam("SoundMode_USER_EQ_100", a[0]+"");
				mIuartservice.SaveParam("SoundMode_USER_EQ_300", a[1]+"");
				mIuartservice.SaveParam("SoundMode_USER_EQ_1K", a[2]+"");
				mIuartservice.SaveParam("SoundMode_USER_EQ_3K", a[3]+"");
				mIuartservice.SaveParam("SoundMode_USER_EQ_10K", a[4]+"");					
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}	
	}
	
	public void BalancerKeyProcess(int soundmode) {
		
		String mode=null;
		if(soundmode==0)
			mode="STD";
		else if(soundmode==1)
			mode="MUSIC";
		else if(soundmode==2)
			mode="NEWS";
		else if(soundmode==3)
			mode="THEATER";
		else if(soundmode==4)
			mode="USER";
		if(mode!=null){
			try {
				mIuartservice.SaveParam("SoundMode", mode);
				mIuartservice.UartSend("SoundMode", soundmode+"");
			} catch (RemoteException e) {
				e.printStackTrace();
			}			
		}
	}
	
	public List<String> InitBalancer() {
		
		if(mIuartservice==null){
			Log.d("uart", "........ InitBalancer mIuartservice==null .........\n");
			return null;
		}
		List<String> balance_data=new ArrayList<String>(); 
		try {			
			balance_data.add(mIuartservice.GetParam("SoundMode_STD_EQ_100"));
			balance_data.add(mIuartservice.GetParam("SoundMode_STD_EQ_300"));
			balance_data.add(mIuartservice.GetParam("SoundMode_STD_EQ_1K"));
			balance_data.add(mIuartservice.GetParam("SoundMode_STD_EQ_3K"));
			balance_data.add(mIuartservice.GetParam("SoundMode_STD_EQ_10K"));
			
			balance_data.add(mIuartservice.GetParam("SoundMode_MUSIC_EQ_100"));
			balance_data.add(mIuartservice.GetParam("SoundMode_MUSIC_EQ_300"));
			balance_data.add(mIuartservice.GetParam("SoundMode_MUSIC_EQ_1K"));
			balance_data.add(mIuartservice.GetParam("SoundMode_MUSIC_EQ_3K"));
			balance_data.add(mIuartservice.GetParam("SoundMode_MUSIC_EQ_10K"));
			
			balance_data.add(mIuartservice.GetParam("SoundMode_NEWS_EQ_100"));
			balance_data.add(mIuartservice.GetParam("SoundMode_NEWS_EQ_300"));
			balance_data.add(mIuartservice.GetParam("SoundMode_NEWS_EQ_1K"));
			balance_data.add(mIuartservice.GetParam("SoundMode_NEWS_EQ_3K"));
			balance_data.add(mIuartservice.GetParam("SoundMode_NEWS_EQ_10K"));
			
			balance_data.add(mIuartservice.GetParam("SoundMode_THEATER_EQ_100"));
			balance_data.add(mIuartservice.GetParam("SoundMode_THEATER_EQ_300"));
			balance_data.add(mIuartservice.GetParam("SoundMode_THEATER_EQ_1K"));
			balance_data.add(mIuartservice.GetParam("SoundMode_THEATER_EQ_3K"));
			balance_data.add(mIuartservice.GetParam("SoundMode_THEATER_EQ_10K"));
			
			balance_data.add(mIuartservice.GetParam("SoundMode_USER_EQ_100"));
			balance_data.add(mIuartservice.GetParam("SoundMode_USER_EQ_300"));
			balance_data.add(mIuartservice.GetParam("SoundMode_USER_EQ_1K"));
			balance_data.add(mIuartservice.GetParam("SoundMode_USER_EQ_3K"));
			balance_data.add(mIuartservice.GetParam("SoundMode_USER_EQ_10K"));	
					
			
			balance_data.add(mIuartservice.GetParam("SoundMode"));
			
			return balance_data;
			
		} catch (RemoteException e) {			
			e.printStackTrace();
		}
		return null;		
	}

	public int InitSelectFrameValue(String MenuItemName, String mediaType) {
		
		if(mIuartservice==null){
			Log.d("uart", "........ InitSelectFrameValue mIuartservice==null .........\n");
			return -1;
		}
		
		try {
			String channel = mIuartservice.GetParam("CHANNEL");		
			
			// ................����ͼƬ��Ƶ��������.....................
			if(MenuItemName.equals("shortcut_common_playmode_")){
				String ct;
				if(mediaType.equals("music"))
					ct=mIuartservice.GetParam("MusRepeatMode");
				else if(mediaType.equals("picture"))
					ct=mIuartservice.GetParam("PicRepeatMode");
				else
					ct=mIuartservice.GetParam("VidRepeatMode");
				if(ct.equals("FOLDER"))
					return 1;
				else if(ct.equals("RANDOM"))
					return 2;
				else
					return 0;
			}
			
			//......................ͼƬ��������........................
			 if (MenuItemName.equals("shortcut_picture_switch_time_")) {
					String ct=mIuartservice.GetParam("PicPlayMode");
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
				 String ct=mIuartservice.GetParam("PicSwitchMode");
					if(ct.equals("Normal"))
						return 0;
					else			//Random
						return 1;
			 }	
			 
			//......................�ı���������........................
			 if (MenuItemName.equals("shortcut_txt_turn_page_")) {
				 String ct=mIuartservice.GetParam("TxtTurnNum");
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
			 
				//......................��������........................
			 if(MenuItemName.equals("shortcut_videochat_local_video_"))
			 { 
				 String ct=mIuartservice.GetParam("LocalVideoOn");
				 Log.e("amlogicmenu", ct);
					if(ct.equals("on"))										
						return 0;					
					else 					
						return 1;
				
			 }
			 
			 if(MenuItemName.equals("shortcut_videochat_remote_video_"))
			 { 
				 String ct=mIuartservice.GetParam("RemoteVideoOn");
				 Log.e("amlogicmenu", ct);
					if(ct.equals("on"))										
						return 0;					
					else 					
						return 1;
				
			 }
			 
			 if(MenuItemName.equals("shortcut_videochat_fullscr_"))
			 { 
				 String ct=mIuartservice.GetParam("FullScreenOn");
				 Log.e("amlogicmenu", ct);
					if(ct.equals("on"))										
						return 0;					
					else 					
						return 1;
				
			 }
			
			
			// ......................amlogicPicSetup........................	
			if (MenuItemName.equals("shortcut_setup_video_temperature_")) {
				String ct=mIuartservice.GetParam("ColorTemp");
				if(ct.equals("COLD"))
					return 0;
				else if(ct.equals("STD"))
					return 1;
				else
					return 2;			
			} else if (MenuItemName.equals("shortcut_setup_video_dnr_")) {
				String ct=mIuartservice.GetParam(channel + "_NR");
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
				String ct=mIuartservice.GetParam("PictureMode");
				if(ct.equals("STD"))
					return 0;
				else if(ct.equals("VIVID"))
					return 1;
				else if(ct.equals("SOFT"))
					return 2;
				else
					return 3;
			} else if (MenuItemName.equals("shortcut_setup_video_display_mode_")) {
				String ct=mIuartservice.GetParam(channel + "_DisplayMode");
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
			
			// ......................amlogicSoundSetup........................
			if (MenuItemName.equals("shortcut_setup_audio_srs_")) {
				String ct=mIuartservice.GetParam("SRS");
				if(ct.equals("ON"))
					return 1;
				else 
					return 0;
			} else if (MenuItemName.equals("shortcut_setup_audio_voice_")) {
				String ct=mIuartservice.GetParam("VOICE");
				if(ct.equals("ON"))
					return 1;
				else 
					return 0;
			} else if (MenuItemName.equals("shortcut_setup_audio_increase_bass_")) {
				String ct=mIuartservice.GetParam("INCREASE_BASS");
				if(ct.equals("ON"))
					return 1;
				else 
					return 0;
			}else if (MenuItemName.equals("shortcut_setup_audio_sound_mode_")) {
				String ct=mIuartservice.GetParam("SoundMode");
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
				String ct=mIuartservice.GetParam("PowerOnSource");
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
				String ct=mIuartservice.GetParam("SleepTime");
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
				String ct=mIuartservice.GetParam("SixColor");
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
				String ct=mIuartservice.GetParam("LocalDimming");
				if(ct.equals("OFF"))
					return 0;
				else if(ct.equals("ON"))
					return 1;
//				else
//					return 2;
			} 
			else if (MenuItemName.equals("shortcut_setup_sys_dream_panel_")) {
				String ct=mIuartservice.GetParam("DreamPanel");
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
				String ct=mIuartservice.GetParam("MEMC");
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
				String ct=mIuartservice.GetParam("SubwooferSwitch");
				if(ct.equals("OFF"))
					return 0;
				else 
					return 1;
			}
			else if (MenuItemName.equals("shortcut_setup_sys_poweron_music_")) {
				String ct=mIuartservice.GetParam("PowerOnMusic");
				if(ct.equals("OFF"))
					return 0;
				else 
					return 1;
			}
			else if (MenuItemName.equals("shortcut_setup_sys_wall_effects_")) {
				String ct=mIuartservice.GetParam("WallEffects");
				if(ct.equals("OFF"))
					return 0;
				else 
					return 1;
			}
			else if (MenuItemName.equals("shortcut_setup_sys_filelist_mode_")) {
				String ct=mIuartservice.GetParam("ListMode");
				if(ct.equals("FILE"))
					return 0;
				else 
					return 1;
			}	
			
			//......................shortcut_common_source.....................
//			if (MenuItemName.equals("shortcut_common_source_")) {
//				String ct = mIuartservice.GetParam("CHANNEL");
//			}			
			
		
		} catch (RemoteException e) {			
			e.printStackTrace();
		}
				
		return -1;
	}
	
	
	public String InitParamValue(String MenuItemName) {
		
		if(mIuartservice==null){
			Log.d("uart", "........ InitParamValue mIuartservice==null .........\n");
			return "";
		}
		
		try {
			String picmode = mIuartservice.GetParam("PictureMode");

			//picmode
			if(!picmode.equals(""))	{			
				
				if (MenuItemName.equals("shortcut_setup_video_brightness_")) {
					return mIuartservice.GetParam("Brightness_" + picmode);
				} else if (MenuItemName.equals("shortcut_setup_video_contrast_")) {
					return mIuartservice.GetParam("Contrast_" + picmode);
				} else if (MenuItemName.equals("shortcut_setup_video_color_")) {
					return mIuartservice.GetParam("Color_" + picmode);
				} else if (MenuItemName.equals("shortcut_setup_video_sharpness_")) {
					return mIuartservice.GetParam("Sharpness_" + picmode);
				}
			}
		
			// ......................amlogicSoundSetup........................
			if (MenuItemName.equals("shortcut_common_vol_")) {
				return mIuartservice.GetParam("Volumn");
			} 
//			else if (MenuItemName.equals("shortcut_setup_audio_bass_")) {
//				return mIuartservice.GetParam("SoundMode_USER_Bass");
//			} else if (MenuItemName.equals("shortcut_setup_audio_treble_")) {
//				return mIuartservice.GetParam("SoundMode_USER_Trebble");
//			} 
			else if (MenuItemName.equals("shortcut_setup_audio_balance_")) {
				return mIuartservice.GetParam("Balance");
			}
			
			//......................SysSetupMenu........................
			if (MenuItemName.equals("shortcut_setup_sys_back_light_")) {
				return mIuartservice.GetParam("BackLight");
			} else if (MenuItemName.equals("shortcut_setup_sys_woofer_vol_")) {
				return mIuartservice.GetParam("SubwooferVol");
			} 
			
		
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
		
		return "";		
	}

	public void UartSendAndSaveParam(String mediaType,String... Menu){
		if(mIuartservice==null){
			Log.d("uart", "........ UartSendAndSaveParam mIuartservice==null .........\n");
			return;
		}
			
		if(Menu[0].equals("")||(Menu[0]==null))
			return;
	try {	
		//�����
		if (Menu[0].equals("netupdate")) {
			mIuartservice.UartSend("netupdate", Menu[1]);
			return;
		}	
		//3D game
		if (Menu[0].equals("3dgame")) {
			mIuartservice.UartSend("3dgame", Menu[1]);
			return;
		}			
		//����
		if (Menu[0].equals("shortcut_common_mute_")) {
			String mute = mIuartservice.GetParam("Mute");
			if(mute != null){
				if(mute.equals("0"))
				{
					int value=((1<<8)& 0xFFff)+Integer.parseInt(mIuartservice.GetParam("Volumn"));
					mIuartservice.UartSend("Volumn", String.valueOf(value));
					mIuartservice.SaveParam("Mute", "1");
				}
				else
				{
					int value=Integer.parseInt(mIuartservice.GetParam("Volumn"));
					mIuartservice.UartSend("Volumn", String.valueOf(value));
					mIuartservice.SaveParam("Mute", "0");
				}
			}
			return;
		}
		
		//......................�ظ�ģʽ      ����ͼƬ��Ƶ........................
		 if (Menu[0].contains("shortcut_common_playmode_")) {
		 
				if (Menu[0].equals("shortcut_common_playmode_single")) {
					if(mediaType.equals("music"))
						mIuartservice.SaveParam("MusRepeatMode", "SINGLE");
					else
						mIuartservice.SaveParam("VidRepeatMode", "SINGLE");
				} else if (Menu[0].equals("shortcut_common_playmode_folder")) {
					if(mediaType.equals("music"))
						mIuartservice.SaveParam("MusRepeatMode", "FOLDER");
					else if(mediaType.equals("picture"))
						mIuartservice.SaveParam("PicRepeatMode", "FOLDER");
					else
						mIuartservice.SaveParam("VidRepeatMode", "FOLDER");
				} else if (Menu[0].equals("shortcut_common_playmode_rand")) {
					if(mediaType.equals("music"))
						mIuartservice.SaveParam("MusRepeatMode", "RANDOM");
					else if(mediaType.equals("picture"))
						mIuartservice.SaveParam("PicRepeatMode", "RANDOM");
					else
						mIuartservice.SaveParam("VidRepeatMode", "RANDOM");
				} else if (Menu[0].equals("shortcut_common_playmode_normal")) {
					mIuartservice.SaveParam("PicRepeatMode", "ORDER");
				}	
				return;
		 }

		//......................ͼƬ��������........................
		 if (Menu[0].contains("shortcut_picture_switch_time_")) {

				if (Menu[0].equals("shortcut_picture_switch_time_3s")) {
					mIuartservice.SaveParam("PicPlayMode", "3S");
				} else if (Menu[0].equals("shortcut_picture_switch_time_5s")) {
					mIuartservice.SaveParam("PicPlayMode", "5S");
				} else if (Menu[0].equals("shortcut_picture_switch_time_10s")) {
					mIuartservice.SaveParam("PicPlayMode", "10S");
				} else	//�ֶ�
					mIuartservice.SaveParam("PicPlayMode", "HAND");
				return;
		 }
		 else  if (Menu[0].contains("shortcut_picture_switch_mode_")) {

				if (Menu[0].equals("shortcut_picture_switch_mode_1")) {
					mIuartservice.SaveParam("PicSwitchMode", "Normal");
				} 
				else if (Menu[0].equals("shortcut_picture_switch_mode_2")) {
					mIuartservice.SaveParam("PicSwitchMode", "Random");
				}
				return;
		 }	
		 
		//......................�ı���������........................
		 if (Menu[0].contains("shortcut_txt_turn_page_")) {

				if (Menu[0].equals("shortcut_txt_turn_page_next_10p")) {
					mIuartservice.SaveParam("TxtTurnNum", "10");
				} else if (Menu[0].equals("shortcut_txt_turn_page_next_20p")) {
					mIuartservice.SaveParam("TxtTurnNum", "20");
				} else if (Menu[0].equals("shortcut_txt_turn_page_next_50p")) {
					mIuartservice.SaveParam("TxtTurnNum", "50");
				} else if (Menu[0].equals("shortcut_txt_turn_page_prve_10p")) {
					mIuartservice.SaveParam("TxtTurnNum", "-10");
				} else if (Menu[0].equals("shortcut_txt_turn_page_prve_20p")) {
					mIuartservice.SaveParam("TxtTurnNum", "-20");
				} else if (Menu[0].equals("shortcut_txt_turn_page_prve_50p")) {
					mIuartservice.SaveParam("TxtTurnNum", "-50");
				}
				return;
		 }
	
	//..........................��������............................
		 if(Menu[0].contains("shortcut_videochat_local_video_"))
		 {
			 Log.e("amlogicmenu", Menu[0]);
			 if (Menu[0].equals("shortcut_videochat_local_video_on")) {
					mIuartservice.SaveParam("LocalVideoOn", "on");
				} else if (Menu[0].equals("shortcut_videochat_local_video_off")) {
					mIuartservice.SaveParam("LocalVideoOn", "off");
				}
			 return;
		 }	
		 
		 if(Menu[0].contains("shortcut_videochat_remote_video_"))
		 {
			 Log.e("amlogicmenu", Menu[0]);
			 if (Menu[0].equals("shortcut_videochat_remote_video_on")) {
					mIuartservice.SaveParam("RemoteVideoOn", "on");
				} else if (Menu[0].equals("shortcut_videochat_remote_video_off")) {
					mIuartservice.SaveParam("RemoteVideoOn", "off");
				}
			 return;
		 }	
		 
		 if(Menu[0].contains("shortcut_videochat_fullscr_"))
		 {
			 Log.e("amlogicmenu", Menu[0]);
			 if (Menu[0].equals("shortcut_videochat_fullscr_on")) {
					mIuartservice.SaveParam("FullScreenOn", "on");
				} else if (Menu[0].equals("shortcut_videochat_fullscr_off")) {
					mIuartservice.SaveParam("FullScreenOn", "off");
				}
			 return;
		 }	
		
		//......................amlogicPicSetup........................
		String channel = mIuartservice.GetParam("CHANNEL");		
		String picmode = mIuartservice.GetParam("PictureMode");

		if (Menu[0].equals("shortcut_setup_video_brightness_")) {

			mIuartservice.UartSend("brightness", Menu[1]);
			mIuartservice.SaveParam("Brightness_USER", Menu[1]);
			
			if(!picmode.equals("USER")){

				mIuartservice.SaveParam("Contrast_USER", mIuartservice.GetParam("Contrast_"+picmode));
				mIuartservice.SaveParam("Color_USER", mIuartservice.GetParam("Color_"+picmode));
				mIuartservice.SaveParam("Sharpness_USER", mIuartservice.GetParam("Sharpness_"+picmode));
			
				mIuartservice.UartSend("PictureMode", "3");
				mIuartservice.SaveParam("PictureMode", "USER");		
			}
	
		} else if (Menu[0].equals("shortcut_setup_video_contrast_")) {

			mIuartservice.UartSend("contrast", Menu[1]);
			mIuartservice.SaveParam("Contrast_USER", Menu[1]);
			
			if(!picmode.equals("USER")){

				mIuartservice.SaveParam("Brightness_USER", mIuartservice.GetParam("Brightness_"+picmode));
				mIuartservice.SaveParam("Color_USER", mIuartservice.GetParam("Color_"+picmode));
				mIuartservice.SaveParam("Sharpness_USER", mIuartservice.GetParam("Sharpness_"+picmode));
			
				mIuartservice.UartSend("PictureMode", "3");
				mIuartservice.SaveParam("PictureMode", "USER");		
			}
			
			
		} else if (Menu[0].equals("shortcut_setup_video_color_")) {

			mIuartservice.UartSend("colour", Menu[1]);
			mIuartservice.SaveParam("Color_USER", Menu[1]);
			
			if(!picmode.equals("USER")){

				mIuartservice.SaveParam("Brightness_USER", mIuartservice.GetParam("Brightness_"+picmode));
				mIuartservice.SaveParam("Contrast_USER", mIuartservice.GetParam("Contrast_"+picmode));
				mIuartservice.SaveParam("Sharpness_USER", mIuartservice.GetParam("Sharpness_"+picmode));
			
				mIuartservice.UartSend("PictureMode", "3");
				mIuartservice.SaveParam("PictureMode", "USER");	
			}
			
			
//		} else if (Menu[0].equals("shortcut_setup_video_hue_")) {
//
//			mIuartservice.UartSend("hue", Menu[1]);
//			mIuartservice.SaveParam(channel + "_Hue", Menu[1]);
		} else if (Menu[0].equals("shortcut_setup_video_sharpness_")) {

			mIuartservice.UartSend("sharpness", Menu[1]);
			mIuartservice.SaveParam("Sharpness_USER", Menu[1]);
			
			if(!picmode.equals("USER")){

				mIuartservice.SaveParam("Brightness_USER", mIuartservice.GetParam("Brightness_"+picmode));
				mIuartservice.SaveParam("Contrast_USER", mIuartservice.GetParam("Contrast_"+picmode));
				mIuartservice.SaveParam("Color_USER", mIuartservice.GetParam("Color_"+picmode));
			
				mIuartservice.UartSend("PictureMode", "3");
				mIuartservice.SaveParam("PictureMode", "USER");	
			}
			
			
		} else if (Menu[0].contains("shortcut_setup_video_temperature_")) {

			if (Menu[0].equals("shortcut_setup_video_temperature_std")) {
				mIuartservice.UartSend("ColorTemp", "1");
				mIuartservice.SaveParam("ColorTemp", "STD");
			} else if (Menu[0].equals("shortcut_setup_video_temperature_warm")) {
				mIuartservice.UartSend("ColorTemp", "2");
				mIuartservice.SaveParam("ColorTemp", "WARM");
			} else if (Menu[0].equals("shortcut_setup_video_temperature_cold")) {
				mIuartservice.UartSend("ColorTemp", "0");
				mIuartservice.SaveParam("ColorTemp", "COLD");
			}

		} else if (Menu[0].contains("shortcut_setup_video_dnr_")) {

			if (Menu[0].equals("shortcut_setup_video_dnr_off")) {
				mIuartservice.UartSend("_NR", "0");
				mIuartservice.SaveParam(channel + "_NR", "OFF");
			} else if (Menu[0].equals("shortcut_setup_video_dnr_weak")) {
				mIuartservice.UartSend("_NR", "1");
				mIuartservice.SaveParam(channel + "_NR", "LOW");
			} else if (Menu[0].equals("shortcut_setup_video_dnr_mid")) {
				mIuartservice.UartSend("_NR", "2");
				mIuartservice.SaveParam(channel + "_NR", "MID");
			} else if (Menu[0].equals("shortcut_setup_video_dnr_strong")) {
				mIuartservice.UartSend("_NR", "3");
				mIuartservice.SaveParam(channel + "_NR", "HIGH");
			} else{
				mIuartservice.UartSend("_NR", "4");
				mIuartservice.SaveParam(channel + "_NR", "AUTO");
			}
				

		} else if (Menu[0].contains("shortcut_setup_video_picture_mode_")) {

			if (Menu[0].equals("shortcut_setup_video_picture_mode_std")) {
				mIuartservice.UartSend("PictureMode", "0");
				mIuartservice.SaveParam("PictureMode", "STD");
			} else if (Menu[0]
					.equals("shortcut_setup_video_picture_mode_vivid")) {
				mIuartservice.UartSend("PictureMode", "1");
				mIuartservice.SaveParam("PictureMode", "VIVID");
			} else if (Menu[0]
					.equals("shortcut_setup_video_picture_mode_soft")) {
				mIuartservice.UartSend("PictureMode", "2");
				mIuartservice.SaveParam("PictureMode", "SOFT");
			} else if (Menu[0]
					.equals("shortcut_setup_video_picture_mode_user")) {
				mIuartservice.UartSend("PictureMode", "3");
				mIuartservice.SaveParam("PictureMode", "USER");
			}

		} 
		else if (Menu[0].contains("shortcut_setup_video_display_mode_")) {

			if (Menu[0].equals("shortcut_setup_video_display_mode_169")) {
				mIuartservice.UartSend("_DisplayMode", "0");
				mIuartservice.SaveParam(channel + "_DisplayMode","169");
			} else if (Menu[0].equals("shortcut_setup_video_display_mode_personal")) {
				mIuartservice.UartSend("_DisplayMode", "1");
				mIuartservice.SaveParam(channel + "_DisplayMode", "PERSON");
			} else if (Menu[0].equals("shortcut_setup_video_display_mode_theater")) {
				mIuartservice.UartSend("_DisplayMode", "2");
				mIuartservice.SaveParam(channel + "_DisplayMode", "THEATER");
			} else if (Menu[0].equals("shortcut_setup_video_display_mode_subtitle")) {
				mIuartservice.UartSend("_DisplayMode", "3");
				mIuartservice.SaveParam(channel + "_DisplayMode", "SUBTITLE");
			} else if (Menu[0].equals("shortcut_setup_video_display_mode_43")) {
				mIuartservice.UartSend("_DisplayMode", "4");
				mIuartservice.SaveParam(channel + "_DisplayMode", "4:3");
			}else if (Menu[0].equals("shortcut_setup_video_display_mode_panorama")) {
				mIuartservice.UartSend("_DisplayMode", "5");
				mIuartservice.SaveParam(channel + "_DisplayMode", "PANORA");
			}
		}
		
		//......................amlogicSoundSetup........................
		if(Menu[0].equals("shortcut_common_vol_")){	
			mIuartservice.UartSend("Volumn", Menu[1]);
			mIuartservice.SaveParam("Volumn", Menu[1]);
		}
//		else if(Menu[0].equals("shortcut_setup_audio_bass_")){
//			mIuartservice.UartSend("SoundMode_USER_Bass", Menu[1]);
//			mIuartservice.SaveParam("SoundMode_USER_Bass", Menu[1]);
//		}
//		else if(Menu[0].equals("shortcut_setup_audio_treble_")){
//			mIuartservice.UartSend("SoundMode_USER_Trebble", Menu[1]);
//			mIuartservice.SaveParam("SoundMode_USER_Trebble", Menu[1]);
//		}
		else if(Menu[0].equals("shortcut_setup_audio_balance_")){
			mIuartservice.UartSend("balance", Menu[1]);
			mIuartservice.SaveParam("Balance", Menu[1]);
		}
		else if(Menu[0].contains("shortcut_setup_audio_srs_")){
			
			if(Menu[0].equals("shortcut_setup_audio_srs_on")){
				mIuartservice.UartSend("SRS", "1");
				mIuartservice.SaveParam("SRS", "ON");
			}
			else if(Menu[0].equals("shortcut_setup_audio_srs_off")){
				mIuartservice.UartSend("SRS", "0");
				mIuartservice.SaveParam("SRS", "OFF");
			}					
		}
		else if(Menu[0].contains("shortcut_setup_audio_voice_")){
			
			if(Menu[0].equals("shortcut_setup_audio_voice_on")){
				mIuartservice.UartSend("VOICE", "1");
				mIuartservice.SaveParam("VOICE", "ON");
			}
			else if(Menu[0].equals("shortcut_setup_audio_voice_off")){
				mIuartservice.UartSend("VOICE", "0");
				mIuartservice.SaveParam("VOICE", "OFF");
			}
		}
		else if(Menu[0].contains("shortcut_setup_audio_increase_bass_")){
			
			if(Menu[0].equals("shortcut_setup_audio_increase_bass_on")){
				mIuartservice.UartSend("INCREASE_BASS", "1");
				mIuartservice.SaveParam("INCREASE_BASS", "ON");
			}
			else if(Menu[0].equals("shortcut_setup_audio_increase_bass_off")){
				mIuartservice.UartSend("INCREASE_BASS", "0");
				mIuartservice.SaveParam("INCREASE_BASS", "OFF");
			}
		}
		else if(Menu[0].contains("shortcut_setup_audio_sound_mode_")){
			
			if(Menu[0].equals("shortcut_setup_audio_sound_mode_std")){
				mIuartservice.UartSend("SoundMode", "0");
				mIuartservice.SaveParam("SoundMode", "STD");
			}
			else if(Menu[0].equals("shortcut_setup_audio_sound_mode_news")){
				mIuartservice.UartSend("SoundMode", "2");
				mIuartservice.SaveParam("SoundMode", "NEWS");
			}
			else if(Menu[0].equals("shortcut_setup_audio_sound_mode_theater")){
				mIuartservice.UartSend("SoundMode", "3");
				mIuartservice.SaveParam("SoundMode", "THEATER");
			}
			else if(Menu[0].equals("shortcut_setup_audio_sound_mode_music")){
				mIuartservice.UartSend("SoundMode", "1");
				mIuartservice.SaveParam("SoundMode", "MUSIC");
			}
			else if(Menu[0].equals("shortcut_setup_audio_sound_mode_user")){
				mIuartservice.UartSend("SoundMode", "4");
				mIuartservice.SaveParam("SoundMode", "USER");
			}
		}
		
		//......................SysSetupMenu........................
		if(Menu[0].contains("shortcut_setup_sys_poweron_source_")){			
			if(Menu[0].equals("shortcut_setup_sys_poweron_source_memory")){
				mIuartservice.UartSend("srcselect", "0");
				mIuartservice.SaveParam("PowerOnSource", "MEMORY");
			}
			else if(Menu[0].equals("shortcut_setup_sys_poweron_source_coocaa")){
				mIuartservice.UartSend("srcselect", "12");
				mIuartservice.SaveParam("PowerOnSource", "COOCAA");
			}
			else if(Menu[0].equals("shortcut_setup_sys_poweron_source_tv")){
				mIuartservice.UartSend("srcselect", "1");
				mIuartservice.SaveParam("PowerOnSource", "TV");
			}		
			else if(Menu[0].equals("shortcut_setup_sys_poweron_source_av1")){
				mIuartservice.UartSend("srcselect", "2");
				mIuartservice.SaveParam("PowerOnSource", "AV");
			}
			else if(Menu[0].equals("shortcut_setup_sys_poweron_source_yuv1")){
				mIuartservice.UartSend("srcselect", "6");
				mIuartservice.SaveParam("PowerOnSource", "YUV");
			}
			else if(Menu[0].equals("shortcut_setup_sys_poweron_source_hdmi1")){
				mIuartservice.UartSend("srcselect", "8");
				mIuartservice.SaveParam("PowerOnSource", "HDMI1");
			}
			else if(Menu[0].equals("shortcut_setup_sys_poweron_source_hdmi2")){
				mIuartservice.UartSend("srcselect", "9");
				mIuartservice.SaveParam("PowerOnSource", "HDMI2");
			}
			else if(Menu[0].equals("shortcut_setup_sys_poweron_source_hdmi3")){
				mIuartservice.UartSend("srcselect", "10");
				mIuartservice.SaveParam("PowerOnSource", "HDMI3");
			}
			else if(Menu[0].equals("shortcut_setup_sys_poweron_source_vga")){
				mIuartservice.UartSend("srcselect", "11");
				mIuartservice.SaveParam("PowerOnSource", "VGA");
			}
		}
		else if(Menu[0].contains("shortcut_setup_sys_sleep_time_")){
			
			if(Menu[0].equals("shortcut_setup_sys_sleep_time_off")){
				mIuartservice.UartSend("sleeptime", "0");
				mIuartservice.SaveParam("SleepTime", "0");
			}
			else if(Menu[0].equals("shortcut_setup_sys_sleep_time_5")){
				mIuartservice.UartSend("sleeptime", "1");
				mIuartservice.SaveParam("SleepTime", "5");
			}	
			else if(Menu[0].equals("shortcut_setup_sys_sleep_time_15")){
				mIuartservice.UartSend("sleeptime", "2");
				mIuartservice.SaveParam("SleepTime", "15");
			}	
			else if(Menu[0].equals("shortcut_setup_sys_sleep_time_30")){
				mIuartservice.UartSend("sleeptime", "3");
				mIuartservice.SaveParam("SleepTime", "30");
			}	
			else if(Menu[0].equals("shortcut_setup_sys_sleep_time_60")){
				mIuartservice.UartSend("sleeptime", "4");
				mIuartservice.SaveParam("SleepTime", "60");
			}	
			else if(Menu[0].equals("shortcut_setup_sys_sleep_time_90")){
				mIuartservice.UartSend("sleeptime", "5");
				mIuartservice.SaveParam("SleepTime", "90");
			}
			else if(Menu[0].equals("shortcut_setup_sys_sleep_time_120")){
				mIuartservice.UartSend("sleeptime", "6");
				mIuartservice.SaveParam("SleepTime", "120");
			}				
		}
		else if(Menu[0].contains("shortcut_setup_sys_six_color_")){
			
			if(Menu[0].equals("shortcut_setup_sys_six_color_off")){
				mIuartservice.UartSend("sixcolor", "0");
				mIuartservice.SaveParam("SixColor", "OFF");
			}
			else if(Menu[0].equals("shortcut_setup_sys_six_color_opti")){
				mIuartservice.UartSend("sixcolor", "1");
				mIuartservice.SaveParam("SixColor", "OPTI");
			}
			else if(Menu[0].equals("shortcut_setup_sys_six_color_enhance")){
				mIuartservice.UartSend("sixcolor", "2");
				mIuartservice.SaveParam("SixColor", "ENHANCE");
			}
//			else if(Menu[0].equals("shortcut_setup_sys_six_color_demo")){
//				mIuartservice.UartSend("sixcolor", "3");
//				mIuartservice.SaveParam("SixColor", "DEMO");
//			}
		}
		else if(Menu[0].contains("shortcut_setup_sys_matrix_")){
			
			if(Menu[0].equals("shortcut_setup_sys_matrix_off")){
				mIuartservice.UartSend("LocalDimming", "0");
				mIuartservice.SaveParam("LocalDimming", "OFF");
			}
			else if(Menu[0].equals("shortcut_setup_sys_matrix_on")){
				mIuartservice.UartSend("LocalDimming", "1");
				mIuartservice.SaveParam("LocalDimming", "ON");
				//control dreampanel
				mIuartservice.UartSend("dreampanel", "0");
				mIuartservice.SaveParam("DreamPanel", "OFF");			
			}
//			else if(Menu[0].equals("shortcut_setup_sys_matrix_demo")){
//				mIuartservice.UartSend("LocalDimming", "2");
//				mIuartservice.SaveParam("LocalDimming", "DEMO");
//				//control dreampanel
//				mIuartservice.UartSend("dreampanel", "0");
//				mIuartservice.SaveParam("DreamPanel", "OFF");
//			}
		}
		else if(Menu[0].equals("shortcut_setup_sys_back_light_")){
			mIuartservice.UartSend("backlight", Menu[1]);
			mIuartservice.SaveParam("BackLight", Menu[1]);
			//control dreampanel
			//mIuartservice.UartSend("dreampanel", "0");
			//mIuartservice.SaveParam("DreamPanel", "OFF");
		}
		else if(Menu[0].contains("shortcut_setup_sys_dream_panel_")){
			
			if(Menu[0].equals("shortcut_setup_sys_dream_panel_off")){
				mIuartservice.UartSend("dreampanel", "0");
				mIuartservice.SaveParam("DreamPanel", "OFF");
			}
			else if(Menu[0].equals("shortcut_setup_sys_dream_panel_sensor")){
				mIuartservice.UartSend("dreampanel", "1");
				mIuartservice.SaveParam("DreamPanel", "SENSOR");
				//control matrix
				mIuartservice.UartSend("LocalDimming", "0");
				mIuartservice.SaveParam("LocalDimming", "OFF");
				//control backlight
				//mIuartservice.UartSend("backlight", "100");
				//mIuartservice.SaveParam("BackLight", "100");
			}
			else if(Menu[0].equals("shortcut_setup_sys_dream_panel_scene")){
				mIuartservice.UartSend("dreampanel", "2");
				mIuartservice.SaveParam("DreamPanel", "SCENE");
				//control matrix
				mIuartservice.UartSend("LocalDimming", "0");
				mIuartservice.SaveParam("LocalDimming", "OFF");
				//control backlight
				//mIuartservice.UartSend("backlight", "100");
				//mIuartservice.SaveParam("BackLight", "100");
			}
			else if(Menu[0].equals("shortcut_setup_sys_dream_panel_all")){
				mIuartservice.UartSend("dreampanel", "3");
				mIuartservice.SaveParam("DreamPanel", "ALL");
				//control matrix
				mIuartservice.UartSend("LocalDimming", "0");
				mIuartservice.SaveParam("LocalDimming", "OFF");
				//control backlight
				//mIuartservice.UartSend("backlight", "100");
				//mIuartservice.SaveParam("BackLight", "100");
			}
//			else if(Menu[0].equals("shortcut_setup_sys_dream_panel_demo")){
//				mIuartservice.UartSend("dreampanel", "4");
//				mIuartservice.SaveParam("DreamPanel", "DEMO");
//				//control matrix
//				mIuartservice.UartSend("LocalDimming", "0");
//				mIuartservice.SaveParam("LocalDimming", "OFF");
//				//control backlight
//				mIuartservice.UartSend("backlight", "100");
//				mIuartservice.SaveParam("BackLight", "100");
//			}
		}
		else if(Menu[0].contains("shortcut_setup_sys_memc_")){
			
			if(Menu[0].equals("shortcut_setup_sys_memc_off")){
				mIuartservice.UartSend("memc", "0");
				mIuartservice.SaveParam("MEMC", "OFF");
			}
			else if(Menu[0].equals("shortcut_setup_sys_memc_weak")){
				mIuartservice.UartSend("memc", "1");
				mIuartservice.SaveParam("MEMC", "WEAK");
			}
			else if(Menu[0].equals("shortcut_setup_sys_memc_mid")){
				mIuartservice.UartSend("memc", "2");
				mIuartservice.SaveParam("MEMC", "MID");
			}
			else if(Menu[0].equals("shortcut_setup_sys_memc_strong")){
				mIuartservice.UartSend("memc", "3");
				mIuartservice.SaveParam("MEMC", "STRONG");
			}
		}
		else if(Menu[0].contains("shortcut_setup_sys_woofer_switch_")){			
			if(Menu[0].equals("shortcut_setup_sys_woofer_switch_on")){	
				int value=((1<<8)& 0xFFff)+Integer.parseInt(mIuartservice.GetParam("SubwooferVol"));
				mIuartservice.UartSend("SubwooferSwitch", String.valueOf(value));
				mIuartservice.SaveParam("SubwooferSwitch", "ON");
			}
			else if(Menu[0].equals("shortcut_setup_sys_woofer_switch_off")){
				mIuartservice.UartSend("SubwooferSwitch", mIuartservice.GetParam("SubwooferVol"));
				mIuartservice.SaveParam("SubwooferSwitch", "OFF");
			}
		}
		else if(Menu[0].equals("shortcut_setup_sys_woofer_vol_")){
			if(mIuartservice.GetParam("SubwooferSwitch").equals("ON")){		
				int value=((1<<8)& 0xFFff)+Integer.parseInt(Menu[1]);
				mIuartservice.UartSend("SubwooferVol", String.valueOf(value));
				mIuartservice.SaveParam("SubwooferVol", Menu[1]);
			}
		}
		else if(Menu[0].contains("shortcut_setup_sys_poweron_music_")){
			
			if(Menu[0].equals("shortcut_setup_sys_poweron_music_on")){
				mIuartservice.UartSend("PowerOnMusic", "1");
				mIuartservice.SaveParam("PowerOnMusic", "ON");
			}
			else if(Menu[0].equals("shortcut_setup_sys_poweron_music_off")){
				mIuartservice.UartSend("PowerOnMusic", "0");
				mIuartservice.SaveParam("PowerOnMusic", "OFF");
			}
		}
		else if(Menu[0].contains("shortcut_setup_sys_wall_effects_")){
			
			if(Menu[0].equals("shortcut_setup_sys_wall_effects_on")){
				mIuartservice.UartSend("biguayinxiao", "1");
				mIuartservice.SaveParam("WallEffects", "ON");
			}
			else if(Menu[0].equals("shortcut_setup_sys_wall_effects_off")){
				mIuartservice.UartSend("biguayinxiao", "0");
				mIuartservice.SaveParam("WallEffects", "OFF");
			}
		}
		
		else if(Menu[0].contains("shortcut_setup_sys_filelist_mode_")){
			
			if(Menu[0].equals("shortcut_setup_sys_filelist_mode_file")){
				
				mIuartservice.SaveParam("ListMode", "FILE");
			}
			else if(Menu[0].equals("shortcut_setup_sys_filelist_mode_folder")){
				
				mIuartservice.SaveParam("ListMode", "FOLDER");
			}
		}
		//move to MenuControl
//		else if(Menu[0].equals("shortcut_setup_sys_recovery_")){
//			
//			mIuartservice.UartSend("setdefault", "1");
//			mIuartservice.SetDefault();
//			
//		}
				
		//110601 ������
		if (Menu[0].equals("shortcut_setup_audio_mute_panel_")) {
			mIuartservice.UartSend("audioonly", "1");
		}
				
		//......................shortcut_common_source........................
		if (Menu[0].contains("shortcut_common_source")) {
			if (Menu[0].equals("shortcut_common_source_coocaa")) {
				mIuartservice.UartSend("shortcut_common_source", "12");
				mIuartservice.SaveParam("CHANNEL", "COOCAA");
			} else if (Menu[0].equals("shortcut_common_source_tv")) {
				mIuartservice.UartSend("shortcut_common_source", "1");
				mIuartservice.SaveParam("CHANNEL", "TV");
			} else if (Menu[0].equals("shortcut_common_source_av1")) {
				mIuartservice.UartSend("shortcut_common_source", "2");
				mIuartservice.SaveParam("CHANNEL", "AV");
			} else if (Menu[0].equals("shortcut_common_source_av2")) {
				mIuartservice.UartSend("shortcut_common_source", "3");
				mIuartservice.SaveParam("CHANNEL", "AV");
			} else if (Menu[0].equals("shortcut_common_source_yuv1")) {
				mIuartservice.UartSend("shortcut_common_source", "6");
				mIuartservice.SaveParam("CHANNEL", "YUV");
			} else if (Menu[0].equals("shortcut_common_source_yuv2")) {
				mIuartservice.UartSend("shortcut_common_source", "7");
				mIuartservice.SaveParam("CHANNEL", "YUV");
			} else if (Menu[0].equals("shortcut_common_source_hdmi1")) {
				mIuartservice.UartSend("shortcut_common_source", "8");
				mIuartservice.SaveParam("CHANNEL", "HDMI1");
			} else if (Menu[0].equals("shortcut_common_source_hdmi2")) {
				mIuartservice.UartSend("shortcut_common_source", "9");
				mIuartservice.SaveParam("CHANNEL", "HDMI2");
			} else if (Menu[0].equals("shortcut_common_source_hdmi3")) {
				mIuartservice.UartSend("shortcut_common_source", "10");
				mIuartservice.SaveParam("CHANNEL", "HDMI3");
			} else if (Menu[0].equals("shortcut_common_source_vga")) {
				mIuartservice.UartSend("shortcut_common_source", "11");
				mIuartservice.SaveParam("CHANNEL", "VGA");
			}
		}
		
		
		
		
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
