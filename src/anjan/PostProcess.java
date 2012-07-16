package anjan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/* Post processes Classification result using BIO */ 
public class PostProcess {
	public static void main(String args[]) throws IOException{
		if(args.length != 2){
			System.err.println("Usage: <program> inputfile outputfile");
			System.exit(1);
		}
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		PrintWriter pw = new PrintWriter(args[1]);
		String line="";
		String prevClass="O";
		String prevBIO="O";
		String prevV = "O";
		int lineCount = 0;
		while( (line=br.readLine()) != null){
			lineCount++;
			line = line.trim();
			if(! line.equals("")){
				String[] splitted = line.split("(\\s+)|(\\t+)");
				String currentBIO = splitted[splitted.length - 2];
				String currentClass = splitted[splitted.length - 1];
				//prevBIO only used here
				if(currentBIO.equals("I") && prevBIO.equals("O")){
					System.err.println("WARNING: I without preceding B at line " + lineCount);
				}
				String newClass = "";
				//post processing starts
				if(currentBIO.equals("O")){
					//just set the arg-class as O
					newClass="O";
					
				} else if(currentBIO.equals("B")){
					newClass = currentClass;
				} else if(currentBIO.equals("I")){
					newClass = prevClass;
				} else {
					//usually will reach here for cases like VI
					//kept V O O
					//up O VI VI
					if(!(prevV.equals("V") || prevV.equals("VI"))){
						newClass = "VI";
					} else {
						newClass = "O";
					}
				}
				//write to file
				//pw.println(splitted[0] + " " + splitted[1] + " " + splitted[2] + " " + newClass);
				pw.println(splitted[splitted.length - 2] + " " + newClass);
				prevBIO = currentBIO;
				prevClass = newClass;
				prevV = splitted[1];
				
			} else {
				pw.println();
				prevClass = "O";
				prevBIO = "O";
			}
		}
		pw.flush();
		br.close();
		pw.close();
		System.out.println("Done");
	}
}
