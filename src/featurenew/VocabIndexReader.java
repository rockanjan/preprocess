package featurenew;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VocabIndexReader {
	public static String NUM = "__NUM__";
	public static int OOV = 0;
	public static HashMap<String, Integer> vocabIndex;
	
	public static void __init__(String filename) throws IOException{
		File f = new File(filename);
		if(! f.exists() ){
			System.err.println("Vocab Index file not found : " + filename);
			System.exit(1);
		}
		System.out.println("Reading vocab file..." + filename);
		
		vocabIndex = new HashMap<String, Integer>(50000);
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = "";
		while ((line = br.readLine()) != null){
			line = line.trim();
			String[] splitted = line.split("( )|(\t)");
			try{
				int index = Integer.parseInt(splitted[0]);
				String word = null;
				if(index > -1){
					word = splitted[1];
					if(vocabIndex.containsKey(word)){
						System.err.println("Duplicated vocab : " + word);
					}
					vocabIndex.put(word, index);
				}
				
			} catch(Exception e){
				System.err.println("Error parsing vocab file!");
			}
		}
		br.close();
		System.out.println("Vocab Size: " + vocabIndex.size());
		System.out.println("Done");
	}
	
	public static int getIndex(String queryWord){
		if(vocabIndex == null){
			System.err.println("Vocab Index is not initialized");
			System.exit(1);
		}
		int returnIndex = OOV;
		String word = queryWord.toLowerCase();
		Pattern p1 = Pattern.compile("^-{0,1}[0-9]+\\.*[0-9]*"); //eg -9, 100, 100.001 etc
		Pattern p2 = Pattern.compile("^-{0,1}[0-9]*\\.*[0-9]+"); //eg. -.5, .5
		Pattern p3 = Pattern.compile("^-{0,1}[0-9]{1,3}[,[0-9]{3}]*\\.*[0-9]*"); //matches 100,000
		Pattern p4 = Pattern.compile("[0-9]+\\\\/[0-9]+"); // four \ needed, java converts it to \\
		Pattern p5 = Pattern.compile("[0-9]+:[0-9]+"); //ratios and time
		Pattern p6 = Pattern.compile("([0-9]+-)+[0-9]+"); // 1-2-3, 1-2-3-4 etc
		Matcher m1 = p1.matcher(word);
		Matcher m2 = p2.matcher(word);
		Matcher m3 = p3.matcher(word);
		Matcher m4 = p4.matcher(word);
		Matcher m5 = p5.matcher(word);
		Matcher m6 = p6.matcher(word);
		if(m1.matches() || m2.matches() || m3.matches() || m4.matches() || m5.matches() || m6.matches() ){
			word = NUM;
		}
		
		Integer index = vocabIndex.get(word);
		if(index == null){
			returnIndex = OOV;
		} else {
			returnIndex = index;
		}
		return returnIndex;
	}
	
	public static String[] getWordArray(){
		if(vocabIndex == null){
			System.err.println("Vocab Index is not initialized");
			System.exit(1);
		}
		String[] wordArray = new String[vocabIndex.size()+1];
		wordArray[OOV] = "__OOV__";
		for( Entry<String, Integer> entry : vocabIndex.entrySet()){
			wordArray[entry.getValue()] = entry.getKey();
		}
		return wordArray;
	}
	
	public static void main(String[] args) throws IOException{
		String filename = "/work/srl/propprocess/biocommaprocessed/vocab_index_for_feature.txt";
		__init__(filename);
		System.out.println("Index of about : " + getIndex("about"));
	}
}
