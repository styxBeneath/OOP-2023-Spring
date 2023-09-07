// CS108 HW1 -- String static methods

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class StringCode {

	/**
	 * Given a string, returns the length of the largest run.
	 * A a run is a series of adajcent chars that are the same.
	 *
	 * @param str
	 * @return max run length
	 */
	public static int maxRun(@NotNull String str) {
		if (str.length() == 0) {
			return 0;
		}
		int res = 0;
		int currentMax = 1;
		char currentChar = str.charAt(0);
		for (int i = 1; i < str.length(); i++) {
			if (str.charAt(i) == currentChar) {
				currentMax++;
			} else {
				res = Math.max(currentMax, res);
				currentMax = 1;
				currentChar = str.charAt(i);
			}
		}
		return res;
	}

	//region blowup
	/**
	 * Given a string, for each digit in the original string,
	 * replaces the digit with that many occurrences of the character
	 * following. So the string "a3tx2z" yields "attttxzzz".
	 *
	 * @param str
	 * @return blown up string
	 */
	public static String blowup(@NotNull String str) {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < str.length() - 1; i++) {
			if (isNumber(str.charAt(i))) {
				appendChars(res, str.charAt(i + 1), str.charAt(i) - '0');
			} else {
				res.append(str.charAt(i));
			}
		}
		if (str.length() > 0 && !isNumber(str.charAt(str.length() - 1))) {
			res.append(str.charAt(str.length() - 1));
		}
		return res.toString();
	}
	
	// Appends c to str n-times
	private static void appendChars(StringBuilder str, char c, int n) {
		str.append(String.valueOf(c).repeat(Math.max(0, n)));
	}

	private static boolean isNumber(char c) {
		return c >= '0' && c <= '9';
	}
	//endregion

	/**
	 * Given 2 strings, consider all the substrings within them
	 * of length len. Returns true if there are any such substrings
	 * which appear in both strings.
	 * Compute this in linear time using a HashSet. Len will be 1 or more.
	 */
	public static boolean stringIntersect(@NotNull String a, @NotNull String b, int len) {
		Set<String> set = new HashSet<>(); // substrings of a
		for (int i = 0; i < a.length() - len + 1; i++) {
			set.add(a.substring(i, i + len));
		}
		for (int i = 0; i < b.length() - len + 1; i++) {
			if (set.contains(b.substring(i, i + len))) {
				return true;
			}
		}
		return false;
	}
}
