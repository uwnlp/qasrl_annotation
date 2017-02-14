package edu.uw.qasrl_annotation.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class StrUtils {
	public static String join(String delimiter, Object[] objects) {
		String joined = "";
		for (int i = 0; i < objects.length; i++) {
			if (!objects[i].toString().trim().isEmpty()) {
				if (!joined.isEmpty()) {
					joined += delimiter;
				}
				joined += objects[i];
			}
		}
		return joined;
	}

	public static String join(String delimiter, String padding,
	                          Object[] objects) {
		String joined = "";
		for (int i = 0; i < objects.length; i++) {
			if (!joined.isEmpty()) {
				joined += delimiter;
			}
			String ostr = objects[i].toString();
			joined += (ostr.isEmpty() ? padding : ostr);
		}
		return joined;
	}

	public static String join(String delimiter, Object[] objects, int startId) {
		String joined = "";
		for (int i = startId; i < objects.length; i++) {
			if (!objects[i].toString().trim().isEmpty()) {
				if (!joined.isEmpty()) {
					joined += delimiter;
				}
				joined += objects[i];
			}
		}
		return joined;
	}

	public static String join(String delimiter, ArrayList<Object> objects) {
		String joined = "";
		for (Object obj : objects) {
			String str = obj.toString();
			if (!str.trim().isEmpty()) {
				if (!joined.isEmpty()) {
					joined += delimiter;
				}
				joined += str;
			}
		}
		return joined;
	}

	public static String numberedJoin(String delimiter, String[] stringArr) {
		String joined = "";
		for (int i = 0; i < stringArr.length; i++) {
			if (!stringArr[i].trim().isEmpty()) {
				if (!joined.isEmpty()) {
					joined += delimiter;
				}
				joined += stringArr[i] + "(" + i + ")";
			}
		}
		return joined;
	}

	public static String join(String delimiter, String[] stringArr,
	                          int startIdx, int endIdx) {
		String joined = "";
		for (int i = startIdx; i < endIdx && i < stringArr.length; i++) {
			if (!stringArr[i].trim().isEmpty()) {
				if (!joined.isEmpty()) {
					joined += delimiter;
				}
				joined += stringArr[i];
			}
		}
		return joined;
	}

	public static String intArrayToString(String delimiter, int[] intArr) {
		String joined = "";
		for (int i = 0; i < intArr.length; i++) {
			if (i > 0) {
				joined += delimiter;
			}
			joined += intArr[i];
		}
		return joined;
	}

	public static String doubleArrayToString(String delimiter,
	                                         double[] doubleArr) {
		String joined = "";
		for (int i = 0; i < doubleArr.length; i++) {
			if (i > 0) {
				joined += delimiter;
			}
			joined += String.format("%.3f", doubleArr[i]);
		}
		return joined;
	}

	public static boolean isEmptyStringArray(ArrayList<String> strArray) {
		for (String str : strArray) {
			if (!str.trim().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public static HashSet<String> asSet(String... vals) {
		return new HashSet<String>(Arrays.asList(vals));
	}


}
