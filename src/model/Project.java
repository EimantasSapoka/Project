package model;

import ford_fulkerson.graph.Vertex;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;

public class Project implements Comparable<Project>, TableObjectInterface{
	private final int id;			// project id
	private final Vertex vertex;	// vertex associated with the project
	private int timesSelected;		// number of times the project had been added to a pref list. 
	private String name;
        
	public Project (int id){
		this.id = id;
		this.vertex = new Vertex(id, this);
		this.timesSelected = 0;
                this.name = id+"";
	}
        
        public Project(String projectName, int id){
            this(id);
            this.name = projectName;
        }

	public Vertex getVertex() {
		return vertex;
	}

	public int getId() {
		return id;
	}
	
        @Override
	public String toString(){
		return name +"\t ("+this.timesSelected+")";
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
}

    