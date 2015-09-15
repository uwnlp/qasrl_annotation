package edu.uw.qasrl_annotation.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class VerbInflectionDictionary {	
	Corpus corpus;
	public ArrayList<String[]> inflections;
	public int[] inflCount;
	public HashMap<String, ArrayList<Integer>> inflMap;
	
	public VerbInflectionDictionary(Corpus corpus) {
		this.corpus = corpus;
		inflections = new ArrayList<String[]>();
		inflMap = new HashMap<String, ArrayList<Integer>>();
	}
	
	public void loadDictionaryFromFile(String filePath) throws IOException {
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(filePath)));
		String line;
		while ((line = reader.readLine()) != null) {
			if (!line.trim().isEmpty()) {
				String[] strs = line.split("\t");
				String[] infl = new String[5];
				boolean inCorpus = false;
				for (int i = 0; i < 5; i++) {
					infl[i] = strs[i];
					if (!inCorpus && corpus.wordDict.contains(infl[i])) {
						inCorpus = true;
					}
				}
				int inflId = inflections.size();
				inflections.add(infl);
				for (int i = 0; i < infl.length; i++) {
					String v = infl[i];
					if (v.equals("_") || v.equals("-")) {
						continue;
					}
					if (!inflMap.containsKey(v)) {
						inflMap.put(v, new ArrayList<Integer>()); 
					}
					ArrayList<Integer> inflIds = inflMap.get(v);
					if (!inflIds.contains(inflId)) {
						inflIds.add(inflId);
					}
				}
			}
		}
		reader.close();
		countInflections();
		System.out.println(
				String.format("Successfully read inflections. Inflection dictionary size: %d.",
						inflMap.size()));
	}
	
	public int getBestInflectionId(String verb) {		
		ArrayList<Integer> inflIds = inflMap.get(verb);
		if (inflIds == null) {
			return -1;
		}
		int bestId = -1, bestCount = -1;
		for (int i = 0; i < inflIds.size(); i++) {
			int count = inflCount[inflIds.get(i)];
			if (count > bestCount) {
				bestId = inflIds.get(i);
				bestCount = count;
			}
		}
		return bestId;
	}
	
	public String[] getBestInflections(String verb) {
		String verbPrefix = "";
		if (verb.contains("-")) {
			int idx = verb.indexOf('-');
			verbPrefix = verb.substring(0, idx + 1);
			verb = verb.substring(idx + 1);
		}
		ArrayList<Integer> inflIds = inflMap.get(verb);
		if (inflIds == null) {
			return null;
		}
		int bestId = -1, bestCount = -1;
		for (int i = 0; i < inflIds.size(); i++) {
			int count = inflCount[inflIds.get(i)];
			if (count > bestCount) {
				bestId = inflIds.get(i);
				bestCount = count;
			}
		}
		String[] infl = new String[5];
		for (int i = 0; i < 5; i++) {
			infl[i] = verbPrefix + inflections.get(bestId)[i];
		}
		return infl;
	}
	
	public String getBestBaseVerb(String verb) {
		int bestId = getBestInflectionId(verb);
		return bestId < 0 ? verb : inflections.get(bestId)[0];
	}
	
	private void countInflections() {
		inflCount = new int[inflections.size()];
		Arrays.fill(inflCount, 0);
		for (Sentence sent : corpus.sentences) {
			for (int i = 0; i < sent.length; i++) {
				String w = sent.getTokenString(i);
				if (inflMap.containsKey(w)) {
					for (int inflId : inflMap.get(w)) {
						inflCount[inflId] ++;
					}
				}
			}
		}
	}	
}
