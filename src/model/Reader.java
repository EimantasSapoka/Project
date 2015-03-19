package model;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import ford_fulkerson.network.Edge;
import ford_fulkerson.network.NetworkObjectInterface;
import ford_fulkerson.network.Vertex;

public class Reader  implements TableObjectInterface, NetworkObjectInterface{
	
	private final int id;								// reader id
	private final Vertex vertex;						// it's vertex
	private final int markingTarget;							// it's project preference capacity
	private final ArrayList<Project> supervisorProjects;		// list of already assigned projects
	private ArrayList<Project> preferences;				// list of project preferences
    private ArrayList<Project> assigned;                // list of assigned projects
	private String name;								// reader name
	
	private final SimpleStringProperty preferenceCountProperty;
    private final SimpleStringProperty assignedCountProperty;
  
	
	public Reader(int id, int capacity){
		this.id = id;
		this.vertex = new Vertex(id, this);
		this.markingTarget = capacity;
		this.supervisorProjects = new ArrayList<Project>();
		this.preferences = new ArrayList<Project>();
		this.name = String.valueOf(id);
		
		this.preferenceCountProperty = new SimpleStringProperty("0"); 
		this.assignedCountProperty = new SimpleStringProperty("0");
	}
    
    public Reader(String readerName, int id, int capacity){
        this(id,capacity);
        this.name = readerName;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setName(String name){
        this.name = name ;
    }
        
	public void addSupervisingProject(Project proj){
		this.supervisorProjects.add(proj);
	}
	
	public boolean addPreference(Project project){
        return addPreference(preferences.size(), project);
	}
        
	
	/**
	 * adds a preference to this readers list
	 * @param indexToPlace
	 * @param project
	 * @return
	 */
    public boolean addPreference(int indexToPlace, Project project) {
	    if (	markingTarget == 0 || 
	    		preferences.contains(project) ||
	    		supervisorProjects.contains(project.getID()) ){
        	return false;
        } else {
            project.select();
			this.preferences.add(indexToPlace, project);
			this.preferenceCountProperty.set(preferences.size()+"");
            return true;
        }
    }

	public Vertex getVertex() {
		return this.vertex;
	}

	public int getMarkingTarget() {
		return markingTarget;
	}

	public ArrayList<Project> getSupervisorProjects() {
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
    * returns all assigned projects from the graph data. WARNING!!
    * HAS A SIDE EFFECT OF OVERWRITING ASSIGNMENT DATA WITH 
    * WHAT IS IN THE GRAPH.
    *
    * @return
    */
   private ArrayList<Project> getAssignedProjectsFromGraph() {
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
       if (assigned == null){
           assigned = getAssignedProjectsFromGraph();
       }
       this.assignedCountProperty.set(assigned.size()+"");
       return assigned;
   }
	
	public boolean equals(Reader r){
		if (r == null){
			return false;
		}
		return this.id == r.getID() && this.vertex.equals(r.getVertex());
	}
	
	public String toString(){
		return name + "; capacity: " + this.getMarkingTarget() + ", preferences: " + this.getPreferences().size();
	}

	/**
	 * returns how many projects can the reader still take.
	 * @return
	 */
	public int getResidualCapacity() {
		return this.getMarkingTarget() - this.getAssignedProjectsFromGraph().size();
	}

    public void removePreference(Project project) {
        project.unselect();
        this.preferences.remove(project);
        this.preferenceCountProperty.set(preferences.size()+"");
    }


    void resetVertex() {
        this.vertex.resetVertex();
    }
    
    public boolean assignProject(Project p){
        return assignProject(assigned.size(), p);
    } 
    
    /**
     * assigns a project to this reader. 
     * @param indexToPlace
     * @param projectToMove
     * @return
     */
    public boolean assignProject(int indexToPlace, Project projectToMove) {
        if (	this.markingTarget == assigned.size() || 
        		assigned.contains(projectToMove) || 
        		supervisorProjects.contains(projectToMove.getID()) ){
            return false;
        } else {
            projectToMove.assignToReader(this);
            this.assigned.add(indexToPlace, projectToMove);
            this.assignedCountProperty.set(assigned.size()+"");
            return true;
        }
    }
    
    public boolean removeAssignedProject(Project p){
        boolean success = this.assigned.remove(p);
        p.assignToReader(null);
        this.assignedCountProperty.set(assigned.size()+"");
        return success;
    }
    
    public void clearAssignedProjects(){
        if (assigned != null){
            for (Project p: assigned){
                p.assignToReader(null);
            }
            this.assigned = null;
            this.assignedCountProperty.set("0");
        }
    }   
    
    
    /******************** OBSERVABLE ITEMS METHODS *******************/
    
    public SimpleStringProperty getPreferenceCountProperty(){
    	return this.preferenceCountProperty;
    }
    
    public SimpleStringProperty getAssignedCountProperty(){
        return this.assignedCountProperty;
    }    
}
