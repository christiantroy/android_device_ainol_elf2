/* //device/samples/SampleCode/src/com/android/samples/app/RemoteServiceInterface.java
**
** Copyright 2007, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package com.amlogic.pmt;

import android.graphics.Bitmap;

interface IGLMusicService
{
    boolean isPlaying();
    void stop();
    void pause();
    void play();
    void prev();
    void next();
    void setSwitchMode(int repeatmode);
    int getCurrentPosition();
    void reset();
    void setDataSource(in String path);
    void prepare();
    void start();
    int getDuration();
    void seekTo(int msec);
    boolean isPlayer();
    void setIsMusicLayoutExist(boolean b);
    boolean getIsMusicLayoutExist();
    void setFirstFileName(in String name);
    void setFileList(in List<String> list);
    void createDataProvider(String location);
    void setCurrentMusicPath(String path);
    String getNextFile();
    String getPreFile();
    String getFirstFile();
    String getCurrentMusicPath();
    String getCurFilePath();
}

