package featurenew;

import java.util.logging.Logger;

/*
 * stores each row data of a sentence
 */
public class DataRow {
	private Logger logger = Logger.getLogger(DataRow.class.getName());
	private int index = -1;
	private String word;
	private String predicateLabel;
	private String intervenes; //number of verbs intervenining between word and current predicate
	private int hmmState;
	private int hmmState2;
	private String identificationLabel;
	private String classificationLabel;
	
	public void processLine(String line, int index){
		//processes a string of line, stores the field
		String[] splitted = line.split(" ");
		if(splitted.length < 7){
			System.err.println("DataRow: Cannot process line : " + line);
			System.exit(1);
		}
		this.index = index;
		word = splitted[0];
		predicateLabel = splitted[1];
		intervenes = splitted[2];
		/**
		 * NOTE: MODIFIED HERE FOR EXPERIMENT
		 */
		hmmState = Integer.parseInt(splitted[4]);
		hmmState2 = Integer.parseInt(splitted[3]);
		/*
		 * CHANGE LATER
		 */
		identificationLabel = splitted[5];
		classificationLabel = splitted[6];
		if(splitted.length > 7){
			System.err.println("WARNING: data row has more than required columns");
		}
	}
	
	public int getIndex(){
		return index;
	}
	public void setIndex(int index){
		this.index = index;
	}
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getHmmState(){
		return hmmState;
	}
	
	public int getHmmState2(){
		return hmmState2;
	}
	
	public String getPredicateLabel() {
		return predicateLabel;
	}
	public void setPredicateLabel(String predicateLabel) {
		this.predicateLabel = predicateLabel;
	}
	public String getIdentificationLabel() {
		return identificationLabel;
	}
	public void setIdentificationLabel(String identificationLabel) {
		this.identificationLabel = identificationLabel;
	}
	public String getClassificationLabel() {
		return classificationLabel;
	}
	public void setClassificationLabel(String classificationLabel) {
		this.classificationLabel = classificationLabel;
	}

	/* variadic function */
	public String getRowWithAppendedFeatureNew(String... features) {
		StringBuilder sb = new StringBuilder();
		sb.append(word.toLowerCase() + " ");
		sb.append(VocabIndexReader.getIndex(word) + " ");
		sb.append(Stemmer.stemWord(word.toLowerCase()) + " ");
		sb.append(predicateLabel + " ");
		sb.append(intervenes + " ");
		sb.append(hmmState + " ");
		sb.append(hmmState2 + " ");
		sb.append(hmmState + "+" + hmmState2 + " ");
		for(String feature: features){
			sb.append(feature.toLowerCase() + " ");
		}
		sb.append(identificationLabel + " ");
		sb.append(classificationLabel + " ");
		return sb.toString();
	}
	
	
}
