package model;

import ford_fulkerson.graph.Vertex;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;

public class Project implements Comparable<Project>, TableObjectInterface{
	private final int id;			// project id
	private final Vertex vertex;	// vertex associated with the project
	private int timesSelected;		// number of times the project had been added to a pref list. 
        private int supervisorID;       // id of the supervisor.
	private String name;
        private Reader assigned;
        
        
	public Project (int id){
		this.id = id;
		this.vertex = new Vertex(id, this);
		this.timesSelected = 0;
                this.name = id+"";
                this.supervisorID = -1;
	}
        
        public Project(String projectName, int id){
            this(id);
            this.name = projectName;
        }
        
        public Project(String projectName, int id, int projectSupervisorID){
            this(projectName, id);
            this.supervisorID = projectSupervisorID;
        }
        
        public void assignToReader(Reader reader){
            this.assigned = reader;
        }
        
        public int getSupervisorID(){
            return this.supervisorID;
        }

	public Vertex getVertex() {
		return vertex;
	}

	public int getId() {
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
        
        public boolean equals(Project project){
            if (project == null) {
                return false;
            }
            return this.id == project.getId() && this.vertex.equals(project.getVertex());
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
        return assigned;
    }
}

    