package com.amlogic.pmt.music;

import android.media.MediaPlayer;

public class SpectrumUtil {


	public static final int inputBlockSize = 96*2;

	public static int[] getSpectrumData()
	{
		short [] data = new short[inputBlockSize];
//		int dataLen = MediaPlayer.snoop(data, 1);
//		if(dataLen == 0)
//			return null;//gyx comment
		short [] dispdata = new short[inputBlockSize/2];
		for(int i=0;i< inputBlockSize/2;i++)
		{
			dispdata[i] =  data[i];
		}
		return getSpectrumIndex(dispdata);
	}
	
	private static int[] getSpectrumIndex(short[] attitude)
	{
		int[] index = new int[inputBlockSize/2];
		int attLength = attitude.length;
		for(int i = 0;i<attLength/2;i++)
		{
			int absData ;
			if(attitude[i*2] < 0)
				absData = -attitude[i*2];
			else
				absData = attitude[i*2];
			if(absData > 3110)
				index[i] = 25 + (absData-3110)/400; //25~29
			else if(absData > 1610)
				index[i] = 20 + (absData-1610)/300; //20~25
			else if(absData > 610)
				index[i] = 15 + (absData-610)/200; //15~20
			else if(absData > 110)
				index[i] = 10 + (absData-110)/100; //10~15
			else if(absData > 10)
				index[i] = 5 + (absData-10)/20; //5~10
			else
				index[i] = absData/2; //0~5
			
			if(attitude[i] != 0 && index[i] == 0)
				index[i] = 1;
			
			if (index[i]> 29)
				index[i] = 29;
			else if(index[i] < 0)
				index[i] = 0;
//			
//			int absData = Math.abs(attitude[i]);
//			if(absData > 3110)
//				index[i] = 21 + (absData-3110)/400; // 21 ~24
//			else if(absData > 1610)
//				index[i] = 27 + (absData-1610)/300; // 17 ~20
//			else if(absData > 610)
//				index[i] = 13 + (absData-610)/200; // 13 ~16
//			else if(absData > 110)
//				index[i] = 9 + (absData-110)/100; // 9 ~12
//			else if(absData > 10)
//				index[i] = 5 + (absData-10)/20; //5 ~8
//			else
//				index[i] = absData/2; // 0 ~4
//			
//			if(attitude[i] != 0 && index[i] == 0)
//				index[i] = 1;
//			
//			if (index[i] > 24)
//	        	index[i] = 24;
//			else if(index[i] < 0)
//				index[i] = 0;	
		}
		int half = attLength/2;
		for(int i = 0;i<half;i++)
			index[attLength/2+i] = index[half-i-1];
		
		return index;
	}
}
