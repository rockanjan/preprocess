package anjan;

public class DataRow {
	private int index;
	private String word;
	private String verbOrNot;
	private String bio;
	private String arg;
	public DataRow(){
		
	}
	public DataRow(int index, String word, String verbOrNot, String bio, String arg) {
		this.index = index;
		this.word = word;
		this.verbOrNot = verbOrNot;
		this.bio = bio;
		this.arg = arg;
	}
	public DataRow(int index, String line) {
		String[] splitted = line.split("(\\s+|\\t+)");
		this.index = index;
		this.word = splitted[0];
		this.verbOrNot = splitted[2];
		this.bio = splitted[4];
		this.arg = splitted[5];
	}
	public boolean hasVerb(){
		return verbOrNot.equals("V");
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getVerbOrNot() {
		return verbOrNot;
	}
	public void setVerbOrNot(String verbOrNot) {
		this.verbOrNot = verbOrNot;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getArg() {
		return arg;
	}
	public void setArg(String arg) {
		this.arg = arg;
	}
}
