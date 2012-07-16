package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * Splits file into smaller ones for dependency tree induction
 */
public class SplitFile {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Invalid arguments, "
					+ "usage: <program> file numberofsplits");
			System.exit(1);
		}
		String outputPrefix = "wsj10-train_";
		String outputSuffix = ".txt";
		String inputFile = args[0];
		int splitSize = -1;
		try {
			splitSize = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Incorrect split size : " + args[1]);
			System.exit(1);
		}
		File f = new File(inputFile);
		PrintWriter[] out = new PrintWriter[splitSize];
		for (int i = 0; i < splitSize; i++) {
			out[i] = new PrintWriter(f.getParentFile() + "/" + outputPrefix
					+ (i + 1) + outputSuffix);
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
		PrintWriter current = out[0];
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (!line.equals("")) {
				current.println(line);
				blankStart = false;
			} else {
				if (!blankStart) {
					current.println();
					// int fileIndex = (int) Math.ceil(currentCount /
					// divisionSize) - 1;
					int fileIndex = (int) currentCount / divisionSize;
					if (fileIndex == splitSize) {
						fileIndex = fileIndex - 1;
					}
					System.out.println("Current count: " + currentCount);
					System.out.println("File Index : " + fileIndex);
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
