package com.farcore.videoplayer;

import android.os.storage.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.farcore.videoplayer.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.os.SystemProperties;
import com.farcore.playerservice.SettingsVP;
import java.io.IOException;
import java.io.InputStream;

public class FileList extends ListActivity {
	private List<File> listFiles =null;
	private List<File> listVideos =null;
	private List<String> items=null;
	private List<String> paths=null;
	private List<String> currentlist=null;
	private String currenturl = null;
	private String root_path = "/mnt";
	private String extensions ;
	private static String ISOpath = null;
	
	private TextView tileText;
	private File file;
	private static String TAG = "player_FileList";
	
	private int item_position_selected, item_position_first, fromtop_piexl;
	private ArrayList<Integer> fileDirectory_position_selected = new ArrayList<Integer>();
	private ArrayList<Integer> fileDirectory_position_piexl = new ArrayList<Integer>();
	private int pathLevel = 0;
	private final String iso_mount_dir = "/mnt/VIRTUAL_CDROM";
	
	private static final String EXT_SD="/mnt/external_sdcard";
    private boolean isRealSD=false;
	
	 private final StorageEventListener mListener = new StorageEventListener() {
	        public void onUsbMassStorageConnectionChanged(boolean connected)
	        {
	        	//this is the action when connect to pc
	        	return ;
	        }
	        public void onStorageStateChanged(String path, String oldState, String newState)
	        {
	        	if (newState == null || path == null) 
	        		return;
	        	
	        	if(newState.compareTo("mounted") == 0)
	        	{
	        		if(PlayList.getinstance().rootPath==null 
	        		|| PlayList.getinstance().rootPath.equals(root_path))
	        			BrowserFile(root_path); 
	        	}
	        	else if(newState.compareTo("unmounted") == 0
	        			|| newState.compareTo("removed") == 0)
	        	{
	        		if(PlayList.getinstance().rootPath.startsWith(path)
	        		|| PlayList.getinstance().rootPath.equals(root_path))
	        			BrowserFile(root_path);
	        	}
	        	
	        }
	        
	};
	
