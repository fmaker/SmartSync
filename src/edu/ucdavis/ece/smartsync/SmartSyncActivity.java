package edu.ucdavis.ece.smartsync;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.util.Log;

import com.android.internal.os.PowerProfile;

public class SmartSyncActivity extends Activity {
	private static final String TAG = "SmartSyncActivity";
	
	public static PowerProfile sPowerProfile;
	public static UserProfile sUserProfile;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sPowerProfile = new PowerProfile(getApplicationContext());        
        sUserProfile = new UserProfile(getApplicationContext());
        
        Log.v(TAG,String.format("Battery Capacity: %.0f mAh", sPowerProfile.getBatteryCapacity()));

     }
}