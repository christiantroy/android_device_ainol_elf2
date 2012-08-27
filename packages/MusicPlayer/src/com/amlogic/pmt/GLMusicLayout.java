package com.amlogic.pmt;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11Ext;
import org.geometerplus.zlibrary.ui.android.R;
import com.amlogic.pmt.music.MusicItemInfo;
import com.amlogic.pmt.music.ParserMusic;
import com.amlogic.pmt.music.musiclistener;
import com.amlogic.Listener.MenuCallbackListener;
import com.amlogic.control.playerStatus;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.opengl.GLU;
import android.util.Log;
import android.view.KeyEvent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;//gyx comment
import com.amlogic.pmt.Resolution;
import com.amlogic.pmt.MusicPlayer;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.RemoteException;

public class GLMusicLayout extends GLBaseLayout implements
		MenuCallbackListener, musiclistener {
	private MediaPlayer audioPlayer = null;// new MediaPlayer();
	private IGLMusicService musicService=null;
	private LyricParser lyric = null;
	long currentPlayTime;
	long lastCheckTime;
	long current;

	int infoTexID = -1;
	int upBGTexID = -1;
	int lowBGTexID = -1;
	// int[] infoTexRect = new int[]{456, 320, -2, 1008, 392};
	// int[] upBGTexRect = new int[]{0, 360, 50, 1920, 656};
	// int[] lowBGTexRect = new int[]{0, 0, 40, 1904, 416};
	final static short musicBG = 1 << 2;
	final static short musicUnBG = 1 << 3;
	final static int play_PAUSE = 1;
	final static int play_FF = 4;
	final static int play_FB = 3;
	private static final String TAG = "GLMusicLayout";
	int musicBGTexID = -1;
	int musicID3BG = -1;
	int[] musicBGTexRect = new int[] { 0, 0, 40, 1920, 1080 };

	// int[] lyricBGTexRect = new int[]{0, 240, -2, 1920,
	// 80};//0,1080-80-240,1920,80(x,y,w,h)
	// Bitmap songID3BG = BitmapFactory.decodeResource(mContext.getResources(),
	// R.drawable.music_song_info);
	// Bitmap bg_music_thumb =
	// BitmapFactory.decodeResource(mContext.getResources(),
	// R.drawable.bg_music_thumb);

	private float[] cameraEye = new float[] { 0, 0, 20 };
	private float[] cameraCenter = new float[] { 0, 0, 0 };
	private float[] cameraUp = new float[] { 0, 1, 0 };

	private MusicItemInfo song = new MusicItemInfo();

	float[] gridVertex = new float[] { 0, 0, 0, 0, 0.4f, 0, 0.2f, 0, 0, 0.2f,
			0.4f, 0 };
	short[] gridIndex = new short[] { 0, 1, 2, 3 };

	Buffer vertexBuff;
	private Context mcontext;
	// private int pauseTime;
	private boolean isFirstPlay = true;
	private int isFFFB = 0;
	private Map<Integer, String> ChangeMenuIcon = new HashMap<Integer, String>();
	private int IfHaveBG = this.musicBG;
	private boolean delayShowID3 = true;
	public AudioManager audioManager = null;
	private int fffb_len = 8000;

	GLMusicLayout(Context context, String n, String location) {
		super(context, n, location);
		// TODO Auto-generated constructor stub
		vertexBuff = MiscUtil.makeFloatBuffer(gridVertex);
		mcontext = context;
		SystemProperties.set("media.amplayer.enable-local", "true");
		
		//audioPlayer = new MediaPlayer();
		//audioPlayer =MusicPlayer.getMediaPlayer();
		musicService=MusicPlayer.getMediaPlayerService();
		createDataProvider(location);
		
		//audioPlayer.setOnCompletionListener(compleListener);

		// audioPlayer.setOnBufferingUpdateListener();
		//audioPlayer.setOnErrorListener(Errorlistener);
		IfHaveBG = musicUnBG;
		String data = myPreference.getMyParam("MusRepeatMode","FOLDER");

		if (!data.equals("")) {
			if (data.equals("SINGLE"))
				setSwitchMode(DataProvider.playmode_singer);
			else if (data.equals("FOLDER"))
				setSwitchMode(DataProvider.playmode_folder);
			else if (data.equals("RANDOM"))
				setSwitchMode(DataProvider.playmode_rand);
		}
		else
			{
			setSwitchMode(DataProvider.playmode_folder);
			myPreference.setMyParam("MusRepeatMode","FOLDER");
			}

		audioManager = (AudioManager) mcontext
				.getSystemService(Service.AUDIO_SERVICE);
	}

	GLMusicLayout(Context context, String n, String location, int bg) {
		super(context, n, location);
		// TODO Auto-generated constructor stub
		vertexBuff = MiscUtil.makeFloatBuffer(gridVertex);

		mcontext = context;
		SystemProperties.set("media.amplayer.enable-local", "true");
		
		//audioPlayer = new MediaPlayer();
		//audioPlayer =MusicPlayer.getMediaPlayer();
		//audioPlayer.setOnCompletionListener(compleListener);
		//audioPlayer.setOnErrorListener(Errorlistener);

		if (bg == this.musicBG || bg == this.musicUnBG)
			IfHaveBG = bg;

    	String data = myPreference.getMyParam("MusRepeatMode","FOLDER");
		if (!data.equals("")) {
			if (data.equals("SINGLE"))
				setSwitchMode(DataProvider.playmode_singer);
			else if (data.equals("FOLDER"))
				setSwitchMode(DataProvider.playmode_folder);
			else if (data.equals("RANDOM"))
				setSwitchMode(DataProvider.playmode_rand);
		}
	}

	/*OnCompletionListener compleListener = new OnCompletionListener() {

		// Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			PlayNext();
			if (filenamelistener != null)
				filenamelistener.CallDelayHideMenu();

		}
	};*/
	/*OnErrorListener Errorlistener = new OnErrorListener() {
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			try {
				// 鍙戠敓閿欒鏃朵篃瑙ｉ櫎璧勬簮涓嶮ediaPlayer鐨勮祴鍊�
				Log.v("Player", "Errorlistener");
				PlayNext();

				// this.txtPlaying.setText("鎾斁鍙戠敓寮傚父!");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	};*/

	// private float[] SpectrumCollumR =
	// {0.101960784f,0.101960784f,0.105882353f,0.11372549f,0.125490196f,0.137254902f,0.149019608f,0.160784314f,0.17254902f,0.188235294f,0.2f,0.211764706f,0.231372549f,0.243137255f,0.262745098f,0.278431373f,0.290196078f,0.305882353f,0.321568627f,0.341176471f,0.352941176f,0.364705882f,0.380392157f,0.396078431f,0.403921569f,0.415686275f,0.42745098f,0.435294118f,0.443137255f,0.450980392f};
	// private float[] SpectrumCollumG =
	// {0.274509804f,0.274509804f,0.28627451f,0.298039216f,0.31372549f,0.325490196f,0.345098039f,0.364705882f,0.384313725f,0.403921569f,0.42745098f,0.450980392f,0.474509804f,0.498039216f,0.521568627f,0.545098039f,0.568627451f,0.592156863f,0.615686275f,0.639215686f,0.662745098f,0.68627451f,0.705882353f,0.725490196f,0.745098039f,0.760784314f,0.776470588f,0.792156863f,0.807843137f,0.815686275f};
	// private float[] SpectrumCollumB =
	// {0.611764706f,0.611764706f,0.619607843f,0.62745098f,0.639215686f,0.647058824f,0.662745098f,0.674509804f,0.690196078f,0.705882353f,0.717647059f,0.733333333f,0.749019608f,0.768627451f,0.784313725f,0.8f,0.819607843f,0.835294118f,0.850980392f,0.866666667f,0.882352941f,0.898039216f,0.91372549f,0.929411765f,0.945098039f,0.956862745f,0.964705882f,0.97254902f,0.984313725f,0.992156863f};

	private static ByteBuffer makeShortBuffer(int count) {
		ByteBuffer bb = ByteBuffer.allocateDirect(count * 2);
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

	private static ByteBuffer makeFloatBuffer(int count) {
		ByteBuffer bb = ByteBuffer.allocateDirect(count * 4);
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

	/*
	 * ///////////Generate Arrays' C Code/////////////// #define PI 3.14159
	 * 
	 * void printSpectrum(){ int row, column;
	 * 
	 * //x: = 28*sin(PI*(47-column)/180) printf("float[] SpectrumX = {");
	 * for(column = 0; column < 96; column++){ if(column%16 == 0)
	 * printf("\n\t\t"); float x = 28*sin(PI*(47-column)/180);
	 * printf("%8.4ff, ", x ); } printf("};\n");
	 * 
	 * //x1: = x + 0.2 * cos(PI/180*(column-47)/2)
	 * printf("float[] SpectrumX1 = {"); for(column = 0; column < 96; column++){
	 * if(column%16 == 0) printf("\n\t\t"); float x =
	 * 28*sin(PI*(47-column)/180); float x1 = x + 0.2 *
	 * cos(PI/180*(column-47)/2); printf("%8.4ff, ", x1 ); } printf("};\n");
	 * 
	 * //y: = -2.9 + row*0.8 printf("float[] SpectrumY = {"); printf("\n\t\t");
	 * for(row = -15; row < 30; row++){ if(row%16 == 0) printf("\n\t\t"); float
	 * y = -2.9 + row*0.8; printf("%8.4ff, ", y ); } printf("};\n");
	 * 
	 * //y1: = y + 0.4 printf("float[] SpectrumY1 = {"); printf("\n\t\t");
	 * for(row = -15; row < 30; row++){ if(row%16 == 0) printf("\n\t\t"); float
	 * y = -2.9 + row*0.8; float y1 = y + 0.4; printf("%8.4ff, ", y1 ); }
	 * printf("};\n");
	 * 
	 * 
	 * //z: = -45*cos(PI*(47-column)/180) - 5 printf("float[] SpectrumZ = {");
	 * for(column = 0; column < 96; column++){ if(column%16 == 0)
	 * printf("\n\t\t"); float z = -45*cos(PI*(47-column)/180) + 25;
	 * printf("%8.4ff, ", z ); } printf("};\n");
	 * 
	 * //z1: = z - 0.2 * sin(PI/180*(column-47)/2)
	 * printf("float[] SpectrumZ1 = {"); for(column = 0; column < 96; column++){
	 * if(column%16 == 0) printf("\n\t\t"); float z =
	 * -45*cos(PI*(47-column)/180) + 25; float z1 = z - 0.2 *
	 * sin(PI/180*(column-47)/2); printf("%8.4ff, ", z1 ); } printf("};\n");
	 * 
	 * //color printf("float[] SpectrumColor = {"); printf("\n\t\t"); for(row =
	 * -15; row < 30; row++){ if(row%4 == 0) printf("\n\t\t"); float r = 0.0941;
	 * float g = 0.2235; float b = 0.4276; float a = 1; if(row >= 0){ r +=
	 * (row+1) * (0.4509-0.0941)/30; g += (row+1) * (0.8157-0.2235)/30; b +=
	 * (row+1) * (0.9922-0.4276)/30; }else{ r = 0.0706; g = 0.1725; r = 0.3529;
	 * } printf("%5.4ff, %5.4ff, %5.4ff, %5.4ff,  ", r, g, b, a); }
	 * printf("};\n");
	 * 
	 * }
	 */// ///////////End Generate Arrays' C Code///////////////

	float[] SpectrumX = { 20.4779f, 20.1415f, 19.7990f, 19.4504f, 19.0959f,
			18.7356f, 18.3696f, 17.9980f, 17.6210f, 17.2385f, 16.8508f,
			16.4580f, 16.0601f, 15.6574f, 15.2499f, 14.8377f, 14.4211f,
			14.0000f, 13.5747f, 13.1452f, 12.7117f, 12.2744f, 11.8333f,
			11.3886f, 10.9405f, 10.4890f, 10.0343f, 9.5766f, 9.1159f, 8.6525f,
			8.1864f, 7.7178f, 7.2469f, 6.7738f, 6.2986f, 5.8215f, 5.3426f,
			4.8621f, 4.3802f, 3.8968f, 3.4123f, 2.9268f, 2.4404f, 1.9532f,
			1.4654f, 0.9772f, 0.4887f, 0.0000f, -0.4887f, -0.9772f, -1.4654f,
			-1.9532f, -2.4404f, -2.9268f, -3.4123f, -3.8968f, -4.3802f,
			-4.8621f, -5.3426f, -5.8215f, -6.2986f, -6.7738f, -7.2469f,
			-7.7178f, -8.1864f, -8.6525f, -9.1159f, -9.5766f, -10.0343f,
			-10.4890f, -10.9405f, -11.3886f, -11.8333f, -12.2744f, -12.7117f,
			-13.1452f, -13.5747f, -14.0000f, -14.4211f, -14.8377f, -15.2499f,
			-15.6574f, -16.0601f, -16.4580f, -16.8508f, -17.2385f, -17.6210f,
			-17.9980f, -18.3696f, -18.7356f, -19.0959f, -19.4504f, -19.7990f,
			-20.1415f, -20.4779f, -20.8080f, };
	float[] SpectrumX1 = { 20.6613f, 20.3256f, 19.9838f, 19.6359f, 19.2820f,
			18.9224f, 18.5570f, 18.1860f, 17.8095f, 17.4276f, 17.0405f,
			16.6482f, 16.2509f, 15.8487f, 15.4416f, 15.0300f, 14.6138f,
			14.1932f, 13.7683f, 13.3393f, 12.9062f, 12.4693f, 12.0286f,
			11.5842f, 11.1364f, 10.6853f, 10.2309f, 9.7735f, 9.3132f, 8.8500f,
			8.3842f, 7.9159f, 7.4452f, 6.9723f, 6.4973f, 6.0204f, 5.5417f,
			5.0614f, 4.5795f, 4.0964f, 3.6120f, 3.1265f, 2.6402f, 2.1531f,
			1.6653f, 1.1772f, 0.6887f, 0.2000f, -0.2887f, -0.7772f, -1.2655f,
			-1.7533f, -2.2405f, -2.7271f, -3.2127f, -3.6973f, -4.1808f,
			-4.6629f, -5.1436f, -5.6226f, -6.0999f, -6.5753f, -7.0486f,
			-7.5198f, -7.9886f, -8.4549f, -8.9186f, -9.3796f, -9.8376f,
			-10.2927f, -10.7445f, -11.1930f, -11.6380f, -12.0795f, -12.5173f,
			-12.9511f, -13.3810f, -13.8068f, -14.2283f, -14.6455f, -15.0581f,
			-15.4661f, -15.8694f, -16.2678f, -16.6611f, -17.0494f, -17.4324f,
			-17.8101f, -18.1823f, -18.5489f, -18.9099f, -19.2650f, -19.6142f,
			-19.9574f, -20.2945f, -20.6253f, };
	float[] SpectrumY = { -14.9000f, -14.1000f, -13.3000f, -12.5000f,
			-11.7000f, -10.9000f, -10.1000f, -9.3000f, -8.5000f, -7.7000f,
			-6.9000f, -6.1000f, -5.3000f, -4.5000f, -3.7000f, -2.9000f,
			-2.1000f, -1.3000f, -0.5000f, 0.3000f, 1.1000f, 1.9000f, 2.7000f,
			3.5000f, 4.3000f, 5.1000f, 5.9000f, 6.7000f, 7.5000f, 8.3000f,
			9.1000f, 9.9000f, 10.7000f, 11.5000f, 12.3000f, 13.1000f, 13.9000f,
			14.7000f, 15.5000f, 16.3000f, 17.1000f, 17.9000f, 18.7000f,
			19.5000f, 20.3000f, };
	float[] SpectrumY1 = { -14.5000f, -13.7000f, -12.9000f, -12.1000f,
			-11.3000f, -10.5000f, -9.7000f, -8.9000f, -8.1000f, -7.3000f,
			-6.5000f, -5.7000f, -4.9000f, -4.1000f, -3.3000f, -2.5000f,
			-1.7000f, -0.9000f, -0.1000f, 0.7000f, 1.5000f, 2.3000f, 3.1000f,
			3.9000f, 4.7000f, 5.5000f, 6.3000f, 7.1000f, 7.9000f, 8.7000f,
			9.5000f, 10.3000f, 11.1000f, 11.9000f, 12.7000f, 13.5000f,
			14.3000f, 15.1000f, 15.9000f, 16.7000f, 17.5000f, 18.3000f,
			19.1000f, 19.9000f, 20.7000f, };
	float[] SpectrumZ = { -5.6899f, -6.2596f, -6.8198f, -7.3703f, -7.9109f,
			-8.4415f, -8.9619f, -9.4720f, -9.9716f, -10.4605f, -10.9386f,
			-11.4058f, -11.8619f, -12.3067f, -12.7402f, -13.1622f, -13.5725f,
			-13.9712f, -14.3579f, -14.7327f, -15.0953f, -15.4457f, -15.7839f,
			-16.1096f, -16.4227f, -16.7233f, -17.0111f, -17.2862f, -17.5483f,
			-17.7975f, -18.0337f, -18.2568f, -18.4667f, -18.6633f, -18.8467f,
			-19.0166f, -19.1732f, -19.3164f, -19.4460f, -19.5621f, -19.6646f,
			-19.7535f, -19.8288f, -19.8904f, -19.9383f, -19.9726f, -19.9931f,
			-20.0000f, -19.9931f, -19.9726f, -19.9383f, -19.8904f, -19.8288f,
			-19.7535f, -19.6646f, -19.5621f, -19.4460f, -19.3164f, -19.1732f,
			-19.0166f, -18.8467f, -18.6633f, -18.4667f, -18.2568f, -18.0337f,
			-17.7975f, -17.5483f, -17.2862f, -17.0111f, -16.7233f, -16.4227f,
			-16.1096f, -15.7839f, -15.4457f, -15.0953f, -14.7327f, -14.3579f,
			-13.9712f, -13.5725f, -13.1622f, -12.7402f, -12.3067f, -11.8619f,
			-11.4058f, -10.9386f, -10.4605f, -9.9716f, -9.4720f, -8.9619f,
			-8.4415f, -7.9109f, -7.3703f, -6.8198f, -6.2596f, -5.6899f,
			-5.1109f, };
	float[] SpectrumZ1 = { -5.6102f, -6.1815f, -6.7433f, -7.2954f, -7.8376f,
			-8.3699f, -8.8919f, -9.4036f, -9.9048f, -10.3954f, -10.8752f,
			-11.3440f, -11.8017f, -12.2482f, -12.6834f, -13.1070f, -13.5191f,
			-13.9194f, -14.3078f, -14.6843f, -15.0486f, -15.4007f, -15.7406f,
			-16.0680f, -16.3829f, -16.6851f, -16.9747f, -17.2514f, -17.5153f,
			-17.7663f, -18.0042f, -18.2289f, -18.4406f, -18.6389f, -18.8240f,
			-18.9957f, -19.1541f, -19.2989f, -19.4303f, -19.5481f, -19.6524f,
			-19.7430f, -19.8200f, -19.8834f, -19.9331f, -19.9691f, -19.9914f,
			-20.0000f, -19.9949f, -19.9761f, -19.9436f, -19.8974f, -19.8375f,
			-19.7640f, -19.6768f, -19.5760f, -19.4617f, -19.3338f, -19.1924f,
			-19.0375f, -18.8693f, -18.6877f, -18.4928f, -18.2846f, -18.0633f,
			-17.8288f, -17.5813f, -17.3209f, -17.0476f, -16.7614f, -16.4626f,
			-16.1511f, -15.8271f, -15.4907f, -15.1420f, -14.7810f, -14.4080f,
			-14.0229f, -13.6260f, -13.2173f, -12.7970f, -12.3652f, -11.9220f,
			-11.4676f, -11.0021f, -10.5256f, -10.0383f, -9.5404f, -9.0320f,
			-8.5132f, -7.9842f, -7.4452f, -6.8964f, -6.3378f, -5.7697f,
			-5.1922f, };
	float[] SpectrumColor = { 0.0706f, 0.1725f, 0.3529f, 1.0000f, 0.0706f,
			0.1725f, 0.3529f, 1.0000f, 0.0706f, 0.1725f, 0.3529f, 1.0000f,
			0.0706f, 0.1725f, 0.3529f, 1.0000f, 0.0706f, 0.1725f, 0.3529f,
			1.0000f, 0.0706f, 0.1725f, 0.3529f, 1.0000f, 0.0706f, 0.1725f,
			0.3529f, 1.0000f, 0.0706f, 0.1725f, 0.3529f, 1.0000f, 0.0706f,
			0.1725f, 0.3529f, 1.0000f, 0.0706f, 0.1725f, 0.3529f, 1.0000f,
			0.0706f, 0.1725f, 0.3529f, 1.0000f, 0.0706f, 0.1725f, 0.3529f,
			1.0000f, 0.0706f, 0.1725f, 0.3529f, 1.0000f, 0.0706f, 0.1725f,
			0.3529f, 1.0000f, 0.0706f, 0.1725f, 0.3529f, 1.0000f, 0.1060f,
			0.2432f, 0.4464f, 1.0000f, 0.1179f, 0.2630f, 0.4652f, 1.0000f,
			0.1298f, 0.2827f, 0.4841f, 1.0000f, 0.1417f, 0.3025f, 0.5029f,
			1.0000f, 0.1536f, 0.3222f, 0.5217f, 1.0000f, 0.1655f, 0.3419f,
			0.5405f, 1.0000f, 0.1774f, 0.3617f, 0.5593f, 1.0000f, 0.1892f,
			0.3814f, 0.5782f, 1.0000f, 0.2011f, 0.4012f, 0.5970f, 1.0000f,
			0.2130f, 0.4209f, 0.6158f, 1.0000f, 0.2249f, 0.4406f, 0.6346f,
			1.0000f, 0.2368f, 0.4604f, 0.6534f, 1.0000f, 0.2487f, 0.4801f,
			0.6723f, 1.0000f, 0.2606f, 0.4999f, 0.6911f, 1.0000f, 0.2725f,
			0.5196f, 0.7099f, 1.0000f, 0.2844f, 0.5393f, 0.7287f, 1.0000f,
			0.2963f, 0.5591f, 0.7475f, 1.0000f, 0.3082f, 0.5788f, 0.7664f,
			1.0000f, 0.3201f, 0.5986f, 0.7852f, 1.0000f, 0.3320f, 0.6183f,
			0.8040f, 1.0000f, 0.3439f, 0.6380f, 0.8228f, 1.0000f, 0.3558f,
			0.6578f, 0.8416f, 1.0000f, 0.3676f, 0.6775f, 0.8605f, 1.0000f,
			0.3795f, 0.6973f, 0.8793f, 1.0000f, 0.3914f, 0.7170f, 0.8981f,
			1.0000f, 0.4033f, 0.7367f, 0.9169f, 1.0000f, 0.4152f, 0.7565f,
			0.9357f, 1.0000f, 0.4271f, 0.7762f, 0.9546f, 1.0000f, 0.4390f,
			0.7960f, 0.9734f, 1.0000f, 0.4509f, 0.8157f, 0.9922f, 1.0000f, };

	final int TOTAL_COLS = 96;
	final int MAX_ROWS = 30;
	final int MIN_ROWS = -15;
	ByteBuffer vertexbuffer = makeFloatBuffer(3 * 4 * (MAX_ROWS - MIN_ROWS)
			* TOTAL_COLS);
	ByteBuffer colorbuffer = makeFloatBuffer(4 * 4 * (MAX_ROWS - MIN_ROWS)
			* TOTAL_COLS);
	ByteBuffer pointerbuffer = makeShortBuffer(6 * (MAX_ROWS - MIN_ROWS)
			* TOTAL_COLS);
	ShortBuffer pbuffer = pointerbuffer.asShortBuffer();
	boolean spectInited = false;
	short[] pblock = new short[6];

	private void initSpectrum() {
		float[] vblock = new float[12];
		FloatBuffer vbuffer = vertexbuffer.asFloatBuffer();
		FloatBuffer cbuffer = colorbuffer.asFloatBuffer();

		for (int col = 0; col < TOTAL_COLS; col++) {
			for (int rowidx = 0, cidx = 0; rowidx < MAX_ROWS - MIN_ROWS; rowidx++, cidx += 4) {
				vblock[0] = SpectrumX[col];
				vblock[1] = SpectrumY[rowidx];
				vblock[2] = SpectrumZ[col];

				vblock[3] = SpectrumX[col];
				vblock[4] = SpectrumY1[rowidx];
				vblock[5] = SpectrumZ[col];

				vblock[6] = SpectrumX1[col];
				vblock[7] = SpectrumY1[rowidx];
				vblock[8] = SpectrumZ1[col];

				vblock[9] = SpectrumX1[col];
				vblock[10] = SpectrumY[rowidx];
				vblock[11] = SpectrumZ1[col];

				vbuffer.put(vblock);

				cbuffer.put(SpectrumColor, cidx, 4);
				cbuffer.put(SpectrumColor, cidx, 4);
				cbuffer.put(SpectrumColor, cidx, 4);
				cbuffer.put(SpectrumColor, cidx, 4);
			}
		}

		vertexbuffer.position(0);
		colorbuffer.position(0);
		spectInited = true;
	}

	private int blockcount = 0;

	private void drawSpectrum(GL10 gl) {
		if (!spectInited) {
			initSpectrum();
		}

		synchronized (pointerbuffer) {
//			gl.glBlendFunc(sfactor, dfactor);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			if (myspectrumData != null) {
				vertexbuffer.position(0);
				colorbuffer.position(0);
				pointerbuffer.position(0);
				
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexbuffer);
				gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorbuffer);
				gl.glDrawElements(GL10.GL_TRIANGLES, blockcount * 6,
						GL10.GL_UNSIGNED_SHORT, pointerbuffer);
			}
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}

	}

	/*
	 * private void drawSpectrumCollum(GL10 gl, float x, float y, float z,int
	 * total){
	 * 
	 * gl.glTranslatef(x, y, z); gl.glPushMatrix(); for(int i=0; i<total; i++){
	 * 
	 * gl.glColor4f((float)SpectrumCollumR[29-i],(float)SpectrumCollumG[29-i],(float
	 * )SpectrumCollumB[29-i], 1); gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0,
	 * 4); gl.glTranslatef(0, 0.8f, 0); } gl.glPopMatrix(); gl.glTranslatef(0,
	 * -0.8f, 0); gl.glColor4f(0.0941f, 0.22353f, 0.42745f, 1); for(int i=0;
	 * (i<total)&&(i<15); i++){ gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	 * gl.glTranslatef(0, -0.8f, 0); } }
	 */
	public void HideBg() {
		delLayoutTextures();
		IfHaveBG = this.musicUnBG;
	}

	public void ShowBg() {
		IfHaveBG = this.musicBG;
	}

	Bitmap songinfoBmp = null;
	int songTexID = -1;
	ArrayList<LyricSentence> showList = new ArrayList<LyricSentence>();
	private String PlayingLyric = "";
	int nAlbum ;
	// Override
	public synchronized void drawFrame(GL10 gl) {
		// TODO Auto-generated method stub

		if (IfHaveBG == this.musicUnBG)
			return;
		setCamera(gl);
		if (musicBGTexID < 0) {
			Log.i("GLMusicLayout", "loadTextureOES() -> musicBGTexID");
			// musicBGTexID = TextureManager.loadTextureOES(gl,
			// mcontext.getResources(), R.drawable.bg_music_player);
			musicBGTexID = TextureManager.loadTextureOES(gl, BitmapFactory
					.decodeResource(mcontext.getResources(),
							R.drawable.bg_music_player), false);
		}
		if (musicID3BG < 0) {
			Log.i("GLMusicLayout", "loadTextureOES() -> musicBGTexID");
			// musidID3BG =
			// TextureManager.loadTextureOES(gl,mcontext.getResources(),
			// R.drawable.music_song_info);
			musicID3BG = TextureManager.loadTextureOES(gl, null/*BitmapFactory
					.decodeResource(mcontext.getResources(),
							R.drawable.music_song_info)*/, false);
		}

		gl.glClientActiveTexture(GL10.GL_TEXTURE0);
		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		if (musicBGTexID > 0) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, musicBGTexID);
			((GL11Ext) gl).glDrawTexfOES(musicBGTexRect[0], musicBGTexRect[1],
					musicBGTexRect[2], musicBGTexRect[3]*Resolution.getScaleX(), musicBGTexRect[4]*Resolution.getScaleY());
		}
		if (musicID3BG > 0) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, musicID3BG);
			((GL11Ext) gl).glDrawTexfOES(456*Resolution.getScaleX(), 369*Resolution.getScaleY(), -2, 1008*Resolution.getScaleX(), 392*Resolution.getScaleY());
//			((GL11Ext) gl).glDrawTexfOES(774, -369, -2, 373, 418);
		}

		DrawOESMusicID3(gl);

		//draw album
		if(nAlbum<=0){
			nAlbum = TextureManager.loadTextureOES(gl, BitmapFactory
				.decodeResource(mcontext.getResources(),
				R.drawable.music_song_info), false);
		}
		if(nAlbum>0){
			gl.glBindTexture(GL10.GL_TEXTURE_2D, nAlbum);
			((GL11Ext) gl).glDrawTexfOES(774*Resolution.getScaleX(), 329*Resolution.getScaleY(), -2, 373*Resolution.getScaleX(), 418*Resolution.getScaleY());
		}
		
		
		
		/*
		 * if(lyricTexID < 0){ Canvas mCanvas = new Canvas(lyricBmp);
		 * mCanvas.drawARGB(255, 255, 255, 0); Paint paint = new Paint();
		 * paint.setColor(Color.GREEN); mCanvas.drawText("閿熸枻鎷烽敓鏂ゆ嫹"+lastCheckTime,
		 * 400, 50, paint); if(lyricTexID>0)
		 * TextureManager.delTexture(lyricTexID); lyricTexID =
		 * TextureManager.loadTextureOES(gl, lyricBmp); }else if(lyricTexID>0){
		 * gl.glBindTexture(GL10.GL_TEXTURE_2D, lyricTexID); ((GL11Ext)
		 * gl).glDrawTexfOES(0, 0, 40, 1920, 240); }
		 */

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glClientActiveTexture(GL10.GL_TEXTURE0);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		drawSpectrum(gl);
		gl.glClientActiveTexture(GL10.GL_TEXTURE0);
		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		if (isPlayer())
			if (isPlaying()) {
				showList.clear();
				if (lyric == null) {
					showUnLyricTexture(gl);
				} else {
					currentPlayTime = getCurrentPosition() + 1400;
					// long curTime = System.currentTimeMillis();
					// currentPlayTime += (curTime - lastCheckTime);
					// lastCheckTime = curTime;
					ArrayList<LyricSentence> allList = lyric.getSentenceList();

					for (LyricSentence ls : allList) {
						if (ls.getStartTime() < currentPlayTime
								&& ls.getEndTime() > currentPlayTime) {
							showList.add(ls);
							break;
						}
					}
					showLyricTexture(gl, showList);
				}

			}

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glClientActiveTexture(GL10.GL_TEXTURE0);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

	}

	private void DrawOESMusicID3(GL10 gl) {
		if (delayShowID3)
			return;
		if (songTexID < 0) {
			if (songinfoBmp != null) {
				// songinfoBmp.recycle();
				// songinfoBmp = null;
				songinfoBmp.eraseColor(0);
			} else
				songinfoBmp = Bitmap.createBitmap((int)(1008*Resolution.getScaleX()), (int)(392*Resolution.getScaleY()),
						Bitmap.Config.ARGB_8888);
			Canvas can = new Canvas(songinfoBmp);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTextSize(32*Resolution.getScaleY());
			paint.setAntiAlias(true);
			paint.setTextAlign(Align.CENTER);
//			if (song.getBitmap() != null) {
//				Bitmap pic = Bitmap.createScaledBitmap(song.getBitmap(), 371,
//						371, false);
//				can.drawBitmap(pic, 10, 10, paint);
//			}
			if (song.getTitle() != null) {
				if (song.getTitle().equals(
						mcontext.getResources().getString(R.string.unknown)))
					song.setTitle(ConvertFileName(getCurFilePath()));
				String changeTile = MiscUtil
						.breakText(song.getTitle(), 32, 390);
				can.drawText(changeTile, 500*Resolution.getScaleX(), 130*Resolution.getScaleY(), paint);
			}

			if (song.getAlbum() != null) {
				String changeTile = MiscUtil
						.breakText(song.getAlbum(), 32, 390);
				can.drawText(changeTile, 500*Resolution.getScaleX(), 200*Resolution.getScaleY(), paint);
			}
			if (song.getartist() != null) {
				String changeTile = MiscUtil.breakText(song.getartist(), 32,
						390);
				can.drawText(changeTile, 500*Resolution.getScaleX(), 270*Resolution.getScaleY(), paint);
			}

			paint.setTextSize(40*Resolution.getScaleY());
			//can.drawText(this.dataProvider.getCurPosScale(), 20, 20, paint);
			Log.i("GLMusicLayout", "loadTextureOES() -> songTexID");
			// songTexID=TextureManager.loadTextureOES(gl, songinfoBmp);
			songTexID = TextureManager.loadTextureOES(gl, songinfoBmp, false);
		} else if (songTexID > 0) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, songTexID);
