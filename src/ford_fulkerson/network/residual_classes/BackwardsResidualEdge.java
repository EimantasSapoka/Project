package ford_fulkerson.network.residual_classes;

import ford_fulkerson.network.Edge;
import ford_fulkerson.network.Vertex;

public class BackwardsResidualEdge extends ResidualEdge{

	
	public BackwardsResidualEdge(Vertex parent, Vertex destination, int capacity, Edge e) {
		super(parent, destination, capacity, e);
		this.isBackwards = true;
	}
	
	

}
