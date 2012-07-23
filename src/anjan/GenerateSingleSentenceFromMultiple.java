package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * Takes a file with multiple sentences and the count file and convert into a file with single sentence
 */
public class GenerateSingleSentenceFromMultiple {
	public static void main(String[] args) throws IOException{
		String inFile = "/home/anjan/work/srl/jul11/jdageem/combined.twohmm.before.feature.commabioprocessed.wordonly";
		String countFile = "/home/anjan/work/srl/jul11/jdageem/combined.count";
		String outFile = "/home/anjan/work/srl/jul11/jdageem/combined.single.wordonly";
		PrintWriter pw = new PrintWriter(outFile);
		
		BufferedReader brCount = new BufferedReader(new FileReader(countFile));
		ArrayList<Integer> counts = new ArrayList<Integer>();
		String line = "";
		while ( (line = brCount.readLine()) != null ){
			try {
				int countLine = Integer.parseInt(line.trim());
				counts.add(countLine);
			} catch(Exception e){
				System.err.println("Error parsing the count file!!!");
				System.exit(1);
			}
		}
		brCount.close();
		
		BufferedReader brInFile = new BufferedReader(new FileReader(inFile));
		ArrayList<String> sentence = new ArrayList<String>();
		int counter = 0;
		int singleSentenceNumber = 0;
		while ( (line = brInFile.readLine()) != null ) {
			line = line.trim();
			if(! line.equals("")) {
				sentence.add(line);
			} else {
				counter++;
				if(counter == 1){ //if it is the first time
					singleSentenceNumber++;
					for(String word : sentence){
						pw.println(word);
					}
					pw.println();
					pw.flush();
				}
				//reset counter if required
				int countFromFile = counts.get(singleSentenceNumber - 1);
				//System.out.println(countFromFile);
				if(countFromFile == counter){
					counter = 0;
				}
				sentence = new ArrayList<String>();
			}
		}
		brInFile.close();
		pw.close();
		System.out.println("Done");
	}
}
