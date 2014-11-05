package ford_fulkerson;

import java.io.File;
import java.io.IOException;

import ford_fulkerson.graph.Graph;

public class UserInterface {

	public static void main(String[] args){
		
		/*// hardcoded gaph for testing
		Graph graph = new Graph();
		Reader reader = new Reader(1,2);
		Reader reader2 = new Reader(2,2);
		Reader reader3 = new Reader(3,2);
		
		Project[] projects = new Project[10];
		for (int i = 0; i<5; i++){
			projects[i] = new Project(i);
		}
		
		reader.addPreference(projects[0]);
		reader.addPreference(projects[1]);
		
		reader2.addPreference(projects[1]);
		reader2.addPreference(projects[2]);
		
		reader3.addPreference(projects[2]);
		reader3.addPreference(projects[3]);
		
		
		graph.addReader(reader);
		graph.addReader(reader2);
		graph.addReader(reader3);
		
		
		//graph.graphDescription();
		
		*/
		
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
			Algorithm.runAlgorithm(graph);
			System.out.println(graph);
		}
	}
	
}
