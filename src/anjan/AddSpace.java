package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class AddSpace {
	public static void main(String[] args) throws IOException{
		String blankLinesBrown = "/work/new_experiment/forPrediction/predicted/blank_lines_brown";
		String inFile = "/work/new_experiment/forPrediction/predicted/brown_predicted_no_space";
		String outFile = "/tmp/jpt";
		
		
		BufferedReader in = new BufferedReader(new FileReader(inFile));
		BufferedReader blank = new BufferedReader(new FileReader(blankLinesBrown));
		PrintWriter out = new PrintWriter(new File(outFile));
		
		List<Integer> blankLines = new ArrayList<Integer>();
		int count = 0;
		String line = null;
		while( (line = blank.readLine()) != null){
			blankLines.add(Integer.parseInt(line.trim()));
			count++;
		}

		int currentLine = new Integer(0);
		while( (line = in.readLine()) != null){
			currentLine++;
			if(blankLines.contains(currentLine)){
				out.println();
				currentLine++;
				out.println(line);
			}
			else{
				out.println(line);
			}
		}
		
		in.close();
		blank.close();
		out.close();
		
	}
}
