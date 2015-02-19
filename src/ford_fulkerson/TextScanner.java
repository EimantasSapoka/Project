package ford_fulkerson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import model.MCMFModel;
import model.Project;
import model.Reader;

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
            projectCount = Integer.parseInt(line.split(",")[0]);
            readerCount = Integer.parseInt(line.split(",")[1]);
        } catch (Exception ex) {
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
            throw new InvalidInputException("reader description line malformed! >" + line + "\n" + ex.getMessage(), ex);
        }

    }

    /**
     * parses a text file with bar separated values. Deprecated method, replaced with newer, 
     * parseCommaSeparatedInput.
     * @param textFile
     * @param model
     * @throws Exception 
     */
    @Deprecated
    public static void parseBarSeparatedInput(File textFile, MCMFModel model) throws Exception {
        BufferedReader textReader = new BufferedReader(new FileReader(textFile));
        String line = "";
        try {
            while ((line = textReader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                String[] readerInfo = line.split("\\|");
                int readerID = Integer.parseInt(readerInfo[0].trim());
                int readerCapacity = Integer.parseInt(readerInfo[2].trim());
                if (readerInfo.length == 4) {
                    String[] projectsSplit = readerInfo[3].split(" ");
                    for (String projectID : projectsSplit) {
                        if (projectID.trim().isEmpty()) {
                            continue;
                        }
                        int id = Integer.parseInt(projectID.trim());
                        Project project = new Project(id);
                        model.addProject(project);
                    }
                }
                Reader reader = createReader(model, readerID, readerCapacity, readerInfo);
                parseSupervisingProjects(readerInfo[1], reader);
            }
            textReader.close();
            
        } catch (Exception ex) {
            textReader.close();
            throw new InvalidInputException("Could not parse file " + textFile.getName() + "\n>" + ex.getMessage(), ex);
        }
    }
    
    /**
     * creates a reader with its name as id
     * @throws InvalidInputException 
     */
    private static Reader createReader(MCMFModel model, int readerID, int readerCapacity, String[] readerInfo) throws InvalidInputException{
        return createReader(model,readerID+"",readerID, readerCapacity, readerInfo);
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
            if (readerInfo.length == 4) {
                parseProjectPreferences(readerInfo[3], reader, model);
            }
        } else {
            throw new InvalidInputException("Reader id " + readerID + " repeated!");
        }
        return reader;
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
                reader.addPreference(project);
            }
        } catch (Exception ex){
            throw new InvalidInputException("Could not parse projects for reader " 
                    + reader.getName() + "\n"+ex.getMessage(), ex);
        }
    }

    /**
     * method to parse readers currently supervised projects
     * @param barSplit
     * @param reader
     */
    private static void parseSupervisingProjects(String projects,Reader reader) {
        String[] supervisedProjects = projects.split(" ");
        for (String projectID : supervisedProjects) {
            if (projectID.trim().isEmpty()) {
                continue;
            }
            int id = Integer.parseInt(projectID.trim());
            reader.addSupervisingProject(id);
        }
    }
}
