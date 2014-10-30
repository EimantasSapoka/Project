package ford_fulkerson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import residual_classes.ResidualEdge;

public class Algorithm {
	private final static Logger log = Logger.getLogger(Algorithm.class.getName()); 
	
	/**
	 * runs the algorithm with the given graph.
	 * traverses the graph with breadth first search and looks 
	 * for a path. if one is found, starts from the sink and 
	 * takes the path backwards looking for maximum flow
	 * for all edges on path. Then updates the flows on the edges.
	 * @param graph
	 */
	public static void runAlgorithm(Graph graph){
		log.setLevel(Level.SEVERE);
		
		ArrayList<ResidualEdge> path;
		while( (path = dijkstra(graph)) != null) {
			log.info(path.toString());
			int maxFlow = findPathCapacity(path);
			updateEdges(path, maxFlow);
		}
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
	 * does a breadth first search on the given graph and if
	 * a path is found, returns true, if not, returns false.
	 * when visiting the edge, sets its visited flag and updates
	 * the vertex's path variable with the edge it reached the vertex
	 * (so it's possible to backtrack the path)
	 * @param graph to traverse 
	 * @return true if found path, false if not
	 */
	public static ArrayList<ResidualEdge> bfs(Graph graph){
		log.info("RUNNING ALGORITHM!! ============");
		Graph residualGraph = new Graph(graph); // creates a residual graph.
		
		LinkedList<Vertex> queue = new LinkedList<Vertex>();
		Vertex current;
		
		// starting with source
		Vertex currentVertex = residualGraph.source();
		currentVertex.visit(null);
		queue.add(currentVertex);
		
		while( !queue.isEmpty() ){
			
			currentVertex = queue.removeFirst();
			log.info(currentVertex.getOutEdges().toString());
			for (Edge edge : currentVertex.getOutEdges()){
				
				log.info(edge + " " + (edge instanceof ResidualEdge));
				current = edge.getDestination();
				
				if ( current.equals(graph.sink()) ){
					current.visit(edge);
					return getPathArray(residualGraph);
				} else if (! current.isVisited() ) {
					current.visit(edge);
					queue.add(current);
				}
			}
		}
		return null;
		
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
		
		return path;
	}
	
	
	/**
	 * method to traverse the residual graph using Dijkstra's algorithm and
	 * find the shortest path from source to sink. 
	 * @param realGraph
	 */
	public static ArrayList<ResidualEdge> dijkstra(Graph realGraph){	
		Graph residualGraph = new Graph(realGraph);
		updateWeights(residualGraph);
		ArrayList<Vertex> unvisitedVertices = residualGraph.getVertices(); // list of all vertices
		Vertex current;
		
		residualGraph.source().setDistanceFromSource(0); // set source distance from itself to be 0
		realGraph.source().setDistanceFromSource(0); 
		
		while ( unvisitedVertices.size() > 0 ){
			
			Collections.sort(unvisitedVertices);
			current = unvisitedVertices.remove(0);
			
			/* for each edge perform relaxation on the destination vertex (if it had not been visited yet)
			 	with the curret vertex's distance from source added to edge's weight
			 	and provide the edge as a path reference, if the path turns out to be shorter
			 */
			for (Edge edge : current.getOutEdges()){
				Vertex destination = edge.getDestination();
				
				if (! destination.isVisited()){
					destination.relaxation(edge.getWeight() + current.getDistanceFromSource(), edge);
				}
				
			}
			
			// mark vertex as visited and remove it from unvisited list
			current.visit();

			unvisitedVertices.remove(current);		
		}
		
		if ( residualGraph.sink().getPath() == null ){
			return null;
		} else {
			return getPathArray(residualGraph);
		}
		
	}

	/**
	 * method which updates the edge weights according to the vertices distances
	 * w'(u,v) = w(u,v) + d(u) - d(v)
	 * @param residualGraph
	 */
	private static void updateWeights(Graph residualGraph) {
		for (Edge e: residualGraph.getEdges()){
			ResidualEdge res = (ResidualEdge) e;
			Edge original = res.getOriginalEdge();
			
			int currentWeight = e.getWeight();
			int sourceDistance;
			int destinationDistance;
			
			if (res.isBackwards()){
				sourceDistance = original.getDestination().getDistanceFromSource();
				destinationDistance = original.getParent().getDistanceFromSource();
			} else {
				sourceDistance = original.getParent().getDistanceFromSource();
				destinationDistance = original.getDestination().getDistanceFromSource();
			}
			
			e.setWeight(currentWeight + sourceDistance - destinationDistance );
		}
		
	}

}
