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
	
	public ResidualEdge(Vertex parent, Vertex destination, boolean isBackwards, Edge e){
		if( isBackwards){
			this.setParent(parent);
			this.setDestination(destination);
			this.setWeight(e.getWeight() * -1); 
			this.setCapacity(e.getFlow());
		} else {
			this.setParent(parent);
			this.setDestination(destination);
			this.setWeight(e.getWeight());
			this.setCapacity(e.getResidualCapacity());
		}
		
		this.isBackwards = isBackwards;
		this.originalEdge = e;
	}

	public boolean isBackwards() {
		return isBackwards;
	}

	public Edge getOriginalEdge() {
		return originalEdge;
	}
	
	
	public String toString(){
		return "Edge capacity " + this.capacity + " backwards? " + this.isBackwards
				+ ", parent " + this.parent + ", destination " + this.destination;
	}
	
}