    @Override
    public void onResume() {
        super.onResume();
        File file = null;
        if (PlayList.getinstance().rootPath != null)
			file = new File(PlayList.getinstance().rootPath);
			
        if((file != null) && file.exists()) {
			File[] the_Files;
			the_Files = file.listFiles(new MyFilter(extensions));
			if(the_Files == null) {
				PlayList.getinstance().rootPath =root_path;
			}
			BrowserFile(PlayList.getinstance().rootPath);

		}
		else {
			PlayList.getinstance().rootPath =root_path;
			BrowserFile(PlayList.getinstance().rootPath);
		}
        
        StorageManager m_storagemgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
		m_storagemgr.registerListener(mListener);
        getListView().setSelectionFromTop(item_position_selected, fromtop_piexl);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        StorageManager m_storagemgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        m_storagemgr.unregisterListener(mListener);
    }
    
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		extensions = getResources().getString(R.string.support_video_extensions);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.main);
	    
	    try{
	        Bundle bundle = new Bundle();
	        bundle = this.getIntent().getExtras();
	        if (bundle != null) {
		        item_position_selected = bundle.getInt("item_position_selected");
		        item_position_first = bundle.getInt("item_position_first");
		        fromtop_piexl = bundle.getInt("fromtop_piexl");
		        fileDirectory_position_selected = bundle.getIntegerArrayList("fileDirectory_position_selected");
		        fileDirectory_position_piexl = bundle.getIntegerArrayList("fileDirectory_position_piexl");	    	
	        	pathLevel = fileDirectory_position_selected.size();
	        }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	    currentlist = new ArrayList<String>();
	    
	    /* check whether use real sdcard*/
		//isRealSD=Environment.isExternalStorageBeSdcard();
		isRealSD=true;
	    
		if(PlayList.getinstance().rootPath==null)
			PlayList.getinstance().rootPath =root_path;
	    	
	    BrowserFile(PlayList.getinstance().rootPath);
	    
	    Button home = (Button) findViewById(R.id.Button_home);
	    home.setOnClickListener(new View.OnClickListener() 
	    {

            public void onClick(View v) 
            {
            	FileList.this.finish();
            	PlayList.getinstance().rootPath =null;
            } 
       
	    });
	    Button exit = (Button) findViewById(R.id.Button_exit);
	    exit.setOnClickListener(new View.OnClickListener() 
	    {

            public void onClick(View v) 
            {
            	
                if(paths == null) 
                {
                	FileList.this.finish();
                	PlayList.getinstance().rootPath =null;
                }
                else
                {
                	if(paths.isEmpty())
                	{
                		FileList.this.finish();
                		PlayList.getinstance().rootPath =null;
                	}
                	file = new File(paths.get(0).toString());
                	currenturl =file.getParentFile().getParent();
                	if((file.getParent().compareToIgnoreCase(root_path)!=0)&&(pathLevel>0)){
						if(false==isRealSD)
						{
							String path = file.getParent();
							if(path.equals(EXT_SD))
							{
								currenturl=root_path;
								//pathLevel--;
							}
						}
						
                		BrowserFile(currenturl);
                		pathLevel--;
                		getListView().setSelectionFromTop(fileDirectory_position_selected.get(pathLevel), fileDirectory_position_piexl.get(pathLevel));
                		fileDirectory_position_selected.remove(pathLevel);
                		fileDirectory_position_piexl.remove(pathLevel);
                	}
                	else
                	{
            			FileList.this.finish();
            			PlayList.getinstance().rootPath =null;
                	}
                }
                
            } 
       
	    });
        
	}
	
	private void BrowserFile(String filePath) {
		int i = 0;
		file = new File(filePath);
		listFiles = new ArrayList<File>();
	    items=new ArrayList<String>();
	    paths=new ArrayList<String>();
		String[] files =file.list();  
		if (files != null) {
	        for(i=0;i<files.length;i++){			
	            if(files[i].equals("VIRTUAL_CDROM")){                    
	                execCmd("vdc loop unmount");
					break;
	            }
			}
		}
	    searchFile(file);
	    if(listFiles.isEmpty()) {
	    	Toast.makeText(FileList.this, R.string.str_no_file, Toast.LENGTH_SHORT).show();
	    	//paths =currentlist;
	    	paths.clear();
	    	paths.addAll(currentlist);
	    	return;
	    }
	    Log.d(TAG, "BrowserFile():"+filePath);
	    PlayList.getinstance().rootPath =filePath;
	    
	    File [] fs = new File[listFiles.size()];
	    for(i=0;i<listFiles.size();i++) {
	    	fs[i] = listFiles.get(i);
	    }
	    Arrays.sort(fs, new MyComparator(MyComparator.NAME_ASCEND));

		if(false==isRealSD)
		{
			if(filePath.equals("/mnt"))
			{
				Arrays.sort(fs, new MyComparator(MyComparator.NAME_DESCEND)); 
			}
		}
	    
	    for(i=0;i<fs.length;i++)
	    {
	    	File tempF = fs[i];
	    	String tmppath = tempF.getName();
	    	
		    //change device name;	
	    	if(filePath.equals("/mnt"))
	    	{
	    		String tpath = tempF.getAbsolutePath();
	    	
	    		if (tpath.equals("/mnt/flash"))
	    			 tmppath = "nand";
				else if (tpath.equals(EXT_SD))
	    			 tmppath = "external_sdcard";
	    		else if((!tpath.equals("/mnt/sdcard"))&&tpath.startsWith("/mnt/sd"))
	    			 tmppath = "usb"+" "+tpath.substring(5);//5 is the len of "/mnt/"
	    		//delete used folder
	    		if((!tpath.equals("/mnt/asec"))&&(!tpath.equals("/mnt/secure"))&&
	    			(!tpath.equals("/mnt/obb")))
	    		{
	    			//if(false==isRealSD)
    				//{
		    			String path=changeDevName(tmppath);
		    			items.add(path);
    				/*}
					else
					{
						items.add(tmppath);
					}*/
					
	    	    	paths.add(tempF.getPath());
	    		}
	    	}
	    	else
	    	{
	    		items.add(tmppath);
	    		paths.add(tempF.getPath());
	    	}
		 }
		    
	    tileText =(TextView) findViewById(R.id.TextView_path);
	    tileText.setText(catShowFilePath(filePath));
	    setListAdapter(new MyAdapter(this,items,paths));
	}

	private String changeDevName(String tmppath)
	{
		String path="";
		String internal = getString(R.string.memory_device_str);
		String sdcard = getString(R.string.sdcard_device_str);
		String usb = getString(R.string.usb_device_str);
		String sdcardExt = getString(R.string.ext_sdcard_device_str);

		//Log.i("wxl","[changeDevName]tmppath:"+tmppath);

		if(tmppath.equals("flash"))
		{
			path=internal;
		}
		else if(tmppath.equals("sdcard"))
		{
			if(true==isRealSD)
				path=sdcardExt;
			else
				path=sdcard;
		}
		else if(tmppath.equals("usb"))
		{
			path=usb;
		}
		else if(tmppath.equals("external_sdcard"))
		{
			path=sdcardExt;
		}
		else
		{
			path=tmppath;
		}

		//Log.i("wxl","[changeDevName]path:"+path);
		return path;
	}

    private String catShowFilePath(String path) {
    	String text = null;
    	
    	if(path.startsWith("/mnt/flash"))
    		text=path.replaceFirst("/mnt/flash","/mnt/nand");
    	else if(path.startsWith("/mnt/sda"))
    		text=path.replaceFirst("/mnt/sda","/mnt/usb sda");
    	else if(path.startsWith("/mnt/sdb"))
    		text=path.replaceFirst("/mnt/sdb","/mnt/usb sdb");
    	//else if(path.startsWith("/mnt/sdcard"))
    		//text=path.replaceFirst("/mnt/sdcard","sdcard");
    	return text;
    }	
    
	public void searchFile(File file)
	{
	    File[] the_Files;
	    the_Files = file.listFiles(new MyFilter(extensions));
	
	    if(the_Files == null)
	    {
		  Toast.makeText(FileList.this, R.string.str_no_file, Toast.LENGTH_SHORT).show();
		  return;
		 }
	    
	    String curPath=file.getPath();

	    for(int i=0;i<the_Files.length;i++) 
	    {
	    	File tempF = the_Files[i];
	    	
	    	if (tempF.isDirectory())
	    	{
	    		if(!tempF.isHidden())
	    			listFiles.add(tempF);
				
				if(false==isRealSD)
				{
		    		if(curPath.equals(root_path))
		    		{
		    			if((tempF.toString()).equals("/mnt/sdcard"))
						{
			    			File[] tempFSub = tempF.listFiles();
			    			if(tempFSub.length>0)
			    			{
				    			for(int n=0;n<tempFSub.length;n++)
				    			{
				    				if(tempFSub[n].isDirectory() && tempFSub[n].exists())
				    				{
				    					String path=tempFSub[n].getAbsolutePath();
				    					if(path.equals(EXT_SD))
				    					{
				    						if(!tempF.isHidden())
				    							listFiles.add(tempFSub[n]);  
				    					}
				    				}
				    			}
			    			}
						}
		    		}
		    		else if((tempF.toString()).equals(EXT_SD))
		    		{
		    			listFiles.remove(tempF);
		    			continue;
		    		}
				}
	    	} 
	    	else
	    	{
	    		try {
	    			listFiles.add(tempF);
	    		} 
	    		catch (Exception e) {
	    			return;
	    		}
	    	}
	    }
	}
	
	public boolean isISOFile(File file){		
		String fname = file.getName();		
		String sname = ".iso";		
		
		if (fname == "") {
			Log.e(TAG, "NULL file");
			return false;
		}
		if (fname.toLowerCase().endsWith(sname)) {
			return true;
		}		
		return false;		
	}	
	
	public void execCmd(String cmd){		
		int ch;
		Process p = null;
		Log.d(TAG, "exec command: " + cmd);
		try { 		
			p = Runtime.getRuntime().exec(cmd); 
			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream(); 
			StringBuffer sb = new StringBuffer(512);
			while ((ch = in.read()) != -1) {
				sb.append((char) ch);
			}
			if(sb.toString() != "")
				Log.d(TAG, "exec out:"+sb.toString());	
			while ((ch = err.read()) != -1) {
				sb.append((char) ch);
			}
			if(sb.toString() != "")
				Log.d(TAG, "exec error:"+sb.toString());				
		} catch (IOException e) {				
			Log.d(TAG, "IOException: " + e.toString());
		}	
	}
	
	@Override
	protected void onListItemClick(ListView l,View v,int position,long id) {		
		File file = new File(paths.get(position));		
		currentlist.clear();
		currentlist.addAll(paths);
	    //currentlist =paths;
	    
	    if(file.isDirectory()) {				
		    item_position_selected = getListView().getSelectedItemPosition();
			item_position_first = getListView().getFirstVisiblePosition();
			View cv = getListView().getChildAt(item_position_selected - item_position_first);
		    if (cv != null) {
		        fromtop_piexl = cv.getTop();
		    }	
		    BrowserFile(paths.get(position));
		    if(!listFiles.isEmpty()) {
		    		fileDirectory_position_selected.add(item_position_selected);
		    		fileDirectory_position_piexl.add(fromtop_piexl);
		    		pathLevel++;
	    	}
	    }else if(isISOFile(file)){	
	    	execCmd("vdc loop unmount");
	    	ISOpath = file.getPath();
	    	String cm = "vdc loop mount "+"\""+ISOpath+"\"";
			Log.d(TAG, "ISO path:"+ISOpath);	 			
			execCmd(cm);
			BrowserFile(iso_mount_dir);
	    }else 
	    {
		//stopMediaPlayer();
	    	//file = new File(file.getParent());
	    	int pos = filterDir(file);
	    	//Log.i(TAG, "play path:"+file.getPath()+", pos:"+Integer.toString(pos));
	    	if(pos < 0) 
	    		return;
	    	PlayList.getinstance().rootPath= file.getParent();
	    	//int dircount =listFiles.size()-listVideos.size();
	    	
	    	//if(dircount>=0&&(position-dircount)>=0)
			//PlayList.getinstance().setlist(paths, position-dircount);
			//else
			//PlayList.getinstance().setlist(paths, position);
	    	PlayList.getinstance().setlist(paths, pos);
	    	
			item_position_selected = getListView().getSelectedItemPosition();
			item_position_first = getListView().getFirstVisiblePosition();
			View cv = getListView().getChildAt(item_position_selected - item_position_first);
	        if (cv != null) {
	        	fromtop_piexl = cv.getTop();
	        }
			showvideobar();
	    }
	}
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_BACK) {        	
            if(paths == null) 
            {
            	FileList.this.finish();
            	PlayList.getinstance().rootPath =null;
            }
            else
            {
            	if(paths.isEmpty())
            	{
            		FileList.this.finish();
            		PlayList.getinstance().rootPath =null;
            	}
            	file = new File(paths.get(0).toString());
				if(file.getParent().compareTo(iso_mount_dir) == 0 && ISOpath != null) {					
					file = new File(ISOpath);
					ISOpath = null;
				}
            	currenturl =file.getParentFile().getParent();
            	if((file.getParent().compareToIgnoreCase(root_path)!=0)&&(pathLevel>0)){
            		pathLevel--;

					if(false==isRealSD)
					{
						String path = file.getParent();
						if(path.equals(EXT_SD))
						{
							currenturl=root_path;
						}
					}
            		BrowserFile(currenturl);
                    getListView().setSelectionFromTop(fileDirectory_position_selected.get(pathLevel), fileDirectory_position_piexl.get(pathLevel));
            		fileDirectory_position_selected.remove(pathLevel);
            		fileDirectory_position_piexl.remove(pathLevel);
            	}
            	else
            	{
        			FileList.this.finish();
        			PlayList.getinstance().rootPath =null;
            	}
            }  
            return true;                 
        }
        return super.onKeyDown(keyCode, event); 
    }
	private void showvideobar() {
		//* new an Intent object and ponit a class to start
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("item_position_selected", item_position_selected);
	    bundle.putInt("item_position_first", item_position_first);
	    bundle.putInt("fromtop_piexl", fromtop_piexl);
	    bundle.putIntegerArrayList("fileDirectory_position_selected", fileDirectory_position_selected);
	    bundle.putIntegerArrayList("fileDirectory_position_piexl", fileDirectory_position_piexl);
	    bundle.putBoolean("backToOtherAPK", false);
		intent.setClass(FileList.this, playermenu.class);
		intent.putExtras(bundle);

		if(SettingsVP.chkEnableOSD2XScale() == true)
  	  		this.setVisible(false);

		startActivity(intent);
		FileList.this.finish();
	}
	
	public int filterDir(File file)
	{
		int pos = -1;
	    File[] the_Files;
	    File parent = new File(file.getParent());
	    the_Files = parent.listFiles(new MyFilter(extensions));
	
	    if(the_Files == null)
	    	return pos;
	    
	    pos = 0;
	    listVideos = new ArrayList<File>();
	    for(int i=0;i<the_Files.length;i++) 
	    {
	    	File tempF = the_Files[i];
	    	
	    	if (tempF.isFile())
	    	{
	    		listVideos.add(tempF);
	    	} 
	    }
	    
	    paths=new ArrayList<String>();
    	File [] fs = new File[listVideos.size()];
	    for(int i=0;i<listVideos.size();i++) {
	    	fs[i] = listVideos.get(i);
	    }
	    Arrays.sort(fs, new MyComparator(MyComparator.NAME_ASCEND));   
	    
	    for(int i=0;i<fs.length;i++) {
	    	File tempF = fs[i];
	    	if(tempF.getPath().equals(file.getPath())) {
	    		pos = i;
	    	}
	    	paths.add(tempF.getPath());
	    }
	    return pos;
	}
    
    //option menu
    private final int MENU_ABOUT = 0;
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, MENU_ABOUT, 0, R.string.str_about);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
	        case MENU_ABOUT:
				try {
					Toast.makeText(FileList.this, " VideoPlayer \n Version: " +
		        			FileList.this.getPackageManager().getPackageInfo("com.farcore.videoplayer", 0).versionName,
		        			Toast.LENGTH_SHORT)
		        			.show();
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	return true;
        }
        return false;
    }
    
    public void stopMediaPlayer()//stop the backgroun music player
    {
    	Intent intent = new Intent();
    	intent.setAction("com.android.music.musicservicecommand.pause");
    	intent.putExtra("command", "stop");
    	this.sendBroadcast(intent);
    }
}
