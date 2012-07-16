package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Removes punctuations and manages the labels after the removal
 */
public class RemovePunctuationFromPropTest {
	public static final String[] punctuation_array = { "\\", "\\*\\*", "\\*",
			"*", "!", "#", /* ",", */"`", "``", "\'\'", "\'", "(", ")", "{",
			"}", ".", "?", "-", ":", ";", "=", "@" };
	public static List<String> punctuations = new ArrayList<String>(
			Arrays.asList(punctuation_array));
	public static PrintWriter pw = null;

	public static void main(String[] args) throws IOException {
		args = new String[1];
		args[0] = "/work/conll05st-release/test.brown/test-brown-set";
		//args[0] = "/work/conll05st-release/test.wsj/test-wsj-set";
		int PROP_START_COL = 2; // starting with 0 index
		String input = args[0];

		int MAX_SENT_LENGTH = 200;
		int MAX_PROP_LENGTH = 50;

		String[][] sentenceProp = new String[MAX_SENT_LENGTH][MAX_PROP_LENGTH];
		String[][] sentencePropOut = new String[MAX_SENT_LENGTH][MAX_PROP_LENGTH];

		BufferedReader br = new BufferedReader(new FileReader(input));
		pw = new PrintWriter(input + ".nopunct");
		String line = "";
		int row = 0;
		int mincol = Integer.MAX_VALUE;
		int maxcol = Integer.MIN_VALUE;
		int linenumber = 0;
		int sentencenumber = 0;
		while ((line = br.readLine()) != null) {
			linenumber++;
			line = line.trim();
			if (!line.equals("")) {
				String[] splitted = line.split("\\s+");
				int cols = splitted.length;
				if (cols < mincol)
					mincol = cols;
				if (cols > maxcol)
					maxcol = cols;
				if (mincol != maxcol)
					System.err.println("Columns not of same length at line : "
							+ linenumber);
				// fill matrix
				for (int i = 0; i < cols; i++) {
					sentenceProp[row][i] = splitted[i];
				}
				row++;
			} else {
				// display(sentenceProp, row, maxcol);
				sentencenumber++;
				int out_index = 0;
				for (int j = 0; j < row; j++) {
					// first two columns
					if (!punctuations.contains(sentenceProp[j][0])) {
						for (int i = 0; i < PROP_START_COL; i++) {
							sentencePropOut[out_index][i] = sentenceProp[j][i];
						}
						out_index++;
					}
				}
				// System.out.println("Index : " + out_index);
				// display(sentencePropOut, out_index, maxcol);
				String prefix = ""; // if prefix is needed
				// System.out.println("PROP_START_COL, MAXCOL" + PROP_START_COL
				// + " " + maxcol);
				boolean continuing = false;
				for (int i = PROP_START_COL; i < maxcol; i++) {
					int prop_out_index = 0;
					for (int j = 0; j < row; j++) {
						if (!punctuations.contains(sentenceProp[j][0])) {
							if (sentenceProp[j][i].startsWith("(")) {
								continuing = true;
							}
							if (!continuing) {
								prefix = "";
							}
							sentencePropOut[prop_out_index][i] = prefix
									+ sentenceProp[j][i];
							prop_out_index++;
							prefix = "";
						} else {
							if (sentenceProp[j][i].endsWith(")")) {
								if (continuing) {
									if (prop_out_index > 0) {
										if (!sentencePropOut[prop_out_index - 1][i]
												.endsWith(")")) {
											sentencePropOut[prop_out_index - 1][i] += ")";
										}
									}
								}
								continuing = false;
							} else if (sentenceProp[j][i].startsWith("(")) {
								continuing = true;
								if (sentenceProp[j][i].endsWith(")")) {
									continuing = false;
									// no need to do anything
								} else {
									// move this to the next one
									prefix = sentenceProp[j][i];
									prefix = prefix.substring(0,
											prefix.length() - 1); // except the
																	// last *

								}
							}
						}
					}
				}
				print(sentencePropOut, out_index, maxcol);
				//display(sentencePropOut, out_index, maxcol);
				// reset
				row = 0;
				mincol = Integer.MAX_VALUE;
				maxcol = Integer.MIN_VALUE;
				sentenceProp = new String[MAX_SENT_LENGTH][MAX_PROP_LENGTH];
				sentencePropOut = new String[MAX_SENT_LENGTH][MAX_PROP_LENGTH];
			}
		}
		System.out.println("Sentences processed : " + sentencenumber);
		br.close();
		pw.close();
	}

	public static void print(String[][] sentence, int row, int col) {
		for (int i = 0; i < row; i++) {
			for(int j=0; j<col; j++){
			//for (int j = 4; j < col; j++) {//wsj-test
			//for (int j = 2; j < col; j++) {//brown-test
				pw.print(sentence[i][j] + spaces(10 - sentence[i][j].length())
						+ " ");
				// System.out.print(sentence[i][j] + "\t\t");
			}
			pw.println();
		}
		pw.println();
	}

	public static void display(String[][] sentence, int row, int col) {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				System.out.print(sentence[i][j]
						+ spaces(15 - sentence[i][j].length()) + " ");
				// System.out.print(sentence[i][j] + "\t\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	public static String spaces(int count) {
		String spaces = "";
		for (int i = 0; i < count; i++) {
			spaces += " ";
		}
		return spaces;
	}
}
