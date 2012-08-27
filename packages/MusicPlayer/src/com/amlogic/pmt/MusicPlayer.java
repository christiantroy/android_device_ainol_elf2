/**
 Earth
 @copyright Gan Yu Xiong
 */
package com.amlogic.pmt;

import  android.util.Log;
import android.media.MediaPlayer;
import com.amlogic.pmt.GLMusicService;

/**
 * TODO
 * @date 2011-7-29 下午01:41:53
 * @author Administrator
 */
public class MusicPlayer{
	static private MediaPlayer MediaPlayer = null;
	static private IGLMusicService MediaPlayerService = null;
	
	static public void setMediaPlayer(MediaPlayer player)
	{
		MediaPlayer=player;
	}

	static public void setMediaPlayerService(IGLMusicService service)
	{
		MediaPlayerService=service;
		//setMediaPlayer(service.mediaPlayer);
	}

	static public MediaPlayer getMediaPlayer()
	{
		return MediaPlayer;
	}
	
	static public IGLMusicService getMediaPlayerService()
	{
		return MediaPlayerService;
	}
	
}
