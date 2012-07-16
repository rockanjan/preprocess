package anjan;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProcessProp {
	static int spaceSize = 5;
	public static void main(String[] args) throws IOException {
		if(args.length != 2){
			System.err.println("Usage: <program> inputFile propstartcol");
			System.err.println("NOTE:Output will be inputFile.propprocessed");
			System.exit(1);
		}
		int PROP_START_COL = 0; //for test 5, for train and dev 6. train and dev have extra column which includes numbers
		String input = args[0];
		try{
			PROP_START_COL = Integer.parseInt(args[1]);
		} catch(NumberFormatException nfe){
			System.err.println("Invalid propstartcol, should be integer, found: " + args[1]);
			System.exit(1);
		}
		int MAX_SENT_LENGTH = 200;
		int MAX_PROP_LENGTH = 50;
		
		int OUT_COL = 6; // word, pos, verbOrNot, interveningVerbs, BIO, label

		String[][] sentenceProp = new String[MAX_SENT_LENGTH][MAX_PROP_LENGTH];
		BufferedReader br = new BufferedReader(new FileReader(input));
		PrintWriter pw = new PrintWriter(
				input + ".propprocessed");
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
				String[] splitted = line.split("(\\s+)|(\\t+)");
				int cols = splitted.length;
				if (cols < mincol)
					mincol = cols;
				if (cols > maxcol)
					maxcol = cols;
				if (mincol != maxcol)
					System.err.println("Columns not of same length at line : "
							+ linenumber);
				// System.out.println("Splitted into :" + splitted.length +
				// splitted[6]);
				// fill matrix
				for (int i = 0; i < cols; i++) {
					sentenceProp[row][i] = splitted[i];
				}
				row++;
			} else {
				sentencenumber++;
				//System.out.println("Max Col : " + maxcol);
				int verbCount = maxcol - PROP_START_COL;
				int[] verbIndex = null;
				if (verbCount > 0)
					verbIndex = new int[verbCount];
				// int index=0;
				for (int j = PROP_START_COL; j < maxcol; j++) {
					for (int i = 0; i < row; i++) {
						if (sentenceProp[i][j].contains("(V*")) {
							verbIndex[j - PROP_START_COL] = i;
						}
					}
				}

				// process sentence
				String[][][] outSentence = null;
				if (verbIndex != null) {
					outSentence = new String[verbCount][row][OUT_COL];
					int outSentenceNumber = 0;
					for (int j = 0; j < maxcol - PROP_START_COL; j++) {
						String continuingLabel = "O";
						String verbOrNot = "O"; //can have VI label as well (continuing verb)
						String interveningVerbs = "";
						String bio = "O";
						String label = "O";
						int currentVerbIndex = verbIndex[outSentenceNumber];
						for (int k = 0; k < row; k++) {
							/***** Intervening verb count starts *****/
							int interveningVerbCount = 0;
							if (k < currentVerbIndex) {
								for (int z = k + 1; z < currentVerbIndex; z++) {
									for (int index : verbIndex) {
										if (z == index) {
											interveningVerbCount++;
										}
									}
								}
							} else {
								for (int z = currentVerbIndex + 1; z < k; z++) {
									for (int index : verbIndex) {
										if (z == index) {
											interveningVerbCount++;
										}
									}
								}
							}
							interveningVerbs = interveningVerbCount + "";
							if(k == currentVerbIndex){
								interveningVerbs = "V";
							}
							/***** Intervening verb count completes *****/

							String currentLabel = sentenceProp[k][j
									+ PROP_START_COL];
							outSentence[outSentenceNumber][k][0] = sentenceProp[k][0];
							if (currentLabel.equals("*")) {// continuing
								if (continuingLabel.equals("V") || continuingLabel.equals("VI")) {
									verbOrNot = "VI";
									bio = "O";
									label = "O";
									// interveningVerbs = 0;
								} else {
									verbOrNot = "O";
									label = continuingLabel;
									bio = "I";
									if (continuingLabel.equals("O")) {
										bio = "O";
										label = "O";
									}
								}
							}
							else if (currentLabel.equals("*)")) {
								if (continuingLabel.equals("V") || continuingLabel.equals("C-V")) {
									if(continuingLabel.equals("V")){
										verbOrNot = "VI";
										bio = "O";
										label = "O";
									} else {
										verbOrNot = "VI";
										bio = "I";
										label = "C-V";
									}
								} else {
									verbOrNot = "O";
									bio = "I";
									label = continuingLabel;
								}
								continuingLabel = "O"; // stop continuing

							} else if (currentLabel.startsWith("(")) {
								if (currentLabel.endsWith(")")) {
									// starts and ends at same place
									label = currentLabel.substring(1,
											currentLabel.length() - 2);
									if (label.equals("V")) {
										verbOrNot = "V";
										bio = "O";
										label = "O";
									} else {
										verbOrNot = "O";
										bio = "B";
									}
									continuingLabel = "O";
								} else { // will be continuing
									label = currentLabel.substring(1,
											currentLabel.length() - 1);
									continuingLabel = label;
									if (label.equals("V")) {
										verbOrNot = "V";
										bio = "O";
										label = "O";
									} else {
										verbOrNot = "O";
										bio = "B";
									}
								}
							} else {
								System.err.println("SHOULD NOT BE HERE!!! label processing error, found: " + currentLabel);
								System.err.println("line: " + line);
								System.err.println("Line number: " + linenumber);
								
							}
							outSentence[outSentenceNumber][k][0] = sentenceProp[k][0];
							outSentence[outSentenceNumber][k][1] = sentenceProp[k][1];
							
							outSentence[outSentenceNumber][k][2] = verbOrNot;
							outSentence[outSentenceNumber][k][3] = interveningVerbs;
							outSentence[outSentenceNumber][k][4] = bio;
							outSentence[outSentenceNumber][k][5] = label;
						}
						outSentenceNumber++;
					}
				} else {
					outSentence = new String[1][row][OUT_COL];
					for (int k = 0; k < row; k++) {
						outSentence[0][k][0] = sentenceProp[k][0];
						outSentence[0][k][1] = sentenceProp[k][1];
						outSentence[0][k][2] = "O";
						outSentence[0][k][3] = "-1"; //shows no verb in this sentence
						outSentence[0][k][4] = "O";
						outSentence[0][k][5] = "O";
					}

				}

				if (verbCount != 0) {
					for (int i = 0; i < verbCount; i++) {
						for (int j = 0; j < row; j++) {
							for (int k = 0; k < OUT_COL; k++) {
								String current = outSentence[i][j][k]; 
								pw.print(current);
								if(k == 0){
									pw.print(spaces(15 - current.length()) + " ");
								}
								else if (k != OUT_COL - 1) {
									pw.print(spaces(spaceSize - current.length()) + " ");
								}
							}
							// System.out.println();
							pw.println();
						}
						pw.println();
					}
				} else {
					for (int j = 0; j < row; j++) {
						for (int k = 0; k < OUT_COL; k++) {
							String current = outSentence[0][j][k];
							pw.print(current);
							if(k == 0){
								pw.print(spaces(15 - current.length()) + " ");
							}
							else if (k != OUT_COL - 1) {
								pw.print(spaces(spaceSize - current.length()) + " ");
							}
						}
						pw.println();
					}
					pw.println();
				}
				pw.flush();
				// reset
				sentenceProp = new String[MAX_SENT_LENGTH][MAX_PROP_LENGTH];
				row = 0;
				mincol = Integer.MAX_VALUE;
				maxcol = Integer.MIN_VALUE;
			}
		}
		System.out.println("Total Sentences processed: " + (sentencenumber - 1));
		br.close();
		pw.flush();
		pw.close();
		System.out.println("Complete!");
	}
	
	public static String spaces(int count){
		String spaces = "";
		for(int i=0; i<count; i++){
			spaces += " ";
		}
		return spaces;
	}
}
