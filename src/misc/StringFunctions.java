package misc;

import java.text.DecimalFormat;

public enum StringFunctions {
	;

	public static int getMostEqualIndexIgnoreCase(String a, String[] other) {
		if (a == null || other == null) return -1;
	
		a = a.toUpperCase();

		int max = 0;
		int max_i = -1;
		for (int i = 0; i < other.length; i++) {
			other[i] = other[i].toUpperCase();
			int t = 0;
			while (t < other[i].length() && t < a.length() && other[i].charAt(t) == a.charAt(t))
				t++;
			if (t > max) {
				max_i = i;
				max = t;
			}
		}
		return max_i;
	}
	
//	TODO dirty
	public static String getTimeAsString(float time) {
		String min = String.valueOf((int) time / 60);
		min = min.length() == 1 ? "0"+min : min.length() == 0 ? "00" : min.length() > 2 ? "99" : min;
		
		String sec = String.valueOf((int) time % 60);
		sec = sec.length() == 1 ? "0"+sec : sec.length() == 0 ? "00" : sec.length() > 2 ? "99" : sec;
		
		DecimalFormat df = new DecimalFormat(".00");
		String milli = df.format(time);
		milli = milli.substring(milli.length() - 2);
		
		return min+":"+sec+":"+milli;
	}
}
