package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/*
 * Counts the instances of each sentence in the file
 */
public class CountMultipleSentence {
	String inFilename;
	String outFilename;
	
	public CountMultipleSentence(String inFile, String outFile) {
		inFilename = inFile;
		outFilename = outFile;
	}
	
	public void run() throws IOException{
		File inFile = new File(inFilename);
		File outFile = new File(outFilename);
		
		BufferedReader br;
		PrintStream p;
		br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
		FileOutputStream out = new FileOutputStream(outFile);
		p = new PrintStream(out) ;
		
		//PrintStream pParagraph = new PrintStream(new FileOutputStream(new File("/tmp/combined_unique_para.mrg")));
			
		String line = null;
		String paragraph = "";
		int paragraphLineCount = 0;
		int currentLine = 0;
		String comparingParagraph = "";
		int count = 0;
		while( (line = br.readLine()) != null){
			currentLine ++;
			if(currentLine % 1000 == 0){
				System.out.println("Processed Line number : " + currentLine);
			}
			if(line.trim().equals("")){
				if(comparingParagraph.equals("")){
					comparingParagraph = paragraph;
					paragraph = "";
					count = 1;
				}
				else{
					if(comparingParagraph.equals(paragraph) && ! paragraph.isEmpty()){
						count++;
						paragraph = "";
					}
					else{
						if(! comparingParagraph.equals("")){
							//time to write
							p.println(count);
							p.flush();
							comparingParagraph = paragraph;
							paragraph = "";
							count = 1;
						}
					}
				}
				paragraph = "";
				//reset lineCount
				paragraphLineCount = 0;
			}
			else{
				paragraph += line.split("(\\s+|\\t+)")[0] + "\n";
				paragraphLineCount++;
			}
		}
		//at last, it has only updated the count not written it
		if(! comparingParagraph.isEmpty()) {
			p.println(count);
			p.flush();
		}
		br.close();
		p.close();
		System.out.println("Done!");
		
	}
	public static void main(String[] args) throws IOException
	{
		if(args.length != 2){
			System.err.println("Usage: <program> inputFile outputFile");
			System.exit(1);
		}
		CountMultipleSentence cms = new CountMultipleSentence(args[0], args[1]);
		cms.run();
	}
}
