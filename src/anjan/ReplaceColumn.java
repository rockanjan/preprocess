package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * Reads two files. Replaces a column of the first with the column from the second
 */
public class ReplaceColumn {
	public static void main(String[] args) throws IOException {
		int SPACE_SIZE = 12;
		int COL_NUM = 2;
		String orgFile = "/home/anjan/Dropbox/research/srl/combined.final.commaBIOprocessed.newhmm";
		String colFile = "/home/anjan/work/conll05-backup/punct/combined/combined.alex.multiple.dep";
		String outFile = "/home/anjan/Dropbox/research/srl/combined.final.commaBIOprocessed.newhmm.newdep";
		
		PrintWriter pw = new PrintWriter(outFile);
		BufferedReader brOrg = new BufferedReader(new FileReader(orgFile));
		BufferedReader brCol = new BufferedReader(new FileReader(colFile));
		//TODO assert they have same length
		String lineOrg = "";
		String lineCol = "";
		int lineNumber = 0;
		while( (lineOrg = brOrg.readLine()) != null && (lineCol = brCol.readLine()) != null ) {
			lineNumber++;
			lineOrg = lineOrg.trim();
			lineCol = lineCol.trim();
			if(lineOrg.equals("")) {
				if(! lineCol.equals("")) {
					System.err.println("Sentences not aligned at line : " + lineNumber);
					System.exit(1);
				}
				pw.println();
			} else {
				String[] splitted = lineOrg.split("(\\s+|\\t+)");
				for(int i=0; i<splitted.length; i++) {
					if(COL_NUM - 1 == i) {
						pw.print(lineCol);
					} else {
						pw.print(splitted[i]);
					}
					
					if(i != splitted.length -1) {
						pw.print(spaces(SPACE_SIZE - splitted[i].length()) + " ");
					} else {
						pw.println();
					}
				}
				pw.flush();
			}
		}
		pw.close();
		brOrg.close();
		brCol.close();
		System.out.println("Done");
	}
	
	public static String spaces(int size) {
		String space = "";
		for(int i=0; i<size; i++){
			space += " ";
		}
		return space;
	}
}
