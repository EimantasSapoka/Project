package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import test.graph_creator.RandomArbitraryGraph;
import test.graph_creator.RandomReaderAllocationGraph;
import ford_fulkerson.Algorithm;
import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Project;
import ford_fulkerson.graph.Reader;

public class Constraits_Test {
	
	Graph arbitraryGraph,readerGraph;
	
	
	
	@Before 
	public void createGraph(){
		arbitraryGraph = new RandomArbitraryGraph();
		readerGraph = new RandomReaderAllocationGraph();
	}
	
	/**
	 * checks that the constraints hold after running the algorithm on the 
	 * reader allocation graph
	 */
	@Test
	public void checkReaderGraph(){
		
		Algorithm.runAlgorithm(readerGraph);
		int capacity;
		int assigned;
		ArrayList<Integer> alreadyTaken = new ArrayList<Integer>();
		
		
		for (Reader r: readerGraph.getReaders()){
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
		
	@Test
	public void testArbitraryGraph(){
		Algorithm.runAlgorithm(arbitraryGraph);
		int flowIn = 0;
		int flowOut = 0;
		
		for (Edge e : arbitraryGraph.getEdges()){
			// checks that the flow is equal or less to the capacity
			assertTrue(e.getFlow() <= e.getCapacity());
			
			if (e.getParent().equals(arbitraryGraph.source())){
				flowIn += e.getFlow();
			}
			if (e.getDestination().equals(arbitraryGraph.sink())){
				flowOut += e.getFlow();
			}
			
		}
		// checks that flow coming into the graph is the same as the flow going out.
		assertEquals(flowIn, flowOut);
		arbitraryGraph.statistics();
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
