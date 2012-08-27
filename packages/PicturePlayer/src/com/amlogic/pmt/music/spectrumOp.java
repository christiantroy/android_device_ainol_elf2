package com.amlogic.pmt.music;

import android.util.Log;


public class spectrumOp {

	private musiclistener mylistener = null;
	mRequestDataThread  requestData = null;
	private boolean  DataThreadbutton = false;
	
	
	public void resume()
	{
		requestData();
	}
	
	public void Destroy()
	{
		cancelData();
	}
	
	class  mRequestDataThread extends  Thread{  
		private static final String TAG = "spectrum";

		public void run()
		{
			synchronized(this){
				while(DataThreadbutton )
				{
					try {
						sleep(80);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(mylistener != null)
					{
						int[] tempData =SpectrumUtil.getSpectrumData();
						//Log.d(TAG,"tempData:"+tempData);
						if(tempData !=null)
							mylistener.getSpectrumInfo(tempData);
					}
						
				}
					
				
			}
		}
	 }

	 public void requestData()
	 {
		 if(requestData == null)
		 {
			 requestData = new mRequestDataThread();
			 DataThreadbutton = true;
			 requestData.start();
		 }
		 
	 }
	 
	 public void cancelData()
	 {
		 if( requestData!=null)
		 {
			 DataThreadbutton = false;
			 synchronized(requestData){
				 requestData = null;
			 }
		 }
	 }
	 
	 
	 
	 
	public void setMusicListener(musiclistener listener  )
	{
		mylistener = listener;
	}
}
