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
		this.originalEdge = e;
		this.isBackwards = isBackwards;
		this.setCapacity(capacity);

		if( isBackwards){
			this.setParent(parent);
			this.setDestination(destination);
			this.setWeight((e.getWeight()*-1) + e.getDestination().getDistanceFromSource() - e.getParent().getDistanceFromSource());
			System.out.println("BACKWARD Edge weight:" + (e.getWeight()*-1) + "+SD:" +e.getDestination().getDistanceFromSource()+
								"-DD:" + e.getParent().getDistanceFromSource() + "=" + this.getWeight());
		} else {
			this.setParent(parent);
			this.setDestination(destination);
			this.setWeight(e.getWeight() + e.getParent().getDistanceFromSource() - e.getDestination().getDistanceFromSource());
			System.out.println("Edge weight:" + e.getWeight() + "+SD:" +e.getParent().getDistanceFromSource()+
					"-DD:" + e.getDestination().getDistanceFromSource() + "=" + this.getWeight());
		}	
		
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
