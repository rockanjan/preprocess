package anjan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Counts the maximum length of sentences in a file
 */
public class CountMaxSentenceLength {

	BufferedReader br;

	public CountMaxSentenceLength(String inFilename) throws IOException {
		br = new BufferedReader(new InputStreamReader(new FileInputStream(
				inFilename)));
	}

	public void run() throws IOException {
		int max = -1;
		String line = null;
		String paragraph = "";
		int paragraphLineCount = 0;
		int currentLine = 0;
		while ((line = br.readLine()) != null) {
			if (line.trim().equals("")) {
				max = Math.max(paragraphLineCount, max);
				paragraph = "";
				paragraphLineCount = 0;
			} else {
				String[] splitted = line.split(" ");
				String word = splitted[0];
				paragraph += line + "\n";
				paragraphLineCount++;
			}
		}
		System.out.println("Max Length of sentence = " + max);
	}

	public void close() throws IOException {
		br.close();
	}

	public static void main(String[] args) throws IOException {
		String inFile = "/home/anjan/Dropbox/research/srl/dev/evaluation/combined.word.pos.prop";
		System.out.println("In File " + inFile);
		CountMaxSentenceLength rp = new CountMaxSentenceLength(inFile);
		rp.run();
		rp.close();
		System.out.println("Done!");
	}

}
