package edu.uw.qasrl_annotation.config;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.PrintStream;

public class GenerationConfig {
	@Option(name = "-verb-infl-path", usage = "")
	public String verbInflPath = "./data/en_verb_inflections.txt";

	@Option(name = "-input", usage = "")
	public String inputFilePath = "./sample/input.txt";

	@Option(name = "-output", usage = "")
	public String outputFilePath = "./sample/output.xlsx";

	@Option(name = "-nsheets", usage = "Max. number of sheets per file.")
	public int maxNumSheetsPerFile = 10;

	@Option(name = "-nsents", usage = "Max. number of sentences per sheet.")
	public int maxNumSentsPerSheet = 10;

	@Option(name = "-nqas", usage = "Max. number of Q/A pairs per sentence.")
	public int maxNumQAs = 8;

	public GenerationConfig(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		parser.setUsageWidth(120);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			e.printStackTrace();
		}
	}

	public void print(PrintStream ostr) {
		ostr.println("============ CONFIG ============");
		ostr.println("-verb-infl-path\t" + verbInflPath);
		ostr.println("-input-path\t" + inputFilePath);
		ostr.println("-output-path\t" + outputFilePath);
		ostr.println("-nsheets\t" + maxNumSheetsPerFile);
		ostr.println("-nsents\t" + maxNumSentsPerSheet);
		ostr.println("-nqas\t" + maxNumQAs + "\n");
	}
}
