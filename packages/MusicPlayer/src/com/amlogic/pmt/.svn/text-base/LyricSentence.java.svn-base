package com.amlogic.pmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LyricSentence {
	public final static String ARTIST_TAG	= "ARTIST_TAG";
	public final static String TITLE_TAG 	= "TITLE_TAG";
	public final static String ALBUM_TAG 	= "ALBUM_TAG";
	public final static String PROVIDER_TAG 	= "PROVIDER_TAG";
	public final static String OFFSET_TAG 	= "OFFSET_TAG";
	public final static String TIME_TAG 	= "TIME_TAG";
	private String name = null;
	private int startTime = 0;
	private int endTime = 0;
	private String sentence;
	private ArrayList<LyricTimeWordMap> startTimePerWord = null;
	private LyricSentence(String n, int sTime, int eTime, String s, ArrayList<LyricTimeWordMap> stPerWord){
		name = n;
		startTime = sTime;
		endTime = eTime;
		sentence = s;
		startTimePerWord = stPerWord;
	}
	
	public String getTagName(){
		return name;
	}
	public int getStartTime(){
		return startTime;
	}
	public void setStartTime(int st){
		startTime = st;
	}
	public int getEndTime(){
		return endTime;
	}
	public void setEndTime(int et){
		endTime = et;
	}
	public String getSentence(){
		return sentence;
	}
	public void setSentence(String sen){
		sentence = sen;
	}
	
	
	static private String curTagType;
	static private String curTagInfo;
	static private int curTagTime;
	static boolean parseTag(String tag){
		curTagType = null;
		curTagInfo = null;
		curTagTime = 0;
		if(tag.startsWith("ar:")){
			curTagType = ARTIST_TAG;
			curTagInfo = tag.substring(3, tag.length()-1);
		}else if(tag.startsWith("ti:")){
			curTagType = TITLE_TAG;
			curTagInfo = tag.substring(3, tag.length()-1);
		}else if(tag.startsWith("al:")){
			curTagType = ALBUM_TAG;
			curTagInfo = tag.substring(3, tag.length()-1);
		}else if(tag.startsWith("by:")){
			curTagType = PROVIDER_TAG;
			curTagInfo = tag.substring(3, tag.length()-1);
		}else if(tag.startsWith("offset:")){
			curTagType = OFFSET_TAG;
			curTagInfo = tag.substring(7, tag.length()-1);
			curTagTime = Integer.parseInt(curTagInfo);
		}else if(tag.startsWith("")){
			curTagType = TIME_TAG;
			curTagInfo = tag.substring(0, tag.length()-1);
			String[] timeStrings = curTagInfo.split(":");
			curTagTime = (int)((Integer.parseInt(timeStrings[0])*60+Float.parseFloat(timeStrings[1]))*1000);
		}
		else{
			return false;
		}
		return true;
	}
	
	static void parseLine(String line){
		boolean timePerWord = false;
		if(sentences == null){
			sentences = new ArrayList<LyricSentence>();
		}else{
			sentences.clear();
		}
		String rawLine = new String(line);
		String[] tagStartSplitStrings = rawLine.split("\\[");//rawLine.split("[");
		String lyric = null;
		for(int i = tagStartSplitStrings.length-1; i>=0; i--){
			int tagEndIndex = tagStartSplitStrings[i].indexOf("]");
			if(tagEndIndex<0) continue;
			String t = tagStartSplitStrings[i].substring(0, tagEndIndex+1);
			String s = tagStartSplitStrings[i].substring(tagEndIndex+1);
			if(!parseTag(t)) continue;
			if(lyric != null && !s.equalsIgnoreCase("")){
				//Case 3: KalaOK line,
				timePerWord = true;
				if(curTagType.equalsIgnoreCase(TIME_TAG)){
					sentences.add(0, new LyricSentence(curTagType, curTagTime, -1, s, null));
				}
				lyric += s;
			}else if(lyric != null && s.equalsIgnoreCase("")){
				//Case 2: Normal line, several tags, including info is in ahead, following by lyric string;
				if(curTagType.equalsIgnoreCase(ARTIST_TAG) ||
					curTagType.equalsIgnoreCase(TITLE_TAG) ||	
					curTagType.equalsIgnoreCase(ALBUM_TAG) ||	
					curTagType.equalsIgnoreCase(PROVIDER_TAG)){
					sentences.add(0, new LyricSentence(curTagType, -1, -1, curTagInfo, null));
				}else if(curTagType.equalsIgnoreCase(OFFSET_TAG)){
					sentences.add(0, new LyricSentence(curTagType, curTagTime, -1, null, null));
				}else if(curTagType.equalsIgnoreCase(TIME_TAG)){
					sentences.add(0, new LyricSentence(curTagType, curTagTime, -1, lyric, null));
				}else{
					continue;
				}
			}else if(lyric == null && !s.equalsIgnoreCase("")){
				//Case 2/3: Start
				if(curTagType.equalsIgnoreCase(TIME_TAG)||
					curTagType.equalsIgnoreCase(OFFSET_TAG)){
					sentences.add(0, new LyricSentence(curTagType, curTagTime, -1, s, null));
				}
				else if(curTagType.equalsIgnoreCase(ARTIST_TAG) ||
						curTagType.equalsIgnoreCase(TITLE_TAG) ||	
						curTagType.equalsIgnoreCase(ALBUM_TAG) ||	
						curTagType.equalsIgnoreCase(PROVIDER_TAG)){
					sentences.add(0, new LyricSentence(curTagType, -1, -1, curTagInfo, null));
				}
				lyric = s;
			}else if(lyric == null && s.equalsIgnoreCase("")){
				//Case 1: Tag Only
				if(curTagType.equalsIgnoreCase(ARTIST_TAG) ||
					curTagType.equalsIgnoreCase(TITLE_TAG) ||	
					curTagType.equalsIgnoreCase(ALBUM_TAG) ||	
					curTagType.equalsIgnoreCase(PROVIDER_TAG)){
					sentences.add(0, new LyricSentence(curTagType, -1, -1, curTagInfo, null));
				}else if(curTagType.equalsIgnoreCase(OFFSET_TAG) ||
					curTagType.equalsIgnoreCase(TIME_TAG)){
					sentences.add(0, new LyricSentence(curTagType, curTagTime, -1, null, null));
				}else{
					continue;
				}
			}
		}
		if(timePerWord){
			//get one sentence from multi sentences;
			ArrayList<LyricTimeWordMap> perWordMap = new ArrayList<LyricTimeWordMap>();
			for(LyricSentence ls : sentences){
				if(ls.getTagName().equalsIgnoreCase(TIME_TAG)){
					perWordMap.add(new LyricTimeWordMap(ls.getStartTime(), ls.getSentence()));
				}
			}
			sentences.clear();
			sentences.add(0, new LyricSentence(TIME_TAG, perWordMap.get(0).startTime, ((perWordMap.get((perWordMap.size()-1))).sentence == null)? (perWordMap.get((perWordMap.size()-1))).startTime : -1, lyric, perWordMap));
		}
	}
	public static ArrayList<LyricSentence> sentences;
}
