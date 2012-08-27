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
		if (Menu[0].equals("shortcut_common_vol_")) {// ����
			// ucp.setParams("Volumn", Menu[1]);

		} else if (Menu[0].equals("shortcut_setup_video_brightness_")) {// ����
			// ucp.setParams("Brightness", Menu[1]);

		} else if (Menu[0].equals("shortcut_setup_video_contrast_")) {// �Աȶ�
			// ucp.setParams("Contrast", Menu[1]);

		} else if (Menu[0].equals("shortcut_setup_video_color_")) {// ɫ��

		} else if (Menu[0].equals("shortcut_setup_video_sharpness_")) {// �����

		}
		// ͼ��ģʽ
		else if (Menu[0].equals("shortcut_setup_video_picture_mode_std")) {// ��׼

		} else if (Menu[0].equals("shortcut_setup_video_picture_mode_vivid")) {// ����
			// ucp.setParams("PictureMode", "VIVID");

		} else if (Menu[0].equals("shortcut_setup_video_picture_mode_soft")) {// ���

		} else if (Menu[0].equals("shortcut_setup_video_picture_mode_user")) {// ����

		}
		// ��ʾģʽ
		else if (Menu[0].equals("shortcut_setup_video_display_mode_169")) {// 16:9

		} else if (Menu[0].equals("shortcut_setup_video_display_mode_43")) {// 4:3

		} else if (Menu[0].equals("shortcut_setup_video_display_mode_subtitle")) {// ��Ļģʽ
			// ucp.setParams("DisplayMode", "CAPTION");

		} else if (Menu[0].equals("shortcut_setup_video_display_mode_theater")) {// ��Ӱģʽ

		}
		// ����
		else if (Menu[0].equals("shortcut_setup_video_dnr_off")) {// ��

		} else if (Menu[0].equals("shortcut_setup_video_dnr_weak")) {// ��

		} else if (Menu[0].equals("shortcut_setup_video_dnr_mid")) {// ��

		} else if (Menu[0].equals("shortcut_setup_video_dnr_strong")) {// ǿ

		}
		// ����ģʽ
		else if (Menu[0].equals("shortcut_setup_audio_sound_mode_std")) {// ��׼

		} else if (Menu[0].equals("shortcut_setup_audio_sound_mode_news")) {// ����

		} else if (Menu[0].equals("shortcut_setup_audio_sound_mode_theater")) {// ӰԺ

		} else if (Menu[0].equals("shortcut_setup_audio_sound_mode_music")) {// ����

		} else if (Menu[0].equals("shortcut_setup_audio_sound_mode_user")) {// ����

		}
		// SRS������
		else if (Menu[0].equals("shortcut_setup_audio_srs_on")) {// ��

		} else if (Menu[0].equals("shortcut_setup_audio_srs_off")) {// ��

		}
		// ͨ��ѡ��
		else if (Menu[0].equals("shortcut_common_source_coocaa")) {// �Ὺ

		} else if (Menu[0].equals("shortcut_common_source_tv")) {// ����

		} else if (Menu[0].equals("shortcut_common_source_av1")) {// ��Ƶһ

		} else if (Menu[0].equals("shortcut_common_source_av2")) {// ��Ƶ��

		} else if (Menu[0].equals("shortcut_common_source_yuv1")) {// ����һ

		} else if (Menu[0].equals("shortcut_common_source_yuv2")) {// ������

		} else if (Menu[0].equals("shortcut_common_source_hdmi1")) {// HDMI1

		} else if (Menu[0].equals("shortcut_common_source_hdmi2")) {// HDMI2

		} else if (Menu[0].equals("shortcut_common_source_vga")) {// ����

		}		
		
	}
	
	
	public void SettingProgramControl(String... Menu){
		//��̨
		if(Menu[0].equals("shortcut_program_skip_on")){//��
			
		}
		else if(Menu[0].equals("shortcut_program_skip_off")){//��
			
		}
		else if(Menu[0].contains("shortcut_program_video_sys_")){//��ɫ��ʽ
			
			if(Menu[0].equals("shortcut_program_video_sys_auto")){//�Զ�
				
			}else if(Menu[0].equals("shortcut_program_video_sys_pal")){//PAL
				
			}else if(Menu[0].equals("shortcut_program_video_sys_ntsc")){//NTSC
				
			}			
		}
		else if(Menu[0].contains("shortcut_program_sound_sys_")){//������ʽ
			
			if(Menu[0].equals("shortcut_program_sound_sys_auto")){//�Զ�
				
			}else if(Menu[0].equals("shortcut_program_sound_sys_dk")){//DK
				
			}else if(Menu[0].equals("shortcut_program_sound_sys_i")){//I
				
			}else if(Menu[0].equals("shortcut_program_sound_sys_bg")){//BG
				
			}else if(Menu[0].equals("shortcut_program_sound_sys_m")){//M
				
			}

		}
		else if(Menu[0].contains("shortcut_program_band_")){//Ƶ��
			
			if(Menu[0].equals("shortcut_program_band_vhfl")){//VHFL
				
			}else if(Menu[0].equals("shortcut_program_band_vhfh")){//VHFH
				
			}else if(Menu[0].equals("shortcut_program_band_ufh")){//UFH
				
			}	
		}
	}
	
	
	
	
	
}
