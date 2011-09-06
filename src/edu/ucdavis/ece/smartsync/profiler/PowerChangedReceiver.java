package edu.ucdavis.ece.smartsync.profiler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * Records log of battery charging and discharging events
 * @todo Make content provider
 * @todo Handle battery charging events when system not running
 * 
 * @author fmaker
 */
public class PowerChangedReceiver extends BroadcastReceiver{
	private static final String TAG = "BatteryChangedReceiver";
	
	public static final int POWER_DISCONNECTED = 0;
	public static final int POWER_CONNECTED = 1;

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		int powerConnected = -1;
		
		if(action.equals(Intent.ACTION_POWER_CONNECTED)){
			powerConnected = POWER_CONNECTED;
		}
		else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)){
			powerConnected = POWER_DISCONNECTED;
		}
		
		if(powerConnected != -1){
			
			/* Open database */
			BatteryLogOpenHelper mDbHelper = new BatteryLogOpenHelper(context);

			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			String stmt = String.format("INSERT INTO %s ( %s, %s) VALUES (%s, %s);",
					BatteryLogOpenHelper.TABLE_NAME, 
					BatteryLogOpenHelper.KEY_TIMESTAMP, 
					BatteryLogOpenHelper.KEY_POWER_CONNECTED,
					"strftime('%s', 'now')", powerConnected);
			db.execSQL(stmt);
			db.close();
		}

	}
    
    public int secondsSinceLastCharge(Context context){
		
		/* Open database */
		BatteryLogOpenHelper mDbHelper = new BatteryLogOpenHelper(context);

		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = db.query(
					BatteryLogOpenHelper.TABLE_NAME, 
					new String[]{BatteryLogOpenHelper.KEY_TIMESTAMP},
					String.format("%s == %d",
								BatteryLogOpenHelper.KEY_POWER_CONNECTED,
								String.valueOf(POWER_CONNECTED)),
					null, null, null,
					String.format("%s DESC",BatteryLogOpenHelper.KEY_TIMESTAMP),
					"1");

		int lastChargeTimestamp = c.getInt(c.getColumnIndex(BatteryLogOpenHelper.KEY_TIMESTAMP));

		db.close();
		
		return lastChargeTimestamp;
    }
    


}
