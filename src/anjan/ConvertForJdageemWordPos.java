package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ConvertForJdageemWordPos {
	public static int N=10;
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("/work/srl/propprocess/hmm/combined.wordandstate.truncated"));
		PrintWriter pw = new PrintWriter(new File("/work/srl/propprocess/hmm/combined.hmm.jdageem"));
		String line = "";
		
		String[][] sequence = new String[N][2]; //word and POS
		int i=0;
		while( (line = br.readLine()) != null){
			line = line.trim();
			if(! "".equals(line)){
				String[] row = line.split("( )|(\t)");
				sequence[i][0] = row[0];
				sequence[i][1] = row[1];
				i++;
			} else {
				//write
				for(int c=0; c<i; c++){
					pw.print(sequence[c][1]);
					if(c == i-1){
						pw.println();
					} else {
						pw.print(" ");
					}
				}
				//reset
				for(int c=0; c<10; c++){
					sequence[c][0] = "";
					sequence[c][1] = "";
				}
				i=0;
				
			}
		}
		br.close();
		pw.close();
		System.out.println("Done");
	}
}
