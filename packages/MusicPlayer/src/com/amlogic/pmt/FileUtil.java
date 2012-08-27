package com.amlogic.pmt;

import java.io.File;

public class FileUtil {
	static String defaultVideoTex;
	static String defaultAudioTex;
	static String defaultPictureTex;
	static String defaultDocTex;
	static String defaultUnknownTex;
	static String defaultNotExistTex;
	static String defaultDirTex;
	static int defaultVideoTexID = 0;
	static int defaultAudioTexID = 0;
	static int defaultPictureTexID = 0;
	static int defaultDocTexID = 0;
	static int defaultUnknownTexID = 0;
	static int defaultNotExistTexID = 0;
	static int defaultDirTexID = 0;

	public static String getFileExtension(String fileName){
		final int index = fileName.lastIndexOf('.');
		return (index == -1)? "": fileName.substring(index + 1).toLowerCase().intern();
	}
	public static void setDefaultTexForFiles(
			String dirTexture,
			String videoTexture,
			String audioTexture,
			String pictureTexture,
			String docTexture,
			String unknownTexture,
			String notexistTexture
			){
		defaultDirTex = new String(dirTexture);
		defaultVideoTex = new String(videoTexture);
		defaultAudioTex = new String(audioTexture);
		defaultPictureTex = new String(pictureTexture);
		defaultDocTex = new String(docTexture);
		defaultUnknownTex = new String(unknownTexture);
		defaultNotExistTex = new String(notexistTexture);		
		
		//Generate Texture by TextureManager
		//To do...
	}
	
	public static FileType getFileType(String fileName){
		File file = new File(fileName);
		if(file.exists()){
			if(file.isDirectory()){
				return FileType.FILE_IS_DIR;
			}else if( "txt".equalsIgnoreCase(getFileExtension(fileName)) &&
				"epub".equalsIgnoreCase(getFileExtension(fileName)) &&
				"fb2".equalsIgnoreCase(getFileExtension(fileName))){
				return FileType.FILE_IS_DOC;
				
			}else if( "jpg".equalsIgnoreCase(getFileExtension(fileName)) &&
					  "jpeg".equalsIgnoreCase(getFileExtension(fileName)) &&
					  "jpg".equalsIgnoreCase(getFileExtension(fileName))){
				return FileType.FILE_IS_PICTURE;
			}else if( "avi".equalsIgnoreCase(getFileExtension(fileName)) &&
					  "rmvb".equalsIgnoreCase(getFileExtension(fileName)) &&
					  "mpeg".equalsIgnoreCase(getFileExtension(fileName))){
				return FileType.FILE_IS_VIDEO;
			}else if( "mp3".equalsIgnoreCase(getFileExtension(fileName)) &&
					  "wav".equalsIgnoreCase(getFileExtension(fileName)) &&
					  "ogg".equalsIgnoreCase(getFileExtension(fileName))){
				return FileType.FILE_IS_VIDEO;
			}else{				
				return FileType.FILE_IS_UNKNOWN;
			}
		}
		else{
			return FileType.FILE_NOT_EXIST;
		}
	}
	public static int getFileDefaultTexture(String fileName){
		FileType ft = getFileType(fileName);
		if( FileType.FILE_IS_DIR == ft){
			return defaultDirTexID;
		}else if( FileType.FILE_IS_AUDIO == ft){
			return defaultAudioTexID;
		}else if(FileType.FILE_IS_DOC == ft){
			return defaultDocTexID;
		}else if(FileType.FILE_IS_PICTURE == ft){
			return defaultPictureTexID;
		}else if(FileType.FILE_IS_VIDEO == ft){
			return defaultVideoTexID;
		}else if(FileType.FILE_IS_UNKNOWN == ft){
			return defaultUnknownTexID;
		}else{
			return defaultNotExistTexID;
		}
	}
}

