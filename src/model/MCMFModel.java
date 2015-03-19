/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ford_fulkerson.ReaderShortlistException;
import ford_fulkerson.TextScanner;
import ford_fulkerson.network.Network;

/**
 *
 * @author Eimantas
 */
public class MCMFModel {

    private static final String READER_AT_MARKING_TARGET_ERR_MSG = "Reader is at his marking target";
	private static final String READER_ALREADY_ASSIGNED_PROJECT_ERR_MSG = "Reader is already assigned this project";
	private static final String READER_CAPACITY_ZERO_ERR_MSG = "Reader has reader target of zero";
	private static final String PROJECT_ALREADY_PREFERENCE_ERROR_MSG = "Project already in preference list";
	private static final String PROJECT_SUPERVISED_ERROR_MSG = "Project is already supervised by this reader";
	
	protected ArrayList<Reader> readers;		// readers list
    protected ArrayList<Project> projects;		// projects list

    private Network network;

    public MCMFModel(File file) throws Exception {
       this();
       loadGraphFromFile(file);
    }
    
    public MCMFModel(){
        this.readers = new ArrayList<Reader>();
        this.projects = new ArrayList<Project>();
    }

    public void loadGraphFromFile(File file) throws Exception {
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
    public ArrayList<Project> getProjects() {
        return this.projects;
    }

    public ArrayList<Reader> getReaders() {
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
        String warnings = "";
        String errors = "";
        network = new Network();
        
        for (Project project : this.projects){
            
           project.resetVertex();
           network.addProject(project);
           
           if (project.getSelectedCount() == 0){
               errors += ">PROJECT " + project.getName() + " ("+project.getID()+") HAS NOT BEEN SELECTED BY ANYONE\n" ;
           } 
        }

        for (Reader reader : this.readers) {
            reader.resetVertex();
            reader.clearAssignedProjects();
            
            network.addReader(reader);
            
            if (reader.getPreferences().size() < reader.getMarkingTarget()) {
                errors += ">READER "+reader.getName() +" (" + reader.getID() + ") HAS CAPACITY OF " + reader.getMarkingTarget()
                        + " AND PREFERENCE LIST SIZE " + reader.getPreferences().size()+"\n";
            } else if (reader.getPreferences().size() < reader.getMarkingTarget() * 2) {
                warnings += ">reader "+reader.getName() +" (" + reader.getID() + ") has capacity of " + reader.getMarkingTarget()
                        + " and preference list size " + reader.getPreferences().size()+"\n";
            }
        }
        
        if (!errors.isEmpty()){
            if (warnings.isEmpty()){
                throw new ReaderShortlistException(">>>> ERRORS:\n"+errors,true);
            } else {
                throw new ReaderShortlistException(">>>> ERRORS:\n"+errors + "\n>>>> WARNINGS:\n"+warnings, true);
            }
        } else if (!warnings.isEmpty()){
            throw new ReaderShortlistException(">>>> WARNINGS:\n"+warnings);
        }
    }

    /**
     * method which extends readers preference lists to 2x their capacities
     */
    @SuppressWarnings("unchecked")
    public void extendPreferenceLists() {
        for (Reader r : this.readers) {

            ArrayList<Project> preferences = r.getPreferences();
            ArrayList<Project> projectList = (ArrayList<Project>) this.projects.clone();

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

    
    public String movePreference(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore) {
        
    	if (readerToAdd.getPreferences().contains(projectToMove) && !readerToAdd.equals(readerToRemoveFrom)) {
    		return PROJECT_ALREADY_PREFERENCE_ERROR_MSG;
    	}
    	
    	if (readerToAdd.getSupervisorProjects().contains(projectToMove.getID())){
    		return PROJECT_SUPERVISED_ERROR_MSG;
    	}
    	if (projectToMove.equals(projectToPlaceBefore) ){
        	return null; // do not take action, as no change needed, but do not report an error
        }
    		
        readerToRemoveFrom.removePreference(projectToMove);
        int indexToPlace = readerToAdd.getPreferences().indexOf(projectToPlaceBefore);
        if (indexToPlace == -1){
        	readerToAdd.addPreference(projectToMove);
        } else {
        	readerToAdd.addPreference(indexToPlace, projectToMove);
        }
        return null; 
    }

    public String movePreference(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove) {
    	return movePreference(readerToAdd, readerToRemoveFrom, projectToMove, null);
    }
    
    public String moveAssignedProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove){
    	if (readerToAdd.equals(readerToRemoveFrom)){
            return null; // just so it wouldn't throw an error window
        }
        if (readerToAdd.getAssigned().contains(projectToMove)){
        	return READER_ALREADY_ASSIGNED_PROJECT_ERR_MSG;
        }
        if (readerToAdd.getAssigned().size() == readerToAdd.getMarkingTarget()) {
            return READER_AT_MARKING_TARGET_ERR_MSG;
        }
        if (readerToAdd.getSupervisorProjects().contains(projectToMove.getID())){
    		return PROJECT_SUPERVISED_ERROR_MSG;
    	}
        
        readerToRemoveFrom.removeAssignedProject(projectToMove);
        readerToAdd.assignProject(projectToMove);
        return null;
    }
    
    public String addProjectToReaderPreferences(Reader readerToAdd, Project projectToAdd){
    	 if (readerToAdd.getPreferences().contains(projectToAdd)){
             return PROJECT_ALREADY_PREFERENCE_ERROR_MSG;
         } 
    	 if (readerToAdd.getSupervisorProjects().contains(projectToAdd.getID())){
         	return PROJECT_SUPERVISED_ERROR_MSG;
         }
 
         readerToAdd.addPreference(projectToAdd);
         return null;
    }
     
     public String assignProjectToReader(Reader readerToAdd, Project projectToAdd) {
    	 if (readerToAdd.getAssigned().contains(projectToAdd)){
             return READER_ALREADY_ASSIGNED_PROJECT_ERR_MSG;
         } 
         
    	 if (readerToAdd.getSupervisorProjects().contains(projectToAdd.getID())){
         	return PROJECT_SUPERVISED_ERROR_MSG;
         }
         readerToAdd.assignProject(projectToAdd);
         return null;
     }
    
    
    public String addProjectToReaderPreferences(Reader readerToAdd, Project projectToAdd, Project projectToAddBefore){
    	if (readerToAdd.getPreferences().contains(projectToAdd)){
            return PROJECT_ALREADY_PREFERENCE_ERROR_MSG;
        } 
        if (readerToAdd.getSupervisorProjects().contains(projectToAdd.getID())){
        	return PROJECT_SUPERVISED_ERROR_MSG;
        }
        
        int indexToPlace = readerToAdd.getPreferences().indexOf(projectToAddBefore);
        readerToAdd.addPreference(indexToPlace, projectToAdd);
        return null;
        
    }

    
    public void removeProjectFromReaderPreferences(Reader readerToRemoveFrom, Project projectToRemove) {
        readerToRemoveFrom.removePreference(projectToRemove);
    }

    /**
     * gets unselected projects
     * @return 
     */
    @SuppressWarnings("unchecked")
	public List<Project> getUnselectedProjects() {
        ArrayList<Project> unassigned = (ArrayList<Project>) projects.clone();
        
        for (Reader reader : readers){
            for (Project project : reader.getAssigned()){
                if (unassigned.contains(project)){
                    unassigned.remove(project);
                }
            }
        }
       
        return unassigned;
    }

    public Double getAverageReaderCapacity() {
        int totalCap = 0;
        for (Reader r : readers){
            if (r.getMarkingTarget() != 0){
                totalCap += r.getMarkingTarget();
            }
        }
        Double avg = new Double(totalCap);
        return avg/readers.size();
    }

	public void reset() {
		for (Reader r: readers){
			r.clearAssignedProjects();
		}
	}

	public String canAddPreference(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToAdd) {
		if (readerToAdd.getPreferences().contains(projectToAdd) && !readerToAdd.equals(readerToRemoveFrom)) {
    		return PROJECT_ALREADY_PREFERENCE_ERROR_MSG;
    	}
		
		if (readerToAdd.getMarkingTarget() == 0){
			return READER_CAPACITY_ZERO_ERR_MSG;
		}
    	
    	if (readerToAdd.getSupervisorProjects().contains(projectToAdd.getID())){
    		return PROJECT_SUPERVISED_ERROR_MSG;
    	}
    	return null;
	}

	public String canAddAssignment(Reader readerToAdd,Reader readerToRemoveFrom, Project projectToAdd) {
		if (readerToAdd.equals(readerToRemoveFrom)){
			return null;
		}
		
		if (readerToAdd.getMarkingTarget() == 0){
			return READER_CAPACITY_ZERO_ERR_MSG;
		}
		
		if (readerToAdd.getAssigned().contains(projectToAdd)) {
    		return READER_ALREADY_ASSIGNED_PROJECT_ERR_MSG;
    	}
		if (readerToAdd.getMarkingTarget() == readerToAdd.getAssigned().size()){
			return READER_AT_MARKING_TARGET_ERR_MSG;
		}
		
    	if (readerToAdd.getSupervisorProjects().contains(projectToAdd.getID())){
    		return PROJECT_SUPERVISED_ERROR_MSG;
    	}
		return null;
	}

}
