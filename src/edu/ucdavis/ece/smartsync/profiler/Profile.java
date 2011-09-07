package edu.ucdavis.ece.smartsync.profiler;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Pair;

import com.android.internal.os.PowerProfile;


/**
 * Represents the current profile of the user, battery and device.
 * 
 * @author fmaker
 *
 */
public class Profile implements IProfile{
	private static final String TAG = "Profile";
	private static final int SECS_IN_MIN = 60;
	private static final int SECS_IN_HOUR = 60*SECS_IN_MIN;
	private static final int MIN_HORIZON = SECS_IN_HOUR*24; /* 1 Day */
	private static final float HORIZON_TOL = 1.10F; /* 110% */

	private BatteryStatsReceiver batteryStatsReceiver;
	public PowerProfile mPowerProfile;
	public UserProfile mUserProfile;
	public DeviceProfile mDeviceProfile;
	
	private float mPercent;
	private float mEnergy; /* In Joules */

	public Profile(Context context) {

		/* Setup and register battery information receiver */
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryStatsReceiver = new BatteryStatsReceiver();
		context.registerReceiver(batteryStatsReceiver, filter);
		
        mPowerProfile = new PowerProfile(context);        
        mUserProfile = new UserProfile(context);
        mDeviceProfile = new DeviceProfile(context);

	}

	/* Determines the charge probability by taking the count of all the
	 * previous discharge durations less than or equal to timeSinceSync divided
	 * by the total number discharge durations. */
	@Override
	public double getChargeProb(int timeSinceSync) {
		int count = 0;
		List<Integer> d = mUserProfile.getDischargeTimes();
		
		for(int i : d){
			if(i <= timeSinceSync)
				count++;
		}
		return (double) count/d.size();
	}

	@Override
	public ArrayList<Pair<Integer, Double>> getEnergyUsed(int t) {
		
		return null;
	}

	@Override
	public int getHorizon() {
		int max = MIN_HORIZON;
		List<Integer> d = mUserProfile.getDischargeTimes();
		
		for(int i : d){
			if(i > max)
				max = i;
		}

		/* Return max times tolerance in case duration even larger */
		return (int) (max * HORIZON_TOL);
	}

	@Override
	public int getMaxBattery() {
		// TODO Auto-generated method stub
		return 0;
	}

	public class BatteryStatsReceiver extends BroadcastReceiver {
		private static final float MA_IN_AMP = 1000;
		private static final float MV_IN_VOLT = 1000;

		@Override
		public void onReceive(Context context, Intent intent) {
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			float voltage = (float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / (float) MV_IN_VOLT;
			mPercent = (float) level / (float)scale;
			
			mEnergy = (float) ((mPowerProfile.getBatteryCapacity() / MA_IN_AMP) * SECS_IN_HOUR * voltage * mPercent);

		}

	}

	@Override
	public String toString() {
        String s = "";
		
        s += String.format("Remaining Battery Energy:\n\t%.2f mAh, %.2f J, (%.2f %%)\n", 
        		mPowerProfile.getBatteryCapacity() * mPercent,
        		mEnergy,
        		mPercent * 100);

        s += "Discharge times:\n";
		for(long i : mUserProfile.getDischargeTimes()){
			s += String.format("\t%d seconds = %.2f minutes = %.2f hours\n", i, (float)i/SECS_IN_MIN, (float)i/SECS_IN_HOUR);
		}
		
		return s;
	}
	
	

}
