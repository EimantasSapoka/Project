package ford_fulkerson;

public class Edge{
	
	protected int capacity;
	protected int flow;
	protected Vertex parent;
	protected Vertex destination;
	
	public Edge(){
		this.parent = null;
		this.destination = null;
		this.capacity = 0;
	}
	
	public Edge (Vertex parent, Vertex destination, int capacity){
		this(parent, destination);
		this.capacity = capacity;
	}
	
	public Edge (Vertex parent, Vertex destination){
		this.capacity = 0;
		this.flow = 0;
		this.parent = parent;
		this.destination = destination;
	}
	
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	
	public int getFlow() {
		return flow;
	}

	public void setFlow(int flow) {
		this.flow = flow;
	}

	public Vertex getParent() {
		return parent;
	}
	public void setParent(Vertex parent) {
		this.parent = parent;
	}
	public Vertex getDestination() {
		return destination;
	}
	public void setDestination(Vertex destination) {
		this.destination = destination;
	}

	public int getResidualCapacity() {
		return capacity - flow;
	}
	
	
	public String toString(){
		return "Edge capacity " + this.capacity + ", flow " + this.flow 
				+ ", parent " + this.parent + ", destination " + this.destination;
	}

}
