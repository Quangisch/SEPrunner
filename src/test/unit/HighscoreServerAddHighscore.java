package test.unit;

import net.HighscoreServer;

public class HighscoreServerAddHighscore {

	public static void main(String[] args) {
		test(true);
	}

	public static void test(boolean expect) {
		new HighscoreServer().addHighScore(0, "Test", 123);;
		
		//System.out.println(" out: " + outBool + " expect: " + expect + " success: " + (outBool == expect));
	}

}
