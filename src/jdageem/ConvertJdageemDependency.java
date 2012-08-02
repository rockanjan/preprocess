package jdageem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * Takes a file and converts jdageem dependency into Alex's format
 */
public class ConvertJdageemDependency {
	
	public static void main(String[] args) throws IOException {
		String inFile = "/home/anjan/workspace/jdageem/nopunct/combined.hmm.jdageem.dep";
		String outFile = "/home/anjan/workspace/jdageem/nopunct/combined.hmm.jdageem.alex.dep";
		
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		
		PrintWriter pw = new PrintWriter(outFile);
		String lastLine = ""; //last line before blank has dependency
		String line = "";
		while( (line = br.readLine()) != null ){
			line = line.trim();
			if(! line.equals("")) {
				lastLine = line;
			} else {
				//lastLine already has the dependency line
				String[] deps = lastLine.split(" ");
				for(String dep : deps) {
					int depInt = Integer.parseInt(dep);
					pw.println(--depInt);
				}
				pw.println();
			}
		}
		br.close();
		pw.close();
		System.out.println("Done");
	}

}
