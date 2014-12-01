package ford_fulkerson.residual_classes;

import java.util.ArrayList;

import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Vertex;

public class ResidualGraph extends ford_fulkerson.graph.Graph {

	/**
	 * creates a residual graph from the given graph
	 * @param g
	 */
	public ResidualGraph(Graph g){
		
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		
		for (Vertex v : g.getVertices()){
			this.vertices.add(new ResidualVertex(v));
		}
		
		for (Edge e : g.getEdges()){
			if (e.getResidualCapacity() > 0){
				this.addEdge(new ResidualEdge(this.getVertex(e.getParent()), this.getVertex(e.getDestination()),e.getResidualCapacity(), false, e));
			}
			if (e.getFlow() > 0 ){
				this.addEdge(new ResidualEdge( this.getVertex(e.getDestination()), this.getVertex(e.getParent()), e.getFlow(), true, e));
			}
		}
		
		this.source = this.getVertex(SOURCE_ID);
		this.sink = this.getVertex(SINK_ID);
		
	}
	
}