//			((GL11Ext) gl).glDrawTexfOES(456, 369, -2, 1008, 392);// 0, 0, 40,
																	// 1920, 240
			
			((GL11Ext) gl).glDrawTexfOES(456*Resolution.getScaleX(), 669*Resolution.getScaleY(), -2, 1008*Resolution.getScaleX(), 392*Resolution.getScaleY());
//			((GL11Ext) gl).glDrawTexfOES(774, 369, -2, 373, 418);

		}

	}

	public String ConvertFileName(String filepath) {
		if (filepath != null) {
			int i = filepath.lastIndexOf("/");
			String filename = filepath.substring(i + 1, filepath.length());
			return filename;
		} else
			return mcontext.getResources().getString(R.string.unknown);

	}

	private int[] myspectrumData = new int[96];

	public void getSpectrumInfo(int[] spectrumData) {

		if (spectrumData != null) {
			synchronized (pointerbuffer) {
				if (myspectrumData != null) {
					myspectrumData = spectrumData;
					updataSpectrumData();
				} else if (myspectrumData == null) {
					myspectrumData = spectrumData;
					// updataSpectrumData();
				}
			}
		}
	}

	private void updataSpectrumData() {
		pbuffer.position(0);
		blockcount = 0;
		for (int col = 0; col < TOTAL_COLS; col++) {
			int maxrowidx = myspectrumData[col] - 1;
			if (maxrowidx < 0)
				maxrowidx = 0;
			if (maxrowidx > MAX_ROWS - 1)
				maxrowidx = MAX_ROWS - 1;
			int rowidx = -maxrowidx / 2;
			if (rowidx < MIN_ROWS)
				rowidx = MIN_ROWS;
			maxrowidx -= MIN_ROWS;
			rowidx -= MIN_ROWS;
			int vertexidx = 4 * ((MAX_ROWS - MIN_ROWS) * col + rowidx);
			for (; rowidx <= maxrowidx; rowidx++, blockcount++, vertexidx += 4) {
				pblock[0] = (short) (vertexidx);
				pblock[1] = (short) (vertexidx + 1);
				pblock[2] = (short) (vertexidx + 2);
				pblock[3] = (short) (vertexidx);
				pblock[4] = (short) (vertexidx + 2);
				pblock[5] = (short) (vertexidx + 3);

				pbuffer.put(pblock);
			}
		}
	}

	/*
	 * private double[] spectrumCx = {20.4774 , 20.1411 , 19.7985 , 19.4500 ,
	 * 19.0955 , 18.7352, 18.3692 ,17.9976 ,17.6205 ,17.2381 ,16.8504 ,16.4576
	 * ,16.0597 ,15.6570 , 15.2495, 14.8373, 14.4207 , 13.9996, 13.5743 ,
	 * 13.1448 ,12.7114 ,12.2741 , 11.8330 , 11.3883 ,10.9402 ,10.4887 ,10.0340
	 * ,9.5763 ,9.1156 , 8.6522, 8.1862 , 7.7176 , 7.2467 , 6.7736 , 6.2984 ,
	 * 5.8214 , 5.3425 ,4.8620 ,4.3800 ,3.8967 ,3.4122 , 2.9267 ,2.4403 ,1.9531
	 * , 1.4654, 0.9772 , 0.4887 , 0 , -0.4887 , -0.9772 , -1.4654 , -1.9531
	 * ,-2.4403 ,-2.9267 ,-3.4122 , -3.8967 , -4.3800 ,-4.8620 ,-5.3425 ,
	 * -5.8214, -6.2984 , -6.7736 , -7.2467 , -7.7176 , -8.1862 , -8.6522 ,
	 * -9.1156 ,-9.5763 ,-10.0340 ,-10.4887 ,-10.9402 ,-11.3883 ,-11.8330
	 * ,-12.2741 , -12.7114, -13.1448 , -13.5743 , -13.9996 , -14.4207 ,
	 * -14.8373 , -15.2495 , -15.6570 ,-16.0597 ,-16.4576 ,-16.8504 ,-17.2381
	 * ,-17.6205 ,-17.9976 , -18.3692 ,-18.7352, -19.0955 , -19.4500 , -19.7985
	 * , -20.1411, -20.4774 , -20.8076 };
	 * 
	 * private double[] spectrumCy = { -30.6907, -31.2604, -31.8205, -32.3710,
	 * -32.9116, -33.4422, -33.9626, -34.4726, -34.9721, -35.4610, -35.9391,
	 * -36.4063, -36.8623, -37.3071, -37.7406, -38.1626, -38.5729, -38.9715,
	 * -39.3582, -39.7329, -40.0956, -40.4460, -40.7841, -41.1098, -41.4229,
	 * -41.7235, -42.0113, -42.2863, -42.5485, -42.7977, -43.0338 , -43.2569,
	 * -43.4668, -43.6634, -43.8467, -44.0167, -44.1733, -44.3164, -44.4460,
	 * -44.5621, -44.6646, -44.7535, -44.8288, -44.8904, -44.9383, -44.9726
	 * ,-44.9931, -45.0000, -44.9931, -44.9726, -44.9383, -44.8904, -44.8288,
	 * -44.7535, -44.6646, -44.5621, -44.4460, -44.3164, -44.1733, -44.0167,
	 * -43.8467, -43.6634, -43.4668, -43.2569, -43.0338, -42.7977, -42.5485,
	 * -42.2863, -42.0113, -41.7235, -41.4229, -41.1098, -40.7841, -40.4460,
	 * -40.0956, -39.7329, -39.3582, -38.9715, -38.5729, -38.1626, -37.7406,
	 * -37.3071, -36.8623, -36.4063, -35.9391, -35.4610, -34.9721, -34.4726,
	 * -33.9626, -33.4422, -32.9116, -32.3710, -31.8205, -31.2604, -30.6907,
	 * -30.1117}; private void drawSpectrum(GL10 gl){ gl.glVertexPointer(3,
	 * GL10.GL_FLOAT, 0, vertexBuff);
	 * gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	 * 
	 * for(int i=0; i<96; i++){ gl.glPushMatrix(); gl.glTranslatef(0, -5, -5);
	 * gl.glRotatef((i-48.f)/2, 0, 1, 0); if(myspectrumData != null)
	 * drawSpectrumCollum(gl, (float)spectrumCx[i], 0,
	 * (float)spectrumCy[i],myspectrumData[i]); // drawSpectrumCollum(gl,
	 * (float)(28*Math.sin(Math.PI*(48-i)/180)), 0,
	 * (float)(-45*Math.cos(Math.PI*(48-i)/180)),myspectrumData[i]);
	 * gl.glPopMatrix(); } gl.glDisableClientState(GL10.GL_VERTEX_ARRAY); }
	 */
	public void setCamera(GL10 gl) {
		GLU.gluLookAt(gl, cameraEye[0], cameraEye[1], cameraEye[2],
				cameraCenter[0], cameraCenter[1], cameraCenter[2], cameraUp[0],
				cameraUp[1], cameraUp[2]);
	}

	// Override
	public void setSlotsLayout() {
		// TODO Auto-generated method stub

	}

	Bitmap lyricBmp = null;// 1920,240
	int curLyricTexID = -1;
	int nextLyricTexID = -1;

	public void showLyricTexture(GL10 gl, ArrayList<LyricSentence> list) {

		if (nextLyricTexID > 0) {
			TextureManager.delTexture(curLyricTexID);
			curLyricTexID = nextLyricTexID;
			nextLyricTexID = -1;
		}

		if (curLyricTexID > 0) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, curLyricTexID);
			((GL11Ext) gl).glDrawTexfOES(0, 250*Resolution.getScaleY(), -2, 1920*Resolution.getScaleX(), 80*Resolution.getScaleY());// 0, 0, 40,
																// 1920, 240
		}

		if (list.size() > 0 && list.get(0).getSentence() != null) {
			if (list.get(0).getSentence().equals(PlayingLyric))
				return;
			else {
				if (lyricBmp != null) {
					// lyricBmp.recycle();
					// lyricBmp = null;
					lyricBmp.eraseColor(0);
				} else
					lyricBmp = Bitmap.createBitmap((int)(1920*Resolution.getScaleX()), (int)(80*Resolution.getScaleY()),
							Bitmap.Config.ARGB_8888);// 1920,240

				PlayingLyric = list.get(0).getSentence();
			}

			Canvas canvas = new Canvas(lyricBmp);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTextSize(60*Resolution.getScaleY());
			paint.setTextAlign(Align.CENTER);
			paint.setAntiAlias(true);
			canvas.drawText(list.get(0).getSentence(), 960*Resolution.getScaleX(), 50*Resolution.getScaleY(), paint);
			Log.i("GLMusicLayout", "loadTextureOES() -> nextLyricTexID");
			nextLyricTexID = TextureManager.loadTextureOES(gl, lyricBmp, false);
		}

		if (list.size() > 0 && list.get(0).getSentence() == null) {
			if (curLyricTexID >= 0) {
				TextureManager.delTexture(curLyricTexID);
				curLyricTexID = -1;
			}
		}
	}

	int UnLyricTex = -1;

	private void showUnLyricTexture(GL10 gl) {

		if (UnLyricTex != -1) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, UnLyricTex);
			((GL11Ext) gl).glDrawTexfOES(0, 250*Resolution.getScaleY(), -2, 1920*Resolution.getScaleX(), 80*Resolution.getScaleY());
			return;
		}
		if (lyricBmp != null)
			lyricBmp.eraseColor(0);
		else
			lyricBmp = Bitmap.createBitmap((int)(1920*Resolution.getScaleX()),(int)(80*Resolution.getScaleY()), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(lyricBmp);
		// canvas.drawARGB(255, 0, 0, 0);//255->0
		String showdata = mcontext.getString(R.string.music_lyric);
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(60*Resolution.getScaleY());
		paint.setTextAlign(Align.CENTER);
		paint.setAntiAlias(true);
		canvas.drawText(showdata, 960*Resolution.getScaleX(), 50*Resolution.getScaleY(), paint);
		Log.i("GLMusicLayout", "loadTextureOES() -> UnLyricTex");
		UnLyricTex = TextureManager.loadTextureOES(gl, lyricBmp, false);
	}

	public void playSong(String locaton) {

		if (isPlaying()) {
			reset();
		}

		try {
			// File file = new File(locaton);
			// FileInputStream fis = new FileInputStream(file);
			// audioPlayer.setDataSource(fis.getFD());
			// fis.close();
			setDataSource(locaton);
			setCurrentMusicPath(locaton);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		/*catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		try {
			prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		/*catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.v("Player", "error");
			// PlayNext();
			return;
		}*/
		start();
	}

	public void pauseSong() {
		if (isPlaying()) {
			isFirstPlay = false;
			// pauseTime=getCurrentPosition();
			pause();
		}
	}

	public void stopSong() {
		/*MediaPlayer temp = audioPlayer;
		audioPlayer = null;
		temp.stop();
		temp.release();*/
		if(isPlayer())
			{
			stop();
			}
	}

	public void resetSong() {
		isFirstPlay = true;
		reset();
	}

	public void playSongByLocation(int isFirstplay) {
		if (isFirstplay == 1) {
			// *********************clear lyric********************
			if (curLyricTexID > 0) {
				TextureManager.delTexture(curLyricTexID);
				curLyricTexID = -1;
			}
			if (nextLyricTexID > 0) {
				TextureManager.delTexture(nextLyricTexID);
				nextLyricTexID = -1;
			}
			if (UnLyricTex > 0) {
				TextureManager.delTexture(UnLyricTex);
				UnLyricTex = -1;
			}

			// *********************clear finish lyric********************

			// if (isPlaying())
			// {
			// stop();
			// }
			// isFirstPlay=true;

			File file = new File(
					getLyricName(getCurFilePath()));
			if (file.exists()) {
				lyric = new LyricParser(file);
				lyric.doParser();
				currentPlayTime = 0;
			} else
				lyric = null;
			playSong(getCurFilePath());

		} else {

			try {
				// reset();
				// setDataSource(musicService.getCurFilePath());
				// prepare();
				// seekTo(pauseTime);
				start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void onCompletion() 
	{
	PlayNext();
	if (filenamelistener != null)
		filenamelistener.CallDelayHideMenu();
	}
	
	public void onError()
	{
		try {
			Log.v("Player", "Errorlistener");
			PlayNext();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// Override
	public void CallbackMenuState(String... State) {
	
		if (State[0].equals("BackTo3D")) {
			if (filenamelistener != null)
				filenamelistener.BackTo3D();
			return;
		}

		if (!State[0].equals("Audio"))
			return;
		Log.i(TAG,"------State[1]:"+State[1]);
		
		if (State[1].equals("shortcut_common_play_")) {
			pauseSong();
			setMenuStatus("player_PLAY");
		} else if (State[1].equals("shortcut_common_pause_")) {
			setMenuStatus("player_PLAY");
			isFFFB = 0;
			playSongByLocation(0);
		} else if (State[1].equals("shortcut_common_stop_")) {
			isFFFB = 0;
			if (filenamelistener != null) {
				filenamelistener.stopplayer("Audio");
			}
			// this.onKeyDown(KeyEvent.KEYCODE_BACK, new
			// KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
		} else if (State[1].equals("shortcut_common_fb_")) {
			isFFFB = 0;
			setMenuStatus("player_JUST_PLAY");
			if (audioManager != null)
				if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
					audioManager
							.setStreamMute(AudioManager.STREAM_MUSIC, false);
		} else if (State[1].equals("shortcut_common_fb1_")) {
			current = getCurrentPosition();
			if (!isPlaying())
				start();
			setMenuStatus("player_FF");
			isFFFB = 1;
			if (audioManager != null)
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		}else if (State[1].equals("shortcut_common_fb2_")) {
			current = getCurrentPosition();
			if (!isPlaying())
				start();
			setMenuStatus("player_FF");
			isFFFB = 3;
			if (audioManager != null)
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		}else if (State[1].equals("shortcut_common_fb4_")) {
			current = getCurrentPosition();
			if (!isPlaying())
				start();
			setMenuStatus("player_FF");
			isFFFB = 5;
			if (audioManager != null)
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		}else if (State[1].equals("shortcut_common_fb8_")) {
			current = getCurrentPosition();
			if (!isPlaying())
				start();
			setMenuStatus("player_FF");
			isFFFB = 7;
			if (audioManager != null)
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		} else if (State[1].equals("shortcut_common_ff_")) {
			isFFFB = 0;
			setMenuStatus("player_JUST_PLAY");
			if (audioManager != null)
				if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
					audioManager
							.setStreamMute(AudioManager.STREAM_MUSIC, false);
		} else if (State[1].equals("shortcut_common_ff1_")) {
			current = getCurrentPosition();
			if (!isPlaying())
				start();
			setMenuStatus("player_FB");
			isFFFB = 2;
			if (audioManager != null)
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		}  else if (State[1].equals("shortcut_common_ff2_")) {
			current = getCurrentPosition();
			if (!isPlaying())
				start();
			setMenuStatus("player_FB");
			isFFFB = 4;
			if (audioManager != null)
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		}  else if (State[1].equals("shortcut_common_ff4_")) {
			current = getCurrentPosition();
			if (!isPlaying())
				start();
			setMenuStatus("player_FB");
			isFFFB = 6;
			if (audioManager != null)
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		}  else if (State[1].equals("shortcut_common_ff8_")) {
			current = getCurrentPosition();
			if (!isPlaying())
				start();
			setMenuStatus("player_FB");
			isFFFB = 8;
			if (audioManager != null)
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		} else if (State[1].equals("shortcut_music_prev_")) {
			isFFFB = 0;
			PlayPrev();
		} else if (State[1].equals("shortcut_music_next_")) {
			isFFFB = 0;
			PlayNext();
		} else if (State[1].equals("shortcut_common_playmode_single")) {
			setSwitchMode(DataProvider.playmode_singer);
			myPreference.setMyParam("MusRepeatMode","SINGLE");
		} else if (State[1].equals("shortcut_common_playmode_folder")) {
			setSwitchMode(DataProvider.playmode_folder);
			myPreference.setMyParam("MusRepeatMode","FOLDER");
		} else if (State[1].equals("shortcut_common_playmode_rand")) {
			setSwitchMode(DataProvider.playmode_rand);
			myPreference.setMyParam("MusRepeatMode","RANDOM");
		} else if (State[1].equals("shortcut_common_sync_play_music")) {
			if (filenamelistener != null) {
				filenamelistener.CallbackRelevance("Audio", "Audio");
			}
		} else if (State[1].equals("shortcut_common_sync_play_picture")) {
			if (filenamelistener != null) {
				filenamelistener.CallbackRelevance("Audio", "Picture");
			}
		} else if (State[1].equals("shortcut_common_sync_play_txt")) {
			if (filenamelistener != null) {
				filenamelistener.CallbackRelevance("Audio", "Text");
			}
		} else if (State[1].equals("shortcut_common_sync_control_music")) {
			if (filenamelistener != null) {
				filenamelistener.CallbackName("music", "Nothing");
				// filenamelistener.CallbackPosScale("music","Nothing");

			}
		}else if (State[1].equals("shortcut_common_mute_")) {
			Log.i(TAG,"------add function later:");
		}
	}
	
	public void PlayPrev() {

		setMenuStatus("player_NORMAL_PLAY");
		String filename = getPreFile();
		resetSong();
		playSongByLocation(1);
		// postDelayUpdateID3Message(filename,1000);
		UpdateID3Message(filename);
	}

	public void PlayNext() {
		setMenuStatus("player_NORMAL_PLAY");
		String filename =getNextFile();
		resetSong();
		playSongByLocation(1);
		// postDelayUpdateID3Message(filename,1000);
		UpdateID3Message(filename);
	}


	// Override
	public synchronized void onstop() {
		IfHaveBG = this.musicUnBG;

		stoploop();
		if (songinfoBmp != null) {
			songinfoBmp.recycle();
			songinfoBmp = null;
		}
		if (lyricBmp != null) {
			lyricBmp.recycle();
			lyricBmp = null;
		}
		//stopSong();
		if (audioManager != null)
			if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

	public void stopTimerAutoPlay()
		{
		stoploop();
		}

	// /////////////////////////////////
	private Timer timerAutoPlay = null;
	private boolean forceStopHandlerflag = false;

	private void showProgress() {
		if (filenamelistener != null && forceStopHandlerflag) {
			// filenamelistener.CallbackPosScale("music","Nothing");
			int currenttime = getCurrentPosition();
			int total = getDuration();
			Log.i(TAG,"-----------currenttime"+currenttime);
			Log.i(TAG,"-----------total"+total);
			if (currenttime <= total)
				GLMusicLayout.this.filenamelistener.CallbackMusicState(
						total / 1000, currenttime / 1000);
		}
	}

	public void setProcess(double x)
		{
		if(x>=0 && x<1)
			{
			long current=(long) (x*getDuration());
			seekTo((int) (current));
			}
		}
	

	TimerTask taskAutoPlay = new TimerTask() {
		// Override
		public void run() {

			if (isPlaying()) {

				showProgress();
				if (isFFFB == 1) { // fb1
					if (current > fffb_len) {
						seekTo((int) (current - fffb_len));
						current -= fffb_len;
					} else {
						current = 1;
						seekTo((int) (1));
						setMenuStatus("player_NORMAL_PLAY");
						isFFFB = 0;
					}
				} else if (isFFFB == 2) {//ff1
					if (current <= getDuration()) {
						if ((current + fffb_len) < getDuration()) {
							current += fffb_len;
							seekTo((int) (current));
						} else {
							current = getDuration() - 1;
							seekTo((int) (current));
							isFFFB = 0;
						}
					}

				} else if (isFFFB == 3) { // fb2
					if (current > fffb_len) {
						seekTo((int) (current - fffb_len));
						current -= fffb_len;
					} else {
						current = 1;
						seekTo((int) (1));
						setMenuStatus("player_NORMAL_PLAY");
						isFFFB = 0;
					}
				} else if (isFFFB == 4) {//ff2
					if (current <= getDuration()) {
						if ((current + fffb_len) < getDuration()) {
							current += fffb_len;
							seekTo((int) (current));
						} else {
							current = getDuration() - 1;
							seekTo((int) (current));
							isFFFB = 0;
						}
					}

				}else if (isFFFB == 5) { // fb4
					if (current > fffb_len*2) {
						seekTo((int) (current - fffb_len*2));
						current -= fffb_len*2;
					} else {
						current = 1;
						seekTo((int) (1));
						setMenuStatus("player_NORMAL_PLAY");
						isFFFB = 0;
					}
				} else if (isFFFB == 6) {//ff4
					if (current <= getDuration()) {
						if ((current + fffb_len*2) < getDuration()) {
							current += fffb_len*2;
							seekTo((int) (current));
						} else {
							current = getDuration() - 1;
							seekTo((int) (current));
							isFFFB = 0;
						}
					}

				}else if (isFFFB == 7) { // fb8
					if (current > fffb_len*4) {
						seekTo((int) (current - fffb_len*4));
						current -= fffb_len*4;
					} else {
						current = 1;
						seekTo((int) (1));
						setMenuStatus("player_NORMAL_PLAY");
						isFFFB = 0;
					}
				} else if (isFFFB == 8) {//ff8
					if (current <= getDuration()) {
						if ((current + fffb_len*4) < getDuration()) {
							current += fffb_len*4;
							seekTo((int) (current));
						} else {
							current = getDuration() - 1;
							seekTo((int) (current));
							isFFFB = 0;
						}
					}

				} else {
					if (audioManager != null)
						if (audioManager
								.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
							audioManager.setStreamMute(
									AudioManager.STREAM_MUSIC, false);
				}

			}
		}
	};

	private void startloop() {
		if (timerAutoPlay == null) {
			forceStopHandlerflag = true;
			timerAutoPlay = new Timer();
			timerAutoPlay.schedule(taskAutoPlay, 1000, 1000);
		}
	}

	private void stoploop() {
		if (timerAutoPlay != null) {
			forceStopHandlerflag = false;
			timerAutoPlay.cancel();
			timerAutoPlay = null;

		}
	}

	// Override
	public void startAutoPlay() {
		UpdateID3Message(getFirstFile());
		// playSongByLocation(1);
		// startloop();
		//MusicPlayer.getMediaPlayerService().postDelayPlayMessage();
		postDelayPlayMessage();
	}

	// Override
	public void stopAutoPlay() {
		onstop();
	}

	public void stopPlayer(){
		stopSong();
		}
	public void postDelayPlayMessage() {
		handlerPlayMusic.sendMessageDelayed(handlerPlayMusic.obtainMessage(1),
				50);
	}

	private Handler handlerPlayMusic = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				String name = getFirstFile();
				File file = new File(getLyricName(name));
				if (file.exists()) {
					lyric = new LyricParser(file);
					lyric.doParser();
					currentPlayTime = 0;
				} else
					lyric = null;
				String cur_filepath=getCurrentMusicPath();
				if(!cur_filepath.equals(name))
					{
					playSong(name);
					}
				startloop();
				// UpdateID3Message(name);

				break;

			default:
				break;
			}
		}
	};

	// *************************ID3*************************************

	// *************************ID3 finish*************************************

	// Override
	public synchronized void delLayoutTextures() {
		Log.i("GLMusicLayout", "delLayoutTextures()");
		super.delLayoutTextures();

		if (songTexID > 0) {
			TextureManager.delTexture(songTexID);
			songTexID = -1;
		}
		if (musicBGTexID > 0) {
			TextureManager.delTexture(musicBGTexID);
			musicBGTexID = -1;
		}
		if (musicID3BG > 0) {
			TextureManager.delTexture(musicID3BG);
			musicID3BG = -1;
		}
		if (curLyricTexID > 0) {
			TextureManager.delTexture(curLyricTexID);
			curLyricTexID = -1;
		}
		if (nextLyricTexID > 0) {
			TextureManager.delTexture(nextLyricTexID);
			nextLyricTexID = -1;
		}
		if (UnLyricTex > 0) {
			TextureManager.delTexture(UnLyricTex);
			UnLyricTex = -1;
		}

		if (infoTexID > 0) {
			TextureManager.delTexture(infoTexID);
			infoTexID = -1;
		}
		if (upBGTexID > 0) {
			TextureManager.delTexture(upBGTexID);
			upBGTexID = -1;
		}
		if (lowBGTexID > 0) {
			TextureManager.delTexture(lowBGTexID);
			lowBGTexID = -1;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG,"-------------onKeyDown"+keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:

			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:

			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:

			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:

			return true;
		}
		return false;
	}

	// @Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	private void getCurMusicInfo(String filename) {
		try {
			ParserMusic musicParser = new ParserMusic(filename, mcontext);
			MusicItemInfo musicinfo = musicParser.getMusicInfo();
			song = musicinfo;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String unkown = mcontext.getResources().getString(R.string.unknown);
			song.setAlbum(unkown);
			song.setArtist(unkown);
			song.setTitle(unkown);

		}
	}

	private void UpdateID3Message(String filename) {
		getCurMusicInfo(filename);
		if (songTexID > 0) {
			TextureManager.delTexture(songTexID);
			songTexID = -1;
		}
		delayShowID3 = false;
		filenamelistener.CallbackName("music", getCurFilePath());
	}

	private String getLyricName(String filepath) {
		int i = filepath.lastIndexOf(".");
		String LyricName = i > 0 ? filepath.substring(0, i) + ".lrc" : "";
		return LyricName;
	}

	@SuppressWarnings("unchecked")
	private void setMenuStatus(String type) 
	{
	Log.d(TAG,"setMenuStatus"+type);
		List data = new ArrayList();
		if (type.equals("player_JUST_PLAY")) {
			// data.add(playerStatus.player_PLAY);
			// if(filenamelistener != null )
			// filenamelistener.CallbackUpdataMenu("music",data);
		} else if (type.equals("player_NORMAL_PLAY")) {
			data.add(playerStatus.player_PLAY);
			data.add(playerStatus.player_FB);
			data.add(playerStatus.player_FF);
			if (filenamelistener != null)
				filenamelistener.CallbackUpdataMenu("music", data);
		} else if (type.equals("player_PLAY")) {
			data.add(playerStatus.player_FB);
			data.add(playerStatus.player_FF);
			if (filenamelistener != null)
				filenamelistener.CallbackUpdataMenu("music", data);
		} else if (type.equals("player_PAUSE")) {
			data.add(playerStatus.player_PAUSE);
			data.add(playerStatus.player_FB);
			data.add(playerStatus.player_FF);
			Log.e(TAG,"------------------setMenuStatus");
			if (filenamelistener != null)
				filenamelistener.CallbackUpdataMenu("music", data);

		} else if (type.equals("player_FB")) {
			data.add(playerStatus.player_PLAY);
			data.add(playerStatus.player_FB);
			if (filenamelistener != null)
				filenamelistener.CallbackUpdataMenu("music", data);
		} else if (type.equals("player_FF")) {
			data.add(playerStatus.player_PLAY);
			data.add(playerStatus.player_FF);
			if (filenamelistener != null)
				filenamelistener.CallbackUpdataMenu("music", data);
		}
	}
	
	public int getCurrentPosition()
	{
        try {
            return musicService.getCurrentPosition();
        } catch (RemoteException ex) {
        	return 0;
        }  
	}
	public String getCurFilePath()
	{
        try {
            return musicService.getCurFilePath();
        } catch (RemoteException ex) {
        	return null;
        } 
		
	}
	
	public boolean isPlaying() {
        try {
            return musicService.isPlaying();
        } catch (RemoteException ex) {
        	return false;
        } 
    }
    public void stop() {
        try {
            musicService.stop();
        } catch (RemoteException ex) {
        } 
    }
	public void pause() {
        try {
            musicService.pause();
        } catch (RemoteException ex) {
        } 
    }

	public void setSwitchMode(int repeatmode) {
        try {
            musicService.setSwitchMode(repeatmode);
        } catch (RemoteException ex) {
        }
    }
	public void reset(){
        try {
            musicService.reset();
        } catch (RemoteException ex) {
        }
	}
	public void setDataSource(String path){
        try {
            musicService.setDataSource(path);
        } catch (RemoteException ex) {
        }
	}
	
	public void prepare(){
        try {
            musicService.prepare();
        } catch (RemoteException ex) {
        }
	}

	public void start(){
        try {
            musicService.start();
        } catch (RemoteException ex) {
        }
	}
	
	public int getDuration(){
        try {
            return musicService.getDuration();
        } catch (RemoteException ex) {
        	return 0;
        }

	}
	public void seekTo(int msec){
        try {
            musicService.seekTo(msec);
        } catch (RemoteException ex) {
        }

	}
	public void createDataProvider(String location){
        try {
            musicService.createDataProvider(location);
        } catch (RemoteException ex) {
        }
	}	

	public void setCurrentMusicPath(String path){
		try {
            musicService.setCurrentMusicPath(path);
        } catch (RemoteException ex) {
        }
	}

	public String getNextFile(){
		try {
            return musicService.getNextFile();
        } catch (RemoteException ex) {
        return null;
        }
	}

	public String getFirstFile(){
		try {
			return musicService.getFirstFile();
		} catch (RemoteException ex) {
		return null;
		}
	}
    public String getCurrentMusicPath(){
		try {
			return musicService.getCurrentMusicPath();
		} catch (RemoteException ex) {
		return null;
		}
	}
    public String getPreFile(){
		try {
			return musicService.getPreFile();
		} catch (RemoteException ex) {
		return null;
		}
	}
	public boolean isPlayer(){
		try {
			return musicService.isPlayer();
		} catch (RemoteException ex) {
		return false;
		}
	}

/////////////////////////////////////////////////////	



////////////////////////////////////////

}
