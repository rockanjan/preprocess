package jdageem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ConvertForJdageemPosOnly {
	public static int N=50;
	public static int maxLength = 10;
	public static void main(String[] args) throws IOException{
		String inFile = "/home/anjan/Dropbox/research/srl/pos_depwsj15/combined.pos";
		String outFile = "/home/anjan/Dropbox/research/srl/pos_depwsj15/combined.jdageem";
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		PrintWriter pw = new PrintWriter(new File(outFile));
		PrintWriter pw1 = new PrintWriter(new File(outFile + ".trunc"));
		String line = "";
		int INDEX = 0; //index of POS (or HMM)
		String[][] sequence = new String[N][1]; 
		int i=0;
		while( (line = br.readLine()) != null){
			line = line.trim();
			if(! "".equals(line)){
				String[] row = line.split("( )|(\t)");
				sequence[i][0] = row[INDEX];
				i++;
			} else {
				//write
				for(int c=0; c<i; c++){
					pw.print(sequence[c][0]);
					if(c == i-1){
						pw.println();
					} else {
						pw.print(" ");
					}
				}
				//reset
				for(int c=0; c<N; c++){
					sequence[c][0] = "";
				}
				i=0;
				
			}
		}
		br.close();
		pw.close();
		
		BufferedReader br1 = new BufferedReader(new FileReader(outFile));
		while( (line = br1.readLine()) != null){
			line = line.trim();
			if(! "".equals(line)){
				String[] row = line.split("( )|(\t)");
				if(row.length <= maxLength && row.length > 1){
					pw1.println(line);
					pw1.flush();
				}
			}
		}
		br1.close();
		pw1.close();
		
		
		System.out.println("Done");
	}
}
