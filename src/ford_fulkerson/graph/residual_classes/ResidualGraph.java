package ford_fulkerson.graph.residual_classes;

import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Vertex;

public class ResidualGraph extends ford_fulkerson.graph.Graph {

	/**
	 * creates a residual graph from the given graph
	 * @param realGraph
	 */
	public ResidualGraph(Graph realGraph, Graph previous){
		
		// removes the source and sink vertices created by Graph constructor
		this.vertices.clear(); 
		
		// copy vertices
		for (Vertex v : realGraph.getVertices()){
			this.vertices.add(new ResidualVertex(v));
		}
		
		// set source and sink vertices
		this.source = this.getVertex(SOURCE_ID);
		this.sink = this.getVertex(SINK_ID);
				
		
		// create residual edges
		for (Edge edge : realGraph.getEdges()){
			
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
				
				int sourceDistance = realGraph.getVertex(e.getSource()).getDistanceFromSource();
				int destinationDistance = realGraph.getVertex(e.getDestination()).getDistanceFromSource();
				
				// calculate the new edge weight: previousWeight + sourceVertexDistance - destinationVertexDistance
				int weight = previousEdge.getWeight() + sourceDistance - destinationDistance;
				e.setWeight(weight);
			}
		}
		
	}
	
}
