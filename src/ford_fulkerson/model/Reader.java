package ford_fulkerson.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import ford_fulkerson.network.Edge;
import ford_fulkerson.network.NetworkObjectInterface;
import ford_fulkerson.network.Vertex;

/**
 * class which holds all reader information
 * @author Eimantas
 *
 */
public class Reader  implements NetworkObjectInterface{
	
	private final int id;								// reader id
	private final Vertex vertex;						// it's vertex
	private int readerTarget;							// it's project preference capacity
	private final ArrayList<Project> supervisorProjects;	// list of already assigned projects.
	private List<Project> preferences;					// list of project preferences
    private List<Project> assigned;                		// list of assigned projects
	private String name;								// reader name
	
	/*
	 * UI Specific variables. These are used, because they relate to the 
	 * javaFX framework and have listeners associated to them so if they 
	 * change, the view is updated. 
	 */
	private final IntegerProperty preferenceCountProperty;		// The number of preferences read has
    private final IntegerProperty assignedCountProperty;		// The number of project reader is assigned
    private final SimpleStringProperty readerPreferenceShortlistStyleProperty; // The style this reader's preferences row should have
    private final SimpleStringProperty readerAssignedShortlistStyleProperty; // The style this reader's assigned row should have

	
	public Reader(int id, int target){
		this.id = id;
		this.vertex = new Vertex(this);
		this.readerTarget = target < 0? 0:target;
		this.supervisorProjects = new ArrayList<Project>();
		this.preferences = FXCollections.observableArrayList();
		this.name = String.valueOf(id);
		
		
		// UI specific parameters
		this.preferenceCountProperty = new SimpleIntegerProperty(0); 
		this.assignedCountProperty = new SimpleIntegerProperty(0);
		this.readerPreferenceShortlistStyleProperty = new SimpleStringProperty("");
		this.readerAssignedShortlistStyleProperty = new SimpleStringProperty("");
		instanciateUIElementListeners();
		
		
	}

	/**
	 * Adds listeners to preference count and assigned project count
	 * properties so when these change, they also change the values 
	 * of their respective row styles. For example, if the reader 
	 * has a project added to his preferences or assigned projects list,
	 * those lists styles would react and change appropriately. 
	 */
	private void instanciateUIElementListeners() {
		preferenceCountProperty.addListener((observable, oldValue, newValue) ->{
			int num = (int) newValue;
			if (num < readerTarget){
				readerPreferenceShortlistStyleProperty.set("-fx-background-color: red;");
            } else if (num < readerTarget*2){
            	readerPreferenceShortlistStyleProperty.set("-fx-background-color: orange;");
            } else {
            	readerPreferenceShortlistStyleProperty.set("");
            }
		});
		
		assignedCountProperty.addListener((observable, oldValue, newValue) ->{
			int num = (int) newValue;
			 if (readerTarget - 1 > num){
				 readerAssignedShortlistStyleProperty.set("-fx-background-color: red;");
             } else if (readerTarget > num){
            	 readerAssignedShortlistStyleProperty.set("-fx-background-color: orange;");
             } else {
            	 readerAssignedShortlistStyleProperty.set("");
             }
		});
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
	    if (	readerTarget == 0 || 
	    		preferences.contains(project) ||
	    		supervisorProjects.contains(project.getID()) ){

        	return false;
        } else {
            project.select();
			this.preferences.add(indexToPlace, project);
			this.preferenceCountProperty.set(preferences.size());
            return true;
        }
    }

	public Vertex getVertex() {
		return this.vertex;
	}

	public int getReaderTarget() {
		return readerTarget;
	}

	public List<Project> getSupervisorProjects() {
		return supervisorProjects;
	}

	/**
	 * returns shallow copy of readers preferences
	 * @return
	 */
	public List<Project> getPreferences() {
		return preferences;
	}
	
	public int getID(){
		return this.id;
	}
	
