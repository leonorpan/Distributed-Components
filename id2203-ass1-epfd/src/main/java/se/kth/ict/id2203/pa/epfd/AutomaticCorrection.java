package se.kth.ict.id2203.pa.epfd;

import se.kth.ict.id2203.sim.epfd.EpfdTester;

public class AutomaticCorrection {
	public static void main(String[] args) {
		String email = "leonorap@kth.se";
		String password = "HfpkPC";
		EpfdTester.correctAndSubmit(email, password);
	}
}
