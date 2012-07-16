package anjan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunSystemCommand {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		String command = "";
		for(String x : args){
			command += x + " ";
		}
		System.out.println("Running System Command : " + command);
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
		String line="";
		String output="";
		while( (line = reader.readLine()) !=null) 
		{ 
			output += line + "\n"; 
		}
		System.out.println(output);
		System.out.println("System command run complete!");
	}
	
	public static String run(String command){
		//command = "/bin/bash -c " + "'" + command + "'";
		System.out.println("Running System Command : " + command);

		String output="";
		try{
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			if(p.exitValue() != 0) {
				System.err.println("Error in running!!!");
				BufferedReader errorReader=new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String errorLine="";
				while( (errorLine = errorReader.readLine()) !=null) 
				{ 
					output += errorLine + "\n"; 
				}
				System.err.println(output);
				System.exit(1);
			}
			BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
			String line="";
			while( (line = reader.readLine()) !=null) 
			{ 
				output += line + "\n"; 
			}
			//System.out.println(output);
			System.out.println("System command run complete!");
		}
		catch(Exception e){
			System.err.println("Error running command " + command);
			e.printStackTrace();
		}
		return output;
	}

}
