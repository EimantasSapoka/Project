package ford_fulkerson.graph;

public class Edge{
	
	protected int capacity;			// edge capacity
	protected int weight;			// edge weight
 	protected int flow;				// flow going through this edge
	protected Vertex source;		// where the edge is coming from, origin
	protected Vertex destination;	// where the edge is going to, destination
	
	
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
		this.source = parent;
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

	public Vertex getSource() {
		return source;
	}
	public void setSource(Vertex parent) {
		this.source = parent;
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
	
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Edge)){
			return false;
		}

		Edge e = (Edge) o;
		return this.source.equals(e.getSource()) && this.destination.equals(e.getDestination()) && this.capacity == e.getCapacity();
	}
	
	
	public String toString(){
		return String.format("%d (%d) -> %d (%d) ; weight: %d  -- flow/cap: %d/%d ",
				this.source.getVertexID(),this.source.getDistanceFromSource(),
				this.destination.getVertexID(), this.destination.getDistanceFromSource(), this.weight,
				this.flow, this.capacity);				
	}

	public void decreaseCapacity() {
		this.capacity--;
	}
	
	public void increaseCapacity(){
		this.capacity++;
	}

}
