package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class IdentificationEvaluationDebug {
	public static void main(String[] args) throws IOException{
		if (args.length != 2) {
			System.err.println("Usage: <program> <gold_file_merged_with_predicted> <error_line_file>");
			System.exit(1);
		}
		ArrayList<Integer> errorLineList = new ArrayList<Integer>();
		ArrayList<Integer> blankLine = new ArrayList<Integer>();
		blankLine.add(0);
		
		BufferedReader errorLineFile = new BufferedReader(new FileReader(args[1]));
		PrintWriter errorSentences = new PrintWriter("/tmp/errorsentences.tmp");
		String line ="";
		while( (line = errorLineFile.readLine()) != null){
			errorLineList.add(Integer.parseInt(line.trim()));
		}
		
		BufferedReader bgoldBlank = new BufferedReader(new FileReader(args[0]));
		BufferedReader bgold = new BufferedReader(new FileReader(args[0]));
		int lineNumber=0;
		while( (line=bgoldBlank.readLine()) != null){
			lineNumber++;
			if(line.trim().equals("")){
				blankLine.add(lineNumber);
			}
		}
		
		Set<Integer> errorSentenceIndex = new HashSet<Integer>();
		int sentenceIndex = 0;
		for(int i=0; i< errorLineList.size(); i++){
			int errorLine = errorLineList.get(i);
			for(int j=0; j < blankLine.size() - 1; j++){
				if(errorLine >= blankLine.get(j) && errorLine <= blankLine.get(j+1)){
					errorSentenceIndex.add(j);
				}
			}
		}
		int sentenceNumber=0;
		String paragraph = "";
		while( (line=bgold.readLine()) != null){
			if(! line.trim().equals("") ){
				paragraph += line + "\n";
			} else {
				if(errorSentenceIndex.contains(sentenceNumber)){
					errorSentences.println(paragraph);
				}
				sentenceNumber++;
				paragraph = "";
			}
		}
		errorSentences.println();
		errorSentences.flush();
		errorSentences.close();
		bgold.close();
		bgoldBlank.close();
	}
}
