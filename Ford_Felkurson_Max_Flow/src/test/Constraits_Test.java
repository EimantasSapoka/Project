package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import test.graph_creator.RandomGraph;
import ford_fulkerson.Algorithm;
import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Project;
import ford_fulkerson.graph.Reader;

public class Constraits_Test {
	
	Graph graph;
	
	
	
	@Before 
	public void createGraph(){
		graph = new RandomGraph();
		Algorithm.runAlgorithm(graph);
		System.out.println(graph);
	}
	
	@Test
	public void checkOutput(){
		int capacity;
		int assigned;
		ArrayList<Integer> alreadyTaken = new ArrayList<Integer>();
		
		
		for (Reader r: graph.getReaders()){
			assigned = 0;
			capacity = r.getCapacity();
			for (Edge e : r.getVertex().getOutEdges()){
				if (e.getFlow() > 0){
					assigned ++;
					int projectID = e.getDestination().getObjectID();

					// checks that edge is not over it's capacity
					assertTrue(e.getFlow()<=e.getCapacity());
					// checks that project is in preference list
					assertTrue(projectInList(projectID, r.getPreferences()));
					// checks that the project had not been assigned to another reader already
					assertFalse(alreadyTaken.contains(projectID));
					
					alreadyTaken.add(projectID);
					
				}
			}
			
			// checks that it assigned less than the capacity
			assertTrue(capacity >= assigned);
			
			
		}
		
		
		
	}
	
	/**
	 * checks if the project id is in the reader's preference list
	 * @param projectID
	 * @param preferences
	 * @return
	 */
	private boolean projectInList(int projectID, ArrayList<Project> preferences){
		for (Project p: preferences){
			if (p.getId() == projectID){
				return true;
			}
		}
		return false;
	}

}
