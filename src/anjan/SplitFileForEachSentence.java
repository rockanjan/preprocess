package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * Splits file into smaller ones for dependency tree induction
 */
public class SplitFileForEachSentence {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Invalid arguments, "
					+ "usage: <program> file");
			System.exit(1);
		}
		String outputPrefix = "combined.single.words-";
		String outputSuffix = ".txt";
		String inputFile = args[0];
		
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
		
		br = new BufferedReader(new FileReader(inputFile));
		blankStart = true;
		
		File f = new File(inputFile);
		int currentCount = 0;
		ArrayList<String> sentence = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (!line.equals("")) {
				sentence.add(line);
				blankStart = false;
			} else {
				if (!blankStart) {
					String outFileName = f.getParent() + "/" + outputPrefix + currentCount + outputSuffix;
					PrintWriter pw = new PrintWriter(outFileName);
					
					for(String word : sentence) {
						pw.println(word);
					}
					pw.println();
					pw.flush();
					pw.close();
					if(currentCount % 1000 == 0) {
						System.out.println("Wrote sentence " + currentCount);
					}
					currentCount++;
				}
			}
		}
	}
}
