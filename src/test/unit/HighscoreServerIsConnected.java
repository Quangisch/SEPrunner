package test.unit;

import net.HighscoreServer;

public class HighscoreServerIsConnected {

	public static void main(String[] args) {
		test(true);
	}

	public static void test(boolean expect) {
		boolean outBool = new HighscoreServer().isConnected();
		System.out.println(" out: " + outBool + " expect: " + expect + " success: " + (outBool == expect));
	}

}
