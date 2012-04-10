package featurenew;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

public abstract class FeatureGeneratorBase {
	Logger logger = Logger.getLogger(FeatureGeneratorBase.class.getName());
	protected final int DEFAULT_VERB_INDEX = -1;
	protected static final String ALPHA = "A";
	protected static final String NUM = "N";
	protected static final String NO_VERB= "NV";
	protected static final String VERB= "V";
	protected static final String UP = "U";
	protected static final String DOWN = "D";
	protected static final String JOIN = "+";
	protected static final String NOUP = "NU";
	protected static final String NODOWN = "ND";
	protected static final String BEFORE = "B";
	protected static final String AFTER = "A";
	protected static final String ERROR = "E";
	protected static final String OOB = "OOB"; //out of bound
	protected static final String YES = "Y";
	protected static final String NO = "N";
	protected static final String NO_ARG = "O";
	protected static final String NO_PATH = "NO_PATH";
	protected static final String NO_SUFFIX = "NO_SUF";
	protected static final String NO_HMM = "NO_HMM";
	protected static final String NO_WORD = "NO_WORD";
	protected static final String NO_HMM1 = "NO_HMM1";
	protected static final String NO_WORD1 = "NO_WORD1";
	protected static final String NO_HMM2 = "NO_HMM2";
	protected static final String NO_WORD2 = "NO_WORD2";
	
	private String inputFilename;
	private String outputFilename;
	ArrayList<Sentence> data;
	PrintWriter pw;
	public FeatureGeneratorBase(String inputFilename, String outputFilename){
		this.inputFilename = inputFilename;
		this.outputFilename = outputFilename;
	}
	public void setInputFile(String filename){
		inputFilename = filename;
	}
	public void setOutputfile(String filename){
		outputFilename = filename;
	}
	
	public void load() throws IOException{
		//check if output file is writable
		try{
			pw = new PrintWriter(new File(outputFilename));
		}
		catch(FileNotFoundException f){
			System.err.println("Output file cannot be written : " + outputFilename);
			System.exit(1);
		}
		
		//load the sentences into the memory
		BufferedReader br = null;
		try{
			 br = new BufferedReader(new FileReader(new File(inputFilename)));
		}
		catch(FileNotFoundException fileNotFoundException){
			System.err.println("Input file for feature generation not found: " + inputFilename);
			System.exit(1);
		}
		
		data = new ArrayList<Sentence>();
		String line = "";
		Sentence s = new Sentence();
		int wordIndex = 0;
		while( (line = br.readLine()) != null){
			if(!line.trim().equals("")){
				DataRow dr = new DataRow();
				dr.processLine(line.trim(), wordIndex);
				s.add(dr);
				wordIndex++;
			}
			else{
				//one sentence processing complete
				if(s.size() != 0){
					data.add(s);
				}
				//System.out.println(s.debugString());
				s = null;
				s = new Sentence();
				wordIndex = 0;
			}
		}
		br.close();
	}
	
	public abstract void run();
	
	public void close(){
		pw.close();
	}
}
