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
 * a text scanner class which takes a text file input and generates a graph from it
 * @author Eimantas
 *
 */
public class TextScanner {
    
        public static void parse2(File textFile, MCMFModel model) throws Exception{
            BufferedReader textReader = new BufferedReader(new FileReader(textFile));
            String line = "";
            int projectCount;
            int readerCount;
            
            line = textReader.readLine();
            try {
                projectCount = Integer.parseInt(line.split(",")[0]);
                readerCount = Integer.parseInt(line.split(",")[1]);
            }catch( Exception ex){
                throw new InvalidInputException("text file does not contain two integers with project count and reader count", ex);
            }
            
            try {
                for (int i = 0; i<projectCount; i++){
                    line = textReader.readLine();
                    String[] projectInfo = line.split(",");
                    int projectID = Integer.parseInt(projectInfo[0].trim());
                    String projectName = projectInfo[1].trim();
                    int projectSupervisor = Integer.parseInt(projectInfo[2].trim());
                }
            } catch(Exception ex){
                throw new InvalidInputException("project description line malformed! >" + line, ex);
            }
            
            try{
                for (int i = 0; i<readerCount; i++){
                    line = textReader.readLine();
                    String[] readerInfo = line.split(",");
                    int readerID = Integer.parseInt(readerInfo[0]);
                    String readerName = readerInfo[1];
                    int readerCapacity = Integer.parseInt(readerInfo[2]);
                    List<Integer> readerPreferences = new ArrayList<Integer>();
                    if (readerInfo.length > 3){
                        for (String pref : readerInfo[3].split(" ")){
                            if (!pref.trim().isEmpty()){
                                readerPreferences.add(Integer.parseInt(pref));
                            }
                        }
                    }
                }
            } catch (Exception ex){
                throw new InvalidInputException("reader description line malformed! >" + line, ex);
            }
            
        }
	

	public static void parse(File textFile, MCMFModel model) throws Exception{
		BufferedReader textReader = new BufferedReader(new FileReader(textFile));
		String line = "";
		try {
                    while ( ( line = textReader.readLine() )  != null ){
			if (line.isEmpty()){
				continue;
			}
			
			String[] barSplit = line.split("\\|");
			int readerID = Integer.parseInt(barSplit[0].trim());
			int readerCapacity = Integer.parseInt(barSplit[2].trim());
			
			if (! model.hasReader(readerID)){
				Reader reader = new Reader(readerID, readerCapacity);
				parseSupervisingProjects(barSplit[1], reader);
				if (barSplit.length == 4){
					parseProjectPreferences(barSplit[3], reader, model);
				}
				model.addReader(reader);
			} else {
				textReader.close();
				System.out.println("reader id repeated! " + readerID);
			}
			
		}
		textReader.close();
                } catch( Exception ex){
                    throw new InvalidInputException("Could not parse file " + textFile.getName(), ex);
                }
	}

	/**
	 * method to parse the preferences specified by the reader
	 * @param barSplit
	 * @param reader
	 */
	private static void parseProjectPreferences(String projects, Reader reader, MCMFModel model) {
		String[] projectsSplit = projects.split(" ");
		
		for (String projectID: projectsSplit){
			if (projectID.trim().isEmpty()){
				continue;
			}
			int id = Integer.parseInt(projectID.trim());
			
			Project project = null;
			if( (project = model.getProject(id)) == null){
				project = new Project(id);
			}
			model.addProject(project);
			reader.addPreference(project);
			
		}
	}

	/**
	 * method to parse readers currently supervised projects
	 * @param barSplit
	 * @param reader
	 */
	private static void parseSupervisingProjects(String projects,
			Reader reader) {
		
		String[] supervisedProjects = projects.split(" ");

		for (String projectID: supervisedProjects){
			if (projectID.trim().isEmpty()){
				continue;
			}
			int id = Integer.parseInt(projectID.trim());
			
			reader.addSupervisingProject(id);
		}
	}
}
