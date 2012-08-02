package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * Takes in a file with word POS V/O/VI intervene BIO Arg-class and converts it to prop file
 * POS, intervene won't be used
 */
public class CreateProp {
	static int LEN = 200;
	public static void main(String[] args) throws IOException{
		int spaceSize = 10;
		if(args.length != 2){
			System.err.println("Usage: <program> input-file org-eval-prop-file"); //org-eval-prop-file needed for verbs
			System.exit(1);
		}
		//count of each sentence
		String countFile = "/tmp/count";
		CountMultipleSentence cms = new CountMultipleSentence(args[0], countFile);
		
		cms.run();
		
		BufferedReader countReader = new BufferedReader(new FileReader(countFile));
		ArrayList<Integer> counts= new ArrayList<Integer>();
		String countString = "";
		int MAX = 0;
		while( (countString = countReader.readLine()) != null){
			int count = Integer.parseInt(countString.trim());
			if(count > MAX) MAX = count;
			counts.add(count);
		}
		System.out.println("Unique Sentence count: " + counts.size());
		System.out.println("Maximum verbs in a sentence : " + MAX);
		countReader.close();
		File out = new File(args[0] + ".prop");
		PrintWriter p = new PrintWriter(out);
		String tmpFile = "/tmp/prop.tmp";
		PrintWriter tmpP = new PrintWriter(tmpFile);
		BufferedReader inputReader = new BufferedReader(new FileReader(args[0]));
		
		ArrayList<Sentence> data = new ArrayList<Sentence>();
		String line = "";
		Sentence s = new Sentence();
		int sentenceIndex = 0;
		while( (line = inputReader.readLine()) != null){
			line = line.trim();
			if(! line.equals("") ){
				DataRow dr = new DataRow(sentenceIndex++, line);
				s.add(dr);
			} else{
				if(s.size() != 0){
					data.add(s);
				}
				s = null;
				s = new Sentence();
				sentenceIndex = 0;
			}
		}		
		System.out.println("Total Sentences count : " + data.size());
		
		int index = 0;
		for (int count : counts){
			int sentenceLength = 0;
			String[][] sentenceProp = new String[LEN][MAX + 1]; //+1 for field to include - or verb word
			//clear
			for(int i=0; i<LEN; i++){
				for(int j=0; j< MAX+1; j++){
					sentenceProp[i][j] = "";
				}
			}
			boolean hasAtLeastOneVerb = false;
			for (int i=1; i <= count; i++) {
				Sentence sen = data.get(index);
				//System.out.println("Index : " + index);
				//System.out.println(sen.debugString());
				sentenceLength = sen.size();
				String previousArg = "O";
				boolean firstVerbFound = false;
				for(int j=0; j < sentenceLength; j++){
					DataRow dr = sen.get(j);
					String currentArg = dr.getArg();
					
					if(dr.hasVerb() ){
						hasAtLeastOneVerb = true;
						//manage previous label
						if(! previousArg.equals("O") ){
							//close previous
							sentenceProp[j-1][i] += ")";
						}
						if( !firstVerbFound ){
							firstVerbFound = true;
							sentenceProp[j][0] = dr.getWord();
							sentenceProp[j][i] = "(V*";
							previousArg = "VI";
						} else {
							sentenceProp[j][0] = "-";
							previousArg = currentArg;
						}
					} else { //dr does not have verb
						if(sentenceProp[j][0].isEmpty()){
							sentenceProp[j][0] = "-";
						}
						if(currentArg.equals("O")){
							sentenceProp[j][i] = "*";
							if(previousArg.equals("VI") && dr.getVerbOrNot().equals("VI")){
								previousArg = "VI";
							}
							else if( ! previousArg.equals("O")){
								sentenceProp[j-1][i] += ")";
								previousArg = "O";
							} else {
								previousArg = "O";
							}
						} else {
							if(previousArg.equals("O")){
								sentenceProp[j][i] = "(" + currentArg + "*";
								previousArg = currentArg;
							}
							else if(previousArg.equals("VI")){
								if(currentArg.equals("VI")){
									sentenceProp[j][i] = "*";
									previousArg = "VI";
								} else {
									//close previous
									sentenceProp[j-1][i] += ")";
									//start new
									sentenceProp[j][i] = "(" + currentArg + "*";
									previousArg = currentArg;
								}
							}
							else { // current not O and previous not O
								//there are cases like (C-A1)(C-A1) appearing together, so make use of BIO labels
								if(! dr.getBio().equals("B") ){
									if(previousArg.equals(currentArg)){
										sentenceProp[j][i] = "*";
									}
									else{
										//close previous
										sentenceProp[j-1][i] += ")";
										//start new
										sentenceProp[j][i] = "(" + currentArg + "*";
										previousArg = currentArg;
									}
								} else {
									//close previous
									sentenceProp[j-1][i] += ")";
									//start new
									sentenceProp[j][i] = "(" + currentArg + "*";
									previousArg = currentArg;
								}
							}
						}
					}
					//System.out.println("Previous: " + previousArg);
					
					//some closing might be needed
					if( j == sentenceLength - 1) { //last word
						//previous arg still means the currentArg
						if(! previousArg.equals("O") ){
							sentenceProp[j][i] += ")";
						}
					}
				}
				//System.out.println();
				index++;
			}
			//visualizeSentenceProp(sentenceProp, MAX);
			//write sentenceProp
			for(int i=0; i<sentenceLength; i++){
				if(hasAtLeastOneVerb){
					for(int j=0; j<count+1; j++){
						tmpP.print(sentenceProp[i][j] + " ");
						if(j != count ){
							for(int k=0; k< (10 - sentenceProp[i][j].length()); k++){
								tmpP.print(" ");
							}
						}
					}
					tmpP.println();
				} else {
					tmpP.println(sentenceProp[i][0]);
				}
			}
			tmpP.println();
		}
		tmpP.flush();
		tmpP.close();
		inputReader.close();
		//Now replace the verb words
		String lineOrg = "";
		String lineNew = "";
		
		BufferedReader brOrg = new BufferedReader(new FileReader(args[1]));
		BufferedReader brNew = new BufferedReader(new FileReader(tmpFile));
		int lineNumber = 0;
		while( (lineOrg = brOrg.readLine()) != null) {
			lineNumber++;
			lineNew = brNew.readLine();
			lineOrg = lineOrg.trim();
			lineNew = lineNew.trim();
			if(lineOrg.equals("")) {
				if(! lineNew.equals("")) {
					System.err.println("Original and Created prop files do not align at line " + lineNumber);
					System.exit(1);
				}
				p.println();
			} else {
				String[] splittedNew = lineNew.split("(\\s+|\\t+)");
				String[] splittedOrg = lineOrg.split("(\\s+|\\t+)");
				if(splittedNew.length != splittedOrg.length) {
					System.err.println("The columns for the original prop sentence and new prop sentence do not align at line " + lineNumber);
					System.exit(1);
				}
				for(int i=0; i<splittedNew.length; i++) {
					if(i==0) {
						p.print(splittedOrg[i]);
						p.print(spaces(spaceSize - splittedOrg[i].length()) + " ");
					} else if(i==splittedNew.length) {
						p.print(splittedNew[i]);
					} else {
						p.print(splittedNew[i]);
						p.print(spaces(spaceSize - splittedNew[i].length()) + " ");
					}
				}
				p.println();
				p.flush();
			}
		}
		p.flush();
		p.close();
		System.out.println("Prop file written at " + out.getAbsolutePath());
	}
	
	public static String spaces(int count){
		String spaces = "";
		for(int i=0; i<count; i++){
			spaces += " ";
		}
		return spaces;
	}
	
	public static void visualizeSentenceProp(String[][] sentenceProp, int max){
		for(int i=0; i<LEN; i++){
			for(int j=0; j<max+1; j++){
				System.out.print(sentenceProp[i][j] + " ");
			}
			System.out.println();
		}
	}
}
