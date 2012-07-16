package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceLowFreqWords {
	//words will less freq than K will be replaced by __OOV__.
	static int K=3;
	static final String REPLACE = "__OOV__";
	static final String NUM = "__NUM__";
	static final int SIZE = 50000;
	static HashMap<String, Integer> wordCount = new HashMap<String, Integer>(SIZE);
	static HashSet<String> usedIndices = new HashSet<String>(SIZE);
	static ArrayList<String> smallFreqWords = new ArrayList<String>();
	public static void main(String[] args) throws IOException {
		if(args.length < 3){
			System.err.println("Usage: <program> infile outfile k");
			System.exit(1);
		}
		try{
			K = Integer.parseInt(args[2]);
			if (K < 1){
				throw new Exception();
			}
		} catch(Exception e){
			System.out.println("K must be integer greater than 0");
		}
		Pattern p1 = Pattern.compile("^-{0,1}[0-9]+\\.*[0-9]*"); //eg -9, 100, 100.001 etc
		Pattern p2 = Pattern.compile("^-{0,1}[0-9]*\\.*[0-9]+"); //eg. -.5, .5
		Pattern p3 = Pattern.compile("^-{0,1}[0-9]{1,3}[,[0-9]{3}]*\\.*[0-9]*"); //matches 100,000
		Pattern p4 = Pattern.compile("[0-9]+\\\\/[0-9]+"); // four \ needed, java converts it to \\
		Pattern p5 = Pattern.compile("[0-9]+:[0-9]+"); //ratios and time
		Pattern p6 = Pattern.compile("([0-9]+-)+[0-9]+"); // 1-2-3, 1-2-3-4 etc
		
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		BufferedReader br2 = new BufferedReader(new FileReader(inputFile));
		PrintWriter p = new PrintWriter(outputFile);
		String line = "";
		//read file
		System.out.println("Counting...");
		while( (line = br.readLine()) != null){
			if(!line.trim().equals("")){
				String[] splitted = line.split(" ");
				String word = splitted[0];
				Matcher m1 = p1.matcher(word);
				Matcher m2 = p2.matcher(word);
				Matcher m3 = p3.matcher(word);
				Matcher m4 = p4.matcher(word);
				Matcher m5 = p5.matcher(word);
				Matcher m6 = p6.matcher(word);
				//is word num
				if(m1.matches() || m2.matches() || m3.matches() || m4.matches() || m5.matches() || m6.matches() ){
					word = NUM;
				}
				if(usedIndices.contains(word)){
					wordCount.put(word, wordCount.get(word) + 1);
				}
				else{
					usedIndices.add(word);
					wordCount.put(word, 1);
				}
			}	
		}
		Iterator itr = wordCount.entrySet().iterator();
		while(itr.hasNext()){
			//Map<String, Integer>.Entry pairs = (Map.Entry) itr.next();
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) itr.next();
			//System.out.println(pairs.getKey() + " -> " + pairs.getValue());
			if(pairs.getValue() < K){
				smallFreqWords.add(pairs.getKey());
				//System.out.println(pairs.getKey());
			}
		}
		System.out.println("Replacing");
		//second pass, replace the string
		while( (line = br2.readLine()) != null){
			if(!line.trim().equals("")){
				String[] splitted = line.split(" ");
				String word = splitted[0];
				Matcher m1 = p1.matcher(word);
				Matcher m2 = p2.matcher(word);
				Matcher m3 = p3.matcher(word);
				Matcher m4 = p4.matcher(word);
				Matcher m5 = p5.matcher(word);
				Matcher m6 = p6.matcher(word);
				//alpha or num
				if(m1.matches() || m2.matches() || m3.matches() || m4.matches() || m5.matches() || m6.matches() ){
					word = NUM;
				}
				if(smallFreqWords.contains(word)){
					p.print(REPLACE);
				}
				else{
					p.print(word);
				}
				for(int i=1; i<splitted.length; i++){
					p.print(" " + splitted[i]);
				}
				p.println();
			}
			else{
				p.println();
			}
		}
		System.out.println("Done!");
		
		br.close();
		br2.close();
		p.close();
	}
}
