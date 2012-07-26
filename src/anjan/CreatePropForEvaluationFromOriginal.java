package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * Takes original file and converts it to prop file that can be used with srl-eval.pl
 */
public class CreatePropForEvaluationFromOriginal {
	public static void main(String[] args) throws IOException {
		int START_COL = 6; //zero indexed
		int SPACE_SIZE = 10;
		String inFile = "/home/anjan/work/conll05-backup/nopunct/train/train-set.nopunct" ;
		String outFile = inFile + ".eval.prop";
		
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		PrintWriter pw = new PrintWriter(outFile);
		
		String line = "";
		
		int MAX_SENT_LENGTH = 200;
		int MAX_PROP_LENGTH = 50;
		String[][] sentence = new String[MAX_SENT_LENGTH][MAX_PROP_LENGTH];
		int row = 0;
		int col = 0;
		while ( (line = br.readLine()) != null) {
			line = line.trim();
			
			if (! line.isEmpty() ) {
				String[] splitted = line.split("(\\s+|\\t+)");
				col = splitted.length;
				for(int i=0; i<col; i++) {
					sentence[row][i] = splitted[i];
				}
				row++;
			} else {
				if(col != 0) {
					for(int i=0; i<row; i++) {
						for(int j=START_COL-1; j<col; j++) {
							pw.print(sentence[i][j]);
							if(j != col) {
								 pw.print(spaces(SPACE_SIZE - sentence[i][j].length()) + " ");
							}
						}
						pw.println();
					}
					pw.println();
				}
				
				//reset
				col = 0;
				row = 0;
				for(int i=0; i<MAX_SENT_LENGTH; i++) {
					for(int j=0; j<MAX_PROP_LENGTH; j++) {
						sentence[i][j] = "";
					}
				}
			}
		}		
		br.close();
		pw.close();
		System.out.println("Done");
	}
	
	public static String spaces(int count){
		String spaces = "";
		for(int i=0; i<count; i++){
			spaces += " ";
		}
		return spaces;
	}
}
