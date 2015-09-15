package edu.uw.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.uw.annotation.QuestionEncoder;
import edu.uw.data.AnnotatedSentence;
import edu.uw.data.Corpus;
import edu.uw.data.QAPair;
import edu.uw.data.Sentence;

public class XSSFDataRetriever {
	
	private static int getHeaderId(String header) {
		if (!header.contains("_")) {
			return -1;
		}
		return Integer.parseInt(header.substring(header.indexOf('_') + 1));
	}
	
	public static void readXSSFAnnotation(
			String[] inputFiles,
			Corpus corpus,
			HashMap<Integer, AnnotatedSentence> annotations)
					throws FileNotFoundException, IOException {
		// Map sentence ids to a set of AnnotatedSentence
		assert (annotations != null);
		HashMap<Integer, Integer> sentIdMap = new HashMap<Integer, Integer>();
		int numTotalProps = 0, numNonEmptyProps = 0;
		
		for (String inputFile : inputFiles) {
			XSSFWorkbook workbook = new XSSFWorkbook(
					new FileInputStream(new File(inputFile)));
			int unitId = -1, sentId = -1, propHead = -1;
			Sentence sent = null;
			AnnotatedSentence currSent = null;
			ArrayList<QAPair> qaList = new ArrayList<QAPair>();
			
			int numSentsPerFile = 0, numQAsPerFile = 0;
			for (int sn = 0; sn < workbook.getNumberOfSheets(); sn++) {
				XSSFSheet sheet = workbook.getSheetAt(sn);
				for (int r = 0; r <= sheet.getLastRowNum(); r++) {
					XSSFRow row = sheet.getRow(r);
		        	if (row == null || row.getLastCellNum() == 0 ||
		        		row.getCell(0) == null) {
		        		continue;
		        	}
		        	String header = row.getCell(0).getStringCellValue();
		        	if (header.startsWith("UNIT")) {
		        		++ numTotalProps;
		        		if (unitId > -1  && !qaList.isEmpty()) {
		        			// Process previous unit.
		        			currSent.addProposition(propHead);
		        			for (QAPair qa : qaList) {
		        				currSent.addQAPair(propHead, qa);
		        			}
		        			qaList.clear();
		        			++ numNonEmptyProps;
		        		}
		        		unitId = getHeaderId(header);
		        	} else if (header.startsWith("SENT")) {
		        		if (sentId != getHeaderId(header)) {
		        			// Encountering a new sentence.
			        		sentId = getHeaderId(header);
			        		// A hacky way to process Wikipedia data.
			        		if (corpus.sentences.size() <= sentId) {
			        			if (sentIdMap.containsKey(sentId)) {
			        				sentId = sentIdMap.get(sentId);
			        			} else {
			        				String sentStr = row.getCell(1).toString().trim();
			        				sent = corpus.addNewSentence(sentStr);
				        			sentIdMap.put(sentId, sent.sentenceID);
				        			sentId = sent.sentenceID;
			        			}
			        		}
			        		sent = corpus.getSentence(sentId);
			        		if (!annotations.containsKey(sentId)) {
			        			annotations.put(sentId, new AnnotatedSentence(sent));
			        			numSentsPerFile ++;
			        		}
			        		currSent = annotations.get(sentId);
		        		}
		        	} else if (header.startsWith("TRG")) {
		        		propHead = getHeaderId(header);		        		
		        	} 
		        	if (!header.startsWith("QA") ||
		        		row.getCell(1) == null ||
		        		row.getCell(1).toString().isEmpty()) {
		        		continue;
		        	}
		        	String[] question = new String[7];
		        	for (int c = 1; c <= 7; c++) {
		        		if (row.getCell(c) == null) {
		        			question[c-1] = "";
		        			continue;
		        		} else {
		        			question[c-1] =
		        				row.getCell(c).getStringCellValue().trim();     		
		        		}
		        	}
		        	// Normalizing question:
		        	//   If ph3 in {someone, something}, and ph2=null, pp=null,
		        	//   ph3 is moved to ph2
		        	QuestionEncoder.normalize(question);
		        	QAPair qa = new QAPair(
		        			sent,
		        			propHead,
		        			question,
		        			"" /* answer */,
		        			inputFile /* annotator source */);
		        	for (int c = 9; c <= 13; c++) {
		        		if (row.getCell(c) == null) {
		        			continue;
		        		}
		        		String ans = row.getCell(c).toString();
		        		if (!ans.isEmpty()) {
		        			qa.addAnswer(ans);
		        		}
		        	}
		        	if (row.getCell(14) != null) {
		        		qa.comment = row.getCell(14).getStringCellValue().trim();
		        	}
		        	if (!question[0].isEmpty() && !question[3].isEmpty() &&
		        		!qa.getAnswerString().isEmpty()) {
		        		qaList.add(qa);
		        		numQAsPerFile ++;
		        	}
		        }
//				System.out.println(sheet.getSheetName() + " ... " + annotations.size());
			 }			
			workbook.close();
			System.out.println(String.format(
					"Read %d sentences and %d QAs from %s.",
						numSentsPerFile, numQAsPerFile, inputFile));
		}
		System.out.println(String.format(
				"Total propositons: %d. Skipped %d empty propositions.",
					numTotalProps, numTotalProps - numNonEmptyProps));
	}
	
	private static int[] getSortedKeys(Collection<Integer> keys) {
		int[] sortedKeys = new int[keys.size()];
		int sn = 0;
		for (int k : keys) {
			sortedKeys[sn++] = k;
		}
		Arrays.sort(sortedKeys);
		return sortedKeys;
 	}
	
	public static void outputAnnotations(String outputPath,
			Corpus baseCorpus,
			HashMap<Integer, AnnotatedSentence> annotations) throws IOException {
		// Get sentence ids and sort.
		int[] sentIds = getSortedKeys(annotations.keySet());
		
		// Output to text file.
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
		int numSentsWritten = 0,
			numPropsWritten = 0,
			numQAsWritten = 0;
		for (int sid : sentIds) {
			AnnotatedSentence annotSent = annotations.get(sid);
			Sentence sent = annotSent.sentence;
			
			// Filter empty sentences.
			int[] propIds = getSortedKeys(annotSent.qaLists.keySet());
			int numProps = 0;
			for (int propHead : propIds) {
				if (annotSent.qaLists.get(propHead).size() > 0) {
					numProps ++;
				}
			}
			if (numProps == 0) {
				continue;
			}
			// Write sentence info.
			writer.write(String.format("%s_%d\t%d\n",
					baseCorpus.corpusName, sid, numProps));
			writer.write(sent.getTokensString() + "\n");
			for (int propHead : propIds) {
				String prop = sent.getTokenString(propHead).toLowerCase();
				ArrayList<QAPair> qaList = annotSent.qaLists.get(propHead);
				if (qaList.size() == 0) {
					continue;
				}
				writer.write(String.format("%d\t%s\t%d\n",
						propHead, prop, qaList.size()));
				for (QAPair qa : qaList) {
					writer.write(qa.getPaddedQuestionString() + "\t");
					for (int i = 0; i < qa.answers.size(); i++) {
						writer.write((i > 0 ? " ### " : "") + qa.answers.get(i).trim());
					}
					writer.write("\n");
				}
				numQAsWritten += qaList.size();
			}
			writer.write("\n");
			numSentsWritten ++;
			numPropsWritten += numProps;
		}
		writer.close();
		System.out.println(String.format(
				"Skipped %d empty sentences. " +
				"Successfully wrote %d sentences, %d proposition and %d QAs to %s.",
				sentIds.length - numSentsWritten,
				numSentsWritten,
				numPropsWritten,
				numQAsWritten,
				outputPath));
	}
}
