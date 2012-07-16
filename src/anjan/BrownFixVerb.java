package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * fixes the verb form of senna prop output
 */
public class BrownFixVerb {
	public static void main(String args[]) throws IOException{
		BufferedReader brGold = new BufferedReader(new FileReader("/work/conll05st-release/test.brown.prop.nopunct.truncated"));
		BufferedReader brSenna = new BufferedReader(new FileReader("/work/conll05st-release/test.brown.senna.prop"));
		PrintWriter sennaFixed = new PrintWriter(new File("/work/conll05st-release/test.brown.senna.prop.verbfixed"));
		String line = "";
		
		while( (line = brGold.readLine()) != null){
			line = line.trim();
			String lineSenna = brSenna.readLine().trim();
			if(!line.trim().equals("")){
				String[] splittedGold = line.split("\\s+");
				String[] splittedSenna = lineSenna.split("\\s+");
				if(!splittedGold[0].equals("-") && !splittedSenna[0].equals("-")){
					sennaFixed.print(splittedGold[0] + spaces( 15 - splittedGold[0].length()) + " ");
				} else {
					sennaFixed.print(splittedSenna[0] + spaces( 15 - splittedGold[0].length()) + " ");
				}
				//continue other columns
				for(int i=1; i<splittedSenna.length; i++){
					sennaFixed.print(splittedSenna[i] + spaces(15 - splittedSenna[i].length()) + " ");
				}
				sennaFixed.println();
			}
			else {
				sennaFixed.println();
			}
		}
		brGold.close();
		brSenna.close();
		sennaFixed.close();
		System.out.println("DOne");
	}
	
	public static String spaces(int count){
		String spaces = "";
		for(int i=0; i<count; i++){
			spaces += " ";
		}
		return spaces;
	}
	
}
