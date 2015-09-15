package edu.uw.qasrl_annotation.data;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;

public class Corpus {
	public String corpusName;
	public CountDictionary wordDict;
	public ArrayList<Sentence> sentences;
	
	public Corpus(String corpusName) {
		this.corpusName = corpusName;
		this.wordDict = new CountDictionary();
		this.sentences = new ArrayList<Sentence>();
	}
	
	public Sentence getSentence(int sentId) {
		return sentences.get(sentId);
	}
	
	public Sentence addNewSentence(String sentStr) {
		TIntArrayList tokenIds = new TIntArrayList();
		for (String token : sentStr.trim().split("\\s+")) {
			tokenIds.add(wordDict.addString(token));
		}
		Sentence sent = new Sentence(tokenIds.toArray(), this, sentences.size());
		sentences.add(sent);
		return sent;
	}
}
