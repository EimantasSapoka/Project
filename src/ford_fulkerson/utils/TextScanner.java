package ford_fulkerson.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import ford_fulkerson.model.MCMFModel;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;

/**
 * a text scanner class which takes a text file input and generates a graph from
 * it
 *
 * @author Eimantas
 *
 */
public class TextScanner {

    /**
     * parses comma separated text file
     * @param textFile
     * @param model
     * @throws Exception 
     */
    public static void parseCommaSeparatedInput(File textFile, MCMFModel model) throws Exception {
        
        BufferedReader textReader = new BufferedReader(new FileReader(textFile));
        String line = "";
        int projectCount;
        int readerCount;
        
        try {
            line = textReader.readLine();
            projectCount = Integer.parseInt(line.split(",")[0].trim());
            readerCount = Integer.parseInt(line.split(",")[1].trim());
        } catch (Exception ex) {
        	textReader.close();
            throw new InvalidInputException("text file does not contain two integers with project count and reader count >"+line, ex);
        }

        try {
            for (int i = 0; i < projectCount; i++) {
                line = textReader.readLine();
                String[] projectInfo = line.split(",");
                int projectID = Integer.parseInt(projectInfo[0].trim());
                String projectName = projectInfo[1].trim();
                int projectSupervisor = Integer.parseInt(projectInfo[2].trim());
                if (model.getProject(projectID) == null){
                    Project project = new Project(projectName, projectID, projectSupervisor);
                    model.addProject(project);
                }
            }
        } catch (Exception ex) {
        	textReader.close();
            throw new InvalidInputException("project description line malformed! >" + line +"\n"+ex.getMessage() , ex);
        }

        try {
            for (int i = 0; i < readerCount; i++) {
                line = textReader.readLine();
                
                String[] readerInfo = line.split(",");
                int readerID = Integer.parseInt(readerInfo[0].trim());
                String readerName = readerInfo[1].trim();
                int readerCapacity = Integer.parseInt(readerInfo[2].trim());
                createReader(model, readerName, readerID, readerCapacity, readerInfo);
            }
        } catch (Exception ex) {
        	textReader.close();
            throw new InvalidInputException("reader description line malformed! >" + line + "\n" + ex.getMessage(), ex);
        }
        textReader.close();

    }
    
  

    /**
     * creates a reader from the given data and adds it to the model
     * @throws InvalidInputException 
     */
    private static Reader createReader(MCMFModel model, String readerName, int readerID, int readerCapacity, String[] readerInfo) throws InvalidInputException {
        Reader reader;
        if (!model.hasReader(readerID)) {
            reader = new Reader(readerName, readerID, readerCapacity);
            model.addReader(reader);
            for (int id : retrieveSupervisedProjects(model, reader)){
            	reader.addSupervisingProject(model.getProject(id));
            }
            if (readerInfo.length == 4) {
                parseProjectPreferences(readerInfo[3], reader, model);
            }
        } else {
            throw new InvalidInputException("Reader id " + readerID + " repeated!");
        }
        return reader;
    }

    /**
     * given the reader and the model, retrieves all project ids which are 
     * supervised by the reader. 
     * @param model
     * @param reader
     * @return
     */
    private static List<Integer> retrieveSupervisedProjects(MCMFModel model, Reader reader) {
    	List<Integer> list = new ArrayList<Integer>();
		for (Project p : model.getProjects()){
			if (p.getSupervisorID() == reader.getID()){
				list.add(p.getID());
			}
		}
		return list;
	}

	/**
     * method to parse the preferences specified by the reader
     *
     * @param barSplit
     * @param reader
     */
    private static void parseProjectPreferences(String projects, Reader reader, MCMFModel model) throws InvalidInputException {
        try{
            String[] projectsSplit = projects.split(" ");
            for (String projectID : projectsSplit) {
                if (projectID.trim().isEmpty()) {
                    continue;
                }
                int id = Integer.parseInt(projectID.trim());
                Project project = model.getProject(id);
                if (project == null) {
                    throw new InvalidInputException("Project with id " + id + " is not in among the data input!");
                }
                if (reader.getSupervisorProjects().contains(project.getID())){
                	throw new InvalidInputException("Project id " + id + " is supervised by reader " 
                									+ reader.getName() + " and is in its preference list!");
                }
                reader.addPreference(project);
            }
        } catch (Exception ex){
            throw new InvalidInputException("Could not parse projects for reader " 
                    + reader.getName() + ", because -> "+ex.getMessage(), ex);
        }
    }

 
}
