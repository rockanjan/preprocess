package hmm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ConvertToIntegerSequenceUsingVocab {
	public static void main(String[] args) throws IOException{

		String vocabFile = "/home/anjan/Dropbox/vocab_index_final.txt";
		String inputFile = "/home/anjan/Dropbox/combined.single.words";
		String outputFile = "/home/anjan/Dropbox/combined-sequence.jahmm";
		
		VocabIndexReader.__init__(vocabFile);
		System.out.println("Vocab size: " + VocabIndexReader.vocabIndex.size());
		//start processing
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		PrintWriter pw = new PrintWriter(outputFile);
		String line = "";
		while( (line = br.readLine()) != null ){
			line = line.trim().toLowerCase();
			if(! line.equals("")){
				Integer index = VocabIndexReader.getIndex(line);
				if(index == null){
					System.err.println("WARNING: Vocab Index reader returned null index");
				}
				pw.print(index + ";");
			} else {
				pw.println();
			}
			pw.flush();
		}
		pw.close();
	}
}
