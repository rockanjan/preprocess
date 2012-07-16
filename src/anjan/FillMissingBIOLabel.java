package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * In some sentences, there are missing some BIO labels, replace them with O
 */
public class FillMissingBIOLabel {
	public static void main(String[] args) throws IOException{
		if(args.length != 2){
			System.out.println("Invalid input: use <program> input_file output_file");
			System.exit(1);
		}
		BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
		PrintWriter pw = new PrintWriter(new File(args[1])); 
		String line = "";
		while( (line = br.readLine()) != null){
			if(!line.equals("")){
				int length = line.split(" ").length;
				if(length < 4){
					if(length != 3){
						System.err.println("There should at least be three columns!");
						System.exit(1);
					}
					//System.out.println(line);
					pw.print(line.trim() + " " + "O");
					pw.println();
				}
				else{
					pw.println(line);
				}
			}
			else{
				pw.println();
			}
		}
		
		br.close();
		pw.close();
		System.out.println("Done!");
	}

}
