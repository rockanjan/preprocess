package anjan;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * Evaluates the precision/recall and F1 score for argument identification
 * input files, first the gold file, second the predicted file
 */
public class IdentificationEvaluation {
	// stores arraylist of all the hash maps of arguments
	// hashmap stored in format <linenumber, argLength>
	static boolean debug = false;
	static HashMap<Integer, Integer> goldArgs = new HashMap<Integer, Integer>();
	static HashMap<Integer, Integer> predArgs = new HashMap<Integer, Integer>();

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: <program> <gold file> <predicted file>");
			System.exit(1);
		}
		int tp = 0, fn = 0, fp = 0;
		BufferedReader bg = new BufferedReader(new FileReader(args[0]));
		BufferedReader bp = new BufferedReader(new FileReader(args[1]));
		String errorLineFile = "/tmp/errorLine.tmp";
		PrintWriter errorLine = new PrintWriter(new File(errorLineFile));
		String goldLine = "";
		int lineNumber = 0;
		int argLength = 0;
		String bio;
		boolean cont = false;
		while ((goldLine = bg.readLine()) != null) {
			lineNumber++;
			bio = goldLine.trim();
			
			if (!bio.equals("")) {
				//use the original training file
				String[] splitted = bio.split(" ");
				bio = splitted[splitted.length - 1 ];
				//System.out.println(bio);
				// 3 cases, B, I or O
				if (bio.equals("B")) {
					if(cont){
						//either previous B or I
						int startLine = lineNumber - argLength;
						goldArgs.put(startLine , argLength);
					}
					argLength = 1;
					cont = true;
				} else if (bio.equals("I")) {
					argLength++;
					if (!cont) {
						System.err.println("GOLD: I without preceding B at line " + lineNumber);
					}
				} else if (bio.equals("O")) {
					if (cont) {
						// put the argument into hashmap
						int startLine = lineNumber - argLength;
						goldArgs.put(startLine, argLength);
						// reset
						argLength = 0;
						cont = false;
					}
				} else if(bio.equals("VI")){
					//ignore
				}
				else {
					System.err.println("Processing Gold: Unexpected label in target at line "
							+ lineNumber);
					System.exit(1);
				}
			}
			else{ //empty line
				if(cont){
					int startLine = lineNumber - argLength;
					goldArgs.put(startLine, argLength);
					cont = false;
					argLength = 0;
				}
			}
		}
		//if reaches here, it might be because of B I I and termination
		if(cont){
			//System.out.println("Here!!");
			int startLine = lineNumber - argLength + 1;
			goldArgs.put(startLine, argLength);
		}
		if(debug){
			System.out.println("Gold Args");
			debug(goldArgs);
		}
		// similar for prediction
		String predLine = "";
		lineNumber = 0;
		argLength = 0;
		cont = false;
		while ((predLine = bp.readLine()) != null) {
			lineNumber++;
			bio = predLine.trim();
			
			if (!bio.equals("")) {
				//use the original training file
				String[] splitted = bio.split(" ");
				bio = splitted[splitted.length - 1 ];
				// 3 cases, B, I or O
				if (bio.equals("B")) {
					if(cont){
						//either previous B or I
						int startLine = lineNumber - argLength;
						predArgs.put(startLine , argLength);
					}
					argLength = 1;
					cont = true;
				} else if (bio.equals("I")) {
					if (!cont) {
						System.err.println("PRED: I without preceding B at line " + lineNumber);
					}
					argLength++;
				} else if (bio.equals("O")) {
					if (cont) {
						// put the argument into hashmap
						int startLine = lineNumber - argLength;
						predArgs.put(startLine, argLength);
						// reset
						argLength = 0;
						cont = false;
					}
				} else if(bio.equals("VI")){
					//ignore
				}
				else {
					System.err.println("PRED: Unexpected label in target at line "
							+ lineNumber);
					System.exit(1);
				}
			}
			else{ //empty line
				if(cont){
					int startLine = lineNumber - argLength;
					predArgs.put(startLine, argLength);
					cont = false;
					argLength = 0;
				}
			}
		}
		//if reaches here, it might be because of B I I and termination
		if(cont){
			int startLine = lineNumber - argLength + 1;
			predArgs.put(startLine, argLength);
		}
		if(debug){
			System.out.println("Predicted Debug:");
			debug(predArgs);
		}

		/********* Evaluation ***************/
		Set<Map.Entry<Integer, Integer>> goldEntries = goldArgs.entrySet();
		for (Map.Entry<Integer, Integer> entry : goldEntries) {
			int key = entry.getKey();
			int value = entry.getValue();
			if (predArgs.containsKey(key)) {
				if (predArgs.get(key) == value) {
					// true positive
					tp++;
				} else {
					fn++;
					//errorLine.println(key + " " + value);
					errorLine.println(key);
				}
			} else {
				fn++;
				//errorLine.println(key + " " + value);
				errorLine.println(key);
			}
		}
		// for false positive
		Set<Map.Entry<Integer, Integer>> predEntries = predArgs.entrySet();
		for (Map.Entry<Integer, Integer> entry : predEntries) {
			int key = entry.getKey();
			int value = entry.getValue();
			if (!goldArgs.containsKey(key)) {
				fp++;
				//errorLine.println(key + " " + value);
				errorLine.println(key);
			} else {
				if (goldArgs.get(key) != value) {
					fp++;
					//errorLine.println(key + " " + value);
					errorLine.println(key);
				}
			}
			// else it's tp, already handled
		}

		// finally, F1 score
		System.out.println("True Positives: " + tp);
		System.out.println("False Positives: " + fp);
		System.out.println("False Negatives: " + fn);
		double precision = 1.0 * tp / (tp + fp);
		double recall = 1.0 * tp / (tp + fn);
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		double f1 = 2 * precision * recall / (precision + recall);
		System.out.println("F1 score : " + f1);

		bp.close();
		bg.close();
		errorLine.flush();
		errorLine.close();
		//processDebugging(errorLineFile, goldFile);
	}
	
	public static void debug(HashMap<Integer, Integer> arg) {
		Set<Map.Entry<Integer, Integer>> entries = arg.entrySet();
		for (Map.Entry<Integer, Integer> entry : entries) {
			System.out.println(entry.getKey() + "\t\t-->\t" + entry.getValue());
		}
	}
}
