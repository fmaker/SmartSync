package edu.ucdavis.ece.smartsync;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.internal.os.PowerProfile;

public class SmartSyncActivity extends Activity {
	private static final String TAG = "SmartSyncActivity";
	
	public static PowerProfile sPowerProfile;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sPowerProfile = new PowerProfile(this);
        
        Log.v(TAG,String.format("Battery Capacity: %.0f mAh", sPowerProfile.getBatteryCapacity()));
    }
}