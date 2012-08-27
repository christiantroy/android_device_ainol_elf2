package com.amlogic.pmt.browser;

import org.geometerplus.zlibrary.ui.android.R;

public class SearchDrawableID {

	public  int SearchID(String type,boolean focus)
	{
		int ID = 0;
		if(type.equals("Audio"))
		{
//			if(focus == true)
//				ID = 0;
//			else
				ID = R.drawable.icon_pre_music;
		}
		else
		if(type.equals("folder"))
		{
			//if(focus == true)
				//ID = R.drawable.folder_unfocus;
			//else
				ID = R.drawable.icon_browse_folder;
		}	
		else
		if(type.equals("Text"))
		{
		//	if(focus == true)
		//		ID = 0;
		//	else
				ID = R.drawable.icon_browse_txt;
		}	
		else
		if(type.equals("Video"))
		{
		//	if(focus == true)
				//ID = 0;
			//else
				ID = R.drawable.icon_browse_folder;
		}	
		else
		if(type.equals("Picture"))
		{
		//	if(focus == true)
				//ID = 0;
			//else
				ID = R.drawable.icon_browse_picture;
		}				
			
		return ID;
	}
	
	
	
	public int PlayFormatID(String FileType)
	{
		if( FileType.equals("Picture")||FileType.equals("All"))//sunsikai 1021
			 return R.array.Picture;
		else
		if( FileType.equals("WebText")||FileType.equals("All"))//sunsikai 1021
			 return R.array.WebText;
		else
		if( FileType.equals("Package")||FileType.equals("All"))//sunsikai 1021
			 return R.array.Package;
		else
		if( FileType.equals("Audio")||FileType.equals("All"))//sunsikai 1021
			 return R.array.Audio;
		else	
		if( FileType.equals("Text")||FileType.equals("All"))//sunsikai 1021
			 return R.array.Text;
		else
		if( FileType.equals("Video")||FileType.equals("All"))//sunsikai 1021
			 return R.array.Video;
		else
			return 0;
	
	}
				
	
}
