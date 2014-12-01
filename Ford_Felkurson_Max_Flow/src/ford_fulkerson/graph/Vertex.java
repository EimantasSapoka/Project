package ford_fulkerson.graph;

import java.util.ArrayList;
import java.util.logging.Logger;

import ford_fulkerson.Algorithm;
import ford_fulkerson.residual_classes.ResidualEdge;
import ford_fulkerson.residual_classes.ResidualVertex;

public class Vertex implements Comparable<Vertex>{
	private final static Logger log = Logger.getLogger(Algorithm.class.getName()); 

	private static int vertexIdCounter = 2;			// counter to keep track of how many vertices are in the graph, as well as
													// as used to give each vertex a unique ID
	private int vertexID;							// this vertex ID, equal to current vertexIdCounter value
	private int objectID;							// the object this vertex represents (project, reader, etc) ID
	private ArrayList<Edge> outEdges;				// list of outgoing edges
	private int distanceFromSource;					// the vertex distance from source
	private boolean reachable;						// is the vertex reachable from source
	
	protected boolean visited;						// is the vertex visited
	private ResidualEdge path;						// an edge taken to come to this vertex
	
	private Vertex(){
		this.outEdges = new ArrayList<Edge>();
		this.distanceFromSource = Integer.MAX_VALUE;
	}

	public Vertex(Vertex v){
		this();
		this.vertexID = v.getVertexID();
		this.objectID = v.getObjectID();
	}

	
	public Vertex( int id){
		this();
		this.vertexID = vertexIdCounter++;
		this.objectID = id;
	}
	
	public Vertex(int parentID, int vertexID){
		this();
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
	
	
	public void visit(){
		this.visited = true;
	}
	
	public ArrayList<Edge> getOutEdges() {
		return outEdges;
	}

	

	public void addOutEdge(Edge edge) {
		this.outEdges.add(edge);
	}

	public boolean equals(Vertex v ){
		return this.vertexID  == v.getVertexID();
	}
	
	
	public boolean isVisited() {
		return visited;
	}
	
	public int getDistanceFromSource(){
		return this.distanceFromSource;
	}
	
	public boolean isRreachable(){
		return this.reachable;
	}
	
	public void setDistanceFromSource(int distance){
		this.reachable = true;
		this.distanceFromSource = Math.abs(distance);
	}

	public ResidualEdge getPath() {
		return path;
	}
	
	public void setPath(ResidualEdge e) {
		this.path = e;
	}


	public String toString(){
		String outEdgeIDs = "";
		for (Edge e: this.getOutEdges()){
			outEdgeIDs += e.getDestination().getVertexID() + " ";
		}
		return "Vertex id:" + this.getVertexID()+ " obj:" + this.getObjectID() + " DIST: "+this.getDistanceFromSource()+
				" edges to vertices: [" + outEdgeIDs + "]";
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
