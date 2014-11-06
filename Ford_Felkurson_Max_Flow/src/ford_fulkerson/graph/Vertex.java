package ford_fulkerson.graph;

import java.util.ArrayList;
import java.util.logging.Logger;

import ford_fulkerson.Algorithm;
import ford_fulkerson.residual_classes.ResidualEdge;
import ford_fulkerson.residual_classes.ResidualVertex;

public class Vertex implements Comparable<Vertex>{
	private final static Logger log = Logger.getLogger(Algorithm.class.getName()); 

	private static long vertexIdCounter = 1;
	private long vertexID;
	private int objectID;
	private ArrayList<Edge> outEdges;
	private int distanceFromSource;
	
	private boolean visited;
	private ResidualEdge path;
	
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
	
	public Vertex(int parentID, long vertexID){
		this();
		this.objectID = parentID;
		this.vertexID = vertexID;
	}
	
	public long getVertexID() {
		return vertexID;
	}
	
	public int getObjectID(){
		return this.objectID;
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
	
	public void setDistanceFromSource(int distance){
		this.distanceFromSource = distance;
	}

	public ResidualEdge getPath() {
		return path;
	}


	public String toString(){
		return this.objectID + "";
	}


	@Override
	public int compareTo(Vertex o) {
		if (this == o) {
			return 0;
		} else if (this.getDistanceFromSource() > o.getDistanceFromSource()){
			return 1;
		} else if (this.getDistanceFromSource() < o.getDistanceFromSource()){
			return -1;
		} else {
			return 0;
		}
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
			this.distanceFromSource = distance;
			if (this instanceof ResidualVertex){
				((ResidualVertex) this).getOriginalVertex().setDistanceFromSource(distance);						 
			}
			this.path = (ResidualEdge) e;
		}
	}
}
