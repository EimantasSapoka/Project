package test;

import static org.junit.Assert.*;

import org.junit.Test;

import augustineMCMF.MinCostMaxFlowSPA;
import augustineMCMF.Network;
import test.graph_creator.RandomArbitraryGraph;
import test.graph_creator.RandomReaderAllocationGraph;
import ford_fulkerson.Algorithm;
import ford_fulkerson.graph.Graph;

public class CorrectnessTest {

	private static final int TEST_COUNT = 100;
	Graph arbitraryGraph, readerGraph, graph;
	
	
	/**
	 * test which creates a random reader allocation graph and
	 * runs it against two algorithms. (Packages ford_fulkerson and 
	 * mcmf) then checks that they return a similar flow size
	 * @throws Exception 
	 */
	@Test
	public void readerAllocationGraphTest() throws Exception{
		for (int i=0; i<TEST_COUNT; i++){
			// create a new random reader graph
			readerGraph = new RandomReaderAllocationGraph();
			
			MinCostMaxFlowSPA alg = new MinCostMaxFlowSPA();
			Network network = alg.createNetworkFromGraph(readerGraph);
			
			//run against both algorithms
			Algorithm.runAlgorithm(readerGraph);
			network = alg.solve(network);
			
			
			if (network.getFlowSize() != readerGraph.getFlow() || network.getFlowCost() != readerGraph.getWeight()){
				System.out.println("augustine weight: " + network.getFlowCost() + " augustine flow: " + network.getFlowSize() +
									"\nmy weight: " + readerGraph.getWeight() + " my flow: " + readerGraph.getFlow());
			}
			
			
			// assert equal flow sizes and check reader graph constraints
			Constraints_Test.checkReaderConstraints(readerGraph);
			assertTrue(network.getFlowSize() <= readerGraph.getFlow());
			assertTrue(network.getFlowCost() >= readerGraph.getWeight());
		}
	}
	
	/**
	 * test which creates a random arbitrary graph and
	 * runs it against two algorithms. (Packages ford_fulkerson and 
	 * mcmf) then checks that they return a similar flow size
	 * @throws Exception 
	 */
	@Test
	public void arbitraryGraphTest() throws Exception{
		for (int i = 0; i<TEST_COUNT; i++){
			
			// create a new random arbitrary graph
			arbitraryGraph = new RandomArbitraryGraph();
			
			MinCostMaxFlowSPA alg = new MinCostMaxFlowSPA();
			Network network = alg.createNetworkFromGraph(arbitraryGraph);
			
			// run the graph against both algorithms
			Algorithm.runAlgorithm(arbitraryGraph);
			network = alg.solve(network);
			
			if (network.getFlowSize() != arbitraryGraph.getFlow() || network.getFlowCost() != arbitraryGraph.getWeight()){
				System.out.println("augustine weight: " + network.getFlowCost() + " augustine flow: " + network.getFlowSize() +
									"\nmy weight: " + arbitraryGraph.getWeight() + " my flow: " + arbitraryGraph.getFlow());
			}
			
			// check if the graph satisfies constraints as well as if sizes are similar
			Constraints_Test.graphConstraintsTests(arbitraryGraph);		
			assertTrue(network.getFlowSize() <= arbitraryGraph.getFlow());
			assertTrue(network.getFlowCost() >= arbitraryGraph.getWeight());
		}
	}
	
}