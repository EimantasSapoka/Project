package ford_fulkerson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TextScanner {
	private static Graph graph;


	public static Graph parse(File textFile) throws IOException{
		graph = new Graph();
		
		BufferedReader textReader = new BufferedReader(new FileReader(textFile));
		String line = "";
		while ( ( line = textReader.readLine() )  != null){
			String[] barSplit = line.split("\\|");
			
			int readerID = Integer.parseInt(barSplit[0].trim());
			int readerCapacity = Integer.parseInt(barSplit[2].trim());
			
			if (! graph.hasReader(readerID)){
				Reader reader = new Reader(readerID, readerCapacity);
			
				parseSupervisingProjects(barSplit[1], reader);
				parseProjectPreferences(barSplit[3], reader);
				graph.addReader(reader);
			} else {
				textReader.close();
				System.out.println("reader id repeated! " + readerID);
			}
			
		}
		
		textReader.close();
		return graph;
	}

	/**
	 * method to parse the preferences specified by the reader
	 * @param barSplit
	 * @param reader
	 */
	private static void parseProjectPreferences(String projects, Reader reader) {
		String[] projectsSplit = projects.split(" ");
		
		for (String projectID: projectsSplit){
			if (projectID.trim().isEmpty()){
				continue;
			}
			int id = Integer.parseInt(projectID.trim());
			
			Project project = null;
			if( (project = graph.getProject(id)) == null){
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
