package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DisplaySentence {
	public static void main(String[] args ) throws IOException{
		//displays specific numbered sentence
		int number = 38875;
		BufferedReader br = new BufferedReader(new FileReader("/home/anjan/Dropbox/combined.word.pos.prop"));
		String line = "";
		ArrayList<String> sentence = new ArrayList<String>();
		int count = 0;
		while( (line = br.readLine()) != null ) {
			line = line.trim();
			if (! line.isEmpty()) {
				sentence.add(line);				
			} else {
				count++;
				if(number == count) {
					for(String senLine : sentence) {
						System.out.println(senLine);
					}
					break;
				}
				sentence = new ArrayList<String>();
			}
		}
		
	}
}
