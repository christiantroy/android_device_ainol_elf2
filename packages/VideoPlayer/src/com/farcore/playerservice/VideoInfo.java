package com.farcore.playerservice;

public class VideoInfo {
	
	public final static int TIME_INFO_MSG = 1000;
	public final static int STATUS_CHANGED_INFO_MSG = 1000+1;
	public final static int AUDIO_CHANGED_INFO_MSG = 1000+2;
	public final static int HAS_ERROR_MSG = 1000+3;

    //typedef enum { ... } player_status;
    public static final int PLAYER_UNKNOWN  = 0;
    
	/******************************
	* 0x1000x: 
	* player do parse file
	* decoder not running
	******************************/
    public static final int PLAYER_INITING  	= 0x10001;
	public static final int PLAYER_TYPE_REDY  	= 0x10002;
    public static final int PLAYER_INITOK   	= 0x10003;	

	/******************************
	* 0x2000x: 
	* playback status
	* decoder is running
	******************************/
    public static final int PLAYER_RUNNING  	= 0x20001;
    public static final int PLAYER_BUFFERING 	= 0x20002;
    public static final int PLAYER_PAUSE    	= 0x20003;
    public static final int PLAYER_SEARCHING	= 0x20004;
	
    public static final int PLAYER_SEARCHOK 	= 0x20005;
    public static final int PLAYER_START    	= 0x20006;	
    public static final int PLAYER_FF_END   	= 0x20007;
    public static final int PLAYER_FB_END   	= 0x20008;

	/******************************
	* 0x3000x: 
	* player will exit	
	******************************/
    public static final int PLAYER_ERROR		= 0x30001;
    public static final int PLAYER_PLAYEND  	= 0x30002;	
    public static final int PLAYER_STOPED   	= 0x30003; 
	
	public static final int PLAYER_EXIT   		= 0x30004; 

    /********************************
     * 0x4000x
     * divx about
     * ******************************/
    public static final int DIVX_AUTHOR_ERR     = 0x40001;
    public static final int DIVX_EXPIRED        = 0x40002;
    public static final int DIVX_RENTAL         = 0x40003;

}
