package ford_fulkerson.network;

import java.util.ArrayList;
import java.util.List;

import test.graph_creator.MockNetworkObject;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;

/**
 * class which represents a network.
 * has a list of vertices, edges, source and sink.
 * also has a reader target offset. 
 * @author Eimantas
 *
 */
public class Network {
	
	private static final int PROJECT_READER_CAPACITY = 1;
	private static final int READERS_TO_PROJECTS_CONSTANT = 1;
	public static final int SOURCE_ID = 0;
	public static final int SINK_ID = 1;

	private int readerTargetOffset; 			// the offset used to calculate the lower capacity of edges. 
	
	protected Vertex source;					// the source vertex reference
	protected Vertex sink;						// the sink vertex reference
	
	protected ArrayList<Vertex> vertices;		// all the vertices in the graph
	protected ArrayList<Edge> edges;			// all the edges in the graph
	
	public Network(){
		
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		
		Vertex.resetVertexCounter();
		
		source = new Vertex(SOURCE_ID, new MockNetworkObject(SOURCE_ID));
		sink = new Vertex(SINK_ID, new MockNetworkObject(SINK_ID));
		
		addVertex(source);
		addVertex(sink);
		
		this.readerTargetOffset = 0;
	}
	
	
	/**
	 * adds the edge to the graph and updates the edge's parent
	 * and destination vertices to include this edge.
	 * @param edge
	 */
	public void addEdge(Edge edge){
		if (! edges.contains(edge)){
			this.getVertex(edge.getSource().getVertexID()).addOutEdge(edge);
			edges.add(edge);
		}
	}
	
	public int getReaderTargetOffset(){
		return this.readerTargetOffset;
	}
	
	public void increaseReaderTargetOffset(){
		if (this.readerTargetOffset < 0){
			this.readerTargetOffset++;
		}
		for (Edge e : this.source.getOutEdges()){
			e.increaseCapacity();
		}
	}
	
	public void decreaseReaderTargetOffset(){
		this.readerTargetOffset--;
		for (Edge e : this.source.getOutEdges()){
			e.decreaseCapacity();
		}
	}
	
	public void addVertex(Vertex vertex){
		if (!vertices.contains(vertex)){
			vertices.add(vertex);
		}
	}

	/**
	 * returns a new list with all vertices from the graph. 
	 * It is not a deep copy, modifying the vertices will modify
	 * the graph ones, but removing or adding items to the returned
	 * list will not add them to the network.
	 * @return
	 */
	public List<Vertex> getVertices() {
		List<Vertex> newList = new ArrayList<Vertex>();
		newList.addAll(vertices);
		return newList;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}
	
	public Vertex source(){
		return source;
	}
	
	public Vertex sink(){
		return sink;
	}
	
	/**
	 * returns a vertex with the id
	 * @param l
	 * @return
	 */
	public Vertex getVertex(long l){
		for (Vertex v : vertices){
			if ( v.getVertexID() == l ){
				return v;
			}
		}
		return null;
	}
	
	/**
	 * method to check if the graph contains a vertex with the id
	 * @param id
	 * @return
	 */
	public boolean hasVertex(int id){
		for (Vertex v: vertices){
			if (v.getVertexID() == id){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * method to check if the graph contains a vertex
	 * @param vertex
	 * @return
	 */
	public boolean hasVertex(Vertex vertex){
		for (Vertex v: vertices){
			if (v.equals(vertex)){
				return true;
			}
		}
		return false;
	}
	
	public Vertex getVertex(Vertex vertex){
		for (Vertex v: vertices){
			if (v.equals(vertex)){
				return v;
			}
		}
		return null;
	}
	
	/**
	 * gets the edge of this graph identical to given another edge
	 * @param e
	 * @return
	 */
	public Edge getEdge(Edge edge){
		for (Edge e: this.edges){
			if (e.equals(edge)){
				return e;
			}
		}
		return null;
	}
	
	
	
	public void setVertices(ArrayList<Vertex> vertices){
		this.vertices = vertices;
	}

	
	/**
	 * returns the total capacity of edges going to the sink
	 * @return
	 */
	private int getCapacityOut() {
		int cap = 0;
		for (Edge e : this.getEdges()){
			if (e.getDestination().equals(this.sink)){
				cap += e.getCapacity();
			}
		}
		return cap;
	}
	

	/**
	 * returns the total capacity of edges going from the source
	 * @return
	 */
	private int getCapacityIn() {
		int cap = 0;
		for (Edge e : this.source.getOutEdges()){
			cap += e.getCapacity();	
		}
		return cap;
	}
	
	/**
	 * returns weather the graph has a saturating flow, 
	 * that is weather 
	 * @return
	 */
	public boolean isSaturatingFlow(){
		return getCapacityIn() == getFlow() || getCapacityOut() == getFlow();
	}
	
	
	

	/**
	 * returns the total flow in the graph
	 * @return
	 */
	public int getFlow(){
		int flow = 0;
		for (Edge e: this.source.getOutEdges()){
			flow += e.getFlow();
		}
		return flow;
	}
	
	/**
	 * returns the graph's weight, which is each edge's flow 
	 * multiplied by it's weight, summed up.
	 * @return
	 */
	public int getWeight(){
		int weight = 0;
		for (Edge e : this.getEdges()){
			weight += e.getFlow()*e.getWeight();
		}
		return weight;
	}
	
	/**
	 * adds a project to the graph. adds its vertex and creates
         * project -> sink edges.
	 * @param project
	 */
	public void addProject(Project project){
            addVertex(project.getVertex());
            Edge projectSinkEdge = new Edge(project.getVertex(), sink, PROJECT_READER_CAPACITY);
            addEdge(projectSinkEdge);
	}
        
    /**
     * adds a reader to the graph. adds its vertex and creates
     * source -> reader and reader -> preference edges..
     * @param reader 
     */
    public void addReader(Reader reader) {
         addVertex(reader.getVertex());
        
        // if reader has any capacity, create an edge from source to the reader with the capacity
        if (reader.getReaderTarget() > 0) {
            createSourceReaderEdge(reader);
        }

        int preference = 1; // the initial preference weight
        for (Project project : reader.getPreferences()) {
            
            createReaderProjectEdge(reader, project, preference);
            preference++;
        } 
    }
	
	
	/**
	 * method which resets the graph - removes all flow from edges.
	 */
	public void reset() {
		for (Edge e : edges){
			e.setFlow(0);
		}
		for (Vertex v: vertices){
			v.setDistanceFromSource(0);
		}
		
	}
	

	/**
	 * finds the unassigned projects and returns them in a list
	 * @return
	 */
	public ArrayList<Project> findUnassignedProjects() {
		ArrayList<Project> unassigned = new ArrayList<Project>();
		for (Edge e : edges){
			if (e.getDestination().equals(this.sink)){
				if (e.getFlow() == 0){
					unassigned.add((Project) e.getSource().getObject());
				}
			}
		}
		return unassigned;
	}

    public void createReaderProjectEdge(Reader reader, Project project, int weight) {
        // create the edge between the reader and the project.
        Edge readerProjectEdge = new Edge(reader.getVertex(), project.getVertex(), READERS_TO_PROJECTS_CONSTANT, weight);
        addEdge(readerProjectEdge);
    }

    public void createSourceReaderEdge(Reader reader) {
        Edge sourceReaderEdge = new Edge(source,reader.getVertex(), reader.getReaderTarget());
        addEdge(sourceReaderEdge);	
    }

    


}
