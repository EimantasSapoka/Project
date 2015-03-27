package test;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import test.augustineMCMF.Augustine_Network;
import test.augustineMCMF.MinCostMaxFlowSPA;
import test.graph_creator.RandomArbitraryModel;
import test.graph_creator.RandomReaderAllocationModel;
import ford_fulkerson.MinCostMaxFlowAlgorithm;
import ford_fulkerson.ReaderShortlistException;
import ford_fulkerson.model.MCMFModel;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;

public class CorrectnessTest {

	private static final int TEST_COUNT = 100;
	MinCostMaxFlowSPA alg;
	
	@Before 
	public void initialize(){
		alg = new MinCostMaxFlowSPA();
	}
	
	/**
	 * a test which sets up a reader preferences instance which would
	 * be not load balanced using a regular MCMF algorithm and solves 
	 * it using the load balanced variant of the algorithm and checks 
	 * that the resulting allocation is load balanced.
	 * @throws Exception
	 */
	@Test
	public void presetReaderAllocationLoadBalancingTest() throws Exception{
		
		/*
		 *  a network with two readers and 5 projects.
		 *  Reader 1 preferences: 1,2,3,4,5
		 *  Reader 2 preferences: 1,2,3,4,5
		 *  Both have capacity of 5
		 *  An unbalanced algorithm would assign reader 1 all five
		 *  preferences and none to reader 2. Balanced algorithm 
		 *  would assign one reader three preferences and the remaining
		 *  two for the other. Both results would produce the same network
		 *  flow and weight. 
		 */
		
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
		
        try {
            model.createNetwork();
        } catch(ReaderShortlistException ex){
            // no need for warnings in tests
        }
		
		Augustine_Network augustine_Network = alg.createReaderAugustine_NetworkFromModel(model);
                
		MinCostMaxFlowAlgorithm.runLoadBalancedAlgorithm(model);
		augustine_Network = alg.solve(augustine_Network);
		
		assertTrue(model.isLoadBalanced() && !augustine_Network.isLoadBalanced());
		// prettiest test you've ever seen...
	}
	
	
	/**
	 * test which creates a random reader allocation graph and
	 * runs it against two algorithms. (Packages ford_fulkerson and 
	 * augustineMCMF) then checks that they return a similar flow size
	 * @throws Exception 
	 */
	@Test
	public void readerAllocationGraphTest() throws Exception{
		for (int i=0; i<TEST_COUNT; i++){
			System.out.println(i);
			// create a new random reader graph
			MCMFModel testGraph = new RandomReaderAllocationModel(i%50+1, i%400+1);
			try {
                testGraph.createNetwork();
            } catch(ReaderShortlistException ex){
            	// nobody cares about shortlists in testing.
            }
			Augustine_Network augustine_Network = alg.createReaderAugustine_NetworkFromModel(testGraph);
			
			runTests(testGraph, augustine_Network);
			
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
			System.out.println(i);
			MCMFModel testGraph = new RandomArbitraryModel(i%50+1,i%400+1);
            
			Augustine_Network augustine_Network = alg.createAugustine_NetworkFromGraph(testGraph.getNetwork());
			runTests(testGraph, augustine_Network);
			
			// check if the graph satisfies constraints
			Constraints_Test.networkConstraintsTests(testGraph.getNetwork());		
		}
	}
	
	/**
	 * a method which runs both algorithms on the testGraph and network
	 * @throws Exception
	 */
	private void runTests(MCMFModel testModel, Augustine_Network augustine_Network) throws Exception {
		
		//run against both algorithms, measuring performance
		MinCostMaxFlowAlgorithm.runUnbalancedAlgorithm(testModel.getNetwork());
		augustine_Network = alg.solve(augustine_Network);
		
		// if the results are not the same, print them out
		if (augustine_Network.getFlowSize() != testModel.getNetwork().getFlow() || augustine_Network.getFlowCost() != testModel.getNetwork().getWeight()){
			System.out.println("augustine weight: " + augustine_Network.getFlowCost() + " augustine flow: " + augustine_Network.getFlowSize() +
								"\nmy weight: " + testModel.getNetwork().getWeight() + " my flow: " + testModel.getNetwork().getFlow());
			System.out.println("Augustine balanced: " + augustine_Network.isLoadBalanced() + " mine balanced: " + testModel.isLoadBalanced());
		} 
		
		
		// assert that my algorithm flow is no lower than augustine's
		assertTrue(augustine_Network.getFlowSize() <= testModel.getNetwork().getFlow());		
		// assert that my algorithm cost is no higher than augustine's
		assertTrue(augustine_Network.getFlowCost() >= testModel.getNetwork().getWeight());
		// assert that my algorithm produces load balanced results, if augustine's does.
		assertTrue(testModel.isLoadBalanced() == augustine_Network.isLoadBalanced());
		
	}
}