package edu.uw.qasrl_annotation.annotation;

import java.util.HashSet;


public class QASlotAuxiliaryVerbs {

	public static final String[] values = {
			"",
			"is",
			"are",
			"was",
			"were",
			"does",
			"do",
			"did",
			"has",
			"have",
			"had",
			"can",
			"could",
			"may",
			"might",
			"will",
			"would",
			"should",
			"must",
			"is n't",
			"are n't",
			"was n't",
			"were n't",
			"does n't",
			"do n't",
			"did n't",
			"has n't",
			"have n't",
			"had n't",
			"ca n't",
			"could n't",
			"may not",
			"might not",
			"wo n't",
			"would n't",
			"should n't",
			"must n't",
		};
	
	public static final String[] beValues = {
		"is",
		"are",
		"was",
		"were",
		"is n't",
		"are n't",
		"was n't",
		"were n't",
	};
	
	public static final HashSet<String> beValuesSet;
	static {
		beValuesSet = new HashSet<String>();
		for (String val : beValues) {
			beValuesSet.add(val);
		}
	}
	
	public static final String[] haveValues = {
		"has",
		"have",
		"had",
		"has n't",
		"have n't",
		"had n't",
	};
	public static final HashSet<String> haveValuesSet;
	static {
		haveValuesSet = new HashSet<String>();
		for (String val : haveValues) {
			haveValuesSet.add(val);
		}
	}
}
