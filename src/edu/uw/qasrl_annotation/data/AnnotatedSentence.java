package edu.uw.qasrl_annotation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AnnotatedSentence {
	public Sentence sentence;
	public HashMap<Integer, ArrayList<QAPair>> qaLists;
	public HashSet<String> annotators;
	
	public AnnotatedSentence(Sentence sentence) {
		this.sentence = sentence;
		this.qaLists = new HashMap<Integer, ArrayList<QAPair>>();
		this.annotators = new HashSet<String>();
	}
	
	public boolean addProposition(int propHead) {
		if (qaLists.containsKey(propHead)) {
			return false;
		}
		qaLists.put(propHead, new ArrayList<QAPair>());
		return true;
	}
	
	public boolean addQAPair(int propHead, QAPair qa) {
		if (!qaLists.containsKey(propHead)) {
			return false;
		}
		ArrayList<QAPair> qaList = qaLists.get(propHead);
		/*
		for (QAPair qa0 : qaList) {
			if (qa0.equals(qa)) {
				qa0.cfAnnotationSources.addAll(qa.cfAnnotationSources);
				return false;
			}
		}*/
		qaList.add(qa);
		for (Object annotator : qa.annotators) {
			annotators.add(annotator.toString());
		}
		return true;
	}
}
