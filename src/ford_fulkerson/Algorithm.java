package ford_fulkerson;

import java.util.ArrayList;

import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Project;
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
	
	public static void runUnbalancedAlgorithm(Graph graph){
		graph.createGraph();
		solveGraph(graph);
	}

	/**
	 * runs the algorithm with the given graph. Tries to make it load balanced.
	 * traverses the graph with breadth first search and looks 
	 * for a path. if one is found, starts from the sink and 
	 * takes the path backwards looking for maximum flow
	 * for all edges on path. Then updates the flows on the edges.
	 * @param graph
	 */
	public static void runLoadBalancedAlgorithm(Graph graph){
		// creates the graph
		graph.createGraph();
		solveGraph(graph);
		loadBalance(graph);
		assignUnassignedProjects(graph);
	}

	/**
	 * while and if it's not load balanced, 
	 * reset the graph entirely, decrease all readers' capacities by 1
	 * and try solving the graph again. Repeat until it's load 
	 * balanced. It will eventually stop, as once the capacities are reduced 
	 * to 0, the graph is empty and it is considered to be load balanced. 
	 * then gradually increase the readers' capacities and keep 
	 * running the algorithm. This ensures the graph is load balanced
	 * or as close to load balancing as possible. 
	 */
	
	private static void loadBalance(Graph graph) {
		while( !graph.isLoadBalanced()){
			graph.reset();
			graph.decreaseCapacityOffset();
			solveGraph(graph);
		}

		
		while (graph.getLowerCapacityOffset() != 0){
			graph.increaseCapacityOffset();
			solveGraph(graph);
		}
	}

	/**
	 * if the graph's flow is not saturating, it means a project
	 * had not been assigned. Find those projects and add them to 
	 * readers', who have not yet reached their capacity limit, preference lists.
	 * Run the algorithm again and since it is min cost, max flow, the 
	 * reader who had the smallest preference list will most likely take the project. 
	 */
	public static void assignUnassignedProjects(Graph graph) {
		
		if (!graph.isSaturatingFlow()){
			
			ArrayList<Project> unassignedProjects = graph.findUnassignedProjects();
			for (Reader reader : graph.getReaders()){
				if (reader.getResidualCapacity() > 0){
					for (Project p: unassignedProjects){
						reader.addPreference(p);
					}
				}
			}
			graph.createGraph();
			solveGraph(graph);
		}
	}


	/**
	 * finds the min cost max flow solution to the given graph.
	 * @param graph
	 */
	private static void solveGraph(Graph graph) {
		
		ResidualGraph residualGraph = new ResidualGraph(graph, graph);
		ArrayList<ResidualEdge> path = null;
		
		while( (path = dijkstra(graph, residualGraph)) != null) {
			updateEdges(path, 1);
			residualGraph = new ResidualGraph(graph, residualGraph);
		}
	}
	
	
	
	/**
	 * method to traverse the residual graph using Dijkstra's algorithm and
	 * find the shortest path from source to sink. 
	 * @param realGraph
	 */
	public static ArrayList<ResidualEdge> dijkstra(Graph realGraph, ResidualGraph residualGraph){	
		
		@SuppressWarnings("unchecked")
		ArrayList<Vertex> unvisitedVertices = (ArrayList<Vertex>) residualGraph.getVertices().clone(); // list of all vertices
		Vertex current = residualGraph.source();
		current.setDistanceFromSource(0); // set source distance from itself to be 0
		
		while ( !unvisitedVertices.isEmpty() ){
			
			if (current == null){
				break;
			}
			
			/* for each edge perform relaxation on the destination vertex (if it had not been visited yet)
			 	with the current vertex's distance from source added to edge's weight
			 	and provide the edge as a path reference, if the path turns out to be shorter
			 */
			for (Edge edge : current.getOutEdges()){
				ResidualVertex destination = (ResidualVertex) edge.getDestination();
				
				if ( unvisitedVertices.contains(destination)){	
					destination.relaxation(edge.getWeight() + current.getDistanceFromSource(), (ResidualEdge) edge);
				}
			}
			
			unvisitedVertices.remove(current);
			
			
			// find the next lowest distance vertex
			current = null;
			
			for (Vertex v : unvisitedVertices){
				if (v.isReachable()){
					if (current == null){
						current = v;
					} else if (current.getDistanceFromSource() > v.getDistanceFromSource()){
						current = v;
					}
				}
			}
		}
		
		if ( residualGraph.sink().getPath() == null ){
			return null;
		} else {
			return getPathArray(residualGraph);
		}
		
	}


	/**
	 * traverses the path backwards and for every edge
	 * updates its capacity/flow with the maxFlow value.
	 * @param graph
	 * @param maxFlow 
	 */
	private static void updateEdges(ArrayList<ResidualEdge> path, int maxFlow) {
		Edge realEdge;
		
		for (ResidualEdge edge: path){
			realEdge = edge.getOriginalEdge();
			
			// check if edge is a forward one
			if (! edge.isBackwards()){
				realEdge.setFlow(realEdge.getFlow() + maxFlow);			
			} 
			
			// backwards edge
			else {
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
	
		return path;
	}
	
}