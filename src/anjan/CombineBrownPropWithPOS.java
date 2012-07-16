package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CombineBrownPropWithPOS {
	public static void main(String[] args) throws IOException {
		String brownFile = "/work/conll05st-release/test.brown/test.brown";
		String posFile = "/work/conll05st-release/test.brown/synt.cha/test.brown.synt.cha";
		BufferedReader brMain = new BufferedReader(new FileReader(brownFile));
		BufferedReader brPos = new BufferedReader(new FileReader(posFile));
		String out = "/work/conll05st-release/test.brown/test-brown-set";
		PrintWriter pw = new PrintWriter(out);
		String mainLine = "";
		String posLine = "";
		// read both files side by side
		int lineNumber = 0;
		while ((mainLine = brMain.readLine()) != null
				&& (posLine = brPos.readLine()) != null) {
			lineNumber++;
			mainLine = mainLine.trim();
			posLine = posLine.trim();
			if (!mainLine.equals("")) {
				if (!posLine.equals("")) {
					String[] mainSplitted = mainLine.split("\\s+");
					String[] posSplitted = posLine.split("\\s+");
					pw.print(mainSplitted[0] + "\t" + posSplitted[0] + "\t");
					for (int i = 1; i < mainSplitted.length; i++) {
						pw.print(mainSplitted[i]);
						if (i != mainSplitted.length) {
							pw.print("\t");
						} else {
							pw.println();
						}
					}
				} else {
					System.err
							.println("Error: main and pos file not aligned at line : "
									+ lineNumber);
					System.exit(1);
				}
				pw.println();
			} else {
				if (!posLine.equals("")) {
					System.err
							.println("Error: main and pos file not aligned at line : "
									+ lineNumber);
					System.exit(1);
				}
				pw.println();
			}
		}
		brMain.close();
		brPos.close();
		pw.close();
		System.out.println("Done");
	}
}
