package model;

import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Vertex;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import mcmfuserinterface.TableObjectInterface;

public class Reader  implements TableObjectInterface{
	private final int id;								// reader id
	private final Vertex vertex;						// it's vertex
	private final int capacity;							// it's project preference capacity
	private ArrayList<Integer> supervisorProjects;		// list of already assigned projects
	private ArrayList<Project> preferences;				// list of project preferences
	private String name;
	private int projectUpperLimit;						// the reader's limit of how many projects he can take.
														// Used for load balancing
	
	private SimpleStringProperty preferenceCountProperty;
	
	public Reader(int id, int capacity){
		this.id = id;
		this.vertex = new Vertex(id, this);
		this.capacity = capacity;
		this.supervisorProjects = new ArrayList<Integer>();
		this.preferences = new ArrayList<Project>();
		this.projectUpperLimit = 0;
		this.name = String.valueOf(id);
		this.preferenceCountProperty = new SimpleStringProperty("0");  
	}
        
        public Reader(String readerName, int id){
            this(id,0);
            this.name = readerName;
        }
	
	public void incrementLimit(){
		this.projectUpperLimit++;
	}
        
        public String getName(){
            return this.name;
        }
        
        public void setName(String name){
            this.name = name ;
        }
	
	public int getProjectUpperLimit(){
		return this.projectUpperLimit;
	}
	
	public void addSupervisingProject(int id){
		this.supervisorProjects.add(id);
	}
	
	public boolean addPreference(Project project){
            
            if (this.capacity > 0){
		project.select();
		this.preferences.add(project);
		this.preferenceCountProperty.set(preferences.size()+"");

                return true;
            } else {
                return false;
            }
	}
        
        public boolean addPreference(int indexToPlace, Project project) {
           
            if (this.capacity > 0){
		project.select();
		this.preferences.add(indexToPlace, project);
		this.preferenceCountProperty.set(preferences.size()+"");
                return true;
            } else {
                return false;
            }
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
		return this.id == r.getID() && this.vertex.equals(r.getVertex());
	}
	
	public String toString(){
		return name + "; capacity: " + this.getCapacity() + ", preferences: " + this.getPreferences().size();
	}

	/**
	 * returns how many projects can the reader still take.
	 * @return
	 */
	public int getResidualCapacity() {
		return this.getCapacity() - this.getAssignedProjects().size();
	}

    public void removePreference(Project project) {
        project.unselect();
        this.preferences.remove(project);
		this.preferenceCountProperty.set(preferences.size()+"");

    }

    
    public SimpleStringProperty getPreferenceStringProperty(){
    	return this.preferenceCountProperty;
    }
    

}
