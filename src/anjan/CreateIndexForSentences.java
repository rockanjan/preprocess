package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CreateIndexForSentences {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/home/anjan/work/srl/jul19/deps/wsj15/combined.word.dep"));
		PrintWriter pw = new PrintWriter("/home/anjan/work/srl/jul19/deps/wsj15/combined.word.indexed.dep");
		
		String line = "";
		int indexNumber = 0;
		while ( (line = br.readLine()) != null ){
			line = line.trim();
			if(! line.isEmpty() ) {
				pw.println(indexNumber + "\t" + line);
				indexNumber++;
			} else {
				indexNumber = 0;
				pw.println();
			}
		}
		br.close();
		pw.close();
	}

}
