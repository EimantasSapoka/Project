/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ford_fulkerson.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import ford_fulkerson.ReaderShortlistException;
import ford_fulkerson.TextScanner;
import ford_fulkerson.network.Network;

/**
 * A class containing information needed to run
 * the MCMF algorithm. 
 * @author Eimantas
 */
public class MCMFModel {
	
	protected List<Reader> readers;		// readers list
    protected List<Project> projects;		// projects list
    protected Network network;					// the network

    public MCMFModel(File file) throws Exception {
       this();
       loadNetworkFromFile(file);
    }
    
    public MCMFModel(){
        this.readers = FXCollections.observableArrayList();
        this.projects = FXCollections.observableArrayList();
    }


	public void loadNetworkFromFile(File file) throws Exception {
        TextScanner.parseCommaSeparatedInput(file, this);
    }
    

    public Network getNetwork() {
        return this.network;
    }

    /**
     * method to check if the model contains the reader
     *
     * @param id
     * @return
     */
    public boolean hasReader(int id) {
        for (Reader r : readers) {
            if (r.getID() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * method to check if a model contains the project
     *
     * @param project
     * @return
     */
    public boolean hasProject(Project project) {
        for (Project p : projects) {
            if (p.equals(project)) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns a project with the id
     *
     * @param id
     * @return
     */
    public Project getProject(int id) {
        for (Project p : projects) {
            if (p.getID() == id) {
                return p;
            }
        }
        return null;
    }
    
    public Project getProject(Project project){
        if (project == null){
            return null;
        }
         for (Project p : projects) {
            if (p.equals(project)) {
                return p;
            }
        }
        return null;
    }

    /**
     * returns projects list
     *
     * @return
     */
    public List<Project> getProjects() {
        return this.projects;
    }

    public List<Reader> getReaders() {
        return this.readers;
    }

    public Reader getReader(Reader r){
        for (Reader reader : this.getReaders()){
            if (reader.equals(r)){
                return r;
            }
        }
        return null;
    }
    /**
     * returns if the graph is load balanced. That is weather all the reader's
     * in the graph have no more than one less project assigned with respect to
     * their capacities than any other reader. Example: if a reader has capacity
     * 7 and flow 4, load balanced graph would mean that no other reader has
     * flow higher than their capacity - 3 or lower than their capacity - 4.
     *
     * @return
     */
    public boolean isLoadBalanced() {
        for (Reader reader : this.readers) {
        	
        	int readerCapacityGap = reader.getMarkingTarget() + network.getLowerCapacityOffset() - reader.getAssigned().size();
        	readerCapacityGap = readerCapacityGap < 0? 0:readerCapacityGap;
        	
            for (Reader otherReader : readers){
            	
            	int otherReaderCapacityGap = otherReader.getMarkingTarget() + network.getLowerCapacityOffset() - otherReader.getAssigned().size();
            	otherReaderCapacityGap = otherReaderCapacityGap < 0? 0:otherReaderCapacityGap;
            	
            	if (Math.abs( otherReaderCapacityGap - readerCapacityGap) > 1){
            		return false; 
            	}
            }
        }
        return true;
    }

    /**
     * adds the project to the model
     *
     * @param project
     */
    public void addProject(Project project) {
        if (!projects.contains(project)) {
            projects.add(project);
        }
    }

    /**
     * adds a reader to the graph. This process includes adding all the reader's
     * projects into the graph (if not yet present) and creating all the
     * necessary vertices and edges.
     *
     * @param reader
     */
    public void addReader(Reader reader) {
        readers.add(reader);
        for (Project p : reader.getPreferences()) {
            this.addProject(p);
        }
    }

    /**
     * creates the vertices and edges between them according to the reader and
     * preference information.
     */
    public void createNetwork() throws ReaderShortlistException {
        StringBuilder warnings = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        network = new Network();
        
        for (Project project : this.projects){
            
           project.resetVertex();
           network.addProject(project);
           
           if (project.getSelectedCount() == 0){
               errors.append(String.format(">PROJECT %s (ID: %d) HAS NOT BEEN SELECTED BY ANYONE\n", project.getName(),project.getID()));
           } 
        }

        for (Reader reader : this.readers) {
            reader.resetVertex();
            reader.clearAssignedProjects();
            
            network.addReader(reader);
            
            int preferenceCount = reader.getPreferences().size();
			int markingTarget = reader.getMarkingTarget();
			int id = reader.getID();
			String name = reader.getName();
			
			if (preferenceCount < markingTarget) {
            	
                errors.append(String.format(">READER %s (ID: %d) HAS CAPACITY OF %d AND PREFERENCE LIST SIZE %d\n",
                		name, id, markingTarget, preferenceCount));
                
            } else if (preferenceCount < markingTarget * 2) {
            	
                warnings.append(String.format(">reader %s (ID: %d) has capacity of %d and preference list size %d. "
                		+ "Ideally, %d preferneces are needed\n", name, id, markingTarget, preferenceCount, markingTarget * 2));
                
            }
        }
        
        if (errors.length() > 0){
            if (warnings.length() > 0){
                throw new ReaderShortlistException(">>>> ERRORS:\n"+errors + "\n>>>> WARNINGS:\n"+warnings, true);
            } else {
                throw new ReaderShortlistException(">>>> ERRORS:\n"+errors,true);
            }
        } else if (warnings.length() > 0){
            throw new ReaderShortlistException(">>>> WARNINGS:\n"+warnings);
        }
    }

    /**
     * method which extends readers preference lists to 2x their capacities
     */
    public void extendPreferenceLists() {
        for (Reader r : this.readers) {

           List<Project> preferences = r.getPreferences();
           List<Project> projectList = new ArrayList<Project>();
           projectList.addAll(projects);

            while (preferences.size() < 2 * r.getMarkingTarget() && !projectList.isEmpty()) {

                Collections.sort(projectList);
                Project proj = projectList.remove(0);

                if (!preferences.contains(proj)) {
                    if (!r.getSupervisorProjects().contains(proj.getID())) {
                        r.addPreference(proj);
                    }
                }
            }

        }
    }

    /**
     * prints out the readers and their assigned projects
     *
     * @return
     */
    @Override
    public String toString() {
        String result = "";
        for (Reader reader : readers){
            result += reader.getID() +", ";
            for (Project project : reader.getAssigned()){
                result += project.getID()+" ";
            }
            result+="\n";
        }
        return result;
    }
    
    public Reader getReader(int readerID) {
        for (Reader reader : readers){
            if (reader.getID() == readerID){
                return reader;
            }
        }
        return null;
    }


	public void reset() {
		for (Reader r: readers){
			r.clearAssignedProjects();
		}
	}

}
