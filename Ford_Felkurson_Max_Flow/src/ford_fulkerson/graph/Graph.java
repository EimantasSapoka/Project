package ford_fulkerson.graph;

import java.util.ArrayList;

public class Graph {
	
	private static final int PROJECT_READER_CAPACITY = 1;
	private static final int READERS_TO_PROJECTS_CONSTANT = 1;
	protected static final int SOURCE_ID = 0;
	protected static final int SINK_ID = 1;

	
	public Vertex source;					// the source vertex reference
	public Vertex sink;					// the sink vertex reference
	
	public ArrayList<Vertex> vertices;		// all the vertices in the graph
	public ArrayList<Edge> edges;			// all the edges in the graph
	
	public ArrayList<Reader> readers;		// readers list
	public ArrayList<Project> projects;	// projects list
	
	public Graph(){
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		this.readers = new ArrayList<Reader>();
		this.projects = new ArrayList<Project>();
		Vertex.resetVertexCounter();
		source = new Vertex(SOURCE_ID, SOURCE_ID);
		sink = new Vertex(SINK_ID, SINK_ID);
		addVertex(source);
		addVertex(sink);
	}
	
	
	/**
	 * adds the edge to the graph and updates the edge's parent
	 * and destination vertices to include this edge.
	 * @param edge
	 */
	public void addEdge(Edge edge){
		this.getVertex(edge.getParent().getVertexID()).addOutEdge(edge);
		edges.add(edge);
	}
	
	public void addVertex(Vertex vertex){
		vertices.add(vertex);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Vertex> getVertices() {
		return (ArrayList<Vertex>) vertices.clone();
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
	 * method to check if the graph contains the reader
	 * @param id
	 * @return
	 */
	public boolean hasReader(int id){
		for (Reader r : readers){
			if (r.getID() == id){
				return true;
			}
		}
		return false;
	}
	
	/**
	 *  method to check if the graph contains the reader
	 * @param reader
	 * @return
	 */
	public boolean hasReader(Reader reader){
		for (Reader r : readers){
			if (r.equals(reader)){
				return true;
			}
		}
		return false;
	}
	
	
	public ArrayList<Reader> getReaders(){
		return this.readers;
	}
	
	/**
	 * method to check if a graph contains the project
	 * @param project
	 * @return
	 */
	public boolean hasProject(Project project){
		for (Project p: projects){
			if (p.equals(project)){
				return true;
			}
		}
		return false;
	}
	
	public void setVertices(ArrayList<Vertex> vertices){
		this.vertices = vertices;
	}
	
	/**
	 * method to check if a graph contains the project
	 * @param id
	 * @return
	 */
	public boolean hasProject(int id){
		for (Project p: projects){
			if (p.getId() == id){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * returns a project with the id
	 * @param id
	 * @return
	 */
	public Project getProject(int id){
		for (Project p: projects){
			if (p.getId() == id){
				return p;
			}
		}
		return null;
	}
	
	/**
	 * returns projects list
	 * @return
	 */
	public ArrayList<Project> getProjects(){
		return this.projects;
	}
	
	/** 
	 * prints out the readers and their assigned projects
	 */
	public String toString(){
		String result = "";
		
		int numberProjects = this.getProjects().size();
		@SuppressWarnings("unchecked")
		ArrayList<Project> unselected = (ArrayList<Project>) this.getProjects().clone();
		
		for (Reader r: readers){
			result += "Reader id " + r.getID() + ", capacity " + r.getCapacity();
			int count = 1;
			for (Edge e: r.getVertex().getOutEdges()){
				if (e.getFlow() > 0){
					unselected.remove(this.getProject(e.getDestination().getObjectID()));
					result += "\n"+ count++ + " \t assigned project ID " + e.getDestination().getObjectID();
				} else {
					result += "\n" + count++ + " \t NOT ASSIGNED project ID " + e.getDestination().getObjectID();
				}
			}
			result += "\n";
		}

		String unselectedProjID = "";
		for (Project p : unselected){
			unselectedProjID += " " + p.getId();
		}
		result += String.format(""
				+ "\n number of readers: %d"
				+ "\n number of projects: %d, not assigned: %d [%s ]"
				+ "\n total flow: %d"
				+ "\n total weight: %d", 
				this.getReaders().size(),
				numberProjects, 
				unselected.size(),
				unselectedProjID,
				this.getFlow(),
				this.getWeight());
		return result;
	}
	
	/**
	 * prints out the graph's vertices and edges
	 */
	public void graphDescription(){
		System.out.println("VERTICES: ");
		for (Vertex v : vertices){
			System.out.println("\t" + v);
		}
		System.out.println("EDGES: ");
		for(Edge e: edges){
			System.out.println("\t" + e);
			 
		}
		
	}

	/**
	 * prints out graph statistics, capacity in, capacity out, total flow and total weight.
	 */
	public void statistics() {
		System.out.println("CAPACITY IN: " + this.getCapacityIn());
		System.out.println("CAPACITY OUT: " + this.getCapacityOut());
		System.out.println("TOTAL FLOW: " + this.getFlow());
		System.out.println("TOTAL WEIGHT: " + this.getWeight());
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
	 * adds a project to the graph
	 * @param project
	 */
	private void addProject(Project project){
		projects.add(project);
		addVertex(project.getVertex());
		
		Edge projectSinkEdge = new Edge(project.getVertex(), sink, PROJECT_READER_CAPACITY);
		addEdge(projectSinkEdge);
		
	}
	
	/**
	 * adds a reader to the graph. This process includes adding all the reader's 
	 * projects into the graph (if not yet present) and creating all the necessary
	 * vertices and edges. 
	 * @param reader
	 */
	public void addReader(Reader reader){
		readers.add(reader);
		addVertex(reader.getVertex());
		
		// if reader has any capacity, create an edge from source to the reader with the capacity
		if (reader.getCapacity() > 0){
			Edge sourceReaderEdge = new Edge(source,reader.getVertex(), reader.getCapacity());
			addEdge(sourceReaderEdge);	
		}
		
		int preference = 1; // the initial preference 
		for (Project project : reader.getPreferences()){
			
			if (!hasProject(project.getId())){
				addProject(project); // if project not in graph, add it
			}
			
			// create the edge between the reader and the project.
			Edge readerProjectEdge = new Edge(reader.getVertex(), project.getVertex(), READERS_TO_PROJECTS_CONSTANT, preference++);
			addEdge(readerProjectEdge);
			
		}
	}
	
}