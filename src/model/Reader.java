package model;

import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Vertex;
import java.util.ArrayList;
import mcmfuserinterface.TreeObjectInterface;

public class Reader  implements TreeObjectInterface{
	private final int id;								// reader id
	private final Vertex vertex;						// it's vertex
	private final int capacity;							// it's project preference capacity
	private ArrayList<Integer> supervisorProjects;		// list of already assigned projects
	private ArrayList<Project> preferences;				// list of project preferences
	
	private int projectUpperLimit;						// the reader's limit of how many projects he can take.
														// Used for load balancing
	
	public Reader(int id, int capacity){
		this.id = id;
		this.vertex = new Vertex(id, this);
		this.capacity = capacity;
		this.supervisorProjects = new ArrayList<Integer>();
		this.preferences = new ArrayList<Project>();
		this.projectUpperLimit = 0;
	}
	
	public void incrementLimit(){
		this.projectUpperLimit++;
	}
	
	public int getProjectUpperLimit(){
		return this.projectUpperLimit;
	}
	
	public void addSupervisingProject(int id){
		this.supervisorProjects.add(id);
	}
	
	public void addPreference(Project project){
		project.select();
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

	/**
	 * returns shallow copy of readers preferences
	 * @return
	 */
	public ArrayList<Project> getPreferences() {
		return preferences;
	}
	
	public int getID(){
		return this.id;
	}
	
	/**
	 * returns all assigned projects
	 * @return
	 */
	public ArrayList<Project> getAssignedProjects(){
		ArrayList<Project> projects = new ArrayList<Project>();
		for (Edge edge : this.vertex.getOutEdges()){
			if (edge.getFlow() > 0){
				Project project = (Project) edge.getDestination().getObject();
				projects.add(project);
			}
		}
		return projects;
	}
	
	public boolean equals(Reader r){
		return this.id == r.getID();
	}
	
	public String toString(){
		String result = "Reader id: " +id + ", project capacity: " + capacity;
		result += "\nSupervised projects: ";
		for (Integer p: supervisorProjects){
			result += "\n" + p;
		}
		result += "\nPreferences: ";
		for (Project p: preferences){
			result += "\n" + p;
		}
		return result;
	}

	/**
	 * returns how many projects can the reader still take.
	 * @return
	 */
	public int getResidualCapacity() {
		return this.getCapacity() - this.getAssignedProjects().size();
	}

}
