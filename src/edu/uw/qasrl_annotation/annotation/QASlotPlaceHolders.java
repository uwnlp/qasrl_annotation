package edu.uw.qasrl_annotation.annotation;

import java.util.HashSet;

public class QASlotPlaceHolders {

	public static final String[] values = {
			"",
			"someone",
			"something",
		};
	public static final HashSet<String> valueSet;
	
	public static final String[] ph3Values = {
		"",
		"someone",
		"something",
		"somewhere",
		"do",
		"doing",
		"do something",
		"doing something",
	};
	public static final HashSet<String> ph3ValueSet;
	
	static {
		valueSet = new HashSet<String>();
		ph3ValueSet = new HashSet<String>();
		for (String val : values) {
			valueSet.add(val);
		}
		for (String val : ph3Values) {
			ph3ValueSet.add(val);
		}
	}

}
