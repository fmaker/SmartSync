package edu.ucdavis.ece.smartsync.profiler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.text.format.DateFormat;
import android.util.Log;


/**
 * Provides access to call histograms
 * 
 * @author fmaker
 */
public class UserProfile {
	private static final boolean DEBUG = true;
	private static final String TAG = "UserProfile";
	private static final int SECS_IN_MINUTE = 60;
	private static final int SECS_IN_HOUR = 60 * SECS_IN_MINUTE;
	private static final int MIN_DISCHARGE_TIME = SECS_IN_MINUTE * 30 ; /* Half hour */
	
	private static final int ARRIVAL_BIN_WIDTH = 60; /* In minutes */
	private static final int LENGTH_BIN_WIDTH = 15; /* In seconds */
	
	BatteryLogOpenHelper sDbHelper;
	HashMap<Integer, Integer> arrivalHistogram;
	HashMap<Integer, Integer> lengthHistogram;
	
	public UserProfile(Context context){
		sDbHelper = new BatteryLogOpenHelper(context);
		arrivalHistogram = new HashMap<Integer, Integer>();
		lengthHistogram = new HashMap<Integer, Integer>();

        Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);  
        if (c.moveToFirst()) {
            do{

             	 /* Only consider if incoming call and NOT missed 
             	  * TODO: Consider using missed calls as well */
             	 if(Integer.parseInt(c.getString(c.getColumnIndex(Calls.TYPE))) == CallLog.Calls.INCOMING_TYPE){
             		addArrivalToHistogram(c);
        			addLengthToHistogram(c);
             	 }
            } while (c.moveToNext());
         }
        c.close();
        
	}
	
/*	public long lastDischargeTime(){

		 Doesn't work yet 
		SQLiteDatabase db = sDbHelper.getReadableDatabase();
		Cursor c = db.query(
					BatteryLogOpenHelper.TABLE_NAME, 
					new String[]{BatteryLogOpenHelper.KEY_TIMESTAMP,BatteryLogOpenHelper.KEY_POWER_CONNECTED},
					String.format("%s == %d", BatteryLogOpenHelper.KEY_TIMESTAMP),
					null, null, null, null);
		db.close();
		c.close();
		return Long.MIN_VALUE;
	}*/


	public List<Integer> getDischargeTimes(){
		ArrayList<Integer> times = new ArrayList<Integer>();

		SQLiteDatabase db = sDbHelper.getReadableDatabase();
		Cursor c = db.query(
					BatteryLogOpenHelper.TABLE_NAME, 
					new String[]{BatteryLogOpenHelper.KEY_TIMESTAMP,BatteryLogOpenHelper.KEY_POWER_CONNECTED},
					null, null, null, null, null, null);

        if (c.moveToFirst()) {
        	long lastTimestamp = -1, timestamp;
        	boolean wasConnected = false, connected;
        	
            do{
            	connected = c.getInt(c.getColumnIndex(BatteryLogOpenHelper.KEY_POWER_CONNECTED)) == 1 ? true : false;
            	timestamp = c.getLong(c.getColumnIndex(BatteryLogOpenHelper.KEY_TIMESTAMP));
            	
            	/* Battery was charged and timestamp recorded */
            	if( (connected && !wasConnected) &&
            		lastTimestamp >= 0){
            		final int dischargeTime = (int) (timestamp - lastTimestamp);
            		if(dischargeTime >= MIN_DISCHARGE_TIME)
            			times.add(dischargeTime);
            	}
            	
            	lastTimestamp = timestamp;
            	wasConnected = connected;

            } while (c.moveToNext());
         }
        c.close();
        
        return times;
	}

	private void addLengthToHistogram(Cursor c) {
   	   	  int bin; /* From: LENGTH_BIN_WIDTH * bin to: (LENGTH_BIN_WIDTH+1) * bin - 1 */
   	   	  int length; /* In seconds */
   	   	  int binCount = 0;
   	  
   	   	  length = Integer.parseInt(c.getString(c.getColumnIndex(Calls.DURATION)));
   	   	  bin = getBinIndex(length, LENGTH_BIN_WIDTH);
   	   	  
   	   	  /* If already have this bin, get current count
   	   	   * Only record if greater than minimum charge threshold
   	   	   */
   	   	  if(lengthHistogram.containsKey(bin))
   	   		  binCount = lengthHistogram.get(bin);
   	   	  
   	   	  lengthHistogram.put(bin, binCount + 1);
   	   	  
   	   	  if(DEBUG)
   	   		  Log.d(TAG, String.format("%d seconds, Bin %d", length, bin));
	}

	private void addArrivalToHistogram(Cursor c) {
		int dailyEpoch; /* Seconds since midnight in the day */
		int bin; 
		int binCount = 0;

		Date d = new Date(Long.parseLong(c.getString(c.getColumnIndex(Calls.DATE))));
		dailyEpoch = d.getHours() * SECS_IN_HOUR + d.getMinutes() * SECS_IN_MINUTE + d.getSeconds();
		bin = getBinIndex(dailyEpoch, ARRIVAL_BIN_WIDTH);


		/* If already have this bin, get current count */
		if(arrivalHistogram.containsKey(bin))
			binCount = arrivalHistogram.get(bin);

		arrivalHistogram.put(bin, binCount + 1);

		if(DEBUG)
			Log.d(TAG, String.format("%d dailyEpoch (%s), Bin %d", dailyEpoch,
					DateFormat.format("kk:mm:ss", d),
					bin));

	}
	
	private int getBinIndex(int value, final int BIN_WIDTH){
		return value / BIN_WIDTH;
	}
	
}
