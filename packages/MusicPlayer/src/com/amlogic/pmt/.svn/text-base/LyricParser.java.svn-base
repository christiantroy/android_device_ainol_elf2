package com.amlogic.pmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LyricParser {
	private File lyricFile = null;
	private ArrayList<LyricSentence> sentenceList;
	private String artistInfo;
	private String titleInfo;
	private String albumInfo;
	private String providerInfo;
	private int offsetTime;
	LyricParser(File lrcfile){
		lyricFile=lrcfile;
//		int index = songFileName.lastIndexOf(".");
//		if(index>0){
//			String lyricFileName = songFileName.substring(0, index)+".lrc";
//			lyricFile = new File(lyricFileName);
//		}
	}
	
	public boolean doParser(){
		if(lyricFile==null || !lyricFile.exists() || !lyricFile.isFile()){
			return false;
		}
		//parser line
		sentenceList = new ArrayList<LyricSentence>();
        BufferedReader bufferReader = null;
        try {
        	bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(lyricFile), "GBK"));
            StringBuilder sb = new StringBuilder();
            String lineString = null;
            while ((lineString = bufferReader.readLine()) != null) {
            	LyricSentence.parseLine(lineString);
            	sentenceList.addAll(LyricSentence.sentences);
            }
        } catch (Exception ex) {
        	;
        } finally {
            try {
                if(bufferReader!=null){
                	bufferReader.close();
                }
            } catch (Exception ex) {
            	;
            }
        }
        //Pick out other tags but TIME_TAG;
        for(int i=sentenceList.size()-1; i>=0; i--){
        	if(sentenceList.get(i).getTagName().equalsIgnoreCase(LyricSentence.ARTIST_TAG)){
        		artistInfo = sentenceList.get(i).getSentence();
        	}else if(sentenceList.get(i).getTagName().equalsIgnoreCase(LyricSentence.TITLE_TAG)){
        		titleInfo = sentenceList.get(i).getSentence();
        	}else if(sentenceList.get(i).getTagName().equalsIgnoreCase(LyricSentence.ALBUM_TAG)){
        		albumInfo = sentenceList.get(i).getSentence();
        	}else if(sentenceList.get(i).getTagName().equalsIgnoreCase(LyricSentence.PROVIDER_TAG)){
        		providerInfo = sentenceList.get(i).getSentence();
        	}else if(sentenceList.get(i).getTagName().equalsIgnoreCase(LyricSentence.OFFSET_TAG)){        		
        		offsetTime = sentenceList.get(i).getStartTime();
        	}else{
        		continue;
        	}
        	sentenceList.remove(i);
        }
        //Scan and Order...
        for(int i=sentenceList.size()-1;i>0;i--){
        	for(int j=0;j<i;j++){
            	LyricSentence ls1 = sentenceList.get(j);
            	LyricSentence ls2 = sentenceList.get(j+1);
        		if(ls1.getStartTime()>ls2.getStartTime()){
        			sentenceList.remove(j+1);
        			sentenceList.remove(j);
        			sentenceList.add(j, ls2);
        			sentenceList.add(j+1, ls1);
        		}
        	}
        }
        
        //Fill endTime
        for(int i=0; i<sentenceList.size()-1; i++){
        	if(sentenceList.get(i).getEndTime()<0){
        		sentenceList.get(i).setEndTime(sentenceList.get(i+1).getStartTime());
        	}
        }
        //Add offset;
        if(offsetTime!=0){
	        for(int i=0; i<sentenceList.size(); i++){
	        	sentenceList.get(i).setStartTime(sentenceList.get(i).getStartTime()+offsetTime);
	        	sentenceList.get(i).setEndTime(sentenceList.get(i).getEndTime()+offsetTime);
	        }        
        }

        return true;
	}
	public ArrayList<LyricSentence> getSentenceList(){
		return sentenceList;
	}
	public String getArtist(){
		return artistInfo;
	}
	public String getTitle(){
		return titleInfo;
	}
	public String getAlbum(){
		return albumInfo;
	}
	public String getProvider(){
		return providerInfo;
	}

}
