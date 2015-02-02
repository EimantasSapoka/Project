package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import test.graph_creator.RandomArbitraryModel;
import test.graph_creator.RandomReaderAllocationModel;
import ford_fulkerson.Algorithm;
import ford_fulkerson.ReaderShortlistException;
import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.MCMFModel;
import model.Project;
import model.Reader;

public class Constraints_Test {
	
	private static final int TEST_COUNT = 100;
	MCMFModel arbitraryGraph,readerGraph;;
	
	/**
	 * checks that the constraints hold after running the algorithm on the 
	 * reader allocation graph
	 */
	@Test
	public void testReaderGraph(){
		for (int i=0; i<TEST_COUNT; i++){

			readerGraph = new RandomReaderAllocationModel();
                        try {
                            readerGraph.createGraph();
                        } catch (ReaderShortlistException ex) {
                            System.out.println(ex.getMessage());
                        }
			Algorithm.runLoadBalancedAlgorithm(readerGraph);
			checkReaderConstraints(readerGraph);
		}
	}

	/**
	 * loops through graph's readers list and checks constraints, 
	 * such as if the reader has the assigned project in it's preference
	 * list as well as more general tests like the edge flow can't exceed 
	 * its capacity
	 * @param graph
	 */
	public static void checkReaderConstraints(MCMFModel model) {
		int capacity;
		int assigned;
		ArrayList<Integer> alreadyTaken = new ArrayList<Integer>();
		
		
		for (Reader r: model.getReaders()){
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
		for (int i = 0; i< TEST_COUNT ; i++){
			arbitraryGraph = new RandomArbitraryModel();
                        MCMFModel model = new MCMFModel();
                        try {
                            arbitraryGraph.createGraph();
                        } catch (ReaderShortlistException ex) {
                            System.out.println(ex.getMessage());
                        }
			Algorithm.runLoadBalancedAlgorithm(arbitraryGraph);
			graphConstraintsTests(arbitraryGraph.getGraph());
		}
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
