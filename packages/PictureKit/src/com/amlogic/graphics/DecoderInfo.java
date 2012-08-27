/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amlogic.graphics;

import android.graphics.*;

/**
 * Object to set the decoder. 
 * 
 */
public class DecoderInfo  {
	/** original width of decoding pictures. whill be set after decoding.*/
	public int imageWidth;
	/** original width of decoding pictures. whill be set after decoding. */
	public int imageHeight;
	
	/** width of bitmap user want decode to. */
	public int widthToDecoder;
	/** height of bitmap user want decode to. */
	public int heightToDecoder;
	
	/** preserve, now only RGBA8888 is supported.*/
	public int colorMode;
	
	/** how to scale or crop image. 
	 *	keep size =0 , keep aspect ratio =1 , keep aspect ratio with crop. =2  scretch full screen =3 
	 */
	public int decodemode;  
	
	/** preserve, don't set. */
	public int pictureType;	/*picture type,read only */
	
	/** try to decode thumnail of jpeg if  thumbprefered ==1 . */
	public int thumbprefered; 
	
	/** decode mode for 3d. 0:no 3d, 1:auto lr 3d, 2:force lr 3d,3:auto tb 3d, 4:force tb 3d. **/
	public int image3DPref;
	
	/** 3D appear rate. only for left/right cross mode. 
	 * bit0~7 control moving of right picture, bit0~6, pixels of moving, bit 7 move left or right. 
	 * bit8~15 control moving of left picture, bit8~14, pixels of moving, bit 15 move left or right.
	 **/
	public int image3DParam1;
	
	/** preserved for 3d decoding. **/
	public int image3DParam2;
}
