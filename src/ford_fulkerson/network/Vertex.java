package ford_fulkerson.network;

import java.util.ArrayList;

import ford_fulkerson.network.residual_classes.ResidualEdge;

public class Vertex implements Comparable<Vertex>{

	private static int vertexIdCounter = 2;			// counter to keep track of how many vertices are in the graph, as well as
													// as used to give each vertex a unique ID
	private int vertexID;							// this vertex ID, equal to current vertexIdCounter value
	private NetworkObjectInterface object;			//
	private ArrayList<Edge> outEdges;				// list of outgoing edges
	private int distanceFromSource;					// the vertex distance from source
	private boolean reachable;						// is the vertex reachable from source
	
	private ResidualEdge path;						// an edge taken to come to this vertex
	
	public Vertex(NetworkObjectInterface objectReference){
		this.object = objectReference;
		this.outEdges = new ArrayList<Edge>();
		this.distanceFromSource = 0;
		this.vertexID = vertexIdCounter++;
	}

	public Vertex(Vertex v){
		this(v.getObject());
		this.vertexID = v.getVertexID();
	}

	public Vertex(int vertexID, NetworkObjectInterface obj){
		this(obj);
		this.vertexID = vertexID;
	}
	
	public int getVertexID() {
		return vertexID;
	}
	
	public int getObjectID(){
		return object.getID();
	}
	
	public static void resetVertexCounter(){
		vertexIdCounter = 2;
	}

	public ArrayList<Edge> getOutEdges() {
		return outEdges;
	}

	public NetworkObjectInterface getObject(){
		return this.object;
	}

	public void addOutEdge(Edge edge) {
		this.outEdges.add(edge);
	}

	@Override
	public boolean equals(Object o){
		if ( !(o instanceof Vertex) ) {
			return false;
		}
		Vertex v = (Vertex) o;
		return this.equals(v);
	}
	
	public boolean equals(Vertex v){
		return this.vertexID  == v.getVertexID() && object.getID() == v.getObjectID();
	}
	
	
	public int getDistanceFromSource(){
		return this.distanceFromSource;
	}
	
	public boolean isReachable(){
		return this.reachable;
	}
	
	public void setDistanceFromSource(int distance){
		this.reachable = true;
		this.distanceFromSource = distance;
	}

	public ResidualEdge getPath() {
		return path;
	}
	
	public void setPath(ResidualEdge e) {
		this.path = e;
	}


	public String toString(){
		return ""+this.getDistanceFromSource();
	}


	@Override
	public int compareTo(Vertex o) {
		if (this.equals(o)) {
			return 0;
		} else if (this.getDistanceFromSource() > o.getDistanceFromSource()){
			return 1;
		} else if (this.getDistanceFromSource() < o.getDistanceFromSource()){
			return -1;
		} else {
			return 0;
		}
	}
        
    public void resetVertex(){
        this.distanceFromSource=0;
        this.outEdges.clear();
        this.reachable = false;
    }

}
