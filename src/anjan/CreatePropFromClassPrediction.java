package anjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CreatePropFromClassPrediction {
	public static void main(String[] args) throws IOException{
		String goldPropFile = "/home/anjan/Dropbox/research/srl/dev/final/gold/dev-set.nopunct.trunc50.prop";
		String originalPropProcessedFile = "/home/anjan/Dropbox/research/srl/dev/final/dev.propprocesssed";
		
		/*
		String originalFile = "/home/anjan/work/srl/jul11/dev.classification";
		String taggedFile = "/home/anjan/work/srl/jul11/dev.classification.tagged";
		*/
		
		String originalFile = "/home/anjan/work/srl/jul11/dev.predictediob.classification";
		String taggedFile = "/home/anjan/work/srl/jul11/dev.classification.withpredictedbio.tag";
		
		String outFile = "/home/anjan/work/srl/jul11/dev.predicted.withpredictedbio.prop";
		BufferedReader brOrg = new BufferedReader(new FileReader(originalFile));
		BufferedReader brTagged = new BufferedReader(new FileReader(taggedFile));
		
		/*
		 * Combine IOB file with predicted tags
		 */
		String combinedTmpFile = "/tmp/combined.withpredictedclass.tmp";
		PrintWriter combinedTmp = new PrintWriter(new File(combinedTmpFile));
		
		String lineOrg = "";
		String lineTagged = "";
		
		while ( (lineOrg = brOrg.readLine()) != null && (lineTagged = brTagged.readLine()) != null) {
			String[] splittedLine = lineOrg.split("(\\s+)|(\\t+)");
			String newLine = "";
			for(int i=0; i < splittedLine.length -1 ; i++) {
				newLine += splittedLine[i] + " ";
			}			
			newLine += lineTagged;
			combinedTmp.println(newLine);
			combinedTmp.flush();
		}
		brOrg.close();
		brTagged.close();
		combinedTmp.close();
		
		/*
		 * Postprocess
		 */
		String postProcessFile = combinedTmpFile + ".postprocessed";
		String[] argsPostProcess = {combinedTmpFile, postProcessFile};
		PostProcess.main(argsPostProcess);
		
		/*
		 * Create Prop
		 */
		//create prop required standard fields
		BufferedReader brPostProcessed = new BufferedReader(new FileReader(postProcessFile));
		BufferedReader brOriginalPropProcessed = new BufferedReader(new FileReader(originalPropProcessedFile));
		PrintWriter pwForProp = new PrintWriter("/tmp/combined.forprop");
		//replace final two columns with predicted IOB and the class
		String linePostProcessed = "";
		String lineOriginalPropProcessed = "";
		
		while( (linePostProcessed = brPostProcessed.readLine()) != null && (lineOriginalPropProcessed = brOriginalPropProcessed.readLine()) != null) {
			if(! linePostProcessed.trim().equals("")){
				String[] splittedPostProcessed = linePostProcessed.split("(\\s+)|(\\t+)");
				String[] splittedOriginalPropProcessed = lineOriginalPropProcessed.split("(\\s+)|(\\t+)");
				pwForProp.println(splittedOriginalPropProcessed[0] + " " +
						splittedOriginalPropProcessed[1] + " " +
						splittedOriginalPropProcessed[2] + " " +
						splittedOriginalPropProcessed[3] + " " +
						splittedPostProcessed[splittedPostProcessed.length - 2] + " " +
						splittedPostProcessed[splittedPostProcessed.length - 1]);
				
				pwForProp.flush();
			} else {
				if(! lineOriginalPropProcessed.trim().equals("")) {
					System.err.println("Sentence not aligned.");
				}
				pwForProp.println("");
			}
		}
		brPostProcessed.close();
		brOriginalPropProcessed.close();
		pwForProp.close();
		
		String[] argsCreateProp = {"/tmp/combined.forprop"};
		CreateProp.main(argsCreateProp);
		
		//fix the verbs in the predicted prop file
		String line1 = "";
		String line2 = "";
		BufferedReader br1 = new BufferedReader(new FileReader(goldPropFile));
		BufferedReader br2 = new BufferedReader(new FileReader("/tmp/combined.forprop.prop"));
		PrintWriter pw = new PrintWriter("/tmp/final.predicted.prop");
		while( (line1 = br1.readLine()) != null && (line2 = br2.readLine()) != null ) {
			if(! line1.equals("")) {
				String[] splitted1 = line1.split("(\\s+)|(\\t+)");
				String[] splitted2 = line2.split("(\\s+)|(\\t+)");
				pw.print(splitted1[0]);
				pw.print("\t");
				for(int i=1; i<splitted2.length; i++) {
					pw.print(splitted2[i] + "\t");
				}
				pw.print("\n");
			} else {
				if(! line2.equals("")) {
					System.err.println("Sentences not aligned...");
				}
				pw.println("");
			}
		}
		br1.close();
		br2.close();
		pw.close();
		
		Runtime.getRuntime().exec("cp " + "/tmp/final.predicted.prop" + " " + outFile);
		System.out.println("Final predicted prop file written at : " + outFile);
		
	}
}
