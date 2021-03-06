package ford_fulkerson;

import java.util.ArrayList;
import java.util.List;

import ford_fulkerson.model.MCMFModel;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;
import ford_fulkerson.network.Edge;
import ford_fulkerson.network.Network;
import ford_fulkerson.network.Vertex;
import ford_fulkerson.network.residual_classes.ResidualEdge;
import ford_fulkerson.network.residual_classes.ResidualNetwork;
import ford_fulkerson.network.residual_classes.ResidualVertex;

/**
 * class which runs the min cost max flow algorithm.
 * contains dijkstra and the bfs implementations
 * @author Eimantas
 *
 */
public class MinCostMaxFlowAlgorithm {
	
	public static void runUnbalancedAlgorithm(Network network){
		solveNetwork(network);
	}

	/**
	 * runs the algorithm with the given graph. Tries to make it load balanced.
	 * traverses the graph with breadth first search and looks 
	 * for a path. if one is found, starts from the sink and 
	 * takes the path backwards looking for maximum flow
	 * for all edges on path. Then updates the flows on the edges.
	 * @param graph
	 */
	public static void runLoadBalancedAlgorithm(MCMFModel model){
		solveNetwork(model.getNetwork());
		loadBalance(model);
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
	
	private static void loadBalance(MCMFModel model) {
        Network network = model.getNetwork();
		while( !model.isLoadBalanced()){
			network.reset();
			model.reset();
			network.decreaseReaderTargetOffset();
			solveNetwork(network);
		}

		while (network.getReaderTargetOffset() != 0){
			network.increaseReaderTargetOffset();
			model.reset();
			solveNetwork(network);
		}
	}

	/**
	 * if the graph's flow is not saturating, it means a project
	 * had not been assigned. Find those projects and add them to 
	 * readers', who have not yet reached their capacity limit, preference lists.
	 * Run the algorithm again and since it is min cost, max flow, the 
	 * reader who had the smallest preference list will most likely take the project. 
	 */
	public static void assignUnassignedProjects(MCMFModel model) throws ReaderShortlistException {
		
		if (!model.getNetwork().isSaturatingFlow()){
			
			ArrayList<Project> unassignedProjects = model.getNetwork().findUnassignedProjects();
			for (Reader reader : model.getReaders()){
				if (reader.getResidualCapacity() > 0){
					for (Project p: unassignedProjects){
						reader.addPreference(p);
					}
				}
			}
			model.createNetwork();
			solveNetwork(model.getNetwork());
		}
	}


	/**
	 * finds the min cost max flow solution to the given graph.
	 * @param network
	 */
	private static void solveNetwork(Network network) {
		
		ResidualNetwork residualNetwork = new ResidualNetwork(network, network);
		List<ResidualEdge> path = dijkstra(network, residualNetwork);
		
		while( path != null) {
			updateEdges(path, 1);
			residualNetwork = new ResidualNetwork(network, residualNetwork);
			path = dijkstra(network, residualNetwork);
		}
	}
	
	
	
	/**
	 * method to traverse the residual graph using Dijkstra's algorithm and
	 * find the shortest path from source to sink. 
	 * @param realNetwork
	 */
	public static List<ResidualEdge> dijkstra(Network realNetwork, ResidualNetwork residualNetwork){	
		
		List<Vertex> unvisitedVertices = new ArrayList<Vertex>();
		unvisitedVertices.addAll(residualNetwork.getVertices());	// list of all vertices
		
		Vertex current = residualNetwork.source();
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
		
		if ( residualNetwork.sink().getPath() == null ){
			return null;
		} else {
			return getPathArray(residualNetwork);
		}
		
	}


	/**
	 * traverses the path and for every edge
	 * updates its capacity/flow with the maxFlow value.
	 * @param graph
	 * @param maxFlow 
	 */
	private static void updateEdges(List<ResidualEdge> path, int maxFlow) {
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
	 * @param network
	 * @return
	 */
	private static List<ResidualEdge> getPathArray(Network network) {
		Vertex vertex = network.sink();
		List<ResidualEdge> path = new ArrayList<ResidualEdge>();
		ResidualEdge edge;

		while (! vertex.equals(network.source())){
			edge = vertex.getPath();
			vertex = edge.getSource();
			path.add(0,edge);
		}
	
		return path;
	}
	
}
