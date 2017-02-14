package edu.uw.qasrl_annotation.annotation;

import edu.uw.qasrl_annotation.data.Sentence;

import java.util.ArrayList;
import java.util.Arrays;

public class AnswerSpanAligner {
	public static int[] align(Sentence sentence, String answer) {
		String[] ansTokens = answer.split("\\s+");
		String[] sentTokens = sentence.corpus.wordDict.getStringArray(
				sentence.tokens);

		int[] matched = new int[sentTokens.length];
		Arrays.fill(matched, 0);

		for (int i = 0; i < ansTokens.length; i++) {
			int maxLength = 0;
			ArrayList<Integer> bestMatches = new ArrayList<Integer>();
			for (int j = 0; j + maxLength < sentTokens.length; j++) {
				int k = 0;
				for (; j + k < sentTokens.length &&
						i + k < ansTokens.length; k++) {
					if (!ansTokens[i + k].equalsIgnoreCase(sentTokens[j + k])) {
						break;
					}
				}
				if (k > maxLength) {
					maxLength = k;
					bestMatches.clear();
					bestMatches.add(j);
				} else if (k == maxLength) {
					bestMatches.add(j);
				}
			}
			if (maxLength > 0) {
				for (int match : bestMatches) {
					for (int k = 0; k < maxLength; k++) {
						matched[match + k] = 1;
					}
				}
				//spans.add(new int[] {bestMatch, bestMatch + maxLength});
				i += maxLength - 1;
			}
		}
		return matched;
	}
}
