package ford_fulkerson.graph;

import java.util.ArrayList;

public class Reader {
	private final int id;								// reader id
	private final Vertex vertex;						// it's vertex
	private final int capacity;							// it's project preference capacity
	private ArrayList<Integer> supervisorProjects;		// list of already assigned projects
	private ArrayList<Project> preferences;				// list of project preferences
	
	public Reader(int id, int capacity){
		this.id = id;
		this.vertex = new Vertex(id);
		this.capacity = capacity;
		this.supervisorProjects = new ArrayList<Integer>();
		this.preferences = new ArrayList<Project>();
	}
	
	public void addSupervisingProject(int projectID){
		this.supervisorProjects.add(projectID);
	}
	
	public void addPreference(Project project){
		this.preferences.add(project);
	}

	public Vertex getVertex() {
		return this.vertex;
	}

	public int getCapacity() {
		return capacity;
	}

	public ArrayList<Integer> getSupervisorProjects() {
		return supervisorProjects;
	}

	public ArrayList<Project> getPreferences() {
		return preferences;
	}
	
	public int getID(){
		return this.id;
	}
	
	public boolean equals(Reader r){
		return this.id == r.getID();
	}
	
	public String toString(){
		String result = "Reader id: " +id + ", project capacity: " + capacity;
		result += "\nSupervised projects: ";
		for (int p: supervisorProjects){
			result += "\n" + p;
		}
		result += "\nPreferences: ";
		for (Project p: preferences){
			result += "\n" + p;
		}
		return result;
	}
}
