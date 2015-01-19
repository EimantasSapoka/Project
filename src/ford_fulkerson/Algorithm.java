package ford_fulkerson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Reader;
import ford_fulkerson.graph.Vertex;
import ford_fulkerson.residual_classes.ResidualEdge;
import ford_fulkerson.residual_classes.ResidualGraph;
import ford_fulkerson.residual_classes.ResidualVertex;

/**
 * class which runs the min cost max flow algorithm.
 * contains dijkstra and the bfs implementations
 * @author Eimantas
 *
 */
public class Algorithm {
	private final static Logger log = Logger.getLogger(Algorithm.class.getName()); 

	/**
	 * runs the algorithm with the given graph. Tries to make it load balanced.
	 * traverses the graph with breadth first search and looks 
	 * for a path. if one is found, starts from the sink and 
	 * takes the path backwards looking for maximum flow
	 * for all edges on path. Then updates the flows on the edges.
	 * @param graph
	 */
	public static void runLoadBalancedAlgorithm(Graph graph){
		log.setLevel(Level.SEVERE);
		
		/*
		 *  one of the rare cases where a do-while loop is appropriate
		 *  need to solve the graph first and only then check if it is load 
		 *  balanced and repeat if it's not.
		 */
		solveGraph(graph);
		/*int iteration = 0;
		
		do{
			System.out.println("iteration " + iteration);
			if (iteration++ != 0){
				graph.decreaseCapacityOffset();
			}
			
			graph.reset();
			solveGraph(graph);
		} while( !graph.isLoadBalanced());
		
		while (graph.getLowerCapacityOffset() != 0){
			graph.increaseCapacityOffset();
			solveGraph(graph);
		}
		
		if (!graph.isLoadBalanced()){
			graph.reset();
			solveGraph(graph);
		}*/
	}


	private static void solveGraph(Graph graph) {
		
		ResidualGraph residualGraph = null;
		while( (residualGraph = dijkstra(graph, residualGraph)) != null) {
			ArrayList<ResidualEdge> path = getPathArray(residualGraph);
			int maxFlow = findPathCapacity(path);
			updateEdges(path, maxFlow);
		}
	}
	
	
	/**
	 * method to traverse the residual graph using Dijkstra's algorithm and
	 * find the shortest path from source to sink. 
	 * @param realGraph
	 */
	public static ResidualGraph dijkstra(Graph realGraph, ResidualGraph previousGraph){
		ResidualGraph residualGraph = new ResidualGraph(realGraph, previousGraph); // creates a residual graph
		
		ArrayList<Vertex> unvisitedVertices = residualGraph.getVertices(); // list of all vertices
		Vertex current;
		residualGraph.source().setDistanceFromSource(0); // set source distance from itself to be 0
		realGraph.source().setDistanceFromSource(0); 

		while ( unvisitedVertices.size() > 0 ){
			Collections.sort(unvisitedVertices);
			
			current = unvisitedVertices.remove(0);
			if (!current.isRreachable()){
				break;
			}
			
			/* for each edge perform relaxation on the destination vertex (if it had not been visited yet)
			 	with the current vertex's distance from source added to edge's weight
			 	and provide the edge as a path reference, if the path turns out to be shorter
			 */
			for (Edge edge : current.getOutEdges()){
				ResidualVertex destination = (ResidualVertex) edge.getDestination();
				
				if (! destination.isVisited()){
					destination.relaxation(edge.getWeight() + current.getDistanceFromSource(), (ResidualEdge) edge);
				}
			}
			
			// mark vertex as visited
			current.visit();	
		}
		
		if ( residualGraph.sink().getPath() == null ){
			return null;
		} else {
			return residualGraph;
		}
		
	}


	/**
	 * traverses the path backwards and for every edge
	 * updates its capacity/flow with the maxFlow value.
	 * @param graph
	 * @param maxFlow 
	 */
	private static void updateEdges(ArrayList<ResidualEdge> path, int maxFlow) {
		log.info("UPDATING EDGES =============");
		Edge realEdge;
		
		for (ResidualEdge edge: path){
			realEdge = edge.getOriginalEdge();
			
			// check if edge is a forward one
			if (! edge.isBackwards()){
				log.info("adding flow " + maxFlow + " to edge " + realEdge);
				realEdge.setFlow(realEdge.getFlow() + maxFlow);			
			} else { // backwards edge
				
				log.info("taking away flow " + maxFlow + " from edge " + realEdge);
				realEdge.setFlow(realEdge.getFlow() - maxFlow);
			}
		}
	}

	
	

	

	/**
	 * method to traverse the graph backwards from the sink and return the path as an 
	 * arraylist of edges from source to sink
	 * @param graph
	 * @return
	 */
	private static ArrayList<ResidualEdge> getPathArray(Graph graph) {
		Vertex vertex = graph.sink();
		ArrayList<ResidualEdge> path = new ArrayList<ResidualEdge>();
		ResidualEdge edge;

		while (! vertex.equals(graph.source())){
			edge = vertex.getPath();
			vertex = edge.getParent();
			path.add(0,edge);
		}
		//System.out.println(path.toString());
		return path;
	}
	

	/**
	 * traverses the graph backwards, starting from the sink.
	 * compares the residual capacity value of every edge on the path
	 * to the max flow (initialized to Integer.max_value), thus finding
	 * the maximum capacity the path can carry.
	 * 
	 * @param graph
	 * @return path capacity
	 */
	private static int findPathCapacity(ArrayList<ResidualEdge> path) {
		int maxFlow = Integer.MAX_VALUE;
		
		for (Edge edge: path){
			maxFlow = Math.min(maxFlow, edge.getCapacity());
		}
		log.info("max flow: " + maxFlow);
		return maxFlow;
	}
	
}
