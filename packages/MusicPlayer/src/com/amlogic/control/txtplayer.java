package com.amlogic.control;

public class txtplayer {

	public txtplayer(){
		
	}
	
	public void TxtPlayer(String... State)
	{
		if(State[0].equals("shortcut_common_sync_control_"))  //控制选择
		{
			if(State[1].equals("music"))  //控制音乐
			{
				
			}
			else
			if(State[1].equals("picture"))  //控制图片
			{
				
			}
			else
			if(State[1].equals("txt"))  //控制文本
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_common_stop_"))  //停止
		{
			
		}
		else
		if(State[0].equals("shortcut_txt_prev_"))  //上一页
		{
			
		}
		else
		if(State[0].equals("shortcut_txt_next_"))  //下一页
		{
			
		}
		else
		if(State[0].equals("shortcut_txt_turn_page_"))  //快速翻页
		{
			if(State[1].equals("10p"))  //10页
			{
				
			}
			else
			if(State[1].equals("20p"))  //20页
			{
				
			}
			else
			if(State[1].equals("50p"))  //50页
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_txt_breakpoint_"))  //断点续读
		{
			if(State[1].equals("first"))  //首页
			{
				
			}
			else
			if(State[1].equals("breakpoint"))  //断点续读
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_txt_fontsize_"))  //字号
		{
			if(State[1].equals("big"))  //大
			{
				
			}
			else
			if(State[1].equals("mid"))  //中
			{
				
			}
			else
			if(State[1].equals("small"))  //小
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_common_sync_play_"))  //同步播放
		{
			
			if(State[1].equals("music"))  //音乐
			{
				
			}
			else
			if(State[1].equals("picture"))  //图片
			{
				
			}
			else
			if(State[1].equals("txt"))  //电子书
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_common_source_"))  //通道选择
		{
			if(State[1].equals("coocaa"))  //酷开
			{
				
			}
			else
			if(State[1].equals("tv"))  //电视
			{
				
			}
			else
			if(State[1].equals("av1"))  //视频一
			{
				
			}
			else
			if(State[1].equals("av2"))  //视频二
			{
				
			}
			else
			if(State[1].equals("yuv1"))  //分量一
			{
				
			}
			else
			if(State[1].equals("yuv2"))  //分量二
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
			if(State[1].equals("vga"))  //电脑
			{
				
			}
		}
	}
}
