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

	private Data data;
	Graph arbitraryGraph;
	Graph readerGraph;
	
	@Before
	public void createVariables(){
		readerGraph = new RandomReaderAllocationGraph();
		data = new Data();
		
	}
	
	@Test
	public void readerAllocationGraphTest(){
		Algorithm.runAlgorithm(readerGraph);
		int size = data.drawGraphRunMinCost(readerGraph);
		assertEquals(size, readerGraph.getFlow());
		System.out.println("\n READER GRAPH");
		System.out.println(size);
		readerGraph.statistics();
	}
	
	@Test
	public void arbitraryGraphTest(){
		for (int i = 0; i<1000; i++){
			arbitraryGraph = new RandomArbitraryGraph();
			int size = data.drawGraphRunMinCost(arbitraryGraph);
			Algorithm.runAlgorithm(arbitraryGraph);
			Constraints_Test.graphConstraintsTests(arbitraryGraph);		
			assertTrue(size <= arbitraryGraph.getFlow());
		}
	}
}
