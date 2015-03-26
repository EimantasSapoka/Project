package mcmfuserinterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ford_fulkerson.model.MCMFModel;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;

/**
 * An extension of MCMFModel class which to add user interface
 * specific methods. 
 * @author Eimantas
 *
 */
public class UserInterfaceModel extends MCMFModel {
	
	private static final String READER_AT_MARKING_TARGET_ERR_MSG = "Reader is at his marking target";
	private static final String READER_ALREADY_ASSIGNED_PROJECT_ERR_MSG = "Reader is already assigned this project";
	private static final String READER_CAPACITY_ZERO_ERR_MSG = "Reader has reader target of zero";
	private static final String PROJECT_ALREADY_PREFERENCE_ERROR_MSG = "Project already in preference list";
	private static final String PROJECT_SUPERVISED_ERROR_MSG = "Project is already supervised by this reader";
	
	 
	/**
	 * creates a user interface model from a given MCMF model instance
	 * @param model
	 */
    public UserInterfaceModel(MCMFModel model) {
    	this.projects = model.getProjects();
    	this.readers = model.getReaders();
    	this.network = model.getNetwork();
    }

    /**
     * creates the user interface model from a given file
     * @param file
     * @throws Exception
     */
	public UserInterfaceModel(File file) throws Exception {
		super(file);
	}

	/**
	 * moves preference from a one reader and adds it to the other's end of preference list
	 * @param readerToAdd
	 * @param readerToRemoveFrom
	 * @param projectToMove
	 * @return error message if not allowed, null if allowed
	 */
    public String movePreference(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove) {
    	return movePreference(readerToAdd, readerToRemoveFrom, projectToMove, null);
    }
    
    /**
     * removes a preference from a readers preference list
     * @param readerToRemoveFrom
     * @param projectToRemove
     */
    public void removeProjectFromReaderPreferences(Reader readerToRemoveFrom, Project projectToRemove) {
        readerToRemoveFrom.removePreference(projectToRemove);
    }
    
    /**
     * if allowed, moves a preference from one reader and places it
     * in the other's preference list before another project
     * @param readerToAdd
     * @param readerToRemoveFrom
     * @param projectToMove the project which will be moved
     * @param projectToPlaceBefore the project before which the moved project
     * will be placed. 
     * @return error message if not allowed, null if allowed
     */
    public String movePreference(Reader readerToAdd, Reader readerToRemoveFrom, 
    							Project projectToMove, Project projectToPlaceBefore) {
    	
        String canAddPreference = canAddPreference(readerToAdd, readerToRemoveFrom, projectToMove);
   	    
	   	if (canAddPreference != null){
	   		 return canAddPreference;
	   	} else {
	        removeProjectFromReaderPreferences(readerToRemoveFrom, projectToMove);
	        addProjectToReaderPreferences(readerToAdd, projectToMove, projectToPlaceBefore);
	        return null;
	   	}
    }
    
