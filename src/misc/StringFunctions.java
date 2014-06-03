package misc;

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
}
