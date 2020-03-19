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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.StringBuilder;

/**
 * @author Mohamed Ouladi <mohamed.ouladi@nist.gov>
 *
 */
public class NoStitchingExecutor {

	private static final Logger LOG = Logger.getLogger(NoStitchingExecutor.class.getName());

	private final File folder;	
	private final String fileNamePattern;
	public static final String timePattern = "(.*)(\\{[t]+\\})(.*)";

	public static final String STITCHING_VECTOR_FILENAME_PREFIX = "img-global-positions-";
	public static final String STITCHING_VECTOR_FILENAME_SUFFIX = ".txt";


	public NoStitchingExecutor(File folder, String fileNamePattern){
		this.folder = folder;
		this.fileNamePattern = fileNamePattern;
	}

	public void run(File outputFile) throws IOException {
		File[] files = null;

		// if no pattern was specified, sort the files alphabetically
		// filter the files according to the pattern specified if there is any
		if(fileNamePattern == null || fileNamePattern.equals("")){
			files = folder.listFiles(File::isFile);
		} else {
			try {
				String pattern = getPattern(fileNamePattern, timePattern, false);
				Pattern p = Pattern.compile(pattern);
				files = folder.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File file, String name) {
						Matcher matcher = p.matcher(name);
						return matcher.find();
					}
				});
			}
			catch(IllegalArgumentException exp){
				throw new RuntimeException("File name pattern does not have a time pattern.");
			}
		}

		if (files == null) {
			throw new FileNotFoundException("Folder does not exist: " + folder);
		}

		Arrays.sort(files);

		int nbFiles = files.length;
		int nbDigits = Integer.toString(nbFiles).length();
		int cnt = 0;

		for (File img : files){
			cnt++;
			String fileName = String.format("%s%0" + nbDigits + "d%s",
					STITCHING_VECTOR_FILENAME_PREFIX,
					cnt,
					STITCHING_VECTOR_FILENAME_SUFFIX);
			File ts = new File(outputFile, fileName);

			try (BufferedWriter writer = Files.newBufferedWriter(
					ts.toPath(), Charset.forName("UTF-8"))) {

				StringBuilder sb = new StringBuilder();
				sb.append("file: ").append(img.getName()).append("; ");
				sb.append("corr: -1.0000000000; ");
				sb.append("position: (0, 0); ");
				sb.append("grid: (0, 0);");

				writer.write(sb.toString());
				writer.newLine();        
			}
			ts.createNewFile();
		}        
	}

	//copied from MIST - different return 
	public static String getPattern(String filePattern, String regex, boolean silent) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(filePattern);

		// Check if regex is correct. We expect 3 groups: (*)({ppp})(*)
		if (!matcher.find() || matcher.groupCount() != 3) {
			if (!silent) {
				LOG.info("Incorrect filePattern: " + filePattern);
				LOG.info("Regex: " + regex);
				LOG.info("Regex Groups count: " + matcher.groupCount());
				while (matcher.find()) {
					LOG.info(matcher.group());
				}
				throw new IllegalArgumentException("Incorect filePattern: " + filePattern);
			}
			return null;

		}

		// The matcher should find at group: 0 - the entire string,
		// group 1 = prefix
		// group 2 = {i}
		// group 3 = suffix
		String prefix = matcher.group(1);
		int iCount = matcher.group(2).length() - 2;
		String suffix = matcher.group(3);

		return prefix + "\\d{" + iCount + "}" + suffix;
	}

	// Error message handling
	// Might be incorporated later
	public List<Integer> runErrorChecksNoStitching(int timeSlices, File outputFolder){
		List<Integer> result = new ArrayList<>();
		String errorMessage = "Error report: PASSED";

		for(int i = 1; i <= timeSlices; i++){
			writeErrorMessage(i, errorMessage, outputFolder);
			result.add(i);
		}
		return result; 
	}

	public void writeErrorMessage(int timeSlice, String message, File outputFolder) {
		try {
			Files.write(
					new File(outputFolder, "checks-" + timeSlice + ".txt").toPath(),
					Arrays.asList(message),
					StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
