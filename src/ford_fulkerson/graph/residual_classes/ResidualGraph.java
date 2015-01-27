package ford_fulkerson.graph.residual_classes;

import java.util.ArrayList;

import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Vertex;

public class ResidualGraph extends ford_fulkerson.graph.Graph {

	/**
	 * creates a residual graph from the given graph
	 * @param realGraph
	 */
	public ResidualGraph(Graph realGraph, Graph previous){
		
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		
		// copy vertices
		for (Vertex v : realGraph.getVertices()){
			this.vertices.add(new ResidualVertex(v));
		}
		
		// create residual edges
		for (Edge e : realGraph.getEdges()){
			
			if (e.getResidualCapacity() > 0){
				
				this.addEdge(new ResidualEdge(this.getVertex(e.getParent()), this.getVertex(e.getDestination()),e.getResidualCapacity(), false, e));
			}
			if (e.getFlow() > 0 ){
				this.addEdge(new ResidualEdge( this.getVertex(e.getDestination()), this.getVertex(e.getParent()), e.getFlow(), true, e));
			}
		}
		
		// update the edge weights 
		for (Edge e: this.edges){
			Edge previousEdge = previous.getEdge(e);
			if (previousEdge != null){
				// calculate the new edge weight: previousWeight + sourceVertexDistance - destinationVertexDistance
				int weight = realGraph.getVertex(e.getParent()).getDistanceFromSource() + previousEdge.getWeight() - realGraph.getVertex(e.getDestination()).getDistanceFromSource();
				e.setWeight(weight);
			}
		}
		
		
		// set source and sink vertices
		this.source = this.getVertex(SOURCE_ID);
		this.sink = this.getVertex(SINK_ID);
		
	}
	
}
