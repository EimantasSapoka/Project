package ford_fulkerson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import ford_fulkerson.graph.Graph;
import model.MCMFModel;

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
                MCMFModel model = new MCMFModel(new File(args[0]));
		
		if (model.getGraph() != null){
			
			
			if (args.length >1 && args[1].equalsIgnoreCase("balanced") ){
				System.out.println("running load balanced algorithm");
				Algorithm.runLoadBalancedAlgorithm(model);
			} else {
				System.out.println("running not load balanced algoritm");
				Algorithm.runUnbalancedAlgorithm(model);

			}
			writeToFile("results.txt", model);
		}
	}

	/**
	 * writes the output to file 
	 * @param String fileName the file name
	 * @param Graph graph the graph to output
	 */
	private static void writeToFile(String fileName, MCMFModel model) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
		} catch (FileNotFoundException e) {
			System.out.println("Failed writing to file!" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported exception!" + e.getMessage());
		}
		writer.println(model.toString());
		writer.close();
	}
	
}
