package com.amlogic.pmt;

public class FileType {
	public final static FileType FILE_IS_UNKNOWN = new FileType("FILE_IS_UNKNOWN"); 
	public final static FileType FILE_NOT_EXIST = new FileType("FILE_NOT_EXIST"); 
	public final static FileType FILE_IS_DIR = new FileType("FILE_IS_DIR");
	public final static FileType FILE_IS_VIDEO = new FileType("FILE_IS_VIDEO"); 
	public final static FileType FILE_IS_AUDIO = new FileType("FILE_IS_AUDIO"); 
	public final static FileType FILE_IS_PICTURE = new FileType("FILE_IS_PICTURE"); 
	public final static FileType FILE_IS_DOC = new FileType("FILE_IS_DOC");
	
	private String TAG;
	
	private FileType(String info){
		TAG = info;
	}
}
