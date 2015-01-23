package ford_fulkerson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import ford_fulkerson.graph.Graph;

/**
 * class to run the application. Will need to be extended with an elaborate UI 
 * @author Eimantas
 *
 */
public class UserInterface {

	public static void main(String[] args){
		
		if (args.length < 1 ){
			System.out.println("enter file path!");
			System.exit(1);
		}
		
		Graph graph = null;
		try {
			graph = TextScanner.parse(new File(args[0]));
		} catch (IOException e) {
			System.out.println("error reading file");
			e.printStackTrace();
			System.exit(1);
		}
		if (graph != null){
			
			
			if (args.length >1 && args[1].equalsIgnoreCase("balance") ){
				System.out.println("running load balanced algorithm");
				Algorithm.runLoadBalancedAlgorithm(graph);
			} else {
				System.out.println("running not load balanced algoritm");
				Algorithm.runUnbalancedAlgorithm(graph);

			}
			writeToFile("results.txt", graph);
		}
	}

	/**
	 * writes the output to file 
	 * @param String fileName the file name
	 * @param Graph graph the graph to output
	 */
	private static void writeToFile(String fileName, Graph graph) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
		} catch (FileNotFoundException e) {
			System.out.println("Failed writing to file!" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported exception!" + e.getMessage());
		}
		writer.println(graph.toString());
		writer.close();
	}
	
}
