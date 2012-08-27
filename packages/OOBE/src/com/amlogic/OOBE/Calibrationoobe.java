/* Copyright (c) 2009, Code Aurora Forum. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Code Aurora nor
 *       the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.amlogic.OOBE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import java.io.IOException;
import android.os.SystemProperties;

public class Calibrationoobe extends Activity {
    public static String defaultPointercalValues = "1 0 0 0 1 0 1" + "\n";

    final private static boolean DEBUG = false;

    private static String mQuitString;
    private static String mPreInstruc;
    final private static String TAG = "AmTsCalibration";
    final private static int STEP_0 = 0;
    final private static int STEP_1 = 1;
    final private static int STEP_2 = 2;
    final private static int STEP_3 = 3;
    final private static int STEP_4 = 4;
    final private static int STEP_5 = 5;

    final private static int mPtCount = 5;
    final private static int mPtsLength = 2;
    private static float[] mPts;
    private static float[] mResultPts = new float[mPtCount * mPtsLength];
    private static float[] tempPts = new float[mPtsLength];

    private static boolean QUIT = false;
    private static int STEP = STEP_0;

    final private static int maxSample = 128;
	String strPara;
	String rawValues;

    /*
     * File Creation for storing the calibration file
     */
    public static final String fileDir = "/data/etc/";
    public static final String filePathRaw = "/data/etc/pointerraw";
	public static final String filePathCal = "/data/etc/pointercal";

    private final File calibrationFileRaw = new File(filePathRaw);
	private final File calibrationFileCal = new File(filePathCal);
    protected FileWriter fileWriter;

    private FileOutputStream fos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title area
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Get the Text from R.string
        mQuitString = getApplicationContext().getString(R.string.quit);
        mPreInstruc = getApplicationContext().getString(R.string.instruc);

		SystemProperties.set("dev.ts.calibrate", "start");

        super.setContentView(R.layout.intro_oobe);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    /*
     * Override onKeyUp to handle button presses
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        boolean result = doKeyUp(keyCode, msg);

        if (QUIT && keyCode == KeyEvent.KEYCODE_9){
            // Close and Save File
            if (fos != null){
                try{fos.close();}catch(Exception e){
                    Log.d("TAG", "Exception Occured While Trying to Close and Save "
                            + e.toString());
                };
            }

            this.finish();
            super.finish();
        }
        return result;
    }

    boolean doKeyUp(int keyCode, KeyEvent msg) {
        boolean handled = false;
        
        if (keyCode == KeyEvent.KEYCODE_BACK && STEP == STEP_0){
        	this.finish();
            super.finish();
            return handled;
        }

        if ((keyCode == KeyEvent.KEYCODE_5 || keyCode == KeyEvent.KEYCODE_SOFT_LEFT)
            && STEP == STEP_0){
            STEP = STEP_1;

            super.setContentView(new CalibrationView(this));
            handled = true;

        }else if (keyCode == KeyEvent.KEYCODE_7 && STEP == STEP_0){

            //Create Default Pointercal File through Java Non-Native function
            try{
                byte[] defaultPointercal = new byte[20];
                String defaultPointercalValues = "1 0 0 0 1 0 1" + "\n";
                defaultPointercal = defaultPointercalValues.getBytes();
                fos = this.openFileOutput("pointercal", MODE_WORLD_READABLE);
                fos.write(defaultPointercal);
            }catch (Exception e){
                Log.e(TAG, "Exception Occured: Trying to default pointercal: " +
                        e.toString());
                Log.e(TAG, "Finishing the Application");
                super.finish();
            }

            //Reading the pointercal File and logging
            char[] pointercalBuffer = new char[100];
            String pointercalValues = "";
            int count;
            try{
                    FileReader rd = new FileReader(Calibrationoobe.filePathRaw);
                    count = rd.read(pointercalBuffer, 0, 100);
                    for (int i = 0; i < count; i++) {
                        pointercalValues += pointercalBuffer[i];
                    }

                    Log.i("This is the pointercal", pointercalValues);
                    rd.close();
            }
            catch(Exception e){
                Log.e("TAG", "Unable to read the pointercal file: " + e.toString());
            }

        }
        else if (keyCode == KeyEvent.KEYCODE_9 && !QUIT){
            new AlertDialog.Builder(this)
            .setTitle(getApplicationContext().getString(R.string.Confirmation))
            .setMessage(getApplicationContext().getString(R.string.QuitCal))
            .setPositiveButton(getApplicationContext().getString(R.string.BtnYes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    QUIT = true;
                    checkQuit();
                }
            })
            .setNegativeButton(getApplicationContext().getString(R.string.BtnNo), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // No, hence do nothing
                }
            })
            .show();

            handled = true;
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK){
            QUIT = false;
            handled = true;
        }
        return handled;
    }

    @Override
    public void onResume(){
        //Reset the app state since points may not be accurate
        //Reset to have consecutive points.
        QUIT = false;
        STEP = STEP_0;
        super.setContentView(R.layout.intro_oobe);
        super.onResume();
    }

    /*
     * Catch and handle Touch events from the device
     */
    @Override public boolean onTouchEvent(MotionEvent event) {
        final int eventAction = event.getAction();
	float cmp;
        if (eventAction == MotionEvent.ACTION_UP){
            if (STEP != STEP_0){
            	//Retrieve Data from Event
                mResultPts[(STEP -1) * mPtsLength] = event.getRawX();
                mResultPts[(STEP -1) * mPtsLength + 1] = event.getRawY();

				cmp=mResultPts[(STEP -1) * mPtsLength]-mPts[(STEP -1)*2];
				if(cmp>50||cmp<-50){return false;}
				cmp=mResultPts[(STEP -1) * mPtsLength + 1]-mPts[(STEP -1)*2+1];
				if(cmp>50||cmp<-50){return false;}

                switch(STEP){
                case STEP_0:
                    STEP = STEP_1;
                    break;
                case STEP_1:
                    STEP = STEP_2;
                    break;
                case STEP_2:
                    STEP = STEP_3;
                    break;
                case STEP_3:
                    //Checking to see which calibration method is used
                    if (mPtCount == 3)
                        QUIT = true;
                    else
                        STEP = STEP_4;
                    break;
                case STEP_4:
                    STEP = STEP_5;
                    break;
                case STEP_5:
                    QUIT = true;
                    break;
                default:
                    // Do nothing
                    break;
                }
                if((STEP == STEP_3 || STEP == STEP_5) && QUIT) {
                    super.setContentView(R.layout.exit_oobe);

                    int[] params = new int[20];
                    for (int i =0; i < (params.length / 2); i+=2){
                        params[i] = (int)mResultPts[i]; //x
                        params[i+1] = (int)mResultPts[i+1]; //y
                        params[i+10] = (int) mPts[i]; //reference x
                        params[i+11] = (int) mPts[i+1]; // reference y

                        Log.i(TAG+" debug", "X[" + Integer.toString(i)+ "] is="
                              + Integer.toString((int)mResultPts[i]));
                        Log.i(TAG+" debug", "Y[" + Integer.toString(i)+ "] is="
                              + Integer.toString((int)mResultPts[i+1]));
                        Log.i(TAG+" debug", "refX[" + Integer.toString(i)+ "] is="
                              + Integer.toString((int)mPts[i]));
                        Log.i(TAG+" debug", "refY[" + Integer.toString(i)+ "] is="
                              + Integer.toString((int)mPts[i+1]));
                    }

                    try{
                        rawValues = new String();

                        for (int i =0; i < params.length; i++){
                            rawValues += Integer.toString(params[i]);
                            rawValues += " ";
                        }

						/************************************/
						/* Save raw data in /data/etc/pointerraw for tslib, and /data/etc/pointercal for android framework.
						*/
						boolean success = new File(Calibrationoobe.fileDir).mkdir();
			            if (!success) {
			                Log.i(this.toString(), "no success");
			            }
						if (!calibrationFileRaw.exists())
                			try {
                    			calibrationFileRaw.createNewFile();
                			} catch (IOException e) {
                   				e.printStackTrace();
                			}

						fos = new FileOutputStream(calibrationFileRaw);
						try {
							fos.write(rawValues.getBytes());
                    		fos.flush();
                    		fos.close();
                		} catch (IOException e) {
                   			e.printStackTrace();
                		}


						int j;
        				float n, x, y, x2, y2, xy, z, zx, zy;
        				float det, a, b, c, e, f, i;
        				float scaling = (float)65536.0;
						int cal[] = new int[7];
						
       				    // Get sums for matrix
        				n = x = y = x2 = y2 = xy = 0;
        				for (j = 0; j < 5; j++) {
            				n += 1.0;
            				x += (float)params[2*j];
            				y += (float)params[2*j+1];
            				x2 += (float)(params[2*j] * params[2*j]);
            				y2 += (float)(params[2*j+1] * params[2*j+1]);
            				xy += (float)(params[2*j] * params[2*j+1]);
        				}

        				// Get determinant of matrix -- check if determinant is too small
        				det = n * (x2 * y2 - xy * xy) + x * (xy * y - x * y2) + y * (x * xy - y * x2);
        				if (det < 0.1 && det > -0.1) {
            				Log.i("ts_calibrate: determinant is too small -- %f\n", "" + det);
            				return false;
        				}

        				// Get elements of inverse matrix
        				a = (x2 * y2 - xy * xy) / det;
        				b = (xy * y - x * y2) / det;
        				c = (x * xy - y * x2) / det;
        				e = (n * y2 - y * y) / det;
        				f = (x * y - n * xy) / det;
        				i = (n * x2 - x * x) / det;

        				// Get sums for x calibration
        				z = zx = zy = 0;
        				for (j = 0; j < 5; j++) {
            				z += (float)params[2*j+10];
            				zx += (float)(params[2*j+10] * params[2*j]);
            				zy += (float)(params[2*j+10] * params[2*j+1]);
        				}

        				// Now multiply out to get the calibration for framebuffer x coord
        				cal[0] = (int)((a * z + b * zx + c * zy) * (scaling));
        				cal[1] = (int)((b * z + e * zx + f * zy) * (scaling));
        				cal[2] = (int)((c * z + f * zx + i * zy) * (scaling));

        				Log.i("%f %f %f\n", ""+(a * z + b * zx + c * zy)+""+ (b * z + e * zx + f * zy)+""+ (c
                				* z + f * zx + i * zy));

        				// Get sums for y calibration
        				z = zx = zy = 0;
        				for (j = 0; j < 5; j++) {
            				z += (float)params[2*j+11];
            				zx += (float)(params[2*j+11] * params[2*j]);
            				zy += (float)(params[2*j+11] * params[2*j+1]);
        				}

        				// Now multiply out to get the calibration for framebuffer y coord
        				cal[3] = (int)((a * z + b * zx + c * zy) * (scaling));
        				cal[4] = (int)((b * z + e * zx + f * zy) * (scaling));
        				cal[5] = (int)((c * z + f * zx + i * zy) * (scaling));

        				Log.i("%f %f %f\n", ""+(a * z + b * zx + c * zy)+ ""+(b * z + e * zx + f * zy)+ ""+(c
                				* z + f * zx + i * zy));

        				// If we got here, we're OK, so assign scaling to a[6] and return
        				cal[6] = (int)scaling;


						strPara = String.format("%d %d %d %d %d %d %d", cal[1], cal[2], cal[0], cal[4], cal[5], cal[3], cal[6]);
						
            			if (!calibrationFileCal.exists())
                			try {
                    			calibrationFileCal.createNewFile();
                			} catch (IOException e1) {
                   				e1.printStackTrace();
                			}

            			try {
                			fos = new FileOutputStream(calibrationFileCal);
                			try {
                    			fos.write(strPara.getBytes());
                    			fos.flush();
                    			fos.close();								
								new AlertDialog.Builder(this)
						            .setTitle(getApplicationContext().getString(R.string.Confirmation))
						            .setMessage(getApplicationContext().getString(R.string.QuitCal))
						            .setPositiveButton(getApplicationContext().getString(R.string.BtnYes), new DialogInterface.OnClickListener() {
						                public void onClick(DialogInterface dialog, int whichButton) {
						                    QUIT = true;
											SystemProperties.set("dev.ts.calibrate", "done");
											String prop = SystemProperties.get("ro.board.tscalibration", "android");
											if(prop.equalsIgnoreCase("android")){
												SystemProperties.set("dev.ts.calibrate.param", strPara);
											}else{
												SystemProperties.set("dev.ts.calibrate.raw", rawValues);
											}
									
						                    checkQuit();
						                }
						            })
						            .setNegativeButton(getApplicationContext().getString(R.string.BtnNo), new DialogInterface.OnClickListener() {
						                public void onClick(DialogInterface dialog, int whichButton) {
						                    // No, delete cal data and quit
											calibrationFileCal.delete();
											SystemProperties.set("dev.ts.calibrate", "noset");
											String prop = SystemProperties.get("ro.board.tscalibration", "android");
											if(prop.equalsIgnoreCase("android")){
												SystemProperties.set("dev.ts.calibrate.param", "1 0 0 0 1 0 1");
											}
											QUIT = true;
						                    checkQuit();
						                }
						            })
						            .show();
			
                			} catch (IOException e2) {                    
                    			e2.printStackTrace();
                			}
            			} catch (FileNotFoundException e3) {              
                			e3.printStackTrace();
            			}
		
                    }
                    catch (Exception e){
                        Log.e(TAG, "Exception Occured: Trying to change to World Readable: " +
                                e.toString());
                        Log.e(TAG, "Finishing the Application");
                        super.finish();
                    }

                    char[] pointercalBuffer = new char[100];
                    String pointercalValues = "";
                    int count;

                    try{
                        FileReader rd = new FileReader(Calibrationoobe.filePathRaw);
                        count = rd.read(pointercalBuffer, 0, 100);
                        for(int i=0; i<count; i++){
                            pointercalValues += pointercalBuffer[i];
                        }
                        Log.i("This is the pointercal", pointercalValues);
                        rd.close();
                    }
                    catch(Exception e){
                        Log.e(TAG,
                        "Pointercal file unable to be created correctly:" + e.toString());
                    }
                }
                else
                    super.setContentView(new CalibrationView(this));
            }
			else{
				if(STEP==STEP_0){
				STEP = STEP_1;
				super.setContentView(new CalibrationView(this));
			}
			else
			{return false;}
			}
        }
        return true;
    }

    /*
     * Helper function for the alert dialog
     */
    private void checkQuit(){
		ConfigOOBE config = new ConfigOOBE(Calibrationoobe.this);
		String str = config.getNextActivityName("Calibrationoobe");
		
		Intent intent = new Intent();
		if(str.equals("") == false){
			intent.setClass(Calibrationoobe.this,(Class<?>) config.getActivity(str));
		}
		else {
			intent.setClass(Calibrationoobe.this,Finish.class);
		}
		startActivity(intent);
		Calibrationoobe.this.finish();
        if(QUIT)
            super.finish();
    }

    /*
     * More Organized FileWriter Append with try catch
     */
    private boolean appendToFile(String inputString){
        try{
            fileWriter.append(inputString);
        }
        catch(Exception e){
            Log.e("TAG", "Exception Occured While Trying to Append to File: " +
                    e.toString());
            return false;
        }
        return true;
    }

    /*
     * View Class: allow canvas drawing on top of the View to display to users
     */
    private static class CalibrationView extends View {
        private Paint mPaint = new Paint();
        private float[] mInstructionPts;
        private float[] mExitPts;

        final int mTextSize = 24;

        public CalibrationView(Context context) {
            super(context);
        }

        private void buildCalibrationPoints(int cHeight, int cWidth){
            mPts = new float[mPtCount * 2];
            if (mPtCount == 3){
                 mPts[0] = cWidth * 15 / 100;
                 mPts[1] = cHeight * 15 / 100;
                 mPts[2] = cWidth / 2;
                 mPts[3] = cHeight * 85 / 100;
                 mPts[4] = cWidth * 85 / 100;
                 mPts[5] = cHeight / 2;
            }
            else if(mPtCount == 5){

                //Points are suggested by tslib
                 mPts[2] = cWidth - 50; //Top Left
                 mPts[3] = 50;
		 mPts[4] = cWidth - 50; // Top right 
                 mPts[5] = cHeight - 50;
		 mPts[6] = 50;             // Bot Right
                 mPts[7] = cHeight - 50;
                 mPts[0] = 50;             // Bot Left
                 mPts[1] = 50;
                 mPts[8] = cWidth / 2; // Center
                 mPts[9] = cHeight / 2;
            }
        }

        // Access the Calibration Points String to compare to Drivers Raw data
        private static String getCalibrationPoints(){
            String result = "";
            for (int i = 0; i < mPts.length; i+=2){
                result += "\n" + Float.toString(mPts[i]) +","+
                Float.toString(mPts[i+1]) + ";";
            }
            return result;
        }

        private void buildTextPosition(String displayString, int yPosition,
                float[] textPts) {
            for (int i = 0; i < textPts.length; i+=2){
            	if("zh".equalsIgnoreCase(getResources().getConfiguration().locale.getLanguage()))
            	{
                textPts[i] = i * 8;
        	}
        	else
        	{
        		textPts[i] = i * 4;
        	}
                textPts[i+1] = yPosition;
            }
        }

        @Override protected void onDraw(Canvas canvas) {
            Paint paint = mPaint;
            final String calibrationInstruction = mPreInstruc +
                Integer.toString(STEP);
            final int canvasHeight = canvas.getHeight();
            final int canvasWidth  = canvas.getWidth();
		int offsetX = 0;
            if (DEBUG){
                Log.d("***Canvas height *** ", Integer.toString(canvasHeight));
                Log.d("***Canvas width *** ", Integer.toString(canvasWidth));
            }

            //Build Points
            buildCalibrationPoints(canvasHeight, canvasWidth);
            mInstructionPts = new float[calibrationInstruction.length()*2];
            mExitPts = new float[mPreInstruc.length()*2];
            //buildTextPosition(calibrationInstruction, canvasHeight - mTextSize
             //       - 7, mInstructionPts);
            /*buildTextPosition(mQuitString, canvasHeight - ( mTextSize /2 ),
                    mExitPts);*/

        if("zh".equalsIgnoreCase(getResources().getConfiguration().locale.getLanguage()))
            	offsetX = (canvasWidth - mPreInstruc.length()* 6+50)/2;
    	else
		offsetX = (canvasWidth  - mPreInstruc.length()* 3+50)/2;

            canvas.translate(0, 0);
            canvas.drawColor(Color.BLACK);

            paint.setColor(Color.WHITE);
            paint.setTextSize(mTextSize);
		paint.setTextAlign(Paint.Align.CENTER);		
	    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            //canvas.drawPosText(calibrationInstruction, mInstructionPts, paint);
           // canvas.drawPosText(mPreInstruc, mExitPts, paint);
           canvas.drawText(mPreInstruc, offsetX, canvasHeight*2/3, paint);

            // Create sub array of points
            System.arraycopy(mPts, (STEP -1) * mPtsLength, tempPts, 0,
            mPtsLength);


            //Setting up and drawing targets (LARGER)
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(25);
            canvas.drawCircle(tempPts[0], tempPts[1], paint.getStrokeWidth(),
                    paint);

            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(21);
            canvas.drawCircle(tempPts[0], tempPts[1], paint.getStrokeWidth(),
                    paint);

            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(17);
            canvas.drawCircle(tempPts[0], tempPts[1], paint.getStrokeWidth(),
                    paint);

            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(13);
            canvas.drawCircle(tempPts[0], tempPts[1], paint.getStrokeWidth(),
                    paint);

            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(9);
            canvas.drawCircle(tempPts[0], tempPts[1], paint.getStrokeWidth(),
                    paint);

            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(5);
            canvas.drawCircle(tempPts[0], tempPts[1], paint.getStrokeWidth(),
                    paint);

            //Center Point
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(1);
            canvas.drawCircle(tempPts[0], tempPts[1], paint.getStrokeWidth(),
                    paint);

            //Drawing a Cross
            paint.setStrokeWidth(2);
            canvas.drawLine(tempPts[0] + 28, tempPts[1], tempPts[0]
            - 28, tempPts[1], paint);
            canvas.drawLine(tempPts[0], tempPts[1] + 28, tempPts[0],
            tempPts[1] - 28, paint);

        }
    }
}
