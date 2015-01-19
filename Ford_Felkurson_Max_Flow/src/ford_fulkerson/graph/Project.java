package ford_fulkerson.graph;

public class Project {
	private final int id;			// project id
	private final Vertex vertex;	// vertex associated with the project
	private int timesSelected;		// number of times the project had been added to a pref list. 
	
	public Project (int id){
		this.id = id;
		this.vertex = new Vertex(id, this);
		this.timesSelected = 0;
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
	
	public void select(){
		this.timesSelected++;
	}
	
	public int getSelectedCount(){
		return this.timesSelected;
	}
}
