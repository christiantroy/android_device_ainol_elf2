package com.farcore.videoplayer;

import java.util.ArrayList;
import java.util.List;

import com.farcore.playerservice.MediaInfo;
import android.util.Log;

public class AudioTrackOperation {
	public static List<String> AudioStreamFormat = new ArrayList<String>();
	public static List<ASInfo> AudioStreamInfo = new ArrayList<ASInfo>();
	
	public static class ASInfo {
		public int audio_id;
		public String audio_format;
	}
    //audio format
    public static final int AFORMAT_UNKNOWN = -1;
    public static final int AFORMAT_MPEG   = 0;
    public static final int AFORMAT_PCM_S16LE = 1;
    public static final int AFORMAT_AAC   = 2;
    public static final int AFORMAT_AC3   =3;
    public static final int AFORMAT_ALAW = 4;
    public static final int AFORMAT_MULAW = 5;
    public static final int AFORMAT_DTS = 6;
    public static final int AFORMAT_PCM_S16BE = 7;
    public static final int AFORMAT_FLAC = 8;
    public static final int AFORMAT_COOK = 9;
    public static final int AFORMAT_PCM_U8 = 10;
    public static final int AFORMAT_ADPCM = 11;
    public static final int AFORMAT_AMR  = 12;
    public static final int AFORMAT_RAAC  = 13;
    public static final int AFORMAT_WMA  = 14;
    public static final int AFORMAT_WMAPRO   = 15;
    public static final int AFORMAT_PCM_BLURAY = 16;
    public static final int AFORMAT_ALAC = 17;
    public static final int AFORMAT_VORBIS = 18;
    public static final int AFORMAT_UNSUPPORT = 19;
    public static final int AFORMAT_MAX    = 20;
	
	public static void setAudioStream(MediaInfo mi)
	{
		if (null == mi)
		{
			Log.e("setAudioStream", "the MediaInfo is null");
			return;
		}
		for (int i = 0; i < mi.getAudioTrackCount(); i++)
		{
			ASInfo asinfo = new ASInfo();
			asinfo.audio_id = mi.ainfo[i].getUid();
			asinfo.audio_format = mi.ainfo[i].getAudioFormat();
			AudioStreamInfo.add(asinfo);
			AudioStreamFormat.add(asinfo.audio_format);
			Log.d("audiostream","AudioTrackOperation.AudioStream.add: " + asinfo.audio_format);
		}
	}
}
