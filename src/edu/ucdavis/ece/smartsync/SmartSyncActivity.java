package edu.ucdavis.ece.smartsync;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.widget.RemoteViews.ActionException;

import com.android.internal.os.PowerProfile;

public class SmartSyncActivity extends Activity {
	private static final String TAG = "SmartSyncActivity";
	private static final float MA_IN_AMP = 1000;
	private static final float MV_IN_VOLT = 1000;
	private static final int SECS_IN_MIN = 60;
	private static final int SECS_IN_HOUR = 60*SECS_IN_MIN;
	
	public static PowerProfile sPowerProfile;
	public static UserProfile sUserProfile;
	public static DeviceProfile sDeviceProfile;
	private static BatteryStatsReceiver sBatteryStatsReceiver;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sPowerProfile = new PowerProfile(getApplicationContext());        
        sUserProfile = new UserProfile(getApplicationContext());
        sDeviceProfile = new DeviceProfile(getApplicationContext());
        
        /* Log.v(TAG,String.format("Battery Capacity: %.0f mAh", sPowerProfile.getBatteryCapacity())); */
		
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		sBatteryStatsReceiver = new BatteryStatsReceiver();
		registerReceiver(sBatteryStatsReceiver, filter);

        Log.v(TAG, "Discharge times:");
		for(int i : sUserProfile.getDischargeTimes()){
	        Log.v(TAG,String.format("\t%d seconds = %.2f minutes = %.2f hours", i, (float)i/SECS_IN_MIN, (float)i/SECS_IN_HOUR));
		}
     }
	
	public class BatteryStatsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			float voltage = (float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / (float) MV_IN_VOLT;
			final float percent = (float) level / (float)scale;
			
			float joules = (float) ((sPowerProfile.getBatteryCapacity() / MA_IN_AMP) * SECS_IN_HOUR * voltage * percent);
			
			
	        Log.v(TAG,String.format("Remaining Battery Energy: %.2f mAh, %.2f J, (%.2f %%)", 
	        		sPowerProfile.getBatteryCapacity() * percent,
	        		joules,
	        		percent * 100));
		}
		
	}
}