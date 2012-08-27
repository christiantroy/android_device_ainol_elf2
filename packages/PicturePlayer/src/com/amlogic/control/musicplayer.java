package com.amlogic.control;

public class musicplayer {

	public musicplayer()
	{
		
	}
	public void MusicPlay(String... State)
	{
		if(State[0].equals("shortcut_common_sync_control_"))  //����ѡ��
		{
			if(State[1].equals("music"))  //��������
			{
				
			}
			else
			if(State[1].equals("picture"))  //����ͼƬ
			{
				
			}
			else
			if(State[1].equals("txt"))  //�����ı�
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_common_play_"))  //����
		{
			
		}
		else
		if(State[0].equals("shortcut_common_pause_"))  //��ͣ
		{
			
		}
		else
		if(State[0].equals("shortcut_common_stop_"))  //ֹͣ
		{
			
		}
		else
		if(State[0].equals("shortcut_common_fb_"))  //���
		{
			
		}
		else
		if(State[0].equals("shortcut_common_ff_"))  //����
		{
			
		}
		else
		if(State[0].equals("shortcut_music_prev_"))  //��һ��
		{
			
		}
		else
		if(State[0].equals("shortcut_music_next_"))  //��һ��
		{
			
		}
		else
		if(State[0].equals("shortcut_common_vol_"))  //����
		{
			
		}
		else
		if(State[0].equals("shortcut_common_playmode_"))  //�ظ�ģʽ
		{
			if(State[1].equals("single"))  //����ѭ��
			{
				
			}
			else
			if(State[1].equals("folder"))  //Ŀ¼ѭ��
			{
				
			}
			else
			if(State[1].equals("rand"))  //����
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_common_sync_play_"))  //ͬ������
		{
			
			if(State[1].equals("music"))  //����
			{
				
			}
			else
			if(State[1].equals("picture"))  //ͼƬ
			{
				
			}
			else
			if(State[1].equals("txt"))  //������
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_common_source_"))  //ͨ��ѡ��
		{
			if(State[1].equals("coocaa"))  //�Ὺ
			{
				
			}
			else
			if(State[1].equals("tv"))  //����
			{
				
			}
			else
			if(State[1].equals("av1"))  //��Ƶһ
			{
				
			}
			else
			if(State[1].equals("av2"))  //��Ƶ��
			{
				
			}
			else
			if(State[1].equals("yuv1"))  //����һ
			{
				
			}
			else
			if(State[1].equals("yuv2"))  //������
			{
				
			}
			else
			if(State[1].equals("hdmi1"))  //HDMI1
			{
				
			}
			else
			if(State[1].equals("hdmi2"))  //HDMI2
			{
				
			}
			else
			if(State[1].equals("vga"))  //����
			{
				
			}
		}
	}
}
