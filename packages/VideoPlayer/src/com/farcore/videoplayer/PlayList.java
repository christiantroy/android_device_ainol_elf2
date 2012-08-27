package com.farcore.videoplayer;


import java.util.List;


//simple list control
public class PlayList{

	private List<String> hfilelist = null;
	private int pos = 0;
	private static PlayList hlist = null;
	protected String rootPath = null;
	
	public static PlayList getinstance()
	{
		if(hlist == null)
			hlist = new PlayList();
		return hlist;
	}
	
	public  void setlist(List<String> filelist,int startpos)
	{
		hfilelist = filelist;
		pos = startpos;
	}
	
	public String movenext()
	{
		if(pos < hfilelist.size()-1)
			pos ++;
		else
			pos = 0;
		return hfilelist.get(pos);
	}
	
	public String moveprev()
	{
		if(pos > 0)
			pos--;
		else
			pos = hfilelist.size()-1;
		return hfilelist.get(pos);
	}
	
	public String getcur()
	{
		return hfilelist.get(pos);
	}
	
	public String movehead()
	{
		pos = 0;
		return hfilelist.get(pos);
	}

	public String movelast()
	{
		pos = hfilelist.size()-1;
		return hfilelist.get(pos);
	}
	
	public int getindex()
	{
		
		return pos;
	}
}
