package ford_fulkerson;

public class ResidualEdge extends Edge {
	private boolean isBackwards;
	private Edge originalEdge;
	
	public ResidualEdge(Vertex parent, Vertex destination, int capacity, boolean isBackwards, Edge e){
		if( isBackwards){
			this.setParent(parent);
			this.setDestination(destination);
		} else {
			this.setParent(parent);
			this.setDestination(destination);
		}
		
		this.setCapacity(capacity);
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
