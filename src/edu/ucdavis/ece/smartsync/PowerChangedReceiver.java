package edu.ucdavis.ece.smartsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Records log of battery charging and discharging events
 * @todo Make content provider
 * @todo Handle battery charging events when system not running
 */
public class PowerChangedReceiver extends BroadcastReceiver{
	private static final String TAG = "BatteryChangedReceiver";
	
	private static final int POWER_DISCONNECTED = 0;
	private static final int POWER_CONNECTED = 1;

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		int powerConnected = -1;
		
		if(action.equals(Intent.ACTION_POWER_CONNECTED)){
			powerConnected = 1;
		}
		else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)){
			powerConnected = 0;
		}
		
		if(powerConnected != -1){
			
			/* Open database */
			BatteryLogOpenHelper mDbHelper = new BatteryLogOpenHelper(context);

			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			String stmt = String.format("INSERT INTO %s ( %s, %s) VALUES (%s, %s);",
					mDbHelper.TABLE_NAME, mDbHelper.KEY_TIMESTAMP, mDbHelper.KEY_POWER_CONNECTED,
					"strftime('%s', 'now')", powerConnected);
			db.execSQL(stmt);

			db.close();
		}

	}
	
	private class BatteryLogOpenHelper extends SQLiteOpenHelper {

		protected static final int DATABASE_VERSION = 1;
		protected static final String DATABASE_NAME = "battery_log.db";
		protected static final String TABLE_NAME = "log";
	    
		protected static final String KEY_TIMESTAMP = "timestamp";
		protected static final String KEY_POWER_CONNECTED = "power_connected";
	    
	    private static final String TABLE_CREATE =
	                "CREATE TABLE " + TABLE_NAME + " (" +
	                KEY_TIMESTAMP + " INTEGER, " +
	                KEY_POWER_CONNECTED + " INTEGER);";

		public BatteryLogOpenHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

	    @Override
	    public void onCreate(SQLiteDatabase db) {
	        db.execSQL(TABLE_CREATE);
	    }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}

}
