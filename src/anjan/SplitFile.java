package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * Splits file into smaller ones for dependency tree induction
 * If too many files (>4000) specified, linux gives an error (too many files open)
 */
public class SplitFile {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Invalid arguments, "
					+ "usage: <program> file size");
			System.exit(1);
		}
		String outputPrefix = "combined.single.words-";
		String outputSuffix = ".txt";
		String inputFile = args[0];
		int splitSize = -1;
		try {
			splitSize = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Incorrect split size : " + args[1]);
			System.exit(1);
		}

		BufferedReader br = new BufferedReader(new FileReader(inputFile));

		String line = "";
		int totalSentences = 0;
		boolean blankStart = true;
		while ((line = br.readLine()) != null) {
			if (!line.trim().equals("")) {
				blankStart = false;
			} else {
				if (!blankStart) {
					totalSentences++;
				}
				blankStart = true;
			}
		}
		if (!blankStart) { // EOF does not have a blank line
			totalSentences++;
		}
		System.out.println("Sentence Count: " + totalSentences);
		br.close();
		int divisionSize = (int) Math.floor(totalSentences / splitSize);
		System.out.println("Division Size " + divisionSize);
		br = new BufferedReader(new FileReader(inputFile));
		int currentCount = 0;
		blankStart = true;

		File f = new File(inputFile);
		PrintWriter[] out = new PrintWriter[splitSize];
		for (int i = 0; i < splitSize; i++) {
			out[i] = new PrintWriter(f.getParentFile() + "/" + outputPrefix
					+ (i + 1) + outputSuffix);
		}
		PrintWriter current = out[0];
		
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (!line.equals("")) {
				current.println(line);
				blankStart = false;
			} else {
				if (!blankStart) {
					current.println();
					int fileIndex = (int) currentCount / divisionSize;
					if (fileIndex == splitSize) {
						fileIndex = fileIndex - 1;
					}
					currentCount++;
					current = out[fileIndex];
				}
				blankStart = true;
			}
		}
		if (!blankStart) {
			current.println();
		}
		for (int i = 0; i < splitSize; i++) {
			out[i].flush();
			out[i].close();
		}
	}
}
