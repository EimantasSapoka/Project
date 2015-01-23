package ford_fulkerson.residual_classes;

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
	private boolean isBackwards;				// indicator if the edge is backwards
	private Edge originalEdge;					// reference to the real graph edge this residual edge was created from
	
	public ResidualEdge(Vertex parent, Vertex destination, int capacity,  boolean isBackwards, Edge e){
		super(parent, destination, capacity, 0);
		this.originalEdge = e;
		this.isBackwards = isBackwards;
	}

	public boolean isBackwards() {
		return isBackwards;
	}

	public Edge getOriginalEdge() {
		return originalEdge;
	}
	
	
	public String toString(){
		String backwards = this.isBackwards? "BACKWARDS ":"";
		return "Edge " + this.parent.getVertexID() + "->"+ this.destination.getVertexID() + " ("+this.getWeight()+") cap:" + this.capacity + " "+ backwards + 
				"dist:["+ this.parent.getDistanceFromSource()+"/"+this.destination.getDistanceFromSource()+"]";
	}
	
}