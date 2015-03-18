package ford_fulkerson.graph.residual_classes;

import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Vertex;

public class BackwardsResidualEdge extends ResidualEdge{

	
	public BackwardsResidualEdge(Vertex parent, Vertex destination, int capacity, Edge e) {
		super(parent, destination, capacity, e);
		this.isBackwards = true;
	}
	
	

}
