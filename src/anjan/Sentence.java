package anjan;

import java.util.ArrayList;

public class Sentence extends ArrayList<DataRow>{
	public Sentence(){
		super();
	}
	
	public String debugString(){
		StringBuilder sb = new StringBuilder();
		for(DataRow dr : this){
			sb.append(dr.getWord() + " " + dr.getVerbOrNot() + " " + dr.getArg() + "\n");
		}
		return sb.toString();
	}
}
