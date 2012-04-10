package hmm;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Random;

/*
 * Creates HMM template required by jahmm
 */
public class CreateHmmTemplate {
	//static int HMM_STATES = 80;
	//static int MAX_INDEX = 14877; //number of observations
	static int HMM_STATES = 9;
	static int MAX_INDEX = 14; //maximum number it can generate
	
	static final double MAX_CHANGE_IN_STATE = 1.0/(HMM_STATES);
	static final double MAX_CHANGE_IN_INDEX = 1.0/(MAX_INDEX);
	static DecimalFormat df = new DecimalFormat("#.#########");
	public static void main(String[] args) throws IOException{
		if(args.length != 1){
			System.err.println("Usage: <program> outputFile");
			System.exit(1);
		}
		String outputFile = args[0];
		PrintWriter p = new PrintWriter(new File(outputFile));
		p.println("Hmm v1.0");
		p.println();
		p.println("NbStates " + HMM_STATES);
		
		double[] randVariations = getRandomProbabilites(HMM_STATES, MAX_CHANGE_IN_STATE);
		double sumHmmExceptLast = 0;
		for(int i=0; i<HMM_STATES - 1; i++){
			System.out.println("State: " + i);
			p.println("State");
			double prob = 1.0/HMM_STATES + randVariations[i];
			p.println("Pi " +  df.format(prob));
			p.println(getTransitionProbabilities());
			p.println(getOPdf());
			p.println();
			sumHmmExceptLast +=  prob;
		}
		p.println("State");
		double prob = 1 - sumHmmExceptLast;
		p.println("Pi " +  df.format(prob));
		p.println(getTransitionProbabilities());
		p.println(getOPdf());
		p.println();
		
		p.flush();
		p.close();
		System.out.println("Done!");
	}
	
	public static String getTransitionProbabilities(){
		String transitionProb = "A ";
		double randVariations[] = getRandomProbabilites(HMM_STATES, MAX_CHANGE_IN_STATE);
		double sumTransitionExceptLast = 0;
		for(int i=0; i<HMM_STATES - 1; i++){
			double randProb = 1.0/HMM_STATES;
			randProb += randVariations[i];
			transitionProb += df.format(randProb) + " ";
			sumTransitionExceptLast += randProb;
		}
		double randProb = 1 - sumTransitionExceptLast;
		transitionProb += df.format(randProb) + " ";
		//System.out.println(transitionProb);
		return transitionProb;
	}
	
	public static String getOPdf(){
		String OPdf= "IntegerOPDF [";
		double[] randVariations = getRandomProbabilites(MAX_INDEX, MAX_CHANGE_IN_INDEX);
		double sumExceptLast = 0;
		for(int i=0; i<MAX_INDEX - 1; i++){
			double randProb = 1.0/MAX_INDEX;
			randProb += randVariations[i];
			OPdf += df.format(randProb) + " ";
			sumExceptLast += randProb;
		}
		double randProb = 1 - sumExceptLast;
		OPdf += df.format(randProb) + " ";
		OPdf += "]";
		//System.out.println(OPdf);
		return OPdf;
	}
	
	public static double[] getRandomProbabilites(int SIZE, double MAX_CHANGE){
		Random r = new Random();
		double[] randVariations = new double[SIZE];
		double sum = 0;
		for(int i=0; i<SIZE/2; i++){
			double rand = r.nextInt(5) + 5; // range 5-9
			rand = rand/10; //0.5 to 0.9
			if(r.nextBoolean()){
				randVariations[i] = rand * MAX_CHANGE;
				sum += randVariations[i];
			}
			else { //negative
				randVariations[i] = - rand * MAX_CHANGE;
				sum += randVariations[i];
			}
		}
		if(SIZE % 2 == 0){
			for(int i=SIZE/2; i<SIZE; i++){
				randVariations[i] = -randVariations[i-SIZE/2];
				sum += randVariations[i];
			}
		} else {
			for(int i=SIZE/2; i<SIZE-1; i++){
				randVariations[i] = -randVariations[i-SIZE/2];
				sum += randVariations[i];
			}
		}
		//System.out.println("Making sure random variations sum to zero : " + sum);
		return randVariations;
	}
	
}
