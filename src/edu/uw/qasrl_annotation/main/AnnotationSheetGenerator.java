package edu.uw.qasrl_annotation.main;

import java.io.IOException;
import java.util.ArrayList;

import edu.uw.qasrl_annotation.config.GenerationConfig;
import edu.uw.qasrl_annotation.data.Corpus;
import edu.uw.qasrl_annotation.data.TargetPredicate;
import edu.uw.qasrl_annotation.data.Sentence;
import edu.uw.qasrl_annotation.data.VerbInflectionDictionary;
import edu.uw.qasrl_annotation.io.XSSFOutputHelper;

public class AnnotationSheetGenerator {
	static GenerationConfig config = null;
	static Corpus corpus = null;
	static VerbInflectionDictionary inflDict;
	static ArrayList<Sentence> sentences = null;
	static ArrayList<ArrayList<TargetPredicate>> predicates = null;
	
	private static void loadData() {
		try {
			corpus.loadSentenceWithPredicates(config.inputFilePath);
			VerbInflectionDictionary inflDict =
					new VerbInflectionDictionary(corpus);
			inflDict.loadDictionaryFromFile(config.verbInflPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void generateAnnotationSheet() {
		try {
			XSSFOutputHelper.outputXlsx(corpus.sentences, corpus.predicates,
					inflDict, config.outputFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		config = new GenerationConfig(args);
		loadData();
		generateAnnotationSheet();
	}
}
