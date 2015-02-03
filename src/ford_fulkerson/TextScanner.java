package ford_fulkerson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ford_fulkerson.graph.Graph;
import model.MCMFModel;
import model.Project;
import model.Reader;

/**
 * a text scanner class which takes a text file input and generates a graph from it
 * @author Eimantas
 *
 */
public class TextScanner {
	

	public static void parse(File textFile, MCMFModel model) throws Exception{
		BufferedReader textReader = new BufferedReader(new FileReader(textFile));
		String line = "";
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
