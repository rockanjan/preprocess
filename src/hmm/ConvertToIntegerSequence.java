package hmm;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/*
 * Input: Takes in data with each token separated by new line
 * and sentences separated by a blank line
 * Output: Generates the integer sequence representation suitable for jahm
 * 
 * 
 */
public class ConvertToIntegerSequence {
	static final int SIZE = 50000;
	//word index with frequency
	static HashMap<String, Integer> wordIndex = new HashMap<String, Integer>(SIZE);
	
	String inputFile;
	String outputFile;
	public ConvertToIntegerSequence(String i, String o){
		inputFile = i;
		outputFile = o;
	}
	
	public void run() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(inputFile)); //to make index
		BufferedReader br2 = new BufferedReader(new FileReader(inputFile));
		PrintWriter p = new PrintWriter(outputFile);
		String line = "";
		//read file
		System.out.println("Creating Index...");
		int index = 0;
		while( (line = br.readLine()) != null){
			if(!line.trim().equals("")){
				String[] splitted = line.split(" ");
				String word = splitted[0];	
				if(! wordIndex.containsKey(word)){
					wordIndex.put(word, index++);
				}
			}
		}
		System.out.println("MaxIndex: " + (wordIndex.size() - 1)); //starts from 0
		ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream("/tmp/word_index.bin"));
		fos.writeObject(wordIndex);
		fos.close();
		System.out.println("Word Index Saved");
		
		System.out.println("Converting sequence into integers...");
		while( (line = br2.readLine()) != null){
			if(!line.trim().equals("")){
				String[] splitted = line.split(" ");
				String word = splitted[0];	
				if(! wordIndex.containsKey(word)){
					System.err.println("word not found in index : " + word);
				}
				p.print(wordIndex.get(word) + ";");
			}
			else{
				p.println();
			}
		}
		br.close();
		br2.close();
		p.flush();
		p.close();
		System.out.println("Done");
	}
	
	public static void main(String[] args) throws IOException{
		if(args.length != 2){
			System.err.println("Usage: <program> inputFile outputFile");
			System.exit(1);
		}
		System.out.println("Input: " + args[0]);
		System.out.println("Output: " + args[1]);
		ConvertToIntegerSequence converter = new ConvertToIntegerSequence(args[0], args[1]);
		converter.run();
	}
}
