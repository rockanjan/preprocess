package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProcessCommaBIO {
	public ProcessCommaBIO(){}
	final static int COL = 10; //count of cols
	final static int WORD_INDEX = 2; //index starts from 0
	final static int BIO_INDEX = 
								6;
								//COL - 2;
	final static int CLASS_INDEX = 
								7;
								//COL - 1;
	
	public static void main(String[] args) throws IOException{
		if(args.length != 2){
			System.err.println("Usage: <program> <inputfile> <outputfile>");
			System.exit(1);
		}
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		PrintWriter pw = new PrintWriter(args[1]);
		String[][] sentence = new String[200][COL];
		String line = "";
		int lineCount = 0;
		while ( (line = br.readLine() ) != null){
			if(! line.trim().equals("")) {
				
				String[] lineSplit = line.split("(\\s+|\\t+)");
				for(int i=0; i<lineSplit.length; i++){
					sentence[lineCount][i] = lineSplit[i];
				}
				lineCount++;
			} else {
				//do the processing
				//displaySentence(sentence, lineCount);
				for(int i=0; i<lineCount; i++){
					if(sentence[i][WORD_INDEX].equals(",") || sentence[i][WORD_INDEX].equals("--")){
						if(sentence[i][BIO_INDEX].equals("B")){
							//make this O
							sentence[i][BIO_INDEX] = "O";
							sentence[i][CLASS_INDEX] = "O";
							//if it follows I, make that the B
							if(i != lineCount - 1) { //if it is not the last word
								if(sentence[i+1][BIO_INDEX].equals("I")){
									sentence[i+1][BIO_INDEX] = "B";
								}
							}
						} else if(sentence[i][BIO_INDEX].equals("I")) {
							
							if(i == lineCount - 1) { //if it is the last word
								System.out.println("I AT THE LAST SENTENCE");
								sentence[i][BIO_INDEX] = "O";
								sentence[i][CLASS_INDEX] = "O";
							} else { //it is not the last word
								//if it has no continuing I, i.e. continuing is either B or O
								if(! sentence[i+1][BIO_INDEX].equals("I") ){
									//this is the last in the boundary
									//make this O
									sentence[i][BIO_INDEX] = "O";
									sentence[i][CLASS_INDEX] = "O";
								}
							}
						}
					}
					
				}
				//write the sentence
				writeSentence(pw, sentence, lineCount);
				lineCount = 0;
			}
		}
		pw.close();
		br.close();
		System.out.println("Done!");
	}
	
	public static void writeSentence(PrintWriter pw, String[][] sentence, int lineCount) throws IOException{
		int spaceSize = 10;
		for(int i=0; i<lineCount; i++){
			for(int j=0; j<COL; j++){
				pw.print(sentence[i][j]);
				if(j != COL - 1){
					 pw.print(spaces(spaceSize - sentence[i][j].length()) + " ");
				}
			}
			pw.println();
		}
		pw.println();
		pw.flush();
	}
	
	public static void displaySentence(String[][] sentence, int lineCount){
		for(int i=0; i<lineCount; i++){
			for(int j=0; j<COL; j++){
				System.out.print(sentence[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static String spaces(int count){
		String spaces = "";
		for(int i=0; i<count; i++){
			spaces += " ";
		}
		return spaces;
	}
}
