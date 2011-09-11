package edu.ucdavis.ece.smartsync.profiler;

import java.util.ArrayList;
import java.util.Random;

import android.util.Pair;

public class SynthProfile implements IProfile {
	private int horizon;
	private ArrayList<ArrayList<Pair<Integer, Double>>> profile;

	private int maxUsed;
	
	private Random r = new Random();

	public SynthProfile() {
		horizon = 10;
		maxUsed = 5;

		profile = new  ArrayList<ArrayList<Pair<Integer, Double>>>();
		
		for(int n = 0; n<horizon; n++){
			ArrayList<Pair<Integer, Double>> used = new ArrayList<Pair<Integer, Double>>();
			double[] prob = new double[5];

			double total = 0;
			for (int i = 0; i < maxUsed; i++) {
				prob[i] = r.nextDouble();
				total += prob[i];
			}

			for (int i = 0; i < maxUsed; i++) {
				prob[i] /= total;
				used.add(new Pair<Integer, Double>(i, prob[i]));
			}

			profile.add(used);
		}
	}

	@Override
	public double getChargeProb(int timeSinceSync) {
		if (timeSinceSync > horizon)
			return 0;
		else if (timeSinceSync > horizon - 3)
			return 1 / 3;
		else
			return 0;
	}

	@Override
	public ArrayList<Pair<Integer, Double>> getEnergyUsed(int timeSinceSync) {
		return profile.get(timeSinceSync);
	}

	@Override
	public int getHorizon() {
		return horizon;
	}

	@Override
	public int getMaxBattery() {
		return 100;
	}

}
