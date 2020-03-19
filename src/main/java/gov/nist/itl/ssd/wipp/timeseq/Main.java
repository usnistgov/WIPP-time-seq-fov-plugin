/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package gov.nist.itl.ssd.wipp.timeseq;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author Mohamed Ouladi <mohamed.ouladi@nist.gov>
 *
 */
public class Main {

	public static void main( String[] args ) {
				
		// sanity checks
		int i;
		System.out.println("argument length=" + args.length);
		for (i = 0; i < args.length; i++) {
			System.out.println("args[" + i + "]:" + args[i]);
		}	

		Options options = new Options();

		Option input = new Option("i", "input", true, "input folder");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option("o", "output", true, "output folder");
		output.setRequired(true);
		options.addOption(output);
		
		Option thresholdType = new Option("p", "filenamepattern", true, "file name pattern");
		thresholdType.setOptionalArg(true);
		options.addOption(thresholdType);
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("the required arguments", options);

			System.exit(1);
			return;
		}

		String inputFileDir = cmd.getOptionValue("input");
		String outputFileDir = cmd.getOptionValue("output");
		String fileNamePattern = cmd.getOptionValue("filenamepattern");

		File inputFolder = new File (inputFileDir);
		File outputFolder = new File (outputFileDir);
				
    	NoStitchingExecutor nse= new NoStitchingExecutor(inputFolder, fileNamePattern);
		try {
			nse.run(outputFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
