package edu.ucdavis.ece.smartsync.profiler;

import java.util.ArrayList;
import java.util.Random;

import android.util.Pair;

public class SynthProfile implements IProfile {
	private int horizon;
	private int[] profile;

	private int maxUsed;

	public SynthProfile() {
		horizon = 10;
		profile = new int[horizon];
		maxUsed = 5;

		Random r = new Random();

		for (int i = 0; i < horizon; i++) {
			profile[i] = r.nextInt(2);
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
		ArrayList<Pair<Integer, Double>> used = new ArrayList<Pair<Integer, Double>>();
		double[] prob = new double[5];

		Random r = new Random();

		double total = 0;
		for (int i = 0; i < maxUsed; i++) {
			prob[i] = r.nextDouble();
			total += prob[i];
		}

		for (int i = 0; i < maxUsed; i++) {
			prob[i] /= total;
			used.add(new Pair<Integer, Double>(i, prob[i]));
		}

		return used;
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
