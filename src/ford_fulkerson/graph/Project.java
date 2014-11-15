package ford_fulkerson.graph;

public class Project {
	private final int id;			// project id
	private final Vertex vertex;	// vertex associated with the project
	
	public Project (int id){
		this.id = id;
		this.vertex = new Vertex(id);
	}

	public Vertex getVertex() {
		return vertex;
	}

	public int getId() {
		return id;
	}
	
	public String toString(){
		return this.id + "";
	}
	
}
