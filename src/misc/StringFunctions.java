package misc;

import java.text.SimpleDateFormat;
import java.util.Date;

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

	public static String getTimeAsString(float time) {
		time = Math.min(time, 59 * 60 + 59.999f);
		Date d = new Date((long) (time * 1000));
		return (new SimpleDateFormat("mm:ss.S").format(d)+"0000000000").substring(0, 8);
	}
}
