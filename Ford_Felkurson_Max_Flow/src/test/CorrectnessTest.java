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

	private Graph arbitraryGraph;
	private Graph readerGraph;
	private Data data;
	
	@Before
	public void createRandomGraph(){
		arbitraryGraph = new RandomArbitraryGraph();
		readerGraph = new RandomReaderAllocationGraph();
		data = new Data();
		
	}
	
	@Test
	public void readerAllocationGraphTest(){
		int size = data.drawGraphRunMinCost(readerGraph);
		Algorithm.runAlgorithm(readerGraph);
		assertEquals(size, readerGraph.getFlow());
		System.out.println("\n READER GRAPH");
		System.out.println(size);
		readerGraph.statistics();
	}
	
	@Test
	public void arbitraryGraphTest(){
		int size = data.drawGraphRunMinCost(arbitraryGraph);
		Algorithm.runAlgorithm(arbitraryGraph);
		assertEquals(size, arbitraryGraph.getFlow());
		/*System.out.println("\n ARBITRARY GRAPH");
		System.out.println(size);
		arbitraryGraph.statistics();*/
	}
}
