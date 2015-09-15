package edu.uw.qasrl_annotation.data;

import gnu.trove.list.array.TIntArrayList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Corpus {
	public String corpusName;
	public CountDictionary wordDict;
	public ArrayList<Sentence> sentences;
	public ArrayList<ArrayList<TargetPredicate>> predicates;
	
	public Corpus(String corpusName) {
		this.corpusName = corpusName;
		this.wordDict = new CountDictionary();
		this.sentences = new ArrayList<Sentence>();
		this.predicates = new ArrayList<ArrayList<TargetPredicate>>();
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
	
	private void addSentenceAndPredicates(TIntArrayList tokens,
			TIntArrayList pids) {
		int nextSentenceID = sentences.size();
		Sentence sentence = new Sentence(tokens.toArray(), this,
				nextSentenceID);
		ArrayList<TargetPredicate> currPredicates =
				new ArrayList<TargetPredicate>();
		for (int i = 0; i < pids.size(); i++) {
			int pid = pids.get(i);
			currPredicates.add(new TargetPredicate(sentence, i,
					new int[]{pid, pid+1}));
		}
		sentences.add(sentence);
		predicates.add(currPredicates);
		tokens.clear();
		pids.clear();
	}
	
	// Data format: {id} {word} {Y/N}
	public void loadSentenceWithPredicates(String inputFilePath)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputFilePath)));
			
		String currLine;
		TIntArrayList tokens = new TIntArrayList();		   
		TIntArrayList pids = new TIntArrayList();
			
		while ((currLine = reader.readLine()) != null) {
			String[] columns = currLine.split("\\s+");
			if (columns.length < 3) {
				addSentenceAndPredicates(tokens, pids);
			} else {				
				if (columns[2].equals("Y")) {
					pids.add(tokens.size());
				}
				tokens.add(wordDict.addString(columns[1]));
			}
		}
		if (tokens.size() > 0) {
			addSentenceAndPredicates(tokens, pids);
		} 
		reader.close();
		System.out.println(String.format("Read %d sentences from %s.\n",
				sentences.size(), inputFilePath));
	}
}