    /**
     * if allowed, removes an assignment from one reader and assigns it to the other
     * @param readerToAdd
     * @param readerToRemoveFrom
     * @param projectToMove
     * @return error message if not allowed, null if allowed
     */
    public String moveAssignedProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove){
    	String canAssign = canAddAssignment(readerToAdd, readerToRemoveFrom, projectToMove);
    	 
    	 if (canAssign != null){
    		 return canAssign;
    	 } else {
    		 readerToRemoveFrom.removeAssignedProject(projectToMove);
	         assignProjectToReader(readerToAdd, projectToMove);
	         return null;
    	 }
    }
    
   
     /**
      * assigns a project to reader
      * @param readerToAdd
      * @param projectToAdd
      * @return error message if not allowed, null if allowed
      */
     public String assignProjectToReader(Reader readerToAdd, Project projectToAdd) {
    	 String canAssign = canAddAssignment(readerToAdd, null, projectToAdd);
    	 
    	 if (canAssign != null){
    		 return canAssign;
    	 } else {
	         readerToAdd.assignProject(projectToAdd);
	         return null;
    	 }
     }
    
     /**
      * adds a preference to reader
      * @param readerToAdd
      * @param projectToAdd
      * @return error message if not allowed, null if allowed
      */
    public String addProjectToReaderPreferences(Reader readerToAdd, Project projectToAdd){
    	 return addProjectToReaderPreferences(readerToAdd, projectToAdd, null);
    }
     
    /**
     * adds a preference to reader before another project
     * @param readerToAdd
     * @param projectToAdd
     * @param projectToAddBefore project to add the preferece before
     * @return error message if not allowed, null if allowed
     */
    public String addProjectToReaderPreferences(Reader readerToAdd, Project projectToAdd, Project projectToAddBefore){
    	String canAddPreference = canAddPreference(readerToAdd, null, projectToAdd);
   	    
	   	if (canAddPreference != null){
	   		 return canAddPreference;
	   	} 
       
        int indexToPlace = readerToAdd.getPreferences().indexOf(projectToAddBefore);
        
        if (indexToPlace == -1){
	         readerToAdd.addPreference(projectToAdd);
        } else {
        	 readerToAdd.addPreference(indexToPlace, projectToAdd);
        }
       
        return null;
        
    }
    
    /**
     * checks if adding a given project is allowed to the given reader. 
     * @param readerToAdd
     * @param readerToRemoveFrom
     * @param projectToAdd
     * @return error message if not allowed, null if allowed
     */
    public String canAddPreference(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToAdd) {
		if (readerToAdd.getPreferences().contains(projectToAdd) && !readerToAdd.equals(readerToRemoveFrom)) {
    		return PROJECT_ALREADY_PREFERENCE_ERROR_MSG;
    	}
		
		if (readerToAdd.getReaderTarget() == 0){
			return READER_CAPACITY_ZERO_ERR_MSG;
		}
    	
    	if (readerToAdd.getSupervisorProjects().contains(projectToAdd)){
    		return PROJECT_SUPERVISED_ERROR_MSG;
    	}
    	return null;
	}

    /** 
     * checks if assigning a given project is allowed to the given reader
     * @param readerToAdd
     * @param readerToRemoveFrom
     * @param projectToAdd
     * @return error message if not allowed, null if allowed
     */
	public String canAddAssignment(Reader readerToAdd,Reader readerToRemoveFrom, Project projectToAdd) {
		if (readerToAdd.equals(readerToRemoveFrom)){
			return null;
		}
		
		if (readerToAdd.getReaderTarget() == 0){
			return READER_CAPACITY_ZERO_ERR_MSG;
		}
		
		if (readerToAdd.getAssigned().contains(projectToAdd)) {
    		return READER_ALREADY_ASSIGNED_PROJECT_ERR_MSG;
    	}
		if (readerToAdd.getReaderTarget() <= readerToAdd.getAssigned().size()){
			return READER_AT_MARKING_TARGET_ERR_MSG;
		}
		
    	if (readerToAdd.getSupervisorProjects().contains(projectToAdd)){
    		return PROJECT_SUPERVISED_ERROR_MSG;
    	}
		return null;
	}

	/**
	 * returns a string listing all projects, who they were assigned to 
	 * and the project rank in that person's preference list. 
	 * @return
	 */
	public String getProjectReaderInfo() {
		StringBuilder result = new StringBuilder();
		result.append(String.format("%-50s%s\n\n","Project name", "| Rank , Reader assigned to" ));
		List<Project> projectsSortedByID = new ArrayList<Project>();
		projectsSortedByID.addAll(getProjects());
		
		// sort projects by name.
		Collections.sort(projectsSortedByID, new Comparator<Project>(){
			@Override
			public int compare(Project p1, Project p2) {
				return p1.getName().compareTo(p2.getName());
			}
		});
		
		for (Project project : projectsSortedByID){
			result.append(String.format("%-50s",project.getName()));
			
			Reader assignedReader = project.getAssignedReader();
			
			if (assignedReader == null){
				result.append("| Not assigned\n");
			} else {
				int rank = assignedReader.getPreferences().indexOf(project);
				rank = rank ==-1? assignedReader.getPreferences().size()+1: rank+1;
				result.append(String.format("| %-5s, %s\n", rank+"", assignedReader.getName()));
			}
		}
		
		return result.toString();
	}
	
	/**
	 * returns the average reader capacity.
	 * @return
	 */
	public Double getAverageReaderCapacity() {
        int totalCap = 0;
        for (Reader r : readers){
            if (r.getReaderTarget() != 0){
                totalCap += r.getReaderTarget();
            }
        }
        Double avg = new Double(totalCap);
        return avg/readers.size();
    }
	
	

    /**
     * gets unselected projects
     * @return 
     */
	public List<Project> getUnselectedProjects() {
        List<Project> unassigned = new ArrayList<Project>();
        unassigned.addAll(projects);
        
        for (Reader reader : readers){
            for (Project project : reader.getAssigned()){
                if (unassigned.contains(project)){
                    unassigned.remove(project);
                }
            }
        }
       
        return unassigned;
    }
	



}
