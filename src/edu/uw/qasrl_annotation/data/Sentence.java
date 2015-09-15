package edu.uw.qasrl_annotation.data;

import edu.uw.qasrl_annotation.util.StrUtils;

public class Sentence {
	public int[] tokens;
	public int length;
	public int sentenceID;
	public String source;
	
	public transient Corpus corpus;
	
	public Sentence(int[] tokens, Corpus corpus, int sentenceID) {
		this.tokens = tokens;
		this.length = tokens.length;
		this.corpus = corpus;
		this.sentenceID = sentenceID;
	}

	public String getTokensString() {
		return StrUtils.join(" ", corpus.wordDict.getStringArray(tokens));
	}
	
	public String getTokenString(int index) {
		return corpus.wordDict.getString(tokens[index]);
	}
	
	public String getTokenString(int[] span) {
		String str = "";
		for (int i = span[0]; i < span[1]; i++) {
			if (i > span[0]) {
				str += " ";
			}
			str += getTokenString(i);
		}
		return str;
	}

	public boolean containsToken(String token) {
		for (int i = 0; i < length; i++) {
			if (getTokenString(i).equalsIgnoreCase(token)) {
				return true;
			}
		}
		return false;
	}
	
	public String getNumberedTokensString() {
		return StrUtils.numberedJoin(" ",
				corpus.wordDict.getStringArray(tokens));
	}
	
	public boolean containsQuestion() {
		for (int i = 0; i < length; i++) {
			String word = getTokenString(i);
			if (word.equals("?")) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "ID:\t" + this.sentenceID + "\n" +
				this.getTokensString();
	}
	
	
	
	
}
