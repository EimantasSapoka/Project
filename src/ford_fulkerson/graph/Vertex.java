package ford_fulkerson.graph;

import java.util.ArrayList;

import ford_fulkerson.graph.residual_classes.ResidualEdge;

public class Vertex implements Comparable<Vertex>{

	private static int vertexIdCounter = 2;			// counter to keep track of how many vertices are in the graph, as well as
													// as used to give each vertex a unique ID
	private int vertexID;							// this vertex ID, equal to current vertexIdCounter value
	private int objectID;							// the object this vertex represents (project, reader, etc) ID
	private Object object;
	private ArrayList<Edge> outEdges;				// list of outgoing edges
	private int distanceFromSource;					// the vertex distance from source
	private boolean reachable;						// is the vertex reachable from source
	
	private ResidualEdge path;						// an edge taken to come to this vertex
	
	private Vertex(Object objectReference){
		this.object = objectReference;
		this.outEdges = new ArrayList<Edge>();
		this.distanceFromSource = 0;
	}

	public Vertex(Vertex v){
		this(v.getObject());
		this.vertexID = v.getVertexID();
		this.objectID = v.getObjectID();
	}

	
	public Vertex(int id, Object obj){
		this(obj);
		this.vertexID = vertexIdCounter++;
		this.objectID = id;
	}
	
	public Vertex(int parentID, int vertexID, Object obj){
		this(obj);
		this.objectID = parentID;
		this.vertexID = vertexID;
	}
	
	public int getVertexID() {
		return vertexID;
	}
	
	public int getObjectID(){
		return this.objectID;
	}
	
	public static void resetVertexCounter(){
		vertexIdCounter = 2;
	}

	public ArrayList<Edge> getOutEdges() {
		return outEdges;
	}

	public Object getObject(){
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
		return this.vertexID  == v.getVertexID() && this.objectID == v.getObjectID();
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

}
