package ford_fulkerson.network.residual_classes;

import ford_fulkerson.network.Edge;
import ford_fulkerson.network.Network;
import ford_fulkerson.network.Vertex;

/**
 * an extension class of the network. 
 * Represents a residual network .
 * @author Eimantas
 *
 */
public class ResidualNetwork extends ford_fulkerson.network.Network {

	/**
	 * creates a residual graph from the given graph
	 * @param realNetwork
	 */
	public ResidualNetwork(Network realNetwork, Network previous){
		
		// removes the source and sink vertices created by Graph constructor
		this.vertices.clear(); 
		
		// copy vertices
		for (Vertex v : realNetwork.getVertices()){
			this.vertices.add(new ResidualVertex(v));
		}
		
		// set source and sink vertices
		this.source = this.getVertex(SOURCE_ID);
		this.sink = this.getVertex(SINK_ID);
				
		
		// create residual edges
		for (Edge edge : realNetwork.getEdges()){
			
			Vertex source = getVertex(edge.getSource());
			Vertex destination = getVertex(edge.getDestination());
			
			// if it has residual capacity left, add a residual edge with residual capacity
			if (edge.getResidualCapacity() > 0){
				this.addEdge(new ResidualEdge(source, destination,edge.getResidualCapacity(), edge));
			}
			
			// if it has any flow, create a backwards residual edge with capacity equal to flow
			if (edge.getFlow() > 0 ){
				this.addEdge(new BackwardsResidualEdge(destination, source, edge.getFlow(), edge));
			}
		}
		
		// update the edge weights 
		for (Edge e: this.edges){
			Edge previousEdge = previous.getEdge(e);
			
			if (previousEdge != null){
				
				int sourceDistance = realNetwork.getVertex(e.getSource()).getDistanceFromSource();
				int destinationDistance = realNetwork.getVertex(e.getDestination()).getDistanceFromSource();
				
				// calculate the new edge weight: previousWeight + sourceVertexDistance - destinationVertexDistance
				int weight = previousEdge.getWeight() + sourceDistance - destinationDistance;
				e.setWeight(weight);
			}
		}
		
	}
	
}
