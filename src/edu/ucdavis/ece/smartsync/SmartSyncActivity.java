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

import edu.ucdavis.ece.smartsync.profiler.Profile;


/**
 * @author fmaker
 *
 */
public class SmartSyncActivity extends Activity {
	private static final String TAG = "SmartSyncActivity";

	private static Profile sProfile;
	public static ThresholdTableGenerator sTableGenerator;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sProfile = new Profile(getApplicationContext());
        sTableGenerator = new ThresholdTableGenerator(sProfile);
     }
}