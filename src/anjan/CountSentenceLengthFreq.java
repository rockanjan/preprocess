package anjan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class CountSentenceLengthFreq {
	BufferedReader br;
	public CountSentenceLengthFreq(String inFilename) throws IOException {
		br = new BufferedReader(new InputStreamReader(new FileInputStream(
				inFilename)));
	}

	public void run() throws IOException {
		int[] lessAndGreaterThan10 = new int[2];
		String line = null;
		String paragraph = "";
		int paragraphLineCount = 0;
		int currentLine = 0;
		int total = 0;
		while ((line = br.readLine()) != null) {
			if (line.trim().equals("")) {
				if(! paragraph.isEmpty() ) {
					total++;
					if(paragraphLineCount <=10) {
						lessAndGreaterThan10[0]++;
					} else {
						lessAndGreaterThan10[1]++;
					}
					paragraph = "";
					paragraphLineCount = 0;
				}
			} else {
				paragraphLineCount++;
				paragraph += line + "\n";
			}
		}
		System.out.println("Total : " + total);
		System.out.println("Less than or equal 10 : " + lessAndGreaterThan10[0]);
		System.out.println("Greater than 10 : " + lessAndGreaterThan10[1]);
	}

	public void close() throws IOException {
		br.close();
	}

	public static void main(String[] args) throws IOException {
		String inFile = "/home/anjan/work/conll05-backup/devel/dev-set-words";
		CountSentenceLengthFreq cslf = new CountSentenceLengthFreq(inFile);
		cslf.run();
		cslf.close();
		System.out.println("Done!");
	}

}
