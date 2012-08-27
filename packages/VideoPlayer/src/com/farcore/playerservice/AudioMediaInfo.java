package com.farcore.playerservice;



public class AudioMediaInfo {
	public static final int UID_AUDIOTRACK_OFF = 0xFFFF;    
	    
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
    
	public int getUid() {
		return uid;
	}
	
	public String getAudioFormat() {
		String type = null;
		switch(audio_format)
		{
		case AFORMAT_UNKNOWN:
			type = "UNKNOWN";
			break;
		case AFORMAT_MPEG:
			type = "MP3";
			break;
		case AFORMAT_PCM_S16LE:
			type = "PCM";
			break;
		case AFORMAT_AAC:
			type = "AAC";
			break;
		case AFORMAT_AC3:
			type = "AC3";
			break;
		case AFORMAT_ALAW:
			type = "ALAW";
			break;
		case AFORMAT_MULAW:
			type = "MULAW";
			break;
		case AFORMAT_DTS:
			type = "DTS";
			break;
		case AFORMAT_PCM_S16BE:
			type = "PCM_S16BE";
			break;
		case AFORMAT_FLAC:
			type = "FLAC";
			break;
		case AFORMAT_COOK:
			type = "COOK";
			break;
		case AFORMAT_PCM_U8:
			type = "PCM_U8";
			break;
		case AFORMAT_ADPCM:
			type = "ADPCM";
			break;
		case AFORMAT_AMR:
			type = "AMR";
			break;
		case AFORMAT_RAAC:
			type = "RAAC";
			break;
		case AFORMAT_WMA:
			type = "WMA";
			break;
		case AFORMAT_WMAPRO:
			type = "WMAPRO";
			break;
		case AFORMAT_PCM_BLURAY:
			type = "PCM_BLURAY";
			break;
		case AFORMAT_ALAC:
			type = "ALAC";
			break;
		case AFORMAT_VORBIS:
			type = "VORBIS";
			break;
		case AFORMAT_UNSUPPORT:
			type = "UNSUPPORT";
			break;
		case AFORMAT_MAX:
			type = "MAX";
			break;
		default:
			type = "UNKNOWN";
			break;
		}
		return type;
	}
	
	/*public int getAudioChannel() {
		return audio_channel;
	}
	
	public int getBitrate() {
		return bit_rate;
	}
	
	public int getAudioSamplerate() {
		return audio_samplerate;
	} */
	
	int uid;
	private int audio_format;
	//private int audio_channel;   
    //private int bit_rate;
    //private int audio_samplerate;
    
}
