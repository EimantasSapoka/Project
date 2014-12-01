package ford_fulkerson.graph;

public class Edge{
	
	protected int capacity;			// edge capacity
	protected int weight;			// edge weight
 	protected int flow;				// flow going through this edge
	protected Vertex parent;		// where the edge is coming from, origin
	protected Vertex destination;	// where the edge is going to, destination
	
	public Edge(){
		this.parent = null;
		this.destination = null;
		this.capacity = 0;
	}
	
	public Edge (Vertex parent, Vertex destination, int capacity){
		this(parent, destination, capacity, 0);
	}
	
	public Edge (Vertex parent, Vertex destination, int capacity, int weight){
		this(parent, destination);
		this.capacity = capacity;
		this.weight = weight;
	}
	
	public Edge (Vertex parent, Vertex destination){
		this.weight = 0;
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
	
	public int getWeight(){
		return this.weight;
	}
	
	public void setWeight(int weight){
		this.weight = weight;
	}
	
	
	public String toString(){
		return "Edge " + this.parent.getVertexID() + " to " + this.destination.getVertexID() +
				"; flow/cap " + this.flow + "/"+ this.capacity +" weight: " + this.weight;
				
	}

}
