package test;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import augustineMCMF.MinCostMaxFlowSPA;
import augustineMCMF.Network;
import test.graph_creator.RandomArbitraryGraph;
import test.graph_creator.RandomReaderAllocationGraph;
import ford_fulkerson.Algorithm;
import ford_fulkerson.graph.Graph;

public class CorrectnessTest {

	private static final int TEST_COUNT = 10000;
	MinCostMaxFlowSPA alg;
	
	@Before 
	public void initialize(){
		alg = new MinCostMaxFlowSPA();
	}
	
	
	/**
	 * test which creates a random reader allocation graph and
	 * runs it against two algorithms. (Packages ford_fulkerson and 
	 * augustineMCMF) then checks that they return a similar flow size
	 * @throws Exception 
	 */
	@Test
	public void readerAllocationGraphTest() throws Exception{
		System.out.println("STARTING READER GRAPH TESTS");
		for (int i=0; i<TEST_COUNT; i++){
			if (i%1000 == 0){
				System.out.println("TEST " + i);
			}
			
			// create a new random reader graph
			Graph testGraph = new RandomReaderAllocationGraph(i%80 + 20, 3, 3);
			
			Network network = alg.createReaderNetworkFromGraph(testGraph);
			
			runTests(testGraph, network);
			
			// check reader graph constraints
			Constraints_Test.checkReaderConstraints(testGraph);
			
			
			
		}
	}
	
	/**
	 * test which creates a random arbitrary graph and
	 * runs it against two algorithms. (Packages ford_fulkerson and 
	 * augustineMCMF) then checks that they return a similar flow size
	 * @throws Exception 
	 */
	@Test
	public void arbitraryGraphTest() throws Exception{
		for (int i = 0; i<TEST_COUNT; i++){
			if (i%1000 == 0) System.out.println("TEST " + i);
			
			// create a new random arbitrary graph
			Random rand = new Random();
			Graph testGraph = new RandomArbitraryGraph(rand.nextInt(i%18+1) +2, rand.nextInt(i%85+1)+15);
			
			Network network = alg.createNetworkFromGraph(testGraph);
			
			runTests(testGraph, network);
			
			// check if the graph satisfies constraints
			Constraints_Test.graphConstraintsTests(testGraph);		
		}
	}
	
	/**
	 * a method which runs both algorithms on the testGraph and network
	 * @throws Exception
	 */
	private void runTests(Graph testGraph, Network network) throws Exception {
		
		//run against both algorithms, measuring performance
		long start = System.nanoTime();
		Algorithm.runLoadBalancedAlgorithm(testGraph);
		long performanceMine = System.nanoTime() - start;
		
		long start2 = System.nanoTime();
		network = alg.solve(network);
		long performanceAugustine = System.nanoTime() - start2;
		
		if (performanceAugustine < performanceMine){
			System.out.println("Augustine algorithm performed faster by " + (performanceMine-performanceAugustine)/1000000 + "ms");
			System.out.println(testGraph.getVertices().size() + " " + testGraph.getEdges().size()+ "\n");
		}
	
		if (network.getFlowSize() != testGraph.getFlow() || network.getFlowCost() != testGraph.getWeight()){
			System.out.println("augustine weight: " + network.getFlowCost() + " augustine flow: " + network.getFlowSize() +
								"\nmy weight: " + testGraph.getWeight() + " my flow: " + testGraph.getFlow());
			System.out.println("Augustine balanced: " + network.isLoadBalanced() + " mine balanced: " + testGraph.isLoadBalanced());
		}
		
		// assert equal flow sizes and weights 
		assertTrue(network.getFlowSize() <= testGraph.getFlow());
		if (!(testGraph.isLoadBalanced() || !network.isLoadBalanced())){
			System.out.println("my alg: " + testGraph.isLoadBalanced() + "augustine: " + network.isLoadBalanced());
		}
		assertTrue(testGraph.isLoadBalanced() || !network.isLoadBalanced());
		assertTrue(network.getFlowCost() >= testGraph.getWeight());
	}
}