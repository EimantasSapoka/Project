package ford_fulkerson.network.residual_classes;

import ford_fulkerson.network.Edge;
import ford_fulkerson.network.Vertex;

/**
 * a class, which is an extension of residual edge. 
 * It is mainly used for code understandability, but 
 * functionally just provides the same constructor which 
 * differs by changing "is backwards" value to true. 
 * @author Eimantas
 *
 */
public class BackwardsResidualEdge extends ResidualEdge{

	
	public BackwardsResidualEdge(Vertex parent, Vertex destination, int capacity, Edge e) {
		super(parent, destination, capacity, e);
		this.isBackwards = true;
	}

}
