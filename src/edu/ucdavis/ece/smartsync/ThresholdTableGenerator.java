package edu.ucdavis.ece.smartsync;
import java.util.ArrayList;
import java.util.Arrays;

import android.util.Pair;
import edu.ucdavis.ece.smartsync.profiler.IProfile;
import edu.ucdavis.ece.smartsync.profiler.SynthProfile;

/**
 * Threshold calculator
 * 
 * Generates the threshold table which determines the amount of time after the
 * last sync to sync again given the current state.
 * 
 * So time slot 0 in the threshold is the last time the phone is disconnected
 * from charger.
 * 
 * @author yichuan
 * 
 */

public class ThresholdTableGenerator {

	private IProfile profile;
	private int horizon;
	private int maxBattery;

	private int energyPerSync = 1;

	// Reward of other use!
	private double rewardPerEo = 2;
	// Reward of sync!
	private double rewardOfSyncPerTau = 0.5;

	private double[][][] vs;
	private double[][][] vi;

	private double[][][] V;
	public boolean[][][] policy;

	/* Constants */

	// reward of 'unit' remaining energy at charging time
	private double Re = 0;

	// charging time

	public ThresholdTableGenerator(IProfile profile) {
		this.profile = profile;
		horizon = profile.getHorizon();
		maxBattery = profile.getMaxBattery();
		vs = new double[horizon][maxBattery][horizon];
		vi = new double[horizon][maxBattery][horizon];

		for (double[][] mat : vs)
			for (double[] row : mat)
				Arrays.fill(row, -1);

		for (double[][] mat : vi)
			for (double[] row : mat)
				Arrays.fill(row, -1);
	}

	public double[][][] getThreshold() {
		V = new double[horizon][maxBattery][horizon];
		policy = new boolean[horizon][maxBattery][horizon];

		for (int i = horizon - 1; i >= 0; i--) {
			for (int j = 0; j < maxBattery; j++) {
				for (int k = 0; k <= i; k++) {
					V[i][j][k] = V(i, j, k);
				}
			}
		}
		return V;
	}

//	private double V_star(int t, int Er, int tau) {
//		double p = this.profile.getChargeProb(t);
//		return Er * Re * p + V(t, Er, tau) * (1 - p);
//	}

	private double V(int t, int Er, int tau) {
//		if (Er == 0) {
//			return 0;
//		} else if (t == horizon - 1) {
//			return Er * Re;
//		} else {
//			if(t!=9 && Er !=0){
//				double vs1 = vs[t][Er][tau];
//				double vi1 = vi[t][Er][tau];
//				System.out.println();
//			}
			
			
			if (vs[t][Er][tau] == -1) {
				vs[t][Er][tau] = reward(t, Er, tau, true);
			}
			if (vi[t][Er][tau] == -1) {
				vi[t][Er][tau] = reward(t, Er, tau, false);
			}

			double Vs = vs[t][Er][tau];
			double Vi = vi[t][Er][tau];

			
//			if(t!=9 && Er !=0){
//				System.out.println();
//			}
			
			
			policy[t][Er][tau] = (Vs > Vi);

			return (Vs > Vi) ? Vs : Vi;
//		}
	}

	private double reward(int t, int Er, int tau, boolean sync) {
		if (Er == 0) {
			return 0;
		} else if (t == horizon - 1) {
			return Er * Re;
		}
		
		
		ArrayList<Pair<Integer, Double>> RV = profile.getEnergyUsed(t);

		double reward = 0;

		for (int i = 0; i < RV.size(); i++) {
			Pair<Integer, Double> thisSlot = RV.get(i);
			int used = thisSlot.first;
			double prob = thisSlot.second;
			int E = Er - used;
			if (E < 0) {
				used = Er;
				E = 0;
			}

			double syncReward = 0;

			if (sync) {
				E = E - energyPerSync;
				syncReward = rewardOfSyncPerTau * (tau+1);
				if (E < 0) {
					syncReward = 0;
					E = 0;
				}
			}

			double v;
			if (syncReward == 0){
				v = V(t + 1, E, tau + 1) + rewardPerEo * used + syncReward;
			}
			else
				v = V(t + 1, E, 0) + rewardPerEo * used + syncReward;
			
			
			reward += prob * v;
		}
		
		
		
//		if(t==8 && Er ==1){
//			System.out.println();
//		}
		
		
		
		return reward;
	}

	public static void main(String[] args) {
		ThresholdTableGenerator th = new ThresholdTableGenerator(
				new SynthProfile());
		double[][][] V = th.getThreshold();

		// double [][] tau_star = new double []
//		for (int i = th.horizon - 1; i >= 0; i--) {
		int i =8;
			for (int j = 0; j < th.maxBattery; j++) {
				System.out.print("t:"+i+" ");
				System.out.print("Er:"+j+" ");
				System.out.print("-");
				for (int k = 0; k <= i; k++) {
					System.out.print("-"+k+"-");
					System.out.print(th.policy[i][j][k]);
					System.out.print("-");
					System.out.format("%2.2f",th.vs[i][j][k]);
					System.out.print("-");
					System.out.format("%2.2f",th.vi[i][j][k]);
				}
				System.out.println();
			}
//		}

		System.out.println();
	}

}
