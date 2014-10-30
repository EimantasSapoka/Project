package ford_fulkerson;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Vertex implements Comparable<Vertex>{
	private final static Logger log = Logger.getLogger(Algorithm.class.getName()); 

	private static long vertexIdCounter = 1;
	private long ID;
	private int parentID;
	private ArrayList<Edge> outEdges;
	private ArrayList<Edge> inEdges;
	private int distanceFromSource;
	
	private boolean visited;
	private ResidualEdge path;
	
	private Vertex(){
		this.outEdges = new ArrayList<Edge>();
		this.inEdges = new ArrayList<Edge>();
		this.distanceFromSource = Integer.MAX_VALUE;
	}

	public Vertex(Vertex v){
		this();
		this.ID = v.getID();
		this.parentID = v.getParentID();
	}

	
	public Vertex( int id){
		this();
		this.ID = vertexIdCounter++;
		this.parentID = id;
	}
	
	public Vertex(int parentID, long vertexID){
		this();
		this.parentID = parentID;
		this.ID = vertexID;
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
	
	public void visit(){
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
	
	public int getDistanceFromSource(){
		return this.distanceFromSource;
	}
	
	public void setDistanceFromSource(int distance){
		this.distanceFromSource = distance;
	}

	public ResidualEdge getPath() {
		return path;
	}


	public String toString(){
		return this.parentID + "";
	}


	@Override
	public int compareTo(Vertex o) {
		return this.getDistanceFromSource() - o.getDistanceFromSource();
	}

	/**
	 * method which checks if the new path to the vertex is shorter than the previous one.
	 * if not, leaves it as it was. 
	 * if so, sets the vertex distance from source to be the new distance and sets the path
	 * variable to be the edge it took to get here. 
	 * @param distance
	 */
	public void relaxation(int distance, Edge e) {
		if (distance < this.distanceFromSource){
			log.info("performing relaxation on vertex " + this.getID());
			log.info("\t previous distance: " + this.distanceFromSource + ", previous path: " + path);
			log.info("\t new distance: " + distance + ", new path " + e);
			this.distanceFromSource = distance;
			this.path = (ResidualEdge) e;
		}
	}
}
