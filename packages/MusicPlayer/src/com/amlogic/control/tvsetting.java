package com.amlogic.control;

import java.io.IOException;

import android.content.Context;

//import com.amlogic.contentprovider.UserContentProvider;

public class tvsetting {

//	private UserContentProvider ucp=null;
//	public void ucpInstance(Context context){
//		if(ucp==null)
//			ucp=new UserContentProvider(context);
//	}
	
	public tvsetting(Context context){
		//ucpInstance(context);
	}
	
//	public void DefaultSetting(){
//		try {
//			ucp.upgradeToDefault();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void SettingTVControl(String... Menu){
		if (Menu[0].equals("shortcut_common_vol_")) {// 音量
			// ucp.setParams("Volumn", Menu[1]);

		} else if (Menu[0].equals("shortcut_setup_video_brightness_")) {// 亮度
			// ucp.setParams("Brightness", Menu[1]);

		} else if (Menu[0].equals("shortcut_setup_video_contrast_")) {// 对比度
			// ucp.setParams("Contrast", Menu[1]);

		} else if (Menu[0].equals("shortcut_setup_video_color_")) {// 色彩

		} else if (Menu[0].equals("shortcut_setup_video_sharpness_")) {// 清晰度

		}
		// 图像模式
		else if (Menu[0].equals("shortcut_setup_video_picture_mode_std")) {// 标准

		} else if (Menu[0].equals("shortcut_setup_video_picture_mode_vivid")) {// 靓丽
			//ucp.setParams("PictureMode", "VIVID");

		} else if (Menu[0].equals("shortcut_setup_video_picture_mode_soft")) {// 柔和

		} else if (Menu[0].equals("shortcut_setup_video_picture_mode_user")) {// 自设

		}
		// 显示模式
		else if (Menu[0].equals("shortcut_setup_video_display_mode_169")) {// 16:9

		} else if (Menu[0].equals("shortcut_setup_video_display_mode_43")) {// 4:3

		} else if (Menu[0].equals("shortcut_setup_video_display_mode_subtitle")) {// 字幕模式
			// ucp.setParams("DisplayMode", "CAPTION");

		} else if (Menu[0].equals("shortcut_setup_video_display_mode_theater")) {// 电影模式

		}
		// 降噪
		else if (Menu[0].equals("shortcut_setup_video_dnr_off")) {// 关

		} else if (Menu[0].equals("shortcut_setup_video_dnr_weak")) {// 弱

		} else if (Menu[0].equals("shortcut_setup_video_dnr_mid")) {// 中

		} else if (Menu[0].equals("shortcut_setup_video_dnr_strong")) {// 强

		}
		// 声音模式
		else if (Menu[0].equals("shortcut_setup_audio_sound_mode_std")) {// 标准

		} else if (Menu[0].equals("shortcut_setup_audio_sound_mode_news")) {// 新闻

		} else if (Menu[0].equals("shortcut_setup_audio_sound_mode_theater")) {// 影院

		} else if (Menu[0].equals("shortcut_setup_audio_sound_mode_music")) {// 音乐

		} else if (Menu[0].equals("shortcut_setup_audio_sound_mode_user")) {// 自设

		}
		// SRS环绕声
		else if (Menu[0].equals("shortcut_setup_audio_srs_on")) {// 开

		} else if (Menu[0].equals("shortcut_setup_audio_srs_off")) {// 关

		}
		// 通道选择
		else if (Menu[0].equals("shortcut_common_source_coocaa")) {// 酷开

		} else if (Menu[0].equals("shortcut_common_source_tv")) {// 电视

		} else if (Menu[0].equals("shortcut_common_source_av1")) {// 视频一

		} else if (Menu[0].equals("shortcut_common_source_av2")) {// 视频二

		} else if (Menu[0].equals("shortcut_common_source_yuv1")) {// 分量一

		} else if (Menu[0].equals("shortcut_common_source_yuv2")) {// 分量二

		} else if (Menu[0].equals("shortcut_common_source_hdmi1")) {// HDMI1

		} else if (Menu[0].equals("shortcut_common_source_hdmi2")) {// HDMI2

		} else if (Menu[0].equals("shortcut_common_source_vga")) {// 电脑

		}		
		
	}
	
	
	public void SettingProgramControl(String... Menu){
		//跳台
		if(Menu[0].equals("shortcut_program_skip_on")){//开
			
		}
		else if(Menu[0].equals("shortcut_program_skip_off")){//关
			
		}
		else if(Menu[0].contains("shortcut_program_video_sys_")){//彩色制式
			
			if(Menu[0].equals("shortcut_program_video_sys_auto")){//自动
				
			}else if(Menu[0].equals("shortcut_program_video_sys_pal")){//PAL
				
			}else if(Menu[0].equals("shortcut_program_video_sys_ntsc")){//NTSC
				
			}			
		}
		else if(Menu[0].contains("shortcut_program_sound_sys_")){//声音制式
			
			if(Menu[0].equals("shortcut_program_sound_sys_auto")){//自动
				
			}else if(Menu[0].equals("shortcut_program_sound_sys_dk")){//DK
				
			}else if(Menu[0].equals("shortcut_program_sound_sys_i")){//I
				
			}else if(Menu[0].equals("shortcut_program_sound_sys_bg")){//BG
				
			}else if(Menu[0].equals("shortcut_program_sound_sys_m")){//M
				
			}

		}
		else if(Menu[0].contains("shortcut_program_band_")){//频段
			
			if(Menu[0].equals("shortcut_program_band_vhfl")){//VHFL
				
			}else if(Menu[0].equals("shortcut_program_band_vhfh")){//VHFH
				
			}else if(Menu[0].equals("shortcut_program_band_ufh")){//UFH
				
			}	
		}
	}
	
	
	
	
	
}
