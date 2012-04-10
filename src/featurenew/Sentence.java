package featurenew;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Sentence extends ArrayList<DataRow>{
	Logger logger = Logger.getLogger(Sentence.class.getName());
	public Sentence(){
		super();
	}
	
	public int getVerbWordIndex(){
		int returnValue = -1;
		for(DataRow dr : this){
			if(dr.getPredicateLabel().equals("V")){
				returnValue = dr.getIndex();
				break;
			}
		}
		return returnValue;
	}
	
	public String debugString(){
		StringBuilder sb = new StringBuilder();
		for(DataRow dr : this){
			sb.append(dr.getIndex() + " " + dr.getWord() + " ");
		}
		return sb.toString();
	}
}
