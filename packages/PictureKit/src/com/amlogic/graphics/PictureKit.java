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
import java.lang.String;

/**
 * <p>Object to process pictures.</p>
 */
public class PictureKit
{
	
	/* native functions. */
    private static native String PictureKitVersion();
    private static native DecoderInfo getPictureInfoNative(String filename);
    private static native ImageInfo loadPictureNative(String filename,DecoderInfo info);
    private static native Bitmap    loadPicture2BmNative(String filename,DecoderInfo info);
	
	/**
	 * decode a picture with the name of filename and return a Bitmap as the result.
	 * @param filename	The name of picture. 
	 * @param info		The information of decoder.
	 * @return 			A object of android.graphics.Bitmap.
	 * @see				android.graphics.Bitmap.
	 * @see				com.amlogic.graphics.DecoderInfo.
	 */ 
    public static Bitmap loadPicture(String filename,DecoderInfo info) {
        return loadPicture2BmNative(filename,info);
    }
    
    /**
     * get the information of a picture with name of filename.
     * @param filename	The name of picture.
	 * @return			A object of com.amlogic.graphics.DecoderInfo.
	 * @return 			A object of android.graphics.Bitmap.
	 * @see				com.amlogic.graphics.DecoderInfo.
	 */
    public static DecoderInfo getPictureInfo(String filename) {
        return getPictureInfoNative(filename);
    }
    static {
		try {
			System.loadLibrary("amlpicjni");
        } catch (Exception e) {	
        	e.printStackTrace();
        	System.out.print(e.getMessage());
        }
    }    
    
}
