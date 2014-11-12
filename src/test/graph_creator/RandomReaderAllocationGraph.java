package test.graph_creator;

import java.util.ArrayList;
import java.util.Random;

import ford_fulkerson.graph.Graph;
import ford_fulkerson.graph.Project;
import ford_fulkerson.graph.Reader;

/**
 * class which creates a random reader allocation graph. Used for testing.
 * @author Eimantas
 *
 */
public class RandomReaderAllocationGraph extends Graph{
	private static final int PROJECT_ID_OFFSET = 50;
	
	private int readerIDcounter = 1;
	private final int PROJECT_COUNT;
	private final int READER_COUNT;
	private ArrayList<Integer> projects;
	Random rand;
	
	/**
	 * creates a random reader allocation graph 
	 */
	public RandomReaderAllocationGraph(){
		super();
		rand = new Random();
		READER_COUNT = rand.nextInt(20) + 5;
		PROJECT_COUNT = (rand.nextInt(3) + 1)* READER_COUNT/2 * READER_COUNT;
		projects = new ArrayList<Integer>(PROJECT_COUNT);
		for (int i=0; i<PROJECT_COUNT; i++){
			projects.add(i + PROJECT_ID_OFFSET);
		}
		
		for (int i=0; i<READER_COUNT; i++){
			this.addReader(generateReader());
		}
	}
	
	
	/**
	 * generates and returns a single reader instance with a sequentially 
	 * incremented ID (readerIDcounter) and random number capacity.
	 * Also, preference list is random as well.
	 */
	@SuppressWarnings("unchecked")
	public Reader generateReader(){
		ArrayList<Integer> projectPreferenceList = (ArrayList<Integer>) projects.clone();
		int readerCapacity = rand.nextInt(15);
		
		if (readerCapacity > projectPreferenceList.size()){
			readerCapacity = projectPreferenceList.size();
		}
		
		Reader r = new Reader(readerIDcounter++, readerCapacity);
		
		// makes a preference list twice as big as reader's capacity
		for (int i=0; i<r.getCapacity()*2; i++){
			
			// selects a random project number from list
			int randomProjectIndex = rand.nextInt(projectPreferenceList.size()); 
			
			// creates a new project with the id from the list (and removes that id) and preference of loop index
			int id = projectPreferenceList.remove(randomProjectIndex);
			Project project = null;
			if( (project = this.getProject(id)) == null){
				project = new Project(id);
			}
			r.addPreference(project); 
			
		}
		
		return r;
	}
}
