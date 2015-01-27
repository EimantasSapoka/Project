package test;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import test.graph_creator.RandomArbitraryModel;
import test.graph_creator.RandomReaderAllocationModel;
import test.augustineMCMF.MinCostMaxFlowSPA;
import test.augustineMCMF.Network;
import ford_fulkerson.Algorithm;
import model.MCMFModel;
import model.Project;
import model.Reader;

public class CorrectnessTest {

	private static final int TEST_COUNT = 100;
	MinCostMaxFlowSPA alg;
	
	@Before 
	public void initialize(){
		alg = new MinCostMaxFlowSPA();
	}
	
	@Test
	public void presetReaderAllocationLoadBalancingTest() throws Exception{
		
		MCMFModel model = new MCMFModel();
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
		
		model.addReader(reader1);
		model.addReader(reader2);
		
		model.createGraph();
		
		Network network = alg.createReaderNetworkFromModel(model);
		

		Algorithm.runLoadBalancedAlgorithm(model);
		network = alg.solve(network);
		
		assertTrue(model.isLoadBalanced() && !network.isLoadBalanced());
		
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
			if (i%1000 == 0){
				System.out.println("TEST " + i);
			}
			// create a new random reader graph
			MCMFModel testGraph = new RandomReaderAllocationModel(i%40 + 10, 2, 7);
			testGraph.createGraph();
			Network network = alg.createReaderNetworkFromModel(testGraph);
			
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
			MCMFModel testGraph = new RandomArbitraryModel();
			Network network = alg.createNetworkFromGraph(testGraph.getGraph());
			
			runTests(testGraph, network);
			
			// check if the graph satisfies constraints
			Constraints_Test.graphConstraintsTests(testGraph.getGraph());		
		}
	}
	
	/**
	 * a method which runs both algorithms on the testGraph and network
	 * @throws Exception
	 */
	private void runTests(MCMFModel testGraph, Network network) throws Exception {
		
		//run against both algorithms, measuring performance
		long start = System.nanoTime();
		Algorithm.runUnbalancedAlgorithm(testGraph);
		long performanceMine = System.nanoTime() - start;
		
		long start2 = System.nanoTime();
		network = alg.solve(network);
		long performanceAugustine = System.nanoTime() - start2;
		
		if (performanceAugustine < performanceMine){
			System.out.println("Augustine algorithm performed faster by " + (performanceMine-performanceAugustine)/1000000 + "ms");
		}
	
		if (network.getFlowSize() != testGraph.getGraph().getFlow() || network.getFlowCost() != testGraph.getGraph().getWeight()){
			System.out.println("augustine weight: " + network.getFlowCost() + " augustine flow: " + network.getFlowSize() +
								"\nmy weight: " + testGraph.getGraph().getWeight() + " my flow: " + testGraph.getGraph().getFlow());
			System.out.println("Augustine balanced: " + network.isLoadBalanced() + " mine balanced: " + testGraph.isLoadBalanced());
		} 
		
		
		// assert equal flow sizes and weights 
		assertTrue(network.getFlowSize() <= testGraph.getGraph().getFlow());		
		assertTrue(network.getFlowCost() >= testGraph.getGraph().getWeight());
		assertTrue(testGraph.isLoadBalanced() || !network.isLoadBalanced());
		
	}
}