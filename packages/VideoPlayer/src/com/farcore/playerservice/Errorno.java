package com.farcore.playerservice;

import com.farcore.videoplayer.PlayList;

public class Errorno{	
    // error no
    public static final int PLAYER_SUCCESS 			=	0;
    public static final int PLAYER_FAILED  			=	-0x02000001;
    public static final int PLAYER_NOMEM   			=   -0x02000002;
    public static final int PLAYER_EMPTY_P 			=   -0x02000003;
    public static final int PLAYER_NOT_VALID_PID 	=   -0x02000004;
    public static final int PLAYER_CAN_NOT_CREAT_THREADS =   -0x02000005;
    public static final int PLAYER_ERROR_PARAM 		=   -0x02000006;

    public static final int PLAYER_RD_FAILED 		=   -0x02000011;
    public static final int PLAYER_RD_EMPTYP 		=   -0x02000012;
    public static final int PLAYER_RD_TIMEOUT 		=   -0x02000013;
    public static final int PLAYER_RD_AGAIN 		=   -0x02000014;

    public static final int PLAYER_WR_FAILED 		=   -0x02000015;
    public static final int PLAYER_WR_EMPTYP 		=   -0x02000016;
    public static final int PLAYER_WR_FINISH 		=   0x02000001;

    public static final int PLAYER_PTS_ERROR 		=   -0x02000031;
    public static final int PLAYER_NO_DECODER 		=   -0x02000032;
    public static final int DECODER_RESET_FAILED 	=   -0x02000033;
    public static final int DECODER_INIT_FAILED 	=   -0x02000034;
    public static final int PLAYER_UNSUPPORT 		=   -0x02000035;
    public static final int PLAYER_UNSUPPORT_VIDEO 	= 	-0x02000036;
    public static final int PLAYER_UNSUPPORT_AUDIO 	= 	-0x02000037;
    public static final int PLAYER_SEEK_OVERSPILL  	= 	-0x02000038;
    public static final int PLAYER_CHECK_CODEC_ERROR = 	-0x02000039;
    public static final int PLAYER_INVALID_CMD 		=   -0x02000040;
    
    public static final int PLAYER_REAL_AUDIO_FAILED = 	-0x02000041;
	public static final int PLAYER_ADTS_NOIDX		=	-0x02000042;
	public static final int PLAYER_SEEK_FAILED		= 	-0x02000043;
	public static final int PLAYER_NO_VIDEO  		= 	-0x02000044;
	public static final int PLAYER_NO_AUDIO  		= 	-0x02000045;
	public static final int PLAYER_SET_NOVIDEO  	= 	-0x02000046;
	public static final int PLAYER_SET_NOAUDIO  	= 	-0x02000047;
	public static final int PLAYER_FFFB_UNSUPPORT   =   -0x02000048;
	public static final int PLAYER_UNSUPPORT_VCODEC =   -0x02000049;

    public static final int FFMPEG_SUCCESS 			=   0;
    public static final int FFMPEG_OPEN_FAILED 		=   -0x03000001;
    public static final int FFMPEG_PARSE_FAILED 	=   -0x03000002;
    public static final int FFMPEG_EMP_POINTER 		=	-0x03000003;
    public static final int FFMPEG_NO_FILE 			=   -0x03000004;
    
    public static final int DIVX_SUCCESS            =   0;
    public static final int DIVX_AUTHOR_ERR         =   -0x04000001;

    public static String getErrorInfo(int errID)
    {
    	String errStr = null;		
    	switch (errID)
    	{
			case Errorno.PLAYER_UNSUPPORT:
				errStr = "Unsupport Media";
				break;
			case Errorno.PLAYER_UNSUPPORT_VIDEO:
				errStr = "Unsupport Video format";
				break;
			case Errorno.PLAYER_UNSUPPORT_AUDIO:
				errStr = "Unsupport Audio format";
				break;
			case Errorno.FFMPEG_OPEN_FAILED:
				errStr = "Open file ( "+PlayList.getinstance().getcur()+" ) failed";
				break;	
			case  Errorno.FFMPEG_PARSE_FAILED:
				errStr = "Parser file ( "+PlayList.getinstance().getcur()+" ) failed";
				break;
			case  Errorno.DECODER_INIT_FAILED:
				errStr = "Decode Init failed";
				break;
			case  Errorno.PLAYER_NO_VIDEO:
				errStr = "file have no video";
				break;
			case  Errorno.PLAYER_NO_AUDIO:
				errStr = "file have no audio";
				break;
			case  Errorno.PLAYER_SET_NOVIDEO:
				errStr = "set playback without video";
				break;
			case  Errorno.PLAYER_SET_NOAUDIO:
				errStr = "set playback without audio";
				break;
            case Errorno.DIVX_AUTHOR_ERR:
                errStr = "This player is not authorized to play this video";
                break;
				
			case Errorno.PLAYER_UNSUPPORT_VCODEC:
				errStr = "Unsupport Video codec";				
				break;
			case Errorno.PLAYER_FFFB_UNSUPPORT:
				errStr = "Unsupport ff/fb";
				break;
			default:
				errStr = "Unknow Error";
				break;
    	}
    	return errStr;
    }
}
