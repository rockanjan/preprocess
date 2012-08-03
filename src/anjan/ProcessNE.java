package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProcessNE {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String inFile = "/home/anjan/work/conll05st-release/combined.ne.nopunct.trunc50";
		
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		PrintWriter pw = new PrintWriter(inFile + ".neprocessed");
		
		String line = "";
		boolean continuing = false;
		String previous = "O";
		int number = 0;
		while ( (line = br.readLine()) != null ) {
			number++;
			line = line.trim();
			if(! line.isEmpty() ) {
				if(line.startsWith("(")) {
					String arg = "";
					if(line.endsWith(")")) {
						arg = line.substring(1, line.length()-2);
					} else {
						arg = line.substring(1, line.length()-1);
						continuing = true;
						previous = arg;
					}
					pw.println("B-" + arg);
				} else {
					if(continuing) {
						if(previous.equals("O")){
							System.err.println("Continue found when prev is O at line " + number);
							System.exit(1);
						}
						pw.println("I-" + previous);
					} else {
						pw.println("O");
					}
					if(line.endsWith(")")) {
						continuing = false;
					}
				}
			} else {
//				if(continuing) {
//					System.err.println("NER does not close at line " + number);
//					System.exit(1);
//				}
				continuing = false;
				pw.flush();
				pw.println();
			}
		}
		br.close();
		pw.close();
	}
}
