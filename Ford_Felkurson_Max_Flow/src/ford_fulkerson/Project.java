package ford_fulkerson;

public class Project {
	private final int id;
	private final Vertex vertex;
	private final int preferenceNo;
	
	public Project (int id, int preference){
		this.id = id;
		this.vertex = new Vertex(id);
		this.preferenceNo = preference;
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
	
	public int getPreference(){
		return preferenceNo;
	}
	
}
