package edu.ucdavis.ece.smartsync.profiler;
import java.util.ArrayList;

import android.util.Pair;

/**
 * We assume that cell phone is active (from charging to next charging) while
 * the user is 'on the move'. This time is relatively predictable. So instead of
 * average over absolute charging time, the calculation average over the active
 * period.
 * 
 * For profile, each discharge period is a unit. We don't care about when discharging starts, but
 * instead we care only about length of the discharging time. So we 'align' each period
 * by their starting time. E.g. (assume time slot is 1 hour)
 * 
 * Time slot: 1 2 3...
 * 
 * Discharging Period 1: 8am 9am 10am...
 * 
 * Remaining: 100 90 80...
 * 
 * Used: 10 10 15...
 * 
 * ChargeProb: 0.000 0.000 0.001...
 * 
 * -----------------------------------------
 * 
 * Day 2: 9am 10am 11am...
 * 
 * -----------------------------------------
 * 
 * Profile:
 * 
 * Used 5 8 20...
 * 
 * CallProb: 0.001 0.001 0.002...
 * 
 * ChargeProb: 0.000 0.001 0.003...
 * 
 * @author yichuan
 * 
 */

public interface IProfile {

	/**
	 * Get the charging probability
	 * 
	 * @return probability of charging from 0 to 1 inclusive
	 */
	public double ProbCharging(int timeSinceSync);

	/**
	 * Get the energy consumption in a time slot. Energy used by everything
	 * except sync we scheduled. (Which can be phone call only)
	 * 
	 * @return <EnergyUsed, Probability>
	 */
	public ArrayList<Pair<Integer, Double>> EnergyUsedRV(int timeSinceSync);

	/**
	 * Get the maximum length of a discharging period.
	 * 
	 * @return length in seconds
	 */
	public int getHorizon();


	/**
	 * Get the energy consumption in a time slot. Energy used by everything
	 * except sync we scheduled. (Which can be phone call only)
	 * 
	 * @return <EnergyUsed, Probability>
	 */
	public int getMaxBattery();

}