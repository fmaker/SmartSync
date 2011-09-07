package edu.ucdavis.ece.smartsync;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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