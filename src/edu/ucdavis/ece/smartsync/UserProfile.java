package edu.ucdavis.ece.smartsync;

import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.text.format.DateFormat;
import android.util.Log;


/**
 * Provides access to call histograms
 * 
 */
public class UserProfile {
	private static final boolean DEBUG = true;
	private static final String TAG = "UserProfile";
	private static final int SECS_IN_MINUTE = 60;
	private static final int SECS_IN_HOUR = 60 * SECS_IN_MINUTE;
	
	private static final int ARRIVAL_BIN_WIDTH = 60; /* In minutes */
	private static final int LENGTH_BIN_WIDTH = 15; /* In seconds */
	
	HashMap<Integer, Integer> arrivalHistogram;
	HashMap<Integer, Integer> lengthHistogram;
	
	public UserProfile(Context context){
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
	}

	private void addLengthToHistogram(Cursor c) {
   	   	  int bin; /* From: LENGTH_BIN_WIDTH * bin to: (LENGTH_BIN_WIDTH+1) * bin - 1 */
   	   	  int length; /* In seconds */
   	   	  int binCount = 0;
   	  
   	   	  length = Integer.parseInt(c.getString(c.getColumnIndex(Calls.DURATION)));
   	   	  bin = getBinIndex(length, LENGTH_BIN_WIDTH);
   	   	  
   	   	  /* If already have this bin, get current count */
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
