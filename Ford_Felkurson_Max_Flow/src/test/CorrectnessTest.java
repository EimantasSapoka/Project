package test;

import mcmf.Data;

import org.junit.Before;
import org.junit.Test;

import test.graph_creator.RandomGraph;
import ford_fulkerson.Algorithm;
import ford_fulkerson.graph.Graph;

public class CorrectnessTest {

	private Graph graph;
	private Data data;
	
	@Before
	public void createRandomGraph(){
		graph = new RandomGraph();
		data = new Data();
		
	}
	
	@Test
	public void runTest(){
		data.drawGraphRunMinCost(graph);
		Algorithm.runAlgorithm(graph);
		System.out.println("\n" + graph);
	}
}
