package anjan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Useful to remove punctuations and still save the HMM states
 */
public class RemovePunctuationsPreserveBIO {
	public static final int N = 50; // max lenght of sentence
	//note if comma is needed
	public static final String[] punctuation_array = { "\\", "\\*\\*", "\\*",
			"*", "!", "#", /*",",*/"`", "``", "\'\'", "\'", "(", ")", "{", "}",
			".", "?", "-", ":", ";", "=", "@" };

	public static List<String> punctuations = new ArrayList<String>(
			Arrays.asList(punctuation_array));

	BufferedReader br;
	PrintStream p;

	String delimiter;
	int index;
	public static int BIO_INDEX = 4; //should be provided in human count form
	public RemovePunctuationsPreserveBIO(String inFilename, String outFilename,
			String delimiter, int index, int bioIndex) throws IOException {
		br = new BufferedReader(new InputStreamReader(new FileInputStream(
				inFilename)));
		FileOutputStream out = new FileOutputStream(outFilename);
		p = new PrintStream(out);
		if (delimiter.equals("space")) {
			this.delimiter = " ";
		} else if (delimiter.equals("tab")) {
			this.delimiter = "\t";
		} else {
			System.err.println("Invalid delimiter");
			System.exit(1);
		}
		this.index = index;
		this.BIO_INDEX = bioIndex;
	}

	public void run() throws IOException {
		String line = null;
		String paragraph = "";
		int paragraphLineCount = 0;
		int currentLine = 0;
		
		//Sometimes, removing punctuation might remove B.
		//will be a problem in cases like BIIBI where second B is removed
		//make the I right after a B into B if the B has to be removed.
		boolean immediatePreviousRemovedB = false; //keep track if immediate previous removed line had B 
		while ((line = br.readLine()) != null) {
			currentLine++;
			if (currentLine % 1000 == 0) {
				//System.out.println("Processed Line number : " + currentLine);
			}
			if (line.trim().equals("")) {
				// write the file if lines upto N and continue
				// System.out.println("Paragraph line count " +
				// paragraphLineCount);
				if (paragraphLineCount <= N && paragraphLineCount != 0) {
					p.println(paragraph);
					p.flush();
				}
				paragraph = "";
				paragraphLineCount = 0;
				immediatePreviousRemovedB = false;
			} else {
				String[] splitted = line.split(delimiter);
				String word = splitted[index-1];
				if (!punctuations.contains(word)) {
					if (!immediatePreviousRemovedB) {
						paragraph += line + "\n";
						paragraphLineCount++;
					}
					else{
						String bio = splitted[BIO_INDEX - 1];
						if(! (bio.equals("B") || bio.equals("I") || bio.equals("O")) ){
							System.err.println("Warning: Line " + currentLine + "BIO label containing : " + bio);
						}
						if(bio.equals("I")){ //previous removed was B, now I, make this I the beginning
							for(int j=0; j < splitted.length; j++){
								if(j != BIO_INDEX-1) {
									paragraph += splitted[j];
								}
								else{
									paragraph += "B";
								}
								if( j != splitted.length-1){
									paragraph += " ";
								}
							}
							paragraph += "\n";
						}
						else{
							paragraph += line + "\n";
						}
						paragraphLineCount++;
						immediatePreviousRemovedB = false;
					}
				} else {
					String currentBIO = splitted[BIO_INDEX - 1];
					if(! (currentBIO.equals("B") || currentBIO.equals("I") || currentBIO.equals("O")) ){
						System.err.println("Warning: Line " + currentLine + "BIO label containing : " + currentBIO);
					}
					if(currentBIO.equals("B")){
						immediatePreviousRemovedB = true;
					}
					else if(currentBIO.equals("I") && immediatePreviousRemovedB){
						immediatePreviousRemovedB = true;
					}
					else{
						immediatePreviousRemovedB = false;
					}
				}
			}
		}
	}

	public void close() throws IOException {
		br.close();
		p.close();
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 5) {
			System.err
					.println("Invalid arguments, usage: <program> inFile outFile delimiter_to_split(tab/space) word_column_index(1 or 2) bioIndex");
			System.exit(1);
		}
		String inFile = args[0];
		String outFile = args[1];
		String delimiter = args[2];
		int index = -1;
		int bioIndex = -1;
		try {
			index = Integer.parseInt(args[3]);
			bioIndex = Integer.parseInt(args[4]);
		} catch (Exception e) {
			System.err.println("Index should be number");
		}
		System.out.println("In File " + inFile);
		System.out.println("Out File " + outFile);
		RemovePunctuationsPreserveBIO rp = new RemovePunctuationsPreserveBIO(inFile, outFile,
				delimiter, index, bioIndex);
		rp.run();
		rp.close();
		System.out.println("Done!");
	}

}
