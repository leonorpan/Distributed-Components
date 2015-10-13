package se.kth.ict.id2203.pa.consensus;

import se.kth.ict.id2203.sim.consensus.ConsensusTester;

public class AutomaticCorrection {
	public static void main(String[] args) {
		String email = "leonorap@kth.se";
		String password = "HfpkPC";
		ConsensusTester.correctAndSubmit(email, password);
	}
}
