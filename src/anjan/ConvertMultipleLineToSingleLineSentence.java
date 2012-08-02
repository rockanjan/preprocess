package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ConvertMultipleLineToSingleLineSentence {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader("/home/anjan/work/conll05-backup/nopunct/combined/combined.single.words"));
		String outFile = "/home/anjan/work/conll05-backup/nopunct/combined/combined.single.words.line";
		PrintWriter pw = new PrintWriter(outFile);
		String line = "";
		String sentence = "";
		while ( (line = br.readLine()) != null ) {
			line = line.trim();
			if(! line.isEmpty()) {
				sentence += line + " ";
			} else {
				pw.println(sentence);
				//pw.println();
				sentence = "";
			}
		}
		br.close();
		pw.close();
	}

}
