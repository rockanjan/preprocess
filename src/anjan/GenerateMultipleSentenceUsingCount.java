package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class GenerateMultipleSentenceUsingCount {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if(args.length != 3){
			System.err.println("Usage: <program> inputFile countFile outputFile");
			System.exit(1);
		}
		File inSentenceFile = new File(args[0]);
		File inCountFile = new File(args[1]);
		File outFile = new File(args[2]);
		/*
		File inSentenceFile = new File("/work/conll05st-release/hmms/new125-150iter/combined10_train_test_only");
		File inCountFile = new File("/home/anjan/Dropbox/research/srl/dev/fei_data/basic/combined.basic.count");
		File outFile = new File("/work/conll05st-release/hmms/new125-150iter/combined10_train_test_only_multiple_sentence");
		*/
		BufferedReader readerSent = new BufferedReader(new FileReader(inSentenceFile));
		BufferedReader readerCount = new BufferedReader(new FileReader(inCountFile));
		BufferedReader readerCounter = new BufferedReader(new FileReader(inCountFile));
		PrintWriter out = new PrintWriter(outFile);
		
		String line = "";
		int count = 0;
		while( (line = readerCounter.readLine()) != null ){
			count++;
		}
		int distinctParagraphCount = count;
		System.out.println("Distinct ParagraphCounts " + distinctParagraphCount);
		int[] counter = new int[count + 1]; //for the last blank space if any
		counter[counter.length-1] = 0;
		count = 0;
		while( (line = readerCount.readLine()) != null ){
			counter[count] = Integer.parseInt(line);
			count++;
		}
		
		line = "";
		String paragraph = "";
		int sentenceCount = 0;
		while( (line = readerSent.readLine() ) != null ){
			//System.out.println(paragraph);
			if(line.trim().equals("")){
				for(int i=0; i < counter[sentenceCount]; i++){
					out.println(paragraph);
					out.flush();
				}
				paragraph = "";
				sentenceCount++;
				if(sentenceCount == distinctParagraphCount + 1){
					break;
				}
			}
			else{
				paragraph += line + "\n";
			}
		}
		readerSent.close();
		readerCount.close();
		out.close();
		System.out.println("Done!");

	}
}
