package model;

import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Vertex;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;

public class Reader  implements TableObjectInterface{
	private final int id;								// reader id
	private final Vertex vertex;						// it's vertex
	private final int capacity;							// it's project preference capacity
	private ArrayList<Integer> supervisorProjects;		// list of already assigned projects
	private ArrayList<Project> preferences;				// list of project preferences
        private ArrayList<Project> assigned;                        // list of assigned projects
	private String name;
	
	private SimpleStringProperty preferenceCountProperty;
        private SimpleStringProperty assignedCountProperty;
	
	public Reader(int id, int capacity){
		this.id = id;
		this.vertex = new Vertex(id, this);
		this.capacity = capacity;
		this.supervisorProjects = new ArrayList<Integer>();
		this.preferences = new ArrayList<Project>();
                this.assigned = new ArrayList<Project>();
		this.name = String.valueOf(id);
		this.preferenceCountProperty = new SimpleStringProperty("0"); 
                this.assignedCountProperty = new SimpleStringProperty("0");
	}
        
        public Reader(String readerName, int id){
            this(id,0);
            this.name = readerName;
        }
        
        public String getName(){
            return this.name;
        }
        
        public void setName(String name){
            this.name = name ;
        }
        
	public void addSupervisingProject(int id){
		this.supervisorProjects.add(id);
	}
	
	public boolean addPreference(Project project){
            
            if (this.capacity > 0 && !this.preferences.contains(project)){
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
        *
        * @return
        */
       protected ArrayList<Project> getAssignedProjectsFromGraph() {
           ArrayList<Project> temp = new ArrayList<Project>();
           for (Edge edge : this.vertex.getOutEdges()) {
                if (edge.getFlow() > 0) {
                    Project project = (Project) edge.getDestination().getObject();
                    project.assignToReader(this);
                    temp.add(project);
                }
           }
           return temp;
       }
       
       public ArrayList<Project> getAssigned(){
           if (assigned.isEmpty()){
               assigned = getAssignedProjectsFromGraph();
           }
           this.assignedCountProperty.set(assigned.size()+"");
           return assigned;
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
		return this.getCapacity() - this.getAssignedProjectsFromGraph().size();
	}

    public void removePreference(Project project) {
        project.unselect();
        this.preferences.remove(project);
		this.preferenceCountProperty.set(preferences.size()+"");

    }

    
    public SimpleStringProperty getPreferenceStringProperty(){
    	return this.preferenceCountProperty;
    }
    
    public SimpleStringProperty getAssignedCountStringProperty(){
        return this.assignedCountProperty;
    }

    void resetVertex() {
        this.vertex.resetVertex();
    }
    
    public boolean assignProject(Project p){
        p.assignToReader(this);
        this.assigned.add(p);
        this.assignedCountProperty.set(assigned.size()+"");
        return true;
    } 
    
    public void assignProject(int indexToPlace, Project projectToMove) {
        projectToMove.assignToReader(this);
        this.assigned.add(indexToPlace, projectToMove);
        this.assignedCountProperty.set(assigned.size()+"");
    }
    
    public boolean removeAssignedProject(Project p){
        boolean success = this.assigned.remove(p);
        this.assignedCountProperty.set(assigned.size()+"");
        return success;
    }
    
    public void clearAssignedProjects(){
        for (Project p: assigned){
            p.assignToReader(null);
        }
        this.assigned.clear();
        this.assignedCountProperty.set("0");
    }   
    
}
