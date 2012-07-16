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
import java.util.Random;

/*
 * Modifies the file so that it can be input to the NBayesTest
 */
public class PreprocessForNBayes {
	public static final int N = 50; //max length of sentence
	public static final int CLUSTER = 10;
	//public static final String[] punctuation_array = {",", "`", "``", "\'\'", "\'", "(", ")", "{", "}", ".", "?", "-", ":", ";", "=", "@"};
	public static final String[] punctuation_array = { "\\", "\\*\\*", "\\*",
		"*", "!", "#", /*",",*/"`", "``", "\'\'", "\'", "(", ")", "{", "}",
		".", "?", "-", ":", ";", "=", "@" };
	public static List<String> punctuations = new ArrayList<String>(Arrays.asList(punctuation_array));
	Random random = new Random();
	BufferedReader br;
	PrintStream p;
	public PreprocessForNBayes(String inFilename, String outFilename) throws IOException{
		br = new BufferedReader(new InputStreamReader(new FileInputStream(inFilename)));
		FileOutputStream out = new FileOutputStream(outFilename);
		p = new PrintStream(out) ;
	}
	
	public void run() throws IOException{
		String line = null;
		String paragraph = "";
		int paragraphLineCount = 0;
		int currentLine = 0;
		int paragraphLineNumber = 0;
		while( (line = br.readLine()) != null){
			currentLine ++;
			if(currentLine % 1000 == 0){
				System.out.println("Processed Line number : " + currentLine);
			}
			if(line.trim().equals("")){
				//write the file if lines upto 10 and continue
				//System.out.println("Paragraph line count " + paragraphLineCount);
				if(paragraphLineCount <= N){
					p.println(paragraph);
					p.flush();
					paragraphLineNumber = 0;
				}
				//System.out.println(paragraph);
				//clear paragraph buffer
				paragraph = "";
				//reset lineCount
				paragraphLineCount = 0;
			}
			else{
				String[] splitted = line.split(" ");
				String word = splitted[0];
				if(! punctuations.contains(word)){
					//System.out.println("word: " + word);
					int randomCluster = random.nextInt(CLUSTER);
					paragraph += paragraphLineNumber++ + "\t" + line + "\t" + randomCluster + "\t" + (paragraphLineCount -1) + "\n";
					paragraphLineCount++;
				}
			}
		}
	}
	
	public void close() throws IOException{
		br.close();
		p.close();
	}
	
	public static void main(String[] args) throws IOException{
		String inFile = "/work/conll05-backup/combined/forAlexDepInduction/combined.word.hmm.trunc10";
		String outFile = "/work/conll05-backup/combined/forAlexDepInduction/combined.word.hmm.trunc10.nbayes";
		System.out.println("In File : " + inFile);
		System.out.println("Out File : " + outFile);
		PreprocessForNBayes rp = new PreprocessForNBayes(inFile,outFile);
		rp.run();
		rp.close();
		System.out.println("Done!");
	}

}
