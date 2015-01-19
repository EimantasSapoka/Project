package ford_fulkerson;

import java.io.File;
import java.io.IOException;

import ford_fulkerson.graph.Graph;

/**
 * class to run the application. 
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
			Algorithm.runLoadBalancedAlgorithm(graph);
			System.out.println(graph);
		}
	}
	
}
