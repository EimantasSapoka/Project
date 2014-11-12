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

public class Constraints_Test {
	
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
		graphConstraintsTests(arbitraryGraph);
		arbitraryGraph.statistics();
	}

	
	
	/**
	 * checks if the project id is in the reader's preference list
	 * @param projectID
	 * @param preferences
	 * @return
	 */
	private static boolean projectInList(int projectID, ArrayList<Project> preferences){
		for (Project p: preferences){
			if (p.getId() == projectID){
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * method which loops through graph's edges and checks
	 * that each edge's flow does not exceed capacity and
	 * flow into the graph is equal to flow out of the graph.
	 * @param graph
	 */
	public static void graphConstraintsTests(Graph graph) {
		int flowIn = 0;
		int flowOut = 0;
		int capacityIn = 0;
		int capacityOut = 0;
		
		for (Edge e : graph.getEdges()){
			// checks that the flow is equal or less to the capacity
			assertTrue(e.getFlow() <= e.getCapacity());
			
			if (e.getParent().equals(graph.source())){
				flowIn += e.getFlow();
				capacityIn += e.getCapacity();
			}
			if (e.getDestination().equals(graph.sink())){
				flowOut += e.getFlow();
				capacityOut += e.getCapacity();
			}
			
		}
		// checks that flow coming into the graph is the same as the flow going out.
		assertEquals(flowIn, flowOut);
		assertTrue(flowIn <= capacityIn);
		assertTrue(flowOut <= capacityOut);
		
	}
}
