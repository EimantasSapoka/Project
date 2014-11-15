package test;

import static org.junit.Assert.*;
import mcmf.Data;

import org.junit.Before;
import org.junit.Test;

import test.graph_creator.RandomArbitraryGraph;
import test.graph_creator.RandomReaderAllocationGraph;
import ford_fulkerson.Algorithm;
import ford_fulkerson.graph.Graph;

public class CorrectnessTest {

	private static final int TEST_COUNT = 500;
	private Data data;
	Graph arbitraryGraph, readerGraph;
	
	@Before
	public void createVariables(){
		data = new Data();		
	}
	
	/**
	 * test which creates a random reader allocation graph and
	 * runs it against two algorithms. (Packages ford_fulkerson and 
	 * mcmf) then checks that they return a similar flow size
	 */
	@Test
	public void readerAllocationGraphTest(){
		for (int i=0; i<TEST_COUNT; i++){
			// create a new random reader graph
			readerGraph = new RandomReaderAllocationGraph();
			
			//run against both algorithms
			Algorithm.runAlgorithm(readerGraph);
			int size = data.drawGraphRunMinCost(readerGraph);
			
			// assert equal flow sizes and check reader graph constraints
			Constraints_Test.checkReaderConstraints(readerGraph);
			assertEquals(size, readerGraph.getFlow());
		}
	}
	
	/**
	 * test which creates a random arbitrary graph and
	 * runs it against two algorithms. (Packages ford_fulkerson and 
	 * mcmf) then checks that they return a similar flow size
	 */
	@Test
	public void arbitraryGraphTest(){
		for (int i = 0; i<TEST_COUNT; i++){
			// create a new random arbitrary graph
			arbitraryGraph = new RandomArbitraryGraph();
			
			// run the graph against both algorithms
			int size = data.drawGraphRunMinCost(arbitraryGraph);
			Algorithm.runAlgorithm(arbitraryGraph);
			
			
			// check if the graph satisfies constraints as well as if sizes are similar
			Constraints_Test.graphConstraintsTests(arbitraryGraph);		
			assertTrue(size <= arbitraryGraph.getFlow());
		}
	}
}
