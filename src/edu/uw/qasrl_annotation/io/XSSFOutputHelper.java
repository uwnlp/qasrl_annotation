package edu.uw.qasrl_annotation.io;

import edu.uw.qasrl_annotation.annotation.QASlotAuxiliaryVerbs;
import edu.uw.qasrl_annotation.annotation.QASlotPlaceHolders;
import edu.uw.qasrl_annotation.annotation.QASlotPrepositions;
import edu.uw.qasrl_annotation.annotation.QASlotQuestionWords;
import edu.uw.qasrl_annotation.data.Sentence;
import edu.uw.qasrl_annotation.data.TargetPredicate;
import edu.uw.qasrl_annotation.data.VerbInflectionDictionary;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class XSSFOutputHelper {
	private static final String[] kAnnotationHeader = {
			"Annotation", "WH", "AUX",
			"PH1", "TRG", "PH2", "PP", "PH3", "?",
			"Answer1", "Answer2", "Answer3", "Answer4", "Answer5", "Note"};

	public static int maxNumSheetsPerFile = 10;
	public static int maxNumSentsPerSheet = 10;
	public static int maxNumQAs = 8;

	private static void finishSheet(XSSFSheet sheet,
	                                XSSFDataValidationHelper dvHelper,
	                                CellRangeAddressList whCells,
	                                CellRangeAddressList auxCells,
	                                CellRangeAddressList ph1Cells,
	                                CellRangeAddressList ph2Cells,
	                                CellRangeAddressList ph3Cells) {
		// Add WH, AUX, TRG, PH constraints.
		XSSFDataValidationConstraint
				whConstraint = (XSSFDataValidationConstraint)
				dvHelper.createExplicitListConstraint(QASlotQuestionWords.values),
				auxConstraint = (XSSFDataValidationConstraint)
						dvHelper.createExplicitListConstraint(QASlotAuxiliaryVerbs.values),
				phConstraint = (XSSFDataValidationConstraint)
						dvHelper.createExplicitListConstraint(QASlotPlaceHolders.values),
				ph3Constraint = (XSSFDataValidationConstraint)
						dvHelper.createExplicitListConstraint(QASlotPlaceHolders.ph3Values);

		XSSFDataValidation
				whVal = (XSSFDataValidation) dvHelper.createValidation(whConstraint, whCells),
				auxVal = (XSSFDataValidation) dvHelper.createValidation(auxConstraint, auxCells),
				ph1Val = (XSSFDataValidation) dvHelper.createValidation(phConstraint, ph1Cells),
				ph2Val = (XSSFDataValidation) dvHelper.createValidation(phConstraint, ph2Cells),
				ph3Val = (XSSFDataValidation) dvHelper.createValidation(ph3Constraint, ph3Cells);

		whVal.createErrorBox("Invalid input value", "See dropdown box for valid options.");
		whVal.setShowErrorBox(true);
		auxVal.createErrorBox("Invalid input value", "See dropdown box for valid options.");
		auxVal.setShowErrorBox(true);
		ph1Val.createErrorBox("Invalid input value", "See dropdown box for valid options.");
		ph1Val.setShowErrorBox(true);
		ph2Val.createErrorBox("Invalid input value", "See dropdown box for valid options.");
		ph2Val.setShowErrorBox(true);
		ph3Val.createErrorBox("Invalid input value", "See dropdown box for valid options.");
		ph3Val.setShowErrorBox(true);

		sheet.addValidationData(whVal);
		sheet.addValidationData(auxVal);
		sheet.addValidationData(ph1Val);
		sheet.addValidationData(ph2Val);
		sheet.addValidationData(ph3Val);

		sheet.setZoom(125);
		sheet.setDefaultColumnWidth(10);
	}

	public static void outputXlsx(
			ArrayList<Sentence> sentences,
			ArrayList<ArrayList<TargetPredicate>> predicates,
			VerbInflectionDictionary inflDict,
			String xlsxFileName) throws IOException {

		XSSFWorkbook workbook = new XSSFWorkbook();

		// Set editablity
		// sheet.protectSheet("password");
		XSSFCellStyle editableStyle = workbook.createCellStyle();
		DataFormat textFormat = workbook.createDataFormat();
		editableStyle.setDataFormat(textFormat.getFormat("@"));
		editableStyle.setLocked(false);

		// Set fonts cell styles
		XSSFFont headerFont = workbook.createFont(),
				commentFont = workbook.createFont(),
				highlightFont = workbook.createFont();

		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 10);
		commentFont.setColor(new XSSFColor(new java.awt.Color(0, 102, 204)));
		highlightFont.setBold(true);
		highlightFont.setColor(IndexedColors.RED.getIndex());

		XSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFont(headerFont);
		headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 220, 220)));

		XSSFCellStyle infoStyle = workbook.createCellStyle();
		infoStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		infoStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		infoStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(204, 255, 204)));

		XSSFCellStyle commentStyle = workbook.createCellStyle();
		commentStyle.setFont(commentFont);

		int numSentsOnCurrentSheet = 0,
				sheetCounter = 0,
				unitCounter = 0,
				rowCounter = 0;
		XSSFSheet sheet = null;
		XSSFDataValidationHelper dvHelper = null;
		CellRangeAddressList whCells = null,
				auxCells = null,
				ph1Cells = null,
				ph2Cells = null,
				ph3Cells = null;

		for (int i = 0; i < maxNumSheetsPerFile * maxNumSentsPerSheet; i++) {
			if (numSentsOnCurrentSheet == 0) {
				//Create a new blank sheet
				sheet = workbook.createSheet(
						String.format("batch_%d", sheetCounter++));
				dvHelper = new XSSFDataValidationHelper(sheet);
				whCells = new CellRangeAddressList();
				auxCells = new CellRangeAddressList();
				ph1Cells = new CellRangeAddressList();
				ph2Cells = new CellRangeAddressList();
				ph3Cells = new CellRangeAddressList();

				rowCounter = 0;
			}

			Sentence sent = sentences.get(i);
			ArrayList<TargetPredicate> props = predicates.get(i);
			if (props.size() == 0) {
				continue;
			}

			ArrayList<String> ppOptions = getPPOptions(sent);
			XSSFDataValidationConstraint ppConstraint =
					(XSSFDataValidationConstraint)
							dvHelper.createExplicitListConstraint(
									ppOptions.toArray(new String[ppOptions.size()]));

			for (int j = 0; j < props.size(); j++) {
				TargetPredicate prop = props.get(j);
				int propHead = prop.span[1] - 1;
				ArrayList<String> trgOptions = null;
				XSSFDataValidationConstraint trgConstraint = null;
				boolean wildcard = (propHead < 0);

				if (!wildcard) {
					trgOptions = getTrgOptions(sent, propHead, inflDict);
					if (trgOptions == null) {
						System.out.println("Error: unable to get inflection for verb: " +
								sent.getTokenString(propHead));
						continue;
					} else {
						trgConstraint = (XSSFDataValidationConstraint)
								dvHelper.createExplicitListConstraint(
										trgOptions.toArray(new String[trgOptions.size()]));
					}
				}

				// Write unit id and sentence ID
				Row row = sheet.createRow(rowCounter++);
				row.createCell(0).setCellValue(
						String.format("UNIT_%05d", unitCounter++));
				row.getCell(0).setCellStyle(headerStyle);

				// Write partially highlighted sentence
				row = sheet.createRow(rowCounter++);
				row.createCell(0).setCellValue(
						String.format("SENT_%05d", sent.sentenceID));
				XSSFRichTextString sentStr = new XSSFRichTextString("");
				if (wildcard) {
					sentStr.append(sent.getTokensString());
				} else {
					sentStr.append(sent.getTokenString(new int[]{0, prop.span[0]}) + " ");
					sentStr.append(sent.getTokenString(prop.span), highlightFont);
					sentStr.append(" " + sent.getTokenString(new int[]{prop.span[1], sent.length}));
				}
				row.createCell(1).setCellValue(sentStr);
				sheet.addMergedRegion(new CellRangeAddress(
						row.getRowNum(), row.getRowNum(), 1, 50));
				row.getCell(0).setCellStyle(headerStyle);
				row.getCell(1).setCellStyle(infoStyle);

				CellReference sentRef = new CellReference(row.getCell(1));

				// Write target word
				row = sheet.createRow(rowCounter++);
				if (wildcard) {
					row.createCell(0).setCellValue("TRG_unknown");
					row.createCell(1).setCellValue("");
				} else {
					row.createCell(0).setCellValue(String.format("TRG_%05d", prop.span[1] - 1));
					row.createCell(1).setCellValue(sent.getTokenString(prop.span));
				}
				row.getCell(0).setCellStyle(headerStyle);
				row.getCell(1).setCellStyle(infoStyle);

				// Write annotation header
				row = sheet.createRow(rowCounter++);
				for (int c = 0; c < kAnnotationHeader.length; c++) {
					Cell cell = row.createCell(c);
					cell.setCellValue(kAnnotationHeader[c]);
					cell.setCellStyle(headerStyle);
				}

				// Write QA slots
				for (int r = 0; r < maxNumQAs; r++) {
					row = sheet.createRow(rowCounter++);
					row.createCell(0).setCellValue("QA" + r);
					row.getCell(0).setCellStyle(headerStyle);
					for (int c = 1; c < kAnnotationHeader.length; c++) {
						Cell cell = row.createCell(c);
						cell.setCellStyle(editableStyle);
						cell.setCellType(Cell.CELL_TYPE_STRING);
					}

					CellRangeAddressList trgCells = new CellRangeAddressList(),
							ppCells = new CellRangeAddressList();

					int rn = row.getRowNum();
					whCells.addCellRangeAddress(rn, 1, rn, 1);
					auxCells.addCellRangeAddress(rn, 2, rn, 2);
					ph1Cells.addCellRangeAddress(rn, 3, rn, 3);
					trgCells.addCellRangeAddress(rn, 4, rn, 4);
					ph2Cells.addCellRangeAddress(rn, 5, rn, 5);
					ppCells.addCellRangeAddress(rn, 6, rn, 6);
					ph3Cells.addCellRangeAddress(rn, 7, rn, 7);

					for (int c = 9; c <= 13; c++) {
						CellRangeAddressList ansCells = new CellRangeAddressList();
						Cell ansCell = row.getCell(c);
						ansCells.addCellRangeAddress(rn, c, rn, c);
						CellReference ansRef = new CellReference(ansCell);
						XSSFDataValidation ansVal =
								(XSSFDataValidation) dvHelper.createValidation(
										(XSSFDataValidationConstraint) dvHelper.createCustomConstraint(
												String.format("=FIND(LOWER(TRIM(%s)), LOWER(%s))",
														ansRef.formatAsString(),
														sentRef.formatAsString())),
										ansCells);

						ansVal.createErrorBox("Error", "Only use words in the sentence for answer");
						ansVal.setErrorStyle(DataValidation.ErrorStyle.WARNING);
						ansVal.setShowErrorBox(true);
						sheet.addValidationData(ansVal);
					}
					Cell noteCell = row.getCell(14);
					noteCell.setCellStyle(commentStyle);

					// Unit-specific validations
					if (!wildcard) {
						XSSFDataValidation
								trgVal = (XSSFDataValidation) dvHelper.createValidation(
								trgConstraint, trgCells),
								ppVal = (XSSFDataValidation) dvHelper.createValidation(
										ppConstraint, ppCells);

						trgVal.createErrorBox("Invalid input value", "See dropdown box for valid options.");
						trgVal.setShowErrorBox(true);
						ppVal.createErrorBox("Invalid input value", "See dropdown box for valid options.");
						ppVal.setShowErrorBox(true);

						sheet.addValidationData(trgVal);
						sheet.addValidationData(ppVal);
					}
				}
				// Write separator .. whew
				row = sheet.createRow(rowCounter++);
			}

			numSentsOnCurrentSheet++;
			if (numSentsOnCurrentSheet == maxNumSentsPerSheet) {
				// Finish a sheet
				numSentsOnCurrentSheet = 0;
				finishSheet(sheet, dvHelper, whCells, auxCells, ph1Cells, ph2Cells, ph3Cells);
			}
			if (i >= sentences.size() - 1) {
				break;
			}
		}

		if (numSentsOnCurrentSheet > 0) {
			// there are sentences left in this sheet - finalize it
			finishSheet(sheet, dvHelper, whCells, auxCells, ph1Cells, ph2Cells, ph3Cells);
		}


		FileOutputStream outStream = new FileOutputStream(new File(xlsxFileName));
		workbook.write(outStream);
		outStream.close();
		workbook.close();
		System.out.println(String.format("Wrote %d units to file %s",
				unitCounter, xlsxFileName));
	}

	public static ArrayList<String> getTrgOptions(
			Sentence sent,
			int propHeadId,
			VerbInflectionDictionary inflDict) {
		String verb = sent.getTokenString(propHeadId).toLowerCase();
		String verbPrefix = "";
		if (verb.contains("-")) {
			int idx = verb.indexOf('-');
			verbPrefix = verb.substring(0, idx + 1);
			verb = verb.substring(idx + 1);
		}
		ArrayList<Integer> inflIds = inflDict.inflMap.get(verb);
		if (inflIds == null) {
			return null;
		}

		int bestId = -1, bestCount = -1;
		for (int i = 0; i < inflIds.size(); i++) {
			int count = inflDict.inflCount[inflIds.get(i)];
			if (count > bestCount) {
				bestId = inflIds.get(i);
				bestCount = count;
			}
		}
		// Generate list for dropdown.
		HashSet<String> opSet = new HashSet<String>();
		ArrayList<String> options = new ArrayList<String>();
		String[] inflections = new String[5];
		for (int i = 0; i < 5; i++) {
			inflections[i] = verbPrefix + inflDict.inflections.get(bestId)[i];
		}

		// boolean usePresentParticiple = (verb.toLowerCase().endsWith("ing"));	
		for (int i = 0; i < inflections.length; i++) {
			opSet.add(inflections[i]);
		}
		opSet.add("be " + inflections[4]);
		opSet.add("been " + inflections[4]);
		opSet.add("have " + inflections[4]);
		opSet.add("have been " + inflections[4]);

		opSet.add("being " + inflections[4]);
		opSet.add("be " + inflections[2]);
		opSet.add("been " + inflections[2]);
		opSet.add("have been " + inflections[2]);

		for (String op : opSet) {
			options.add(op);
		}

		Collections.sort(options);
		return options;
	}

	public static ArrayList<String> getPPOptions(Sentence sent) {
		HashSet<String> opSet = new HashSet<String>();
		ArrayList<String> options = new ArrayList<String>();
		for (int i = 0; i < sent.length; i++) {
			String tok = sent.getTokenString(i).toLowerCase();
			if (QASlotPrepositions.ppSet.contains(tok)) {
				opSet.add(tok);
				if (i < sent.length - 1) {
					String tok2 = sent.getTokenString(i + 1).toLowerCase();
					if (QASlotPrepositions.ppSet.contains(tok2)) {
						opSet.add(tok + " " + tok2);
						// System.out.println(sent.getTokensString());
						// System.out.println(tok + " " + tok2);
					}
				}
			}
		}
		for (String pp : QASlotPrepositions.mostFrequentPPs) {
			opSet.add(pp);
		}
		for (String op : opSet) {
			options.add(op);
		}
		Collections.sort(options);
		options.add(0, " ");
		return options;
	}
}
