package test;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import test.graph_creator.RandomArbitraryGraph;
import test.graph_creator.RandomReaderAllocationGraph;
import augustineMCMF.MinCostMaxFlowSPA;
import augustineMCMF.Network;
import ford_fulkerson.Algorithm;
import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Project;
import ford_fulkerson.graph.Reader;

public class CorrectnessTest {

	private static final int TEST_COUNT = 100000;
	MinCostMaxFlowSPA alg;
	
	@Before 
	public void initialize(){
		alg = new MinCostMaxFlowSPA();
	}
	
	@Test
	public void presetReaderAllocationLoadBalancingTest() throws Exception{
		Graph testGraph = new Graph();
		
		Reader reader1 = new Reader(1,5);
		Reader reader2 = new Reader(2,5);
		
		Project project1 = new Project(1);
		Project project2 = new Project(2);
		Project project3 = new Project(3);
		Project project4 = new Project(4);
		Project project5 = new Project(5);
		
		reader1.addPreference(project1);
		reader1.addPreference(project2);
		reader1.addPreference(project3);
		reader1.addPreference(project4);
		reader1.addPreference(project5);
		
		reader2.addPreference(project1);
		reader2.addPreference(project2);
		reader2.addPreference(project3);
		reader2.addPreference(project4);
		reader2.addPreference(project5);
		
		testGraph.addReader(reader1);
		testGraph.addReader(reader2);
		
		testGraph.createGraph();
		
		Network network = alg.createReaderNetworkFromGraph(testGraph);
		runTests(testGraph, network);
		System.out.println("\n");
		System.out.println(testGraph);
		network.networkDescription();
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
			System.out.println("TEST " + i);
			
			// create a new random reader graph
			Graph testGraph = new RandomReaderAllocationGraph(i%40 + 10, 2, 7);
			testGraph.createGraph();
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
			if (i%1000 == 0){
				System.out.println("TEST " + i);
			}
			Graph testGraph = new RandomArbitraryGraph();
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
		}
	
		if (network.getFlowSize() != testGraph.getFlow() || network.getFlowCost() != testGraph.getWeight()){
			System.out.println("augustine weight: " + network.getFlowCost() + " augustine flow: " + network.getFlowSize() +
								"\nmy weight: " + testGraph.getWeight() + " my flow: " + testGraph.getFlow());
			System.out.println("Augustine balanced: " + network.isLoadBalanced() + " mine balanced: " + testGraph.isLoadBalanced());
		} 
		
		
		// assert equal flow sizes and weights 
		assertTrue(network.getFlowSize() <= testGraph.getFlow());
		if (testGraph.isLoadBalanced() && !network.isLoadBalanced()){
			System.out.println("SUCCESSFULLY LOAD BALANCED");
			System.out.println("my graph: flow " + testGraph.getFlow() + " weight " + testGraph.getWeight() + " balanced? " + testGraph.isLoadBalanced()  );
			System.out.println("Augustine: flow " + network.getFlowSize() + " weight " + network.getFlowCost() + " balanced? " + network.isLoadBalanced() );
		}
		if (!(testGraph.isLoadBalanced() || !network.isLoadBalanced())){
			System.out.println("my alg: " + testGraph.isLoadBalanced() + " augustine: " + network.isLoadBalanced());
		}
		
		assertTrue(network.getFlowCost() >= testGraph.getWeight() || testGraph.getFlow() != network.getFlowSize() || (testGraph.isLoadBalanced() && !network.isLoadBalanced()));
		assertTrue(testGraph.isLoadBalanced() || !network.isLoadBalanced());
		
	}
}