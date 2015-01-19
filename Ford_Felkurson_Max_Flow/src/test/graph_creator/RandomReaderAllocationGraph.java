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
	
	private final int READER_CAPACITY;
	private int readerIDcounter = 1;
	private final int PROJECT_COUNT;
	private final int READER_COUNT;
	private ArrayList<Integer> projects;
	Random rand;
	
	/**
	 * creates a random reader allocation graph with a random 
	 * number of readers up to the reader limit. 
	 * also creates number of projects equal to the number of readers multiplied 
	 * by the projectMultiplier. 
	 */
	public RandomReaderAllocationGraph(int readerLimit, int projectMultiplier, int readerCapacity){
		super();
		
		rand = new Random();
		READER_CAPACITY = readerCapacity;
		READER_COUNT = rand.nextInt(readerLimit -5) + 5;
		PROJECT_COUNT = READER_COUNT * projectMultiplier;
		
		projects = new ArrayList<Integer>(PROJECT_COUNT);
		
		for (int i=0; i<PROJECT_COUNT; i++){
			projects.add(i + READER_COUNT);
		}
		
		for (int i=0; i<READER_COUNT; i++){
			this.addReader(generateReader());
		}
	}
	
	/**
	 * constructor taking reader count limit. 
	 * will create graph with random number of readers up to the limit
	 * and number of projects twice the reader count. 
	 * @param readerLimit
	 */
	public RandomReaderAllocationGraph(int readerLimit){
		this(readerLimit, 2, 8);
	}
	
	public RandomReaderAllocationGraph(){
		this(15);
	}
	
	/**
	 * generates and returns a single reader instance with a sequentially 
	 * incremented ID (readerIDcounter) and random number capacity.
	 * Also, preference list is random as well.
	 */
	@SuppressWarnings("unchecked")
	public Reader generateReader(){
		ArrayList<Integer> projectPreferenceList = (ArrayList<Integer>) projects.clone();
		int readerCapacity = rand.nextInt(READER_CAPACITY);
		
		if (readerCapacity > PROJECT_COUNT){
			readerCapacity = PROJECT_COUNT/2;
		}
		
		Reader r = new Reader(readerIDcounter++, readerCapacity);
		
		// preference list size is up to twice as big as the reader capacity. 
		int prefListSize = readerCapacity == 0? 0:rand.nextInt(readerCapacity) + readerCapacity;
		
		// makes a preference list
		for (int i=0; i<prefListSize; i++){
			if (projectPreferenceList.size()  == 0){
				break;
			}
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
