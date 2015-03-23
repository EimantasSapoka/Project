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
	
	 
    public UserInterfaceModel(MCMFModel model) {
    	this.projects = model.getProjects();
    	this.readers = model.getReaders();
    	this.network = model.getNetwork();
    }

	public UserInterfaceModel(File file) throws Exception {
		super(file);
	}

	public String movePreference(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore) {
        
    	if (readerToAdd.getPreferences().contains(projectToMove) && !readerToAdd.equals(readerToRemoveFrom)) {
    		return PROJECT_ALREADY_PREFERENCE_ERROR_MSG;
    	}
    	
    	if (readerToAdd.getSupervisorProjects().contains(projectToMove)){
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
    
    public void removeProjectFromReaderPreferences(Reader readerToRemoveFrom, Project projectToRemove) {
        readerToRemoveFrom.removePreference(projectToRemove);
    }
    
    public String moveAssignedProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove){
    	if (readerToAdd.equals(readerToRemoveFrom)){
            return null; // just so it wouldn't throw an error window
        }
    	
    	if (readerToAdd.getMarkingTarget() == 0){
   		 	return READER_CAPACITY_ZERO_ERR_MSG;
		}
    	
        if (readerToAdd.getAssigned().contains(projectToMove)){
        	return READER_ALREADY_ASSIGNED_PROJECT_ERR_MSG;
        }
        if (readerToAdd.getAssigned().size() <= readerToAdd.getMarkingTarget()) {
            return READER_AT_MARKING_TARGET_ERR_MSG;
        }
        if (readerToAdd.getSupervisorProjects().contains(projectToMove)){
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
    	 if (readerToAdd.getSupervisorProjects().contains(projectToAdd)){
         	return PROJECT_SUPERVISED_ERROR_MSG;
         }
 
         readerToAdd.addPreference(projectToAdd);
         return null;
    }
     
     public String assignProjectToReader(Reader readerToAdd, Project projectToAdd) {
    	 if (readerToAdd.getAssigned().contains(projectToAdd)){
             return READER_ALREADY_ASSIGNED_PROJECT_ERR_MSG;
         } 
         
    	 if (readerToAdd.getSupervisorProjects().contains(projectToAdd)){
         	return PROJECT_SUPERVISED_ERROR_MSG;
         }
    	 
    	 if (readerToAdd.getMarkingTarget() == 0){
    		 return READER_CAPACITY_ZERO_ERR_MSG;
 		 }
 		
 		 if (readerToAdd.getMarkingTarget() <= readerToAdd.getAssigned().size()){
 			return READER_AT_MARKING_TARGET_ERR_MSG;
 		 }
         readerToAdd.assignProject(projectToAdd);
         return null;
     }
    
    
    public String addProjectToReaderPreferences(Reader readerToAdd, Project projectToAdd, Project projectToAddBefore){
    	if (readerToAdd.getPreferences().contains(projectToAdd)){
            return PROJECT_ALREADY_PREFERENCE_ERROR_MSG;
        } 
        if (readerToAdd.getSupervisorProjects().contains(projectToAdd)){
        	return PROJECT_SUPERVISED_ERROR_MSG;
        }
        
        int indexToPlace = readerToAdd.getPreferences().indexOf(projectToAddBefore);
        readerToAdd.addPreference(indexToPlace, projectToAdd);
        return null;
        
    }
    
    public String canAddPreference(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToAdd) {
		if (readerToAdd.getPreferences().contains(projectToAdd) && !readerToAdd.equals(readerToRemoveFrom)) {
    		return PROJECT_ALREADY_PREFERENCE_ERROR_MSG;
    	}
		
		if (readerToAdd.getMarkingTarget() == 0){
			return READER_CAPACITY_ZERO_ERR_MSG;
		}
    	
    	if (readerToAdd.getSupervisorProjects().contains(projectToAdd)){
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
		if (readerToAdd.getMarkingTarget() <= readerToAdd.getAssigned().size()){
			return READER_AT_MARKING_TARGET_ERR_MSG;
		}
		
    	if (readerToAdd.getSupervisorProjects().contains(projectToAdd)){
    		return PROJECT_SUPERVISED_ERROR_MSG;
    	}
		return null;
	}

	
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
