package anjan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class ReindexAlexData {
	BufferedReader br;
	PrintStream p;
	public ReindexAlexData(String inFilename, String outFilename) throws IOException{
		br = new BufferedReader(new InputStreamReader(new FileInputStream(inFilename)));
		FileOutputStream out = new FileOutputStream(outFilename);
		p = new PrintStream(out) ;
	}
	public void run() throws IOException{
		String line = null;
		String paragraph = "";
		int paragraphLineCount = 0;
		int currentLine = 0;
		while( (line = br.readLine()) != null){
			if(line.trim().equals("")){
				p.println(paragraph);
				p.flush();
				paragraph = "";
				paragraphLineCount = 0;
			}
			else{
				
				//IMPORTANT: For Alex's data, second column has the word
				String[] splitted = line.split("\t");
				String word = splitted[1];
				String naiveState = splitted[2];
				String dependency = splitted[3];
				paragraph += paragraphLineCount + "\t" + word + "\t" + naiveState + "\t" + (paragraphLineCount-1) + "\n";
				paragraphLineCount++;
			}
		}
	}
	
	public void close() throws IOException{
		br.close();
		p.close();
	}
	
	public static void main(String[] args) throws IOException{
		String inFile = "/home/anjan/Dropbox/research/dep/alex_combined40_aligned_no_extra";
		String outFile = "/home/anjan/Dropbox/research/dep/alex_combined40_aligned_no_extra_reindexed";
		//String inFile = "/work/dep/compare/fei_combined.txt";
		//String outFile = "/work/dep/compare/fei_combined40";
		System.out.println("In File " + inFile);
		System.out.println("Out File " + outFile);
		ReindexAlexData reindex = new ReindexAlexData(inFile, outFile);
		reindex.run();
		reindex.close();
		System.out.println("Done!");
	}
}
