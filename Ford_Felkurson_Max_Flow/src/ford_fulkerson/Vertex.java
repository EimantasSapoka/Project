package ford_fulkerson;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Vertex {
	private final static Logger log = Logger.getLogger(Algorithm.class.getName()); 

	private static long vertexIdCounter = 1;
	private final long ID;
	private final int parentID;
	private ArrayList<Edge> outEdges;
	private ArrayList<Edge> inEdges;
	
	private boolean visited;
	private ResidualEdge path;

	public Vertex(Vertex v){
		this.ID = v.getID();
		this.parentID = v.getParentID();
		this.outEdges = new ArrayList<Edge>();
		this.inEdges = new ArrayList<Edge>();
	}

	
	public Vertex( int id){
		this.ID = vertexIdCounter++;
		this.parentID = id;
		this.outEdges = new ArrayList<Edge>();
		this.inEdges = new ArrayList<Edge>();
	}
	
	public Vertex(int parentID, long vertexID){
		this.parentID = parentID;
		this.ID = vertexID;
		this.outEdges = new ArrayList<Edge>();
		this.inEdges = new ArrayList<Edge>();
	}
	
	public long getID() {
		return ID;
	}
	
	public int getParentID(){
		return this.parentID;
	}
	
	public void visit(Edge e){
		if (e instanceof ResidualEdge){
			this.path = (ResidualEdge) e;
		} else {
			log.warning("NOT THE RIGHT INSTANCE OF EDGE!");
		}
		
		this.visited = true;
	}
	
	public ArrayList<Edge> getOutEdges() {
		return outEdges;
	}

	public ArrayList<Edge> getInEdges() {
		return inEdges;
	}

	public void addOutEdge(Edge edge) {
		this.outEdges.add(edge);
	}
	
	public void addInEdge(Edge edge){
		this.inEdges.add(edge);
	}
	
	public boolean equals(Vertex v ){
		return this.ID  == v.getID();
	}
	
	
	public boolean isVisited() {
		return visited;
	}
	
	


	public ResidualEdge getPath() {
		return path;
	}


	public String toString(){
		return this.parentID + "";
	}
}
