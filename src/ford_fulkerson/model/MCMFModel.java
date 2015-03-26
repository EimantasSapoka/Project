/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ford_fulkerson.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import ford_fulkerson.ReaderShortlistException;
import ford_fulkerson.network.Network;
import ford_fulkerson.utils.InvalidInputException;
import ford_fulkerson.utils.TextScanner;

/**
 * A class containing information needed to run
 * the MCMF algorithm. 
 * @author Eimantas
 */
public class MCMFModel {
	
	protected List<Reader> readers;			// readers list
    protected List<Project> projects;		// projects list
    protected Network network;				// the network

    /**
     * constructor which parses a file and creates the 
     * reader allocation instance. Throws exceptions if 
     * it cannot open the file or parse the file
     * @param file
     * @throws IOException if the file cannot be opened
     * @throws InvalidInputException if the file cannot be parsed
     */
    public MCMFModel(File file) throws InvalidInputException, IOException  {
       this();
       loadNetworkFromFile(file);
    }
    
    /**
     * default constructor which creates an empty instance
     * with zero readers and zero projects.
     */
    public MCMFModel(){
        this.readers = FXCollections.observableArrayList();
        this.projects = FXCollections.observableArrayList();
    }


    /**
     * loads the network from file and populates the model with
     * readers and projects
     * @param file to parse
     * @throws InvalidInputException if file is incorrectly formatted
     * @throws IOException if file cannot be opened
     */
	public void loadNetworkFromFile(File file) throws InvalidInputException, IOException {
        TextScanner.parseCommaSeparatedInput(file, this);
    }
    

    public Network getNetwork() {
        return this.network;
    }

    /**
     * method to check if the model contains the reader
     * with the id
     * @param id of the reader
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
     * @param project
     * @return weather the model contains the project
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
     * @param id
     * @return project with the id or null if not found
     */
    public Project getProject(int id) {
        for (Project p : projects) {
            if (p.getID() == id) {
                return p;
            }
        }
        return null;
    }
    

    /**
     * returns projects list
     * @return
     */
    public List<Project> getProjects() {
        return this.projects;
    }

    /**
     * returns the readers list
     * @return
     */
    public List<Reader> getReaders() {
        return this.readers;
    }


    /**
     * returns if the graph is load balanced. This is calculated by
     * comparing each reader to all other readers and if their marking gap
     * absolute difference is more than one, the graph is not load balanced.
     * A reader's marking gap is the difference between its reader target and
     * the number of projects assigned.
     * @return
     */
    public boolean isLoadBalanced() {
        for (Reader reader : this.readers) {
        	if (reader.getReaderTarget() == 0){
        		continue; // if the reader has target of zero, ignore him
        	}
        	
        	// reader target gap is his target - number assigned projects and takes into account the reader target offset.
        	int readerTargetGap = reader.getReaderTarget() + network.getLowerCapacityOffset() - reader.getAssigned().size();
        	
        	// if lower than zero, just consider it to be zero
        	readerTargetGap = readerTargetGap < 0? 0:readerTargetGap;
        	
        	// loop through all readers and compare reader target gaps
            for (Reader otherReader : readers){
            	if (otherReader.getReaderTarget() == 0){
            		continue; // same here, ignore readers with target of zero
            	}
            	int otherReaderTargetGap = otherReader.getReaderTarget() + network.getLowerCapacityOffset() - otherReader.getAssigned().size();
            	otherReaderTargetGap = otherReaderTargetGap < 0? 0:otherReaderTargetGap;
            	
            	/*
            	 *  if the absolute difference between reader target gaps is more than one, network is 
            	 *  not load balanced
            	 */
            	if (Math.abs( otherReaderTargetGap - readerTargetGap) > 1){
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
     * Resets the previous network, if present. 
     * Creates a new network and sets the vertices and edges between them 
     * according to the reader and preference information. 
     * @throws ReaderShortlistException if any readers have shortlists
     */
    public void createNetwork() throws ReaderShortlistException {
        StringBuilder warnings = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        network = new Network();
        
        for (Project project : this.projects){
            
           project.resetVertex(); 
           network.addProject(project);
           
           // if project not selected, add error message
           if (project.getSelectedCount() == 0){
               errors.append(String.format(">PROJECT %s (ID: %d) HAS NOT BEEN SELECTED BY ANYONE\n", 
            		   						project.getName(),project.getID()));
           } 
        }

        for (Reader reader : this.readers) {
            reader.resetVertex();
            reader.clearAssignedProjects();
            
            network.addReader(reader);
            
            int preferenceCount = reader.getPreferences().size();
			int markingTarget = reader.getReaderTarget();
			int id = reader.getID();
			String name = reader.getName();
			
			if (preferenceCount < markingTarget) {
            	
                errors.append(String.format(">READER %s (ID: %d) HAS CAPACITY OF %d AND PREFERENCE LIST SIZE %d\n",
                		name, id, markingTarget, preferenceCount));
                
            } else if (preferenceCount < markingTarget * 2) {
            	
                warnings.append(String.format(">reader %s (ID: %d) has capacity of %d and %d preferences. "
                		+ "Ideally, %d preferences are needed\n", name, id, markingTarget, preferenceCount, markingTarget * 2));
                
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
     * method which extends readers preference lists to 2x their capacities.
     * Does so by assigning the least selected projects first
     */
    public void extendPreferenceLists() {
        for (Reader r : this.readers) {

           List<Project> preferences = r.getPreferences();
           List<Project> projectList = new ArrayList<Project>();
           projectList.addAll(projects);

            while (preferences.size() < 2 * r.getReaderTarget() && !projectList.isEmpty()) {

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


    /**
     * clears the assigned projects list of each reader.
     */
	public void clearReaderAssignedProjectLists() {
		for (Reader r: readers){
			r.clearAssignedProjects();
		}
	}

	/**
	 * decreases each reader's reader target by 1
	 */
	public void decreaseReaderTargets() {
		for (Reader r: readers){
			r.decreaseReaderTarget();
		}
	}

	/**
	 * increases each reader's reader target by 1
	 */
	public void increaseReaderTargets() {
		for (Reader r: readers){
			r.increaseReaderTarget();
		}
	}

	public void reset() {
		for (Reader r: readers){
			r.clearAssignedProjects();
		}
	}


}
