package com.amlogic.pmt;

import java.util.List;

public interface updataFilenameListener {
	public  void CallbackName(String type,String filename);
	public void CallbackMusicState(int total,int cur);
	public void CallbackUpdataMenu(String type,List<String> data);
	public void stopplayer(String type);
	public void CallbackRelevance(String res,String target);
	public void CallbackPosScale(String type,String scale);
	public void BackTo3D();
	public void CallDelayHideMenu();
}
