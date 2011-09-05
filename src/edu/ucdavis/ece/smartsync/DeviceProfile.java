package edu.ucdavis.ece.smartsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.internal.os.PowerProfile;

public class DeviceProfile {
	private static PowerProfile sPowerProfile;
	
	private double batteryCapacity;
	
	public DeviceProfile(Context c){
		sPowerProfile = new PowerProfile(c);
		batteryCapacity = sPowerProfile.getBatteryCapacity();
		
	}

	/**
	 * This function is used to convert from a battery percentage level
	 * to the actual amount of energy remaining
	 * 
	 * @param level - percentage level from 0 to 100
	 * @return energy remaining in mAh
	 */
	double getEnergyRemaining(int level){
		return batteryCapacity * ( (float) level / 100.00);
	}
}
