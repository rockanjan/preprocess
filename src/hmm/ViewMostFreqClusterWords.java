package hmm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ViewMostFreqClusterWords {
	public static void main(String[] args) throws IOException {
		int NUM = 10; //display top 5 words
		int CLUSTERS = 80;
		//String inFile = "/home/anjan/work/srl/jul19/hmms/combined.word.hmm";
		String inFile = "/home/anjan/work/srl/jul11/hmms/combined.word.hmm";
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		ClusterContent[] clusters = new ClusterContent[CLUSTERS];
		for(int i=0; i<CLUSTERS; i++) {
			clusters[i] = new ClusterContent();
		}
		String line = "";
		while ( (line = br.readLine()) != null) {
			line = line.trim();
			if(! line.isEmpty() ) {
				String[] splitted = line.split("(\\s+|\\t+)");
				int hmm = Integer.parseInt(splitted[0]);
				//System.out.println(hmm);
				String word = splitted[1];
				clusters[hmm].add(word);
			}
		}
		int counter = 0;
		for( ClusterContent cc : clusters ) {
			displayTop(counter, NUM, cc);
			counter++;
		}
	}
	
	public static void displayTop(int counter, int num, ClusterContent cc){
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(String word : cc) {
			if(map.containsKey(word)) {
				map.put(word, map.get(word) + 1);
			} else {
				map.put(word, 1);
			}
		}
        ValueComparator bvc =  new ValueComparator(map);
        TreeMap<String,Integer> sorted_map = new TreeMap(bvc);
        sorted_map.putAll(map);
        
        //display
        System.out.print("Cluster " + counter + " : ");
        int dispCount = 0;
        for(String key : sorted_map.keySet()) {
        	if(dispCount == num) break;
        	System.out.print(key + " ");
        	dispCount++;
        }
        System.out.println();
	}
}

class ValueComparator implements Comparator {

	  Map base;
	  public ValueComparator(Map base) {
	      this.base = base;
	  }

	  public int compare(Object a, Object b) {

	    if((Integer)base.get(a) < (Integer)base.get(b)) {
	      return 1;
	    } else if((Integer)base.get(a) == (Integer)base.get(b)) {
	      return 0;
	    } else {
	      return -1;
	    }
	  }
}

