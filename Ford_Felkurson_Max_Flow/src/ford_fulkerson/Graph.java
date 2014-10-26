package ford_fulkerson;

import java.util.ArrayList;

public class Graph {
	
	private static final int PROJECT_READER_CAPACITY = 1;
	private static final int READERS_TO_PROJECTS_CONSTANT = 1;
	private static final int SOURCE_ID = -1;
	private static final int SINK_ID = -2;

	
	private Vertex source;
	private Vertex sink;
	
	private ArrayList<Vertex> vertices;
	private ArrayList<Edge> edges;
	
	private ArrayList<Reader> readers;
	private ArrayList<Project> projects;
	
	public Graph(){
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		this.readers = new ArrayList<Reader>();
		this.projects = new ArrayList<Project>();
		
		source = new Vertex(-1, SOURCE_ID);
		sink = new Vertex(-2, SINK_ID);
		addVertex(source);
		addVertex(sink);
	}
	
	public Graph(Graph g){
		
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		
		for (Vertex v : g.getVertices()){
			this.vertices.add(new Vertex(v));
		}
		
		for (Edge e : g.getEdges()){
			if (e.getResidualCapacity() > 0){
				this.addEdge(new ResidualEdge(this.getVertex(e.getParent()), this.getVertex(e.getDestination()), e.getResidualCapacity(), false, e));
			}
			if (e.getFlow() > 0 ){
				ResidualEdge res = new ResidualEdge( this.getVertex(e.getDestination()), this.getVertex(e.getParent()), e.getFlow(), true, e);
				this.addEdge(res);
				
			}
		}
		
		this.source = this.getVertex(SOURCE_ID);
		this.sink = this.getVertex(SINK_ID);
		
	}
	
	/**
	 * adds the edge to the graph and updates the edge's parent
	 * and destination vertices to include this edge.
	 * @param edge
	 */
	public void addEdge(Edge edge){
		this.getVertex(edge.getParent().getID()).addOutEdge(edge);
		this.getVertex(edge.getDestination().getID()).addInEdge(edge);
		edges.add(edge);
	}
	
	public void addVertex(Vertex vertex){
		vertices.add(vertex);
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
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
			if ( v.getID() == l ){
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
			if (v.getID() == id){
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
	
	public String toString(){
		String result = "";
		for (Reader r: readers){
			result += "Reader id " + r.getID();
			for (Edge e: r.getVertex().getOutEdges()){
				if (e.getFlow() > 0){
					result += "\n\t" + e;
				}
			}
			result += "\n";
		}
		return result;
	}
	
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
	
	private void addProject(Project project){
		projects.add(project);
		addVertex(project.getVertex());
		
		Edge projectSinkEdge = new Edge(project.getVertex(), sink, PROJECT_READER_CAPACITY);
		addEdge(projectSinkEdge);
		
	}
	
	public void addReader(Reader reader){
		readers.add(reader);
		addVertex(reader.getVertex());
		
		Edge sourceReaderEdge = new Edge(source,reader.getVertex(), reader.getCapacity());
		addEdge(sourceReaderEdge);	
		
		for (Project project : reader.getPreferences()){
			if (!hasProject(project.getId())){
				addProject(project);
			}
			
			Edge readerProjectEdge = new Edge(reader.getVertex(), project.getVertex(), READERS_TO_PROJECTS_CONSTANT);
			addEdge(readerProjectEdge);
			
		}
	}
	
}
