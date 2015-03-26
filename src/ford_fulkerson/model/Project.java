package ford_fulkerson.model;

import ford_fulkerson.network.NetworkObjectInterface;
import ford_fulkerson.network.Vertex;

/**
 * a class which holds all information about a project
 * @author Eimantas
 *
 */
public class Project implements Comparable<Project>, NetworkObjectInterface {
	
	private final int id;			// project id
	private final Vertex vertex;	// vertex associated with the project
	private int timesSelected;		// number of times the project had been added to a pref list. 
    private int supervisorID;       // id of the supervisor.
	private String name;			// the name of the project, obviously...
    private Reader assignedTo;		// the reader, to whom the project was assigned to, reference
        
    /**
     * constructor which creates a project with 
     * its name set to be its ID and supervisor ID to -1.   
     * @param id
     */
	public Project (int id){
		this.id = id;
		this.vertex = new Vertex(this);
		this.timesSelected = 0;
        this.name = id+"";
        this.supervisorID = -1;
	}
        
	/**
	 * constructor which creates a project with supervisor
	 * ID as -1. Mostly should be used for testing and not 
	 * real case instances. 
	 * @param projectName
	 * @param id
	 */
    public Project(String projectName, int id){
        this(id);
        this.name = projectName;
    }
    
    /**
     * constructor which creates a project. 
     * @param projectName
     * @param id
     * @param projectSupervisorID
     */
    public Project(String projectName, int id, int projectSupervisorID){
        this(projectName, id);
        this.supervisorID = projectSupervisorID;
    }
    
    public void assignToReader(Reader reader){
        this.assignedTo = reader;
    }
    
    public int getSupervisorID(){
        return this.supervisorID;
    }

	public Vertex getVertex() {
		return vertex;
	}

	public int getID() {
		return id;
	}
	
    @Override
	public String toString(){
		return "("+id+") " + name;
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
        

    void unselect() {
        this.timesSelected--;
    }

    public String getName() {
        return this.name;
    }

    public void resetVertex() {
        this.vertex.resetVertex();
    }

    public Reader getAssignedReader() {
        return assignedTo;
    }
    
    public boolean equals(int id){
    	return this.id == id;
    } 
    
    /**
     * compares the projects and returns weather they are
     * equal. compares in terms of project IDs and vertex IDs
     * @param project
     * @return
     */
    public boolean equals(Project project){
        if (project == null) {
            return false;
        }
        return this.id == project.getID() && this.vertex.equals(project.getVertex());
    }
    
}

    