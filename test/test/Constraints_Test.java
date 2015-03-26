package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import test.graph_creator.RandomArbitraryModel;
import test.graph_creator.RandomReaderAllocationModel;
import ford_fulkerson.MinCostMaxFlowAlgorithm;
import ford_fulkerson.ReaderShortlistException;
import ford_fulkerson.model.MCMFModel;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;
import ford_fulkerson.network.Edge;
import ford_fulkerson.network.Network;

public class Constraints_Test {
	
	private static final int TEST_COUNT = 1000;
	MCMFModel arbitraryGraph,readerGraph;
	
	/**
	 * checks that the constraints hold after running the algorithm on the 
	 * reader allocation graph
	 */
	@Test
	public void testReaderGraph(){
		for (int i=0; i<TEST_COUNT; i++){
			readerGraph = new RandomReaderAllocationModel(i%50+1, i%400+1);
            try {
                readerGraph.createNetwork();
            } catch (ReaderShortlistException ex) {
                // we don't really care for any reader shortlists. So do nothing.
            }
			MinCostMaxFlowAlgorithm.runLoadBalancedAlgorithm(readerGraph);
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
			capacity = r.getReaderTarget();
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
	public void testArbitraryGraph() throws ReaderShortlistException{
		for (int i = 0; i< TEST_COUNT ; i++){
			arbitraryGraph = new RandomArbitraryModel(i%50+1, i%400+1);
			MinCostMaxFlowAlgorithm.runLoadBalancedAlgorithm(arbitraryGraph);
			networkConstraintsTests(arbitraryGraph.getNetwork());
		}
	}

	
	
	/**
	 * checks if the project id is in the reader's preference list
	 * @param projectID
	 * @param list
	 * @return
	 */
	private static boolean projectInList(int projectID, List<Project> list){
		for (Project p: list){
			if (p.getID() == projectID){
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * method which loops through graph's edges and checks
	 * that each edge's flow does not exceed capacity and
	 * flow into the graph is equal to flow out of the graph.
	 * @param network
	 */
	public static void networkConstraintsTests(Network network) {
		int flowIn = 0;
		int flowOut = 0;
		int capacityIn = 0;
		int capacityOut = 0;
		
		for (Edge e : network.getEdges()){
			// checks that the flow is equal or less to the capacity
			assertTrue(e.getFlow() <= e.getCapacity());
			
			if (e.getSource().equals(network.source())){
				flowIn += e.getFlow();
				capacityIn += e.getCapacity();
			}
			if (e.getDestination().equals(network.sink())){
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
