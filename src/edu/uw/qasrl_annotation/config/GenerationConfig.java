package edu.uw.qasrl_annotation.config;

import java.io.PrintStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class GenerationConfig {
	@Option(name = "-verb-infl-path", usage="")
	public String verbInflPath = "./data/en_verb_inflections.txt";
	
	@Option(name = "-input", usage="")
	public String inputFilePath = "./sample/input.txt";
	
	@Option(name = "-output", usage="")
	public String outputFilePath = "./sample/output.xlsx";
	
	
	public GenerationConfig(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		parser.setUsageWidth(120);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			e.printStackTrace();
		}
	}
	
	public void print(PrintStream ostr)	{
		ostr.println("-verb-infl-path\t" + verbInflPath);
		ostr.println("-input-path\t" + inputFilePath);
		ostr.println("-output-path\t" + outputFilePath);
	}
}
