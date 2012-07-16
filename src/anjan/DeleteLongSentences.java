package anjan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class DeleteLongSentences {
	public static int N = 50; // max length of sentence
	BufferedReader br;
	PrintStream p;

	public DeleteLongSentences(String inFilename, String outFilename, int length) throws IOException {
		this.N = length;
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
				paragraphLineCount++;
				paragraph += line + "\n";
			}
		}
	}

	public void close() throws IOException {
		br.close();
		p.close();
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.err
					.println("Invalid arguments, usage: <program> inFile outFile length");
			System.exit(1);
		}
		String inFile = args[0];
		String outFile = args[1];
		int length = Integer.parseInt(args[2]);
		System.out.println("In File " + inFile);
		System.out.println("Out File " + outFile);
		System.out.println("Length " + length);
		DeleteLongSentences rp = new DeleteLongSentences(inFile, outFile, length);
		rp.run();
		rp.close();
		System.out.println("Done!");
	}

}
