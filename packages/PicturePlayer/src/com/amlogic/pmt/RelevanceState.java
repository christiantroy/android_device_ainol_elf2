package com.amlogic.pmt;

public class RelevanceState {
	private boolean pictureState = false;
	private boolean musicState = false;
	private boolean textState = false;
	
	public void setPlayState(String playtpe,boolean state)
	{
		if(playtpe.equals("Text"))
			textState = state;
		else
		if(playtpe.equals("Audio"))
			musicState = state;
		else
		if(playtpe.equals("Picture"))
			pictureState = state;
	}
	
	public Boolean getPlayState(String playtpe)
	{
		if(playtpe.equals("Text"))
			return textState ;
		else
		if(playtpe.equals("Audio"))
			return musicState ;
		else
		if(playtpe.equals("Picture"))
			return pictureState ;
		else
			return null;
	}
	
	public int getStateCount(){
		int count = 0;
		if(textState)
			count++;
		if(musicState)
			count++;
		if(pictureState)
			count++;
		return count;
	}
}
