package test.unit;

import misc.StringFunctions;

public class StringFunctionsGetTimeAsString {

	public static void main(String[] args) {
		test(34.12f, "00:34.12");
		test(61.52324f, "01:01.52");
		test(999 * 60, "59:59.99");
	}

	public static void test(float inTime, String expect) {
		String outString = StringFunctions.getTimeAsString(inTime);
		System.out.println("in: " + inTime + " out: " + outString + " expect: " + expect + " success: "
				+ outString.equals(expect));
	}

}
