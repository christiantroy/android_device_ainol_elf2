package com.amlogic.pmt.music;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

import org.geometerplus.zlibrary.ui.android.R;

import android.content.Context;
import android.graphics.Bitmap;


public class ParserMusic {
	private RandomAccessFile ran = null;
	private File file = null;
	private MusicItemInfo musicInfo = new MusicItemInfo();
	private String unkown = null;
	
	public ParserMusic(final String path,Context ctx) throws IOException
	{
		unkown = ctx.getResources().getString(R.string.unknown);
		if(isMp3(path) == true)
			parserMp3(path);
		else if(isWav(path) == true)
			parserWav(path);
		else
			parserOther();
	}

	public ParserMusic(URL url,Context ctx) throws IOException
	{
		unkown = ctx.getResources().getString(R.string.unknown);
		parserOther();
	}
	
	public boolean isMp3(final String path)
	{
		if(path.toLowerCase().endsWith(".mp3"))
			return true;
		else
			return false;
	}
	
	public boolean isWav(final String path)
	{
		if(path.toLowerCase().endsWith(".wav"))
			return true;
		else
			return false;
	}
	
	public void parserWav(final String path) throws IOException
	{
		wavInfo info = new wavInfo(path);
		musicInfo.setTitle(unkown);
		musicInfo.setAlbum(unkown);
		musicInfo.setArtist(unkown);
		musicInfo.setBitmap(null);
	
	}
	
	public void parserMp3(final String path) throws IOException
	{
		file = new File(path);
		ran = new RandomAccessFile(file, "r");
		
		byte[] buffer = new byte[128];
		ran.seek(ran.length() - 128);
		ran.read(buffer);
		mp3Id3V2Info info2 = new mp3Id3V2Info(ran);
		mp3Id3V1Info info = new mp3Id3V1Info(buffer);
		ran.close();
		
		String value = info.getSongName();
		if(value == null || value.equals(""))
			musicInfo.setTitle(unkown);
		else
			musicInfo.setTitle(value);
		
		value = info.getAlbum();
		if(value == null || value.equals(""))
			musicInfo.setAlbum(unkown);
		else
			musicInfo.setAlbum(info.getAlbum());
		
		value = info.getArtist();
		if(value == null || value.equals(""))
			musicInfo.setArtist(unkown);
		else
			musicInfo.setArtist(info.getArtist());
		Bitmap bmp = info2.getPicture();
		musicInfo.setBitmap(bmp);

	}
	
	
	public void parserOther()
	{
		musicInfo.setTitle(unkown);
		musicInfo.setAlbum(unkown);
		musicInfo.setArtist(unkown);
		musicInfo.setBitmap(null);
	}
	
	public MusicItemInfo getMusicInfo()
	{
		return this.musicInfo;
	}
}
