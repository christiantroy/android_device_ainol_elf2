package com.amlogic;

public class RelevanceData {

	private String pictureName = null;
	private String TextName= null;
	private String MusicName= null;
	
	private int  picturecur = 0;
	private int  picturetotal= 0;
	
	private int  Textcur= 0;
	private int  Texttotal= 0;
	
	private int  Musiccur = 0;
	private int  Musictotal = 0;
	
	public void setPicture(final String name)
	{
		pictureName = name;
	}
	
	public void setText(final String name)
	{
		TextName = name;
	}
	
	public void setMusic(final String name)
	{
		MusicName = name;
	}
	
	public String getPicture()
	{
		return pictureName;
	}
	
	public String getText()
	{
		return TextName;
	}
	
	public String getMusic()
	{
		return MusicName;
	}
	
	public void setPictureData(final int total ,final int cur)
	{
	    picturecur = cur;
		picturetotal = total;
	}
	
	public void setTextData(final int total ,final int cur)
	{

		Textcur = cur;
		Texttotal = total;
	}
	
	public void setMusicData(final int total ,final int cur)
	{

		Musiccur = cur;
		Musictotal = total;
	}
	
	public int getPictureCurData()
	{
		return picturecur;
	}
	public int getPictureTotalData()
	{
		return picturetotal;
	}
	
	public int getTextCurData()
	{
		return Textcur;
	}
	public int getTextTotalData()
	{
		return Texttotal;
	}
	
	public int getMusicCurData()
	{
		return Musiccur;
	}
	public int getMusicTotalData()
	{
		return Musictotal;
	}
	
	
}
