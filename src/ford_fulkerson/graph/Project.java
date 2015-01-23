package ford_fulkerson.graph;

public class Project implements Comparable<Project> {
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
		return "id: " + this.id + " times selected: " + timesSelected;
	}
	
	public void select(){
		this.timesSelected++;
	}
	
	public int getSelectedCount(){
		return this.timesSelected;
	}

	@Override
	public int compareTo(Project project) { 
		return this.timesSelected - project.getSelectedCount();
	}
}
