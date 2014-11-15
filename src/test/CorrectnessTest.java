package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import mcmf.Data;
import mcmf.Edge;

import org.junit.Before;
import org.junit.Test;

import test.graph_creator.RandomArbitraryGraph;
import test.graph_creator.RandomReaderAllocationGraph;
import ford_fulkerson.Algorithm;
import ford_fulkerson.graph.Graph;

public class CorrectnessTest {

	private static final int TEST_COUNT = 1;
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
			mcmf.Graph graph = data.drawGraphRunMinCost(readerGraph);
			Algorithm.runAlgorithm(readerGraph);
			
			compareEdges(graph.getEdges(), readerGraph.getEdges());
			
			System.out.println("\nmcmf: " + graph.getWeight() + " mine: " + readerGraph.getWeight());
			
			//assertTrue(graph.getWeight() >= readerGraph.getWeight());
			
			// assert equal flow sizes and check reader graph constraints
			Constraints_Test.checkReaderConstraints(readerGraph);
			assertTrue(graph.getFlow() <= readerGraph.getFlow());
		}
	}
	
	private void compareEdges(ArrayList<Edge> edges, ArrayList<ford_fulkerson.graph.Edge> edges2) {
		int sinkID = 0;
		for (Edge e: edges){
			if (e.getDestination().getIndex() > sinkID){
				sinkID = e.getDestination().getIndex();
			}
		}
		
		
		for (Edge e: edges){
			for (ford_fulkerson.graph.Edge edge: edges2){
				
				
				if (e.getOrigin().getIndex() == edge.getParent().getVertexID() &&
						(e.getDestination().getIndex() == edge.getDestination().getVertexID()
						 || (edge.getDestination().getVertexID() == -1) && e.getDestination().getIndex() == sinkID) ){
					
					// same edge
					if (!(e.getCapacity() == edge.getCapacity() && e.getFlow() == edge.getFlow() && e.getWeight() == edge.getWeight())){
						
							System.out.println("my edge: " + edge);
							System.out.println("mcmf edge: " + e);
							System.out.println();
						
					}
				}
				
				
			}
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
			mcmf.Graph graph = data.drawGraphRunMinCost(arbitraryGraph);
			Algorithm.runAlgorithm(arbitraryGraph);
			
			
			// check if the graph satisfies constraints as well as if sizes are similar
			Constraints_Test.graphConstraintsTests(arbitraryGraph);		
			assertTrue(graph.getFlow() <= arbitraryGraph.getFlow());
		}
	}
}
