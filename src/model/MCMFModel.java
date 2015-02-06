/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import ford_fulkerson.ReaderShortlistException;
import ford_fulkerson.TextScanner;
import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Eimantas
 */
public class MCMFModel {

    public ArrayList<Reader> readers;		// readers list
    public ArrayList<Project> projects;		// projects list

    private Graph graph;

    public MCMFModel(File file) throws Exception {
       this();
       loadGraphFromFile(file);
    }
    
    public MCMFModel(){
        this.readers = new ArrayList<Reader>();
        this.projects = new ArrayList<Project>();
    }

    public void loadGraphFromFile(File file) throws Exception {
        TextScanner.parse(file, this);
    }

    public Graph getGraph() {
        return this.graph;
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
            if (p.getId() == id) {
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
        int capacityFlowGap = 0;
        boolean capacitySet = false;

        for (Reader reader : this.readers) {
            if (!capacitySet) {
                capacityFlowGap = reader.getCapacity() + graph.getLowerCapacityOffset() - reader.getAssignedProjectsFromGraph().size();
                if (capacityFlowGap >= 0) {
                    capacitySet = true;
                }
            } else {
                if (reader.getCapacity() + graph.getLowerCapacityOffset() - reader.getAssignedProjectsFromGraph().size() > capacityFlowGap + 1) {
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
    public void createGraph() throws ReaderShortlistException {
        String warnings = "";
        String errors = "";
        graph = new Graph();
        
        for (Project project : this.projects){
            
           project.resetVertex();
           graph.addProject(project);
           
           if (project.getSelectedCount() == 0){
               errors += ">PROJECT " + project.getName() + "("+project.getId()+") HAS NOT BEEN SELECTED BY ANYONE\n" ;
           } 
        }

        for (Reader reader : this.readers) {
            reader.resetVertex();
            reader.clearAssignedProjects();
            
            graph.addReader(reader);
            
            if (reader.getPreferences().size() < reader.getCapacity()) {
                errors += ">READER "+reader.getName() +" (" + reader.getID() + ") HAS CAPACITY OF " + reader.getCapacity()
                        + " AND PREFERENCE LIST SIZE " + reader.getPreferences().size()+"\n";
            } else if (reader.getPreferences().size() < reader.getCapacity() * 2) {
                warnings += ">reader "+reader.getName() +" (" + reader.getID() + ") has capacity of " + reader.getCapacity()
                        + " and preference list size " + reader.getPreferences().size()+"\n";
            }
        }
        
        if (!errors.isEmpty()){
            if (errors.isEmpty()){
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

            while (preferences.size() < 2 * r.getCapacity() && !projectList.isEmpty()) {

                Collections.sort(projectList);
                Project proj = projectList.remove(0);

                if (!preferences.contains(proj)) {
                    if (!r.getSupervisorProjects().contains(proj.getId())) {
//                        System.out.println("Extended reader's " + r.getID() + " pref list with project " + proj.getId());
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
                result += project.getId()+" ";
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

    public int movePreference(Reader reader, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore) {
        if (!reader.getPreferences().contains(projectToMove) || reader.equals(readerToRemoveFrom)) {
            
            int indexToPlace = reader.getPreferences().indexOf(projectToPlaceBefore);
            readerToRemoveFrom.removePreference(projectToMove);
            reader.addPreference(indexToPlace, projectToMove);
            return indexToPlace;
        } else {
            return -1;
        }
    }
    
    public int moveAssignedProject(Reader reader, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore) {
         if (!reader.getAssigned().contains(projectToMove) || reader.equals(readerToRemoveFrom)){
            
            if (reader.getAssigned().size() == reader.getCapacity()) {
                return -1;
            }
            int indexToPlace = reader.getAssigned().indexOf(projectToPlaceBefore);
            readerToRemoveFrom.removeAssignedProject(projectToMove);
            reader.assignProject(indexToPlace, projectToMove);
            return indexToPlace;
        } else {
            return -1;
        }
    }
    


    public boolean movePreference(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove) {
        if (!readerToAdd.getPreferences().contains(projectToMove) || readerToAdd.equals(readerToRemoveFrom)) {
            if (readerToAdd.addPreference(projectToMove)){
                readerToRemoveFrom.removePreference(projectToMove);
                return true;
            } else {
                return false;
            }
            
        } else {
            return false;
        }
    }
    
    public boolean moveAssignedProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove){
        if (!readerToAdd.getAssigned().contains(projectToMove) || readerToAdd.equals(readerToRemoveFrom)){
            
            if (readerToAdd.getAssigned().size() == readerToAdd.getCapacity()) {
                return false;
            }
            
            if (readerToAdd.assignProject(projectToMove)){
                readerToRemoveFrom.removeAssignedProject(projectToMove);
                return true;
            } else {
                return false;
            }
            
        } else {
            return false;
        }
    }
    
     public boolean addProjectToReaderPreferences(Reader reader, Project projectToAdd){
        if (reader.getPreferences().contains(projectToAdd)){
            return false;
        } else {
            return reader.addPreference(projectToAdd);
        }
    }
     
     public boolean assignProjectToReader(Reader reader, Project projectToAdd) {
         if (reader.getAssigned().contains(projectToAdd) || reader.getAssigned().size() == reader.getCapacity()){
            return false;
        } else {
            return reader.assignProject(projectToAdd);
        }
     }
    
    
    public int addProjectToReaderPreferences(Reader reader, Project projectToAdd, Project projectToAddBefore){
        if (reader.getPreferences().contains(projectToAdd)){
            return -1;
        } else {
            int indexToPlace = reader.getPreferences().indexOf(projectToAddBefore);
            reader.addPreference(indexToPlace, projectToAdd);
            return indexToPlace;
        }
    }

    public int assignProjectToReader(Reader reader, Project projectToAdd, Project projectToAddBefore){
        if (reader.getAssigned().contains(projectToAdd) || reader.getAssigned().size() == reader.getCapacity()){
            return -1;
        } else {
            int indexToPlace = reader.getAssigned().indexOf(projectToAddBefore);
            reader.assignProject(indexToPlace, projectToAdd);
            return indexToPlace;
        }
    }
    
    public void removeProjectFromReaderPreferences(Reader readerToRemoveFrom, Project projectToRemove) {
        getReader(readerToRemoveFrom).removePreference(projectToRemove);
    }

    /**
     * gets unselected projects
     * @return 
     */
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
            if (r.getCapacity() != 0){
                totalCap += r.getCapacity();
            }
        }
        Double avg = new Double(totalCap);
        return avg/readers.size();
    }

}
