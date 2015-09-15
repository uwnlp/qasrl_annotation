package edu.uw.qasrl_annotation.data;

import java.util.ArrayList;
import java.util.Arrays;

import edu.uw.qasrl_annotation.annotation.AnswerSpanAligner;
import edu.uw.qasrl_annotation.annotation.QuestionEncoder;
import edu.uw.qasrl_annotation.util.StrUtils;

public class QAPair implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public Sentence sentence;
	public int propHead, questionId;
	public String[] questionWords;
	public String questionLabel, questionString;
	public int[] answerFlags;
	public ArrayList<String> answers;
	public ArrayList<Object> annotators;
	public String comment = "";
	
	public QAPair(Sentence sent, int prop, String[] question, String answer) { 
		sentence = sent;
		propHead = prop;
		questionWords = new String[question.length];
		for (int i = 0; i < question.length; i++) {
			questionWords[i] = question[i].toLowerCase();
		}
		questionLabel = QuestionEncoder.getLabels(question)[0];
		questionString = StrUtils.join(" ", questionWords);
		answerFlags = new int[sent.length];
		answers = new ArrayList<String>();
		Arrays.fill(answerFlags, 0);
		addAnswer(answer);
		annotators = new ArrayList<Object>();
	}
	
	public QAPair(Sentence sent, int prop, String qstr, String answer) { 
		sentence = sent;
		propHead = prop;
		questionWords = null;
		if (qstr.contains(" ")) {
			questionString = qstr;
			questionLabel = "";
		} else {
			questionString = "";
			questionLabel = qstr.toLowerCase();
		}
		answerFlags = new int[sent.length];
		answers = new ArrayList<String>();
		Arrays.fill(answerFlags, 0);
		addAnswer(answer);
		annotators = new ArrayList<Object>();
	}

	public QAPair(Sentence sent, int prop, String[] question, String answer,
			String annotator) { 
		sentence = sent;
		propHead = prop;
		questionWords = new String[question.length];
		for (int i = 0; i < question.length; i++) {
			questionWords[i] = question[i].toLowerCase();
		}
		questionLabel = QuestionEncoder.getLabels(question)[0];
		questionString = StrUtils.join(" ", questionWords);
		answerFlags = new int[sent.length];
		answers = new ArrayList<String>();
		Arrays.fill(answerFlags, 0);
		addAnswer(answer);
		
		annotators = new ArrayList<Object>();
		annotators.add(annotator);
	}
	
	public QAPair(Sentence sent, int propHead, int questionId,
			String[] question) {
		this(sent, propHead, question, "", "");
		this.questionId = questionId;
	}


	/** return false if answer is not aligned */
	public boolean addAnswer(String answer) {
		if (answer.isEmpty()) {
			return false;
		}
		answers.add(answer);
		int[] matched = AnswerSpanAligner.align(sentence, answer);
		boolean aligned = false;
		for (int i = 0; i < sentence.length; i++) {
			answerFlags[i] += matched[i];
			if (matched[i] > 0) {
				aligned = true;
			}
		}
		return aligned;
	}
	
	public void addAnswer(int[] flags) {
		for (int i = 0; i < answerFlags.length; i++) {
			answerFlags[i] += flags[i];
		}
	}
	
	public String getQuestionLabel() {
		return questionLabel;
	}
	
	public String getAnswerString() {
		String answerStr = "";
		int prevIdx = -1;
		for (int i = 0; i < sentence.length; i++) {
			if (answerFlags[i] > 0) {
				answerStr +=  (i > prevIdx + 1 && prevIdx >= 0) ? " ... " : " ";
				answerStr += sentence.getTokenString(i);
				prevIdx = i;
			}
		}
		return answerStr.trim();
	}
	
	public String getQuestionString() {
		if (questionWords == null) {
			return "";
		}
		return StrUtils.join(" ", questionWords) + "?";
	}
	
	public String getPaddedQuestionString() {
		if (questionWords == null) {
			return "";
		}
		String qstr = "";
		for (String qw : questionWords) {
			qstr += (qw.isEmpty() ? "_" : qw.trim()) + "\t";
		}
		return qstr + "?";
	}
	
	public String toString() {
		String result = StrUtils.join(" ", questionWords);
		result += "\t [A]: " + getAnswerString();
		return result;
	}
}
