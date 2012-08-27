package com.amlogic.OOBE;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.app.AlertDialog;
import android.view.View;
import android.content.DialogInterface;

import android.widget.CheckBox;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;


import android.app.Dialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import android.provider.Settings;
import android.util.Log;
import android.os.SystemClock;

import android.content.ComponentName;

public class DateAndTimeSetting extends Activity{
    /** Called when the activity is first created. */
	
	public Button mNext;
	public Button mBack;
	public CheckBox mHFormatCheckBox;
	public LinearLayout  mSetDateBtn;
	public LinearLayout  mTimeZoneBtn;
	public LinearLayout  mSetTimeBtn;
	public LinearLayout mSetDateFormatBtn;

	public TextView mSetDate_summary;
	public TextView mTimeZone_summary;
	public TextView mSetTime_summary;
	public TextView mHourFormat_summary;
	public TextView mDateFormat_summary;

	private Calendar mDummyDate;
	private static final int DIALOG_DATEPICKER = 0;
    private static final int DIALOG_TIMEPICKER = 1;

	private static final String HOURS_12 = "12";
    private static final String HOURS_24 = "24";

	static final String TAG = "DateTimeSettings";
	String [] formattedDates=null;

	private static final String DATE_TIME_SETTING_PACKAGE = "com.android.settings";
    //private static final String DATE_TIME_SETTING_CLASS ="com.android.settings.ZoneList";
	private static final String DATE_TIME_SETTING_CLASS ="com.android.settings.DateTimeSettings";
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dateandtime_setting);
		
		mSetDateBtn=(LinearLayout) findViewById(R.id.set_date_btn);
		mSetDateBtn.setOnClickListener(mSetDateBtnListener);
		
		//mTimeZoneBtn=(LinearLayout) findViewById(R.id.time_zone_btn);
		//mTimeZoneBtn.setOnClickListener(mTimeZoneBtnListener);
		
		mSetTimeBtn=(LinearLayout) findViewById(R.id.set_time_btn);
		mSetTimeBtn.setOnClickListener(mSetTimeBtnListener);

		mSetDateFormatBtn=(LinearLayout) findViewById(R.id.date_format_btn);
		mSetDateFormatBtn.setOnClickListener(mSetDateFormatBtnListener);

		mNext = (Button) findViewById(R.id.btn_datetime_next);
		mBack = (Button) findViewById(R.id.btn_datetime_back);
		mHFormatCheckBox= (CheckBox) findViewById(R.id.cb_datetime_hourformat_checkbox);

		mSetDate_summary = (TextView)findViewById(R.id.tv_datetime_setdate_summary);
		mSetTime_summary = (TextView)findViewById(R.id.tv_datetime_settime_summary);
		mHourFormat_summary = (TextView)findViewById(R.id.tv_datetime_hourformat_summary);
		mDateFormat_summary = (TextView)findViewById(R.id.tv_datetime_dateformat_summary);

		//Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0);
		
		initUI();
		

		mHFormatCheckBox.setOnClickListener(new CheckBox.OnClickListener(){   
			public void onClick(View v)
				{
				 set24Hour(((CheckBox)mHFormatCheckBox).isChecked());
				 UpdateSummaryDisplay();
				 timeUpdated();
				}
			}
		);

        mBack.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				ConfigOOBE config = new ConfigOOBE(DateAndTimeSetting.this);
				String str = config.getPrevActivityName("DateAndTimeSetting");
				
				Intent intent = new Intent();
				if(str.equals("") == false){
					intent.setClass(DateAndTimeSetting.this,(Class<?>) config.getActivity(str));
				}
				else {
					intent.setClass(DateAndTimeSetting.this,OOBE.class);
				}
				startActivity(intent);
				DateAndTimeSetting.this.finish();			
            }
        });


		 mNext.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				ConfigOOBE config = new ConfigOOBE(DateAndTimeSetting.this);
				String str = config.getNextActivityName("DateAndTimeSetting");
				
				Intent intent = new Intent();
				if(str.equals("") == false){
					intent.setClass(DateAndTimeSetting.this,(Class<?>) config.getActivity(str));
				}
				else {
					intent.setClass(DateAndTimeSetting.this,Finish.class);
				}
				startActivity(intent);
				DateAndTimeSetting.this.finish();
            }
        });
    }

    private void initUI() {
        mDummyDate = Calendar.getInstance();
        mDummyDate.set(mDummyDate.get(Calendar.YEAR), 11, 31, 13, 0, 0);

		String [] dateFormats = getResources().getStringArray(R.array.date_format_values);
        formattedDates = new String[dateFormats.length];
        String currentFormat = getDateFormat();
        // Initialize if DATE_FORMAT is not set in the system settings
        // This can happen after a factory reset (or data wipe)
        if (currentFormat == null) {
            currentFormat = "";
        }
        for (int i = 0; i < formattedDates.length; i++) {
            String formatted =
                DateFormat.getDateFormatForSetting(this, dateFormats[i]).
                    format(mDummyDate.getTime());

            if (dateFormats[i].length() == 0) {
                formattedDates[i] = getResources().
                    getString(R.string.normal_date_format, formatted);
            } else {
                formattedDates[i] = formatted;
            }
        }
		/*mDateFormat.setEntries(formattedDates);
        mDateFormat.setEntryValues(R.array.date_format_values);
        mDateFormat.setValue(currentFormat);*/
		
		//Log.e(TAG, "-----------------------------------------formattedDates is "+ formattedDates);
        //Log.e(TAG, "-----------------------------------------currentFormat is "+ currentFormat);
    }

	@Override
    protected void onResume() {
        super.onResume();
        UpdateSummaryDisplay();
    }


	@Override
    public Dialog onCreateDialog(int id) {
        Dialog d;

        switch (id) {
        case DIALOG_DATEPICKER: {
			final Calendar calendar = Calendar.getInstance();
			DatePickerDialog.OnDateSetListener dateListener=
				new DatePickerDialog.OnDateSetListener(){
					public void onDateSet(DatePicker datePicker,
					int year, int month,int dayofMonth)
						{
						Log.e(TAG, "-----------------------------------------year is"+year);
						Log.e(TAG, "-----------------------------------------month is"+month);
						Log.e(TAG, "-----------------------------------------dayofMonth is"+dayofMonth);
						//Calendar c = Calendar.getInstance();

				        calendar.set(Calendar.YEAR, year);
				        calendar.set(Calendar.MONTH, month);
				        calendar.set(Calendar.DAY_OF_MONTH, dayofMonth);
				        long when = calendar.getTimeInMillis();

				        if (when / 1000 < Integer.MAX_VALUE)
							{
							Log.e(TAG, "-----------------------------------------setCurrentTimeMillis"+when/1000);
				            SystemClock.setCurrentTimeMillis(when);
				        	}
				        UpdateSummaryDisplay();
						}
					};
            
            d = new DatePickerDialog(
                this,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
            d.setTitle(getResources().getString(R.string.date_time_changeDate_text));
            break;
        }
        case DIALOG_TIMEPICKER: {
			TimePickerDialog.OnTimeSetListener timeListener=
				new TimePickerDialog.OnTimeSetListener(){
				public void onTimeSet(TimePicker timerPicker,int hourOfDay,int minute)
					{
					Calendar c = Calendar.getInstance();

					c.set(Calendar.HOUR_OF_DAY, hourOfDay);
					c.set(Calendar.MINUTE, minute);
					long when = c.getTimeInMillis();

					if (when / 1000 < Integer.MAX_VALUE) {
					SystemClock.setCurrentTimeMillis(when);
					}
					UpdateSummaryDisplay();
					}
				};
            final Calendar calendar = Calendar.getInstance();
            d = new TimePickerDialog(
                    this,
                    timeListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(this));
            d.setTitle(getResources().getString(R.string.date_time_changeTime_text));
            break;
        }
        default:
            d = null;
            break;
        }

        return d;
    }

	@Override
    public void onPrepareDialog(int id, Dialog d) {
        switch (id) {
        case DIALOG_DATEPICKER: {
            DatePickerDialog datePicker = (DatePickerDialog)d;
            final Calendar calendar = Calendar.getInstance();
            datePicker.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            break;
        }
        case DIALOG_TIMEPICKER: {
            TimePickerDialog timePicker = (TimePickerDialog)d;
            final Calendar calendar = Calendar.getInstance();
            timePicker.updateTime(
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
            break;
        }
        default:
            break;
        }
    }

    private String getDateFormat() {
        return Settings.System.getString(getContentResolver(), 
                Settings.System.DATE_FORMAT);
    }

    private void set24Hour(boolean is24Hour) {
        Settings.System.putString(getContentResolver(),Settings.System.TIME_12_24,is24Hour? HOURS_24 : HOURS_12);
    }

	private void timeUpdated() {
        Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
        sendBroadcast(timeChanged);
    }

	
	private View.OnClickListener mSetDateBtnListener = new View.OnClickListener() {
        public void onClick(View v) {
			Log.e(TAG, "-----------------------------------------mSetDateBtnListener");
			showDialog(DIALOG_DATEPICKER);
        }
    };


	private View.OnClickListener mTimeZoneBtnListener = new View.OnClickListener() {
		public void onClick(View v) {
			/*Intent intent = new Intent();
            intent.setClass(this, ZoneList.class);
            Log.e(TAG, "-----------------------------------------mTimeZoneBtnListener");
			startActivityForResult(intent, 0);*/
			//intent.setClassName(DATE_TIME_SETTING_PACKAGE, DATE_TIME_SETTING_CLASS);  	
            //startActivityForResult(intent, 0);
			//DateAndTimeSetting.this.finish();		
        }
    };


	private View.OnClickListener mSetTimeBtnListener = new View.OnClickListener() {
		public void onClick(View v) {
            // The 24-hour mode may have changed, so recreate the dialog
            Log.e(TAG, "-----------------------------------------mSetTimeBtnListener");
            showDialog(DIALOG_TIMEPICKER);
        }
    };

	private View.OnClickListener mSetDateFormatBtnListener = new View.OnClickListener() {
		public void onClick(View v) {
			Log.e(TAG, "-----------------------------------------mSetDateFormatBtnListener");
			showDateFormat();
        }
    };

	void showDateFormat()
	{
	new AlertDialog.Builder(this)
		.setTitle(R.string.date_format_title)
		.setSingleChoiceItems(formattedDates
				,0,new DialogInterface.OnClickListener()
				{
				public void onClick(DialogInterface dialog,int which)
					{
					//String format = preferences.getString(key, getResources().getString(R.string.default_date_format));
					String [] date_format_array = getResources().getStringArray(R.array.date_format_values);
					String format = date_format_array[which];
            		Settings.System.putString(getContentResolver(), 
                    Settings.System.DATE_FORMAT, format);
            		UpdateSummaryDisplay();
					//finish();
					}
				})
		.setNegativeButton("Cancel",null)
		.show();
   
	}
    
	private void UpdateSummaryDisplay()
		{
		java.text.DateFormat shortDateFormat = DateFormat.getDateFormat(this);
		Date now = Calendar.getInstance().getTime();
        Date dummyDate = mDummyDate.getTime();

		Log.e(TAG, "-----------------------------------------UpdateSummaryDisplay is"+now);
		mSetTime_summary.setText(DateFormat.getTimeFormat(this).format(now));
        //mTimeZone_summary.setText(getTimeZoneText());
        mSetDate_summary.setText(shortDateFormat.format(now));
        mDateFormat_summary.setText(shortDateFormat.format(dummyDate));
		}

	    /*  Helper routines to format timezone */
    
    private String getTimeZoneText() {
        TimeZone    tz = java.util.Calendar.getInstance().getTimeZone();
        boolean daylight = tz.inDaylightTime(new Date());
        StringBuilder sb = new StringBuilder();

        sb.append(formatOffset(tz.getRawOffset() +
                               (daylight ? tz.getDSTSavings() : 0))).
            append(", ").
            append(tz.getDisplayName(daylight, TimeZone.LONG));

        return sb.toString();        
    }

    private char[] formatOffset(int off) {
        off = off / 1000 / 60;

        char[] buf = new char[9];
        buf[0] = 'G';
        buf[1] = 'M';
        buf[2] = 'T';

        if (off < 0) {
            buf[3] = '-';
            off = -off;
        } else {
            buf[3] = '+';
        }

        int hours = off / 60; 
        int minutes = off % 60;

        buf[4] = (char) ('0' + hours / 10);
        buf[5] = (char) ('0' + hours % 10);

        buf[6] = ':';

        buf[7] = (char) ('0' + minutes / 10);
        buf[8] = (char) ('0' + minutes % 10);

        return buf;
    }
    
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateSummaryDisplay();
        }
    };
}
