package com.amlogic.control;

public class txtplayer {

	public txtplayer(){
		
	}
	
	public void TxtPlayer(String... State)
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
		if(State[0].equals("shortcut_common_stop_"))  //ֹͣ
		{
			
		}
		else
		if(State[0].equals("shortcut_txt_prev_"))  //��һҳ
		{
			
		}
		else
		if(State[0].equals("shortcut_txt_next_"))  //��һҳ
		{
			
		}
		else
		if(State[0].equals("shortcut_txt_turn_page_"))  //���ٷ�ҳ
		{
			if(State[1].equals("10p"))  //10ҳ
			{
				
			}
			else
			if(State[1].equals("20p"))  //20ҳ
			{
				
			}
			else
			if(State[1].equals("50p"))  //50ҳ
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_txt_breakpoint_"))  //�ϵ����
		{
			if(State[1].equals("first"))  //��ҳ
			{
				
			}
			else
			if(State[1].equals("breakpoint"))  //�ϵ����
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_txt_fontsize_"))  //�ֺ�
		{
			if(State[1].equals("big"))  //��
			{
				
			}
			else
			if(State[1].equals("mid"))  //��
			{
				
			}
			else
			if(State[1].equals("small"))  //С
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
