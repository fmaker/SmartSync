package edu.ucdavis.ece.smartsync;
import java.util.ArrayList;

/**
 * We assume that cell phone is active (from charging to next charging) while
 * the user is 'on the move'. This time is relatively predictable. So instead of
 * average over absolute charging time, the calculation average over the active
 * period.
 * 
 * For profile, each day is a unit. We don't care about when the day start, but
 * instead we care only about length of the day. So we kind of 'align' each day
 * by their starting time. E.g. (assume time slot is 1 hour)
 * 
 * Time slot: 1 2 3...
 * 
 * Day 1: 8am 9am 10am...
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
	 * @return
	 */
	public double ProbCharging(int t);

	/**
	 * Get the energy consumption in a time slot. Energy used by everything
	 * except sync we scheduled. (Which can be phone call only)
	 * 
	 * @return <EnergyUsed, Probability>
	 */
	public ArrayList<Pair<Double, Double>> EnergyUsedRV(int t);

	public int getHorizon();
}