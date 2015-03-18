package ford_fulkerson.graph.residual_classes;

import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Vertex;

/**
 * A class which represents a residual edge in a graph. These 
 * edges are always created from real graph edges (and extends the Edge class)
 * and therefore can be used as regular edges on the Graph instance.
 * These edges have an indicator specifying if it as a backwards edge compared
 * to the original edge. 
 * @author Eimantas
 *
 */
public class ResidualEdge extends Edge {
	protected boolean isBackwards;				// indicator if the edge is backwards
	protected Edge originalEdge;					// reference to the real graph edge this residual edge was created from
	
	public ResidualEdge(Vertex parent, Vertex destination, int capacity, Edge e){
		super(parent, destination, capacity, 0);
		this.originalEdge = e;
		this.isBackwards = false;
	}

	public boolean isBackwards() {
		return isBackwards;
	}

	public Edge getOriginalEdge() {
		return originalEdge;
	}
	
	@Override
	public String toString(){
		String backwards = this.isBackwards? "BACKWARDS ":"";
		return super.toString() + " " +backwards;
	}
	
}
