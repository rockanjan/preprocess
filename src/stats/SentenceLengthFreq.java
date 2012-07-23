package stats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
 *	Counts the length of sentences and their frequencies 
 */
public class SentenceLengthFreq {
	public static void main(String[] args) throws IOException {
		String file = "/home/anjan/work/conll05-backup/combined/combined.lower.word";
		int N=50;
		Integer[] freq = new Integer[N+1];
		for(int i=0; i<N+1; i++) {
			freq[i] = new Integer(0);
		}
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		int sentenceLength = 0;
		while ( (line = br.readLine()) != null) {
			line = line.trim();
			if( ! line.isEmpty() ) {
				sentenceLength++;
			} else {
				if(sentenceLength != 0) {
					freq[sentenceLength]++;
					sentenceLength = 0;
				}
			}
		}
		br.close();
		
		for(int i=1; i<=N; i++) {
			System.out.println("Length " + i + " = " + freq[i]);
		}
		
		int above40 = 0;
		for(int i=41; i<=N; i++) {
			above40 += freq[i];
		}
		System.out.println("Above 40 = " + above40);
	}
}
