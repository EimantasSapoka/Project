package ford_fulkerson.graph;

import java.util.ArrayList;
import java.util.Collections;

public class Graph {
	
	private static final int PROJECT_READER_CAPACITY = 1;
	private static final int READERS_TO_PROJECTS_CONSTANT = 1;
	protected static final int SOURCE_ID = 0;
	protected static final int SINK_ID = 1;

	public int lowerCapacityOffset; 		// the offset used to calculate the lower capacity of edges. 
	
	public Vertex source;					// the source vertex reference
	public Vertex sink;						// the sink vertex reference
	
	public ArrayList<Vertex> vertices;		// all the vertices in the graph
	public ArrayList<Edge> edges;			// all the edges in the graph
	
	public ArrayList<Reader> readers;		// readers list
	public ArrayList<Project> projects;		// projects list
	
	public Graph(){
		
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		this.readers = new ArrayList<Reader>();
		this.projects = new ArrayList<Project>();
		
		Vertex.resetVertexCounter();
		
		source = new Vertex(SOURCE_ID, SOURCE_ID, null);
		sink = new Vertex(SINK_ID, SINK_ID, null);
		
		addVertex(source);
		addVertex(sink);
		
		this.lowerCapacityOffset = 0;
	}
	
	
	/**
	 * adds the edge to the graph and updates the edge's parent
	 * and destination vertices to include this edge.
	 * @param edge
	 */
	public void addEdge(Edge edge){
		if (! edges.contains(edge)){
			this.getVertex(edge.getParent().getVertexID()).addOutEdge(edge);
			edges.add(edge);
		}
	}
	
	public int getLowerCapacityOffset(){
		return this.lowerCapacityOffset;
	}
	
	public void increaseCapacityOffset(){
		if (this.lowerCapacityOffset < 0){
			this.lowerCapacityOffset++;
		}
		for (Edge e : this.source.getOutEdges()){
			e.increaseCapacity();
		}
	}
	
	public void decreaseCapacityOffset(){
		this.lowerCapacityOffset--;
		for (Edge e : this.source.getOutEdges()){
			e.decreaseCapacity();
		}
	}
	
	public void addVertex(Vertex vertex){
		if (!vertices.contains(vertex)){
			vertices.add(vertex);
		}
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
	
	/**
	 * gets the largest project capacity amongst all the readers
	 * @return
	 */
	public int getLargestCapacity(){
		int max = 0;
		for (Reader reader: this.getReaders()){
			if (reader.getCapacity() > max){
				max = reader.getCapacity();
			}
		}
				
		return max;
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
		
		for (Reader r: readers){
			result += "Reader " + r.getVertex().getObjectID();
			
			int assigned = 0;
			String assignedProj = "";
			for (Edge e: r.getVertex().getOutEdges()){
				if (e.getFlow() > 0){
					assigned++;
					assignedProj += " " + e.getDestination().getObjectID();
				} 
			}
			result +=  " (" + assigned + "/" + r.getCapacity() + ") :" + assignedProj + "\n";
		}
		
		String unselectedProjID = "";
		for (Project p : this.findUnassignedProjects()){
			unselectedProjID += " " + p.getId();
		}
		result += String.format(""
				+ "\n number of readers: %d"
				+ "\n number of projects: %d, not assigned: [%s ]"
				+ "\n total flow: %d"
				+ "\n total weight: %d"
				+ "\n load balanced? : %b"
				+ "\n saturating flow? : %b", 
				this.getReaders().size(),
				numberProjects, 
				unselectedProjID,
				this.getFlow(),
				this.getWeight(),
				this.isLoadBalanced(),
				this.isSaturatingFlow());
		
		return result;
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
	 * returns if the graph is load balanced. That is weather
	 * all the reader's in the graph have no more than one less 
	 * project assigned with respect to their capacities than any other reader.
	 * Example: if a reader has capacity 7 and flow 4, load balanced graph would
	 * mean that no other reader has flow higher than their capacity - 3 or lower
	 * than their capacity - 4. 
	 * @return
	 */
	public boolean isLoadBalanced() {
		int capacityFlowGap = 0;
		boolean capacitySet = false;
		
		for (Reader reader : this.readers){
			if (!capacitySet){
				capacityFlowGap = reader.getCapacity() + lowerCapacityOffset - reader.getAssignedProjects().size();
				if (capacityFlowGap >= 0){
					capacitySet = true;
				}
			} else {
				if (reader.getCapacity() + lowerCapacityOffset - reader.getAssignedProjects().size() > capacityFlowGap+1){
					return false;
				}
			}
		}
		return true;
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
		if (! projects.contains(project)){
			projects.add(project);
			addVertex(project.getVertex());
			
			Edge projectSinkEdge = new Edge(project.getVertex(), sink, PROJECT_READER_CAPACITY);
			addEdge(projectSinkEdge);
		}
	}
	
	/**
	 * adds a reader to the graph. This process includes adding all the reader's 
	 * projects into the graph (if not yet present) and creating all the necessary
	 * vertices and edges. 
	 * @param reader
	 */
	public void addReader(Reader reader){
		readers.add(reader);
		for (Project p : reader.getPreferences()){
			this.addProject(p);
		}
	}
	
	/**
	 * creates the vertices and edges between them according to the reader
	 * and preference information.
	 */
	public void createGraph(){

		for (Reader reader : this.readers){
			
			if (reader.getPreferences().size() < reader.getCapacity()){
				System.err.println("ERROR! READER " + reader.getID() + " HAS CAPACITY OF " + reader.getCapacity() 
						+ " AND PREFERENCE LIST SIZE "  + reader.getPreferences().size());
				System.exit(1);
			} else if (reader.getPreferences().size() < reader.getCapacity()*2){
				System.out.println("WARNING! reader " + reader.getID() + " has capacity of " + reader.getCapacity()
						+ " and preference list size " + reader.getPreferences().size());
			}
			
			
			addVertex(reader.getVertex());
			// if reader has any capacity, create an edge from source to the reader with the capacity
			if (reader.getCapacity() > 0){
				Edge sourceReaderEdge = new Edge(source,reader.getVertex(), reader.getCapacity());
				addEdge(sourceReaderEdge);	
			}
			
			int preference = 1; // the initial preference weight
			for (Project project : reader.getPreferences()){
				
				// create the edge between the reader and the project.
				Edge readerProjectEdge = new Edge(reader.getVertex(), project.getVertex(), READERS_TO_PROJECTS_CONSTANT, preference++);
				addEdge(readerProjectEdge);
				
			}
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
	 * method which extends readers preference lists to 2x their capacities
	 */
	@SuppressWarnings("unchecked")
	public void extendPreferenceLists() {
		for (Reader r: this.readers){
			
			ArrayList<Project> preferences = r.getPreferences();
			ArrayList<Project> projectList = (ArrayList<Project>) this.projects.clone();
			
			while (preferences.size() < 2*r.getCapacity() && !projectList.isEmpty()){
				
				Collections.sort(projectList);
				Project proj = projectList.remove(0);
				
				if (!preferences.contains(proj)){
					if( !r.getSupervisorProjects().contains(proj.getId())){
						System.out.println("Extended reader's " + r.getID() + " pref list with project " + proj.getId());
						r.addPreference(proj);
					}
				}
			}
			
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
					unassigned.add((Project) e.getParent().getObject());
				}
			}
		}
		return unassigned;
	}


}
