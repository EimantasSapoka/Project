package residual_classes;

import ford_fulkerson.Edge;
import ford_fulkerson.Vertex;

public class ResidualEdge extends Edge {
	private boolean isBackwards;
	private Edge originalEdge;
	
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
