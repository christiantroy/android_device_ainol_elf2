package com.amlogic.pmt.browser;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

public class FolderBrowser {
	
	 private List<FilebrowserItemData> ItemList,TemporaryList,preItemList ;
	 private String FileType ;
	 private File currentDirectory ;
	 private SearchDrawableID SearchDid; 
	 private Context MyContext;
	 private String[] PlayFormat;
	 private OnFolderBrowserListener OnBListener;
	 public static boolean reqStopPreList = false;

	 public FolderBrowser(Context context)
	 {
		 MyContext = context;
//		 IfHaveFolder = iffolder;
		 ItemList = new ArrayList<FilebrowserItemData>();
		 TemporaryList = new ArrayList<FilebrowserItemData>();
		 preItemList  = new ArrayList<FilebrowserItemData>();
		 SearchDid = new SearchDrawableID();
		 
		 String sdcardDirectory = android.os.Environment.getExternalStorageDirectory().toString();
		 currentDirectory = new File(sdcardDirectory); 
	 }
	 
	 public FolderBrowser(Context context,String filetype,String devicepath)
	 {
		 MyContext = context;
		 FileType =  filetype;
//		 IfHaveFolder = iffolder;
		 ItemList = new ArrayList<FilebrowserItemData>();
		 TemporaryList = new ArrayList<FilebrowserItemData>();
		 preItemList  = new ArrayList<FilebrowserItemData>();
		 SearchDid = new SearchDrawableID();
		 
//		 String sdcardDirectory = android.os.Environment.getExternalStorageDirectory().toString();
//		 currentDirectory = new File(sdcardDirectory); 
		 currentDirectory = new File(devicepath); 
	 }
	 
	 public void SetFileType(String type)
	 {
		 FileType = type;
		 PlayFormat = MyContext.getResources().	getStringArray((SearchDid.PlayFormatID(FileType)));
		 
	 }
	 private boolean checkEndsWithInStrings(String checkItsEnd, 
				String[] fileEndings)
		{

			for(String aEnd : fileEndings)
			{
				if(checkItsEnd.toLowerCase().endsWith(aEnd))
					return true;
			}
			return false;
			
		}
	 
	/* public  void listFile(String path) {    
	        File file = new File(path);    
	        if (!file.isDirectory()) {    

	        	if(checkEndsWithInStrings(file.getName(),PlayFormat))
	        		ItemList.add(new FilebrowserItemData(file.getName(),SearchDid.SearchID(FileType,true),SearchDid.SearchID(FileType,false)));
	            
	        } else {    
	  
	            File files[] = file.listFiles();  
	            if(files != null )
		            for (int i = 0; i < files.length; i++) {    
		   
		                listFile(files[i].getPath());    
		            }    
	   
	        }    
	    }   
	  */
	 public List<FilebrowserItemData> GetAllFileList()
	 {
//		 ItemList.clear();
//		 listFile(currentDirectory.getPath());   
		 return ItemList;

	 }
	
	 public List<FilebrowserItemData> GetFileList(String path)
	 {
		 ItemList.clear();
		 TemporaryList.clear();
		 File[] files = new File(path).listFiles( new FileFilter(){
				public boolean accept(File pathname) {
					if(pathname.isDirectory() || checkEndsWithInStrings(pathname.getPath(),PlayFormat)){
						return true;
					}
					return false;
				}
			 });
		 
		 if(files == null || files.length ==0 )
			 return null;
		 else
		 {
			 for (File file : files)
			 {
			 	if (file.isDirectory())
		 		{
			 		TemporaryList.add(new FilebrowserItemData(file.getPath(),SearchDid.SearchID("folder",true),SearchDid.SearchID("folder",false)));
		 		}
			 	else
			 	{
//			 		String fileName = file.getName();
//			 		if(checkEndsWithInStrings(fileName,PlayFormat))
//			 		{
			 			TemporaryList.add(new FilebrowserItemData(file.getPath(),SearchDid.SearchID(FileType,true),SearchDid.SearchID(FileType,false)));
//			 		}
			 	}
		     } 	
	      }
	
		 if(TemporaryList.size() >= 1)
		 {
			 Collections.sort(TemporaryList, new FileComparator());
			 for(int i=0;i<TemporaryList.size();i++)
				 ItemList.add(TemporaryList.get(i));
			 return ItemList;
		 }
		 else
			 return null; 
		
	 }
	 
	 
	 public List<FilebrowserItemData> GetPreFileList(String path)
	 {
		 preItemList.clear();
		 File thisFile = new File(path);
		 String[] filenames = thisFile.list();
		 
		 if(filenames == null || filenames.length ==0 )
			 return null;
		 else
		 {
			 for (String filename : filenames)
			 {
				File file = new File(thisFile, filename);
			 	if (file.isDirectory())
		 		{
			 		preItemList.add(new FilebrowserItemData(file.getAbsolutePath(),SearchDid.SearchID("folder",true),SearchDid.SearchID("folder",false)));
		 		}
			 	else
			 	{
			 		String fileName = file.getName();
			 		if(checkEndsWithInStrings(fileName,PlayFormat))
			 		{
			 			preItemList.add(new FilebrowserItemData(file.getAbsolutePath(),SearchDid.SearchID(FileType,true),SearchDid.SearchID(FileType,false)));
			 		}
			 	}
			 	if(preItemList.size() >= 11 || FolderBrowser.reqStopPreList)
					break;
		     } 	
	      }
		 if(preItemList.size() >= 1)
		 {
			 Collections.sort(preItemList, new FileComparator());
			 return preItemList;
		 }
		 else
			 return null; 
		
	 }
	 
	 
	 
//	public List<FilebrowserItemData>  upOneLevel()
	public boolean  upOneLevel()
	{
		File fl = currentDirectory;
		
		int level = 2;
		if(fl.getPath().startsWith("/mnt/sdcard"))
			level = 2;
		
		for(int i=0; i<level; i++){
			if(fl.getParent() == null || 
				fl.getPath().equals("/mnt"))
				return false;// null;
			fl = fl.getParentFile();
		}

		currentDirectory = currentDirectory.getParentFile();
//		return GetFileList(this.currentDirectory.getPath());
		StartLoadData();
		return true;
	}
		
//	 public List<FilebrowserItemData>  FirstFileBrowser()
	 public void  FirstFileBrowser()
	 {
//		 if(IfHaveFolder == false)
//		 	 return GetAllFileList();
//		 else
//		     return GetFileList(this.currentDirectory.getPath());
		 StartLoadData();
	 }
	 
//	 public List<FilebrowserItemData> EnterFocusDir(int position)
	 public void EnterFocusDir(int position)
	 {
		 	String path = this.ItemList.get(position).getAbsoluteFilePath();
		    //String path = this.currentDirectory.getAbsolutePath() +'/'+ this.ItemList.get(position).getFileName(); 
		    File PathFile = new File(path);
			if (PathFile.isDirectory())
				this.currentDirectory = PathFile;
//		    return GetFileList(path);
			StartLoadData();
	 }
	 public String GetSelectFileName(int position)
	 {
		 	String path = this.ItemList.get(position).getAbsoluteFilePath();
		    //String path = this.currentDirectory.getAbsolutePath() +'/'+ this.ItemList.get(position).getFileName(); 
		    File PathFile = new File(path);
		    if (PathFile.isDirectory())
		    	return PathFile.getPath();
		    else
		    	return PathFile.getPath();    
	 }
	
	 public boolean IFIsFolder(int position)
	 {
		 	String path = this.ItemList.get(position).getAbsoluteFilePath();
		    //String path = this.currentDirectory.getAbsolutePath() +'/'+ this.ItemList.get(position).getFileName(); 
		    File PathFile = new File(path);
			if (PathFile.isDirectory())
				return true;
			else
				return false;
	 }
	 public boolean IFIsFolder(String filename)
	 {
		    File PathFile = new File(filename);
			if (PathFile.isDirectory())
				return true;
			else
		    return false;
		    
	 }
	 
	 public ArrayList<String> GetPlayerList(ArrayList<String> playerlist)
	 {
		 playerlist.clear();
		 for(int i =0;i<ItemList.size();i++)
		 {
		
			 if(checkEndsWithInStrings(ItemList.get(i).getFileName(),PlayFormat))
			 {
				 playerlist.add(ItemList.get(i).getAbsoluteFilePath());
			 }
			
		 }
		 return playerlist;
	 }
	public String getCurDirPath(){
		if(currentDirectory != null)
			return currentDirectory.getAbsolutePath();
		return null;
	}
	
	private void StartLoadData() {
		new LoadDataFromUSB().execute();
	}
	
	public void SetOnFolderBrowserListener(OnFolderBrowserListener listener) {
		OnBListener = listener;
	}

	private class LoadDataFromUSB extends
		AsyncTask<Object, FilebrowserItemData, Object> {
	
		// @Override
		protected Object doInBackground(Object... params) {
/*			File[] files = currentDirectory.listFiles();
			if (files == null || files.length == 0)
				return null;
			else {
				for (File file : files) {
					if (file.isDirectory()) {
						publishProgress(new FilebrowserItemData(file.getPath(),
								SearchDid.SearchID("folder", true), SearchDid
										.SearchID("folder", false)));
					} else {
						String fileName = file.getName();
						if (checkEndsWithInStrings(fileName, PlayFormat)) {
							publishProgress(new FilebrowserItemData(file
									.getPath(), SearchDid.SearchID(FileType,
									true), SearchDid.SearchID(FileType, false)));
						}
					}
				}
			}
	
			// if (filetotal == 0)
			// publishProgress(null);
	*/
			if(GetFileList(currentDirectory.getPath()) == null)
				publishProgress(null);
			return null;
		}
		
		// @Override
		public void onProgressUpdate(FilebrowserItemData... value) {
			OnBListener.OnFolderBrowser(value);
		}
		
		// @Override
		protected void onPostExecute(Object result) {
			OnBListener.OnFolderBrowserFinish();
		}
	};

	
	class FileComparator implements Comparator<FilebrowserItemData>{
		 public int compare(FilebrowserItemData o1, FilebrowserItemData o2) {
		  return o1.getAbsoluteFilePath().compareTo(o2.getAbsoluteFilePath());
		 }
	}
}