    /**
    * returns all assigned projects from the graph data. WARNING!!
    * HAS A SIDE EFFECT OF OVERWRITING ASSIGNMENT DATA WITH 
    * WHAT IS IN THE GRAPH. IF CHANGING IMPLEMENTATION, TREAD CAREFULLY.
    * you have been warned. 
    *
    * @return
    */
   private List<Project> getAssignedProjectsFromGraph() {
       List<Project> temp = FXCollections.observableArrayList();
       for (Edge edge : this.vertex.getOutEdges()) {
            if (edge.getFlow() > 0) {
                Project project = (Project) edge.getDestination().getObject();
                project.assignToReader(this);
                temp.add(project);
            }
       }
       return temp;
   }
   
   /**
    * returns the projects which are assigned to this reader. 
    * always returns a list. Never null. 
    * @return
    */
   public List<Project> getAssigned(){
	   /*
	    *  if the assigned list had not been initialized yet, 
	    *  initialize it with assigned projects from the network.
	    *  Do not do this if the list has already been initialized,
	    *  as all user manual modifications will be overwritten with 
	    *  assigned projects from network. 
	    */
	   
       if (assigned == null){
           assigned = getAssignedProjectsFromGraph();
       }
       this.assignedCountProperty.set(assigned.size());
       return assigned;
   }
	
   /**
    * compares the readers with respect to their IDs and vertices.
    * @param r
    * @return
    */
	public boolean equals(Reader r){
		if (r == null){
			return false;
		}
		return this.id == r.getID() && this.vertex.equals(r.getVertex());
	}
	
	public String toString(){
		return name + "; capacity: " + this.getReaderTarget() + ", preferences: " + this.getPreferences().size();
	}

	/**
	 * returns how many projects can the reader still take.
	 * reader target - assigned projects.
	 * @return
	 */
	public int getResidualCapacity() {
		return this.getReaderTarget() - this.getAssignedProjectsFromGraph().size();
	}

    public void removePreference(Project project) {
        project.unselect();
        this.preferences.remove(project);
        this.preferenceCountProperty.set(preferences.size());
    }


    void resetVertex() {
        this.vertex.resetVertex();
    }
    
    public boolean assignProject(Project p){
        if (assigned == null){
            assigned = getAssignedProjectsFromGraph();
        }
        return assignProject(assigned.size(), p);
    } 
    
    /**
     * assigns a project to this reader. 
     * @param indexToPlace
     * @param projectToMove
     * @return weather it had been assigned
     */
    public boolean assignProject(int indexToPlace, Project projectToMove) {
        if (	this.readerTarget == assigned.size() || 
        		assigned.contains(projectToMove) || 
        		supervisorProjects.contains(projectToMove) ){
            return false;
        } else {
            projectToMove.assignToReader(this);
            this.assigned.add(indexToPlace, projectToMove);
            this.assignedCountProperty.set(assigned.size());
            return true;
        }
    }
    
    public boolean removeAssignedProject(Project p){
        boolean success = this.assigned.remove(p);
        p.assignToReader(null);
        this.assignedCountProperty.set(assigned.size());
        return success;
    }
    
    public void clearAssignedProjects(){
        if (assigned != null){
            for (Project p: assigned){
                p.assignToReader(null);
            }
            this.assigned = null;
            this.assignedCountProperty.set(0);
        }
    }   
    
    
    /******************** OBSERVABLE ITEMS METHODS *******************/
    
    public IntegerProperty getPreferenceCountProperty(){
    	return this.preferenceCountProperty;
    }
    
    public IntegerProperty getAssignedCountProperty(){
        return this.assignedCountProperty;
    }

	public SimpleStringProperty getReaderPreferenceShortlistStyleProperty() {
		return readerPreferenceShortlistStyleProperty;
	}

	public SimpleStringProperty getReaderAssignedShortlistStyleProperty() {
		return readerAssignedShortlistStyleProperty;
	}

	public void setReaderTarget(Integer newValue) {
		this.readerTarget = newValue;
		
		// to repaint the rows invoke their listeners by changing value
		this.preferenceCountProperty.set(preferenceCountProperty.get()+1);
		this.assignedCountProperty.set(assignedCountProperty.get()+1);
		this.preferenceCountProperty.set(preferenceCountProperty.get()-1);
		this.assignedCountProperty.set(assignedCountProperty.get()-1);
	}

	public void decreaseReaderTarget() {
		setReaderTarget(readerTarget - 1);
	}    
    
	public void increaseReaderTarget(){
		setReaderTarget(readerTarget + 1);
	}
    
}
