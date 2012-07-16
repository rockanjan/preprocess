package senna;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * Takes the senna prop file and the original prop file and considers only the verbs that senna predicted correctly
 * senna can sometime predict extra verbs and sometime miss them
 */
public class SennaProcessIdentifiedVerbs {
	public static void main(String[] args) throws IOException{
		String inGoldFile = "/work/srl/senna_verb_considered_comparison/test.brown.prop";
		String inSennaFile = "/work/srl/senna_verb_considered_comparison/test.brown.senna.prop";
		
		String outGoldFile = inGoldFile + ".verbprocessed";
		String outSennaFile = inSennaFile + ".verbprocessed";
		
		BufferedReader brGoldFile = new BufferedReader(new FileReader(inGoldFile));
		BufferedReader brSennaFile = new BufferedReader(new FileReader(inSennaFile));
		
		PrintWriter pwGold = new PrintWriter(outGoldFile);
		PrintWriter pwSenna = new PrintWriter(outSennaFile);
		
		String[][] goldPropSentence = new String[50][20]; 
		String[][] sennaPropSentence = new String[50][20];
		String goldLine = "";
		String sennaLine = "";
		int lineNumber = 0;
		int sentenceLine = 0;
		int goldColCount = 0;
		int sennaColCount = 0;
		
		while( (goldLine = brGoldFile.readLine()) != null) {
			lineNumber++;
			goldLine = goldLine.trim();
			sennaLine = brSennaFile.readLine();
			sennaLine = sennaLine.trim();
			if(goldLine.equals("") && !sennaLine.equals("")){
				System.err.println("Gold prop and senna prop not aligned properly at line : " + lineNumber);
				System.exit(1);
			}
			if( ! goldLine.equals("") ){
				String[] splittedGoldLine = goldLine.split("(\\t+|\\s+)");
				//System.out.println(splittedGoldLine.length);
				goldColCount = splittedGoldLine.length;
				for(int i=0; i<splittedGoldLine.length; i++){
					goldPropSentence[sentenceLine][i] = splittedGoldLine[i];
				}
				
				String[] splittedSennaLine = sennaLine.split("(\\t+|\\s+)");
				
				sennaColCount = splittedSennaLine.length;
				//System.out.println(sennaColCount);
				for(int i=0; i<splittedSennaLine.length; i++) {
					sennaPropSentence[sentenceLine][i] = splittedSennaLine[i];
				}
				sentenceLine++;
			} else {
				//process
				ArrayList<Integer> goldSkipColumns = new ArrayList<Integer>();
				ArrayList<Integer> sennaSkipColumns = new ArrayList<Integer>();
				
				//two cases: senna -> (missed existing verb) and (found nonexisting verb) 
				int goldCurrentVerbCount = 0;
				int sennaCurrentVerbCount = 0;
				for(int i=0; i < sentenceLine; i++){
					if(goldPropSentence[i][0].equals("-") && sennaPropSentence[i][0].equals("-")){
							continue;
					}
					else if(!goldPropSentence[i][0].equals("-") && !sennaPropSentence[i][0].equals("-")){
						goldCurrentVerbCount++;
						sennaCurrentVerbCount++;	
						continue;
					}
					else if(goldPropSentence[i][0].equals("-") && !sennaPropSentence[i][0].equals("-")){
						//found non existing verb
						sennaCurrentVerbCount++;
						sennaSkipColumns.add(sennaCurrentVerbCount);
						sennaPropSentence[i][0] = "-";
					}
					else {
						//missed a verb
						goldCurrentVerbCount++;
						goldSkipColumns.add(goldCurrentVerbCount);
						goldPropSentence[i][0] = "-";
					}
				}
				//System.out.println(goldSkipColumns.size());
				//start writing
				for(int i=0; i<sentenceLine; i++){
					for(int j=0; j<goldColCount; j++){
						if(! goldSkipColumns.contains(j))
							pwGold.print(goldPropSentence[i][j] + "\t");
					}
					pwGold.println();
				}
				pwGold.println();
				for(int i=0; i<sentenceLine; i++){
					pwSenna.print(goldPropSentence[i][0] + "\t");
					for(int j=1; j<sennaColCount; j++){
						if(! sennaSkipColumns.contains(j))
							pwSenna.print(sennaPropSentence[i][j] + "\t");
					}
					pwSenna.println();
				}
				pwSenna.println();
				//reset
				goldColCount = 0;
				sennaColCount = 0;
				goldPropSentence = new String[50][20]; 
				sennaPropSentence = new String[50][20];
				sentenceLine = 0;
			}
		}
		brGoldFile.close();
		brSennaFile.close();
		pwGold.close();
		pwSenna.close();
		System.out.println("Done");
	}
}
