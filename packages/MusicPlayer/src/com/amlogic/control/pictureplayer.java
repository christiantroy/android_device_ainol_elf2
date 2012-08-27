package com.amlogic.control;

public class pictureplayer {

	public pictureplayer(){
		
	}
	
	public void PicturePlayer(String... State)
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
		if(State[0].equals("shortcut_common_play_"))  //播放
		{
			
		}
		else
		if(State[0].equals("shortcut_common_pause_"))  //暂停
		{
			
		}
		else
		if(State[0].equals("shortcut_common_stop_"))  //停止
		{
			
		}
		else
		if(State[0].equals("shortcut_common_fb_"))  //快进
		{
			
		}
		else
		if(State[0].equals("shortcut_common_ff_"))  //快退
		{
			
		}
		else
		if(State[0].equals("shortcut_picture_prev_"))  //上一张
		{
			
		}
		else
		if(State[0].equals("shortcut_picture_next_"))  //下一张
		{
			
		}
		else
		if(State[0].equals("shortcut_common_playmode_"))  //重复模式
		{
			if(State[1].equals("single"))  //单曲循环
			{
				
			}
			else
			if(State[1].equals("folder"))  //目录循环
			{
				
			}
			else
			if(State[1].equals("rand"))  //随机播放
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_picture_switch_time_"))  //切换模式
		{
			if(State[1].equals("3s"))  //3s
			{
				
			}
			else
			if(State[1].equals("5s"))  //5s
			{
				
			}
			else
			if(State[1].equals("hand"))  //手动
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_picture_switch_mode_"))  //播放方式
		{
			if(State[1].equals("1"))  //正常
			{
				
			}
			else
			if(State[1].equals("2"))  //消融
			{
				
			}
			else
			if(State[1].equals("3"))  //从上至下
			{
				
			}
			else
			if(State[1].equals("4"))  //从左至右
			{
				
			}
			else
			if(State[1].equals("5"))  //四周向中
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_picture_rotate_"))  //旋转镜像
		{
			if(State[1].equals("normal"))  //正常
			{
				
			}
			else
			if(State[1].equals("90"))  //90度
			{
				
			}
			else
			if(State[1].equals("180"))  //180度
			{
				
			}
			else
			if(State[1].equals("270"))  //270度
			{
				
			}
		}
		else
		if(State[0].equals("shortcut_picture_prev_"))  //上一张
		{
			
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
