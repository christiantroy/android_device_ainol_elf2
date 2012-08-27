package com.amlogic.control;

public class pictureplayer {

	public pictureplayer(){
		
	}
	
	public void PicturePlayer(String... State)
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
		if(State[0].equals("shortcut_picture_prev_"))  //��һ��
		{
			
		}
		else
		if(State[0].equals("shortcut_picture_next_"))  //��һ��
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
		if(State[0].equals("shortcut_picture_switch_time_"))  //�л�ģʽ
		{
			if(State[1].equals("3s"))  //3s
			{
				
			}
			else
			if(State[1].equals("5s"))  //5s
			{
				
			}
			else
			if(State[1].equals("hand"))  //�ֶ�
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_picture_switch_mode_"))  //���ŷ�ʽ
		{
			if(State[1].equals("1"))  //��
			{
				
			}
			else
			if(State[1].equals("2"))  //����
			{
				
			}
			else
			if(State[1].equals("3"))  //��������
			{
				
			}
			else
			if(State[1].equals("4"))  //��������
			{
				
			}
			else
			if(State[1].equals("5"))  //��������
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_picture_rotate_"))  //��ת����
		{
			if(State[1].equals("normal"))  //��
			{
				
			}
			else
			if(State[1].equals("90"))  //90��
			{
				
			}
			else
			if(State[1].equals("180"))  //180��
			{
				
			}
			else
			if(State[1].equals("270"))  //270��
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_picture_prev_"))  //��һ��
		{
			
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
