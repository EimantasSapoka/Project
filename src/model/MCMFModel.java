/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import ford_fulkerson.TextScanner;
import ford_fulkerson.graph.Edge;
import ford_fulkerson.graph.Graph;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Eimantas
 */
public class MCMFModel {

    public ArrayList<Reader> readers;		// readers list
    public ArrayList<Project> projects;		// projects list

    private Graph graph;

    public MCMFModel(File file) {
       this();
       loadGraphFromFile(file);
    }
    
    public MCMFModel(){
        this.readers = new ArrayList<Reader>();
        this.projects = new ArrayList<Project>();
        this.graph = new Graph();
    }

    public void loadGraphFromFile(File file) {
        try {
            TextScanner.parse(file, this);
        } catch (IOException ex) {
            System.out.println("ERROR OPENING FILE");
        }
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
                capacityFlowGap = reader.getCapacity() + graph.getLowerCapacityOffset() - reader.getAssignedProjects().size();
                if (capacityFlowGap >= 0) {
                    capacitySet = true;
                }
            } else {
                if (reader.getCapacity() + graph.getLowerCapacityOffset() - reader.getAssignedProjects().size() > capacityFlowGap + 1) {
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
            graph.addProject(project);

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
    public void createGraph() {

        for (Reader reader : this.readers) {

            if (reader.getPreferences().size() < reader.getCapacity()) {
                System.err.println("ERROR! READER " + reader.getID() + " HAS CAPACITY OF " + reader.getCapacity()
                        + " AND PREFERENCE LIST SIZE " + reader.getPreferences().size());
                System.exit(1);
            } else if (reader.getPreferences().size() < reader.getCapacity() * 2) {
                System.out.println("WARNING! reader " + reader.getID() + " has capacity of " + reader.getCapacity()
                        + " and preference list size " + reader.getPreferences().size());
            }

            graph.addVertex(reader.getVertex());
            // if reader has any capacity, create an edge from source to the reader with the capacity
            if (reader.getCapacity() > 0) {
                graph.createSourceReaderEdge(reader);
            }

            int preference = 1; // the initial preference weight
            for (Project project : reader.getPreferences()) {
                graph.createReaderProjectEdge(reader, project, preference);
                preference++;
            }
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
                        System.out.println("Extended reader's " + r.getID() + " pref list with project " + proj.getId());
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

        int numberProjects = this.projects.size();

        for (Reader r : readers) {
            result += "Reader " + r.getVertex().getObjectID();

            int assigned = 0;
            String assignedProj = "";
            for (Edge e : r.getVertex().getOutEdges()) {
                if (e.getFlow() > 0) {
                    assigned++;
                    assignedProj += " " + e.getDestination().getObjectID();
                }
            }
            result += " (" + assigned + "/" + r.getCapacity() + ") :" + assignedProj + "\n";
        }

        String unselectedProjID = "";
        for (Project p : graph.findUnassignedProjects()) {
            unselectedProjID += " " + p.getId();
        }
        result += String.format(""
                + "\n number of readers: %d"
                + "\n number of projects: %d, not assigned: [%s ]"
                + "\n total flow: %d"
                + "\n total weight: %d"
                + "\n load balanced? : %b"
                + "\n saturating flow? : %b",
                this.getReaders().size(),
                numberProjects,
                unselectedProjID,
                graph.getFlow(),
                graph.getWeight(),
                this.isLoadBalanced(),
                graph.isSaturatingFlow());
        return result;
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
    

    public Reader getReader(int readerID) {
        for (Reader reader : readers){
            if (reader.getID() == readerID){
                return reader;
            }
        }
        return null;
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

    public void removeProjectFromReader(Reader readerToRemoveFrom, Project projectToRemove) {
        getReader(readerToRemoveFrom).removePreference(projectToRemove);
    }

}
