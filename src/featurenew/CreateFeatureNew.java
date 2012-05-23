package featurenew;

import java.io.IOException;

public class CreateFeatureNew {
	public static void main(String[] args) throws IOException{
		if(args.length != 2){
			System.err.println("Usage: <createfeaturenew> inputfile outputfile");
			System.exit(1);
		}
		String vocabIndexFile = "/work/srl/propprocess/biocommaprocessed/vocab_index_for_feature.txt";
		VocabIndexReader.__init__(vocabIndexFile);
		FeatureNew generator = new FeatureNew(args[0], args[1]);
		System.out.println("Running FeatureNew generator");
		System.out.println("Input file: " + args[0]);
		System.out.println("Output file: " + args[1]);
		generator.run();
		System.out.println("Done!");
	}
}
