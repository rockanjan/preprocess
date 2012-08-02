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
 * Useful to remove punctuations for word-only-file
 */
public class RemovePunctuationsSaveComma {
	public static final int N = 50; // max length of sentence
	public static final String[] punctuation_array = { "\\", "\\*\\*", "\\*",
			"*", "!", "#", /*",",*/ "`", "``", "\'\'", "\'", "(", ")", "{", "}",
			".", "?", "-", ":", ";", "=", "@" };

	public static List<String> punctuations = new ArrayList<String>(
			Arrays.asList(punctuation_array));

	BufferedReader br;
	PrintStream p;

	public RemovePunctuationsSaveComma(String inFilename, String outFilename) throws IOException {
		br = new BufferedReader(new InputStreamReader(new FileInputStream(
				inFilename)));
		FileOutputStream out = new FileOutputStream(outFilename);
		p = new PrintStream(out);
	}

	public void run() throws IOException {
		String line = null;
		String paragraph = "";
		int paragraphLineCount = 0;
		int currentLine = 0;
		
		while ((line = br.readLine()) != null) {
			currentLine++;
			if (currentLine % 1000 == 0) {
				System.out.println("Processed Line number : " + currentLine);
			}
			if (line.trim().equals("")) {
				if (paragraphLineCount <= N && paragraphLineCount != 0) {
					p.println(paragraph);
					p.flush();
				}
				paragraph = "";
				paragraphLineCount = 0;
			} else {
				String word = line.trim();
				if (!punctuations.contains(word)) {
					//convert into lowercase
					//line = line.toLowerCase();
					paragraph += line + "\n";
					paragraphLineCount++;
				}
			}
		}
	}

	public void close() throws IOException {
		br.close();
		p.close();
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err
					.println("Invalid arguments, usage: <program> inFile outFile");
			System.exit(1);
		}
		String inFile = args[0];
		String outFile = args[1];
		System.out.println("In File " + inFile);
		System.out.println("Out File " + outFile);
		RemovePunctuationsSaveComma rp = new RemovePunctuationsSaveComma(inFile, outFile);
		rp.run();
		rp.close();
		System.out.println("Done!");
	}

}
