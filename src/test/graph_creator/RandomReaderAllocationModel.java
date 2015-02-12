package test.graph_creator;

import java.util.ArrayList;
import java.util.Random;

import ford_fulkerson.graph.Graph;
import model.MCMFModel;
import model.Project;
import model.Reader;

/**
 * class which creates a random reader allocation graph. Used for testing.
 * @author Eimantas
 *
 */
public class RandomReaderAllocationModel extends MCMFModel{
    
	private int readerIDcounter = 1;
	private final int PROJECT_COUNT;
	private final int READER_COUNT;
        private int projectsLeft;
        private int readersLeft;
	private ArrayList<Integer> projectList;
        private ArrayList<Integer> unselectedProjects;
	Random rand;
	
	/**
	 * creates a random reader allocation graph with a random 
	 * number of readers up to the reader limit. 
	 * also creates number of projects equal to the number of readers multiplied 
	 * by the projectMultiplier. 
        * @param readerCount
        * @param numberProjects
        * @param readerMaxCapacity
	 */
	public RandomReaderAllocationModel(int readerCount, int numberProjects){
		super();
		rand = new Random();
		READER_COUNT = readerCount;
		PROJECT_COUNT = numberProjects;
                projectsLeft = numberProjects;
                readersLeft = readerCount;
		
		projectList = new ArrayList<Integer>(PROJECT_COUNT);
                unselectedProjects = (ArrayList<Integer>) projectList.clone();
		
		for (int i=0; i<PROJECT_COUNT; i++){
			projectList.add(i + READER_COUNT);
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
	public RandomReaderAllocationModel(int readerLimit){
		this(readerLimit, readerLimit*4);
	}
	
	public RandomReaderAllocationModel(){
		this(15);
	}
	
	/**
	 * generates and returns a single reader instance with a sequentially 
	 * incremented ID (readerIDcounter) and random number capacity.
	 * Also, preference list is random as well.
     * @return 
	 */
	@SuppressWarnings("unchecked")
	public final Reader generateReader(){
            
		ArrayList<Integer> projectPreferenceList;
                if (unselectedProjects.isEmpty()){
                    projectPreferenceList = (ArrayList<Integer>) projectList.clone();
                } else {
                    projectPreferenceList = unselectedProjects;
                }
		int readerCapacity = rand.nextInt((projectsLeft/readersLeft)*2 +1);
		
		if (readerCapacity > projectsLeft){
			readerCapacity = projectsLeft/2;
		}
                
                projectsLeft -= readerCapacity;
                readersLeft--;
		
		Reader r = new Reader(readerIDcounter++, readerCapacity);
		
		// preference list size is up to twice as big as the reader capacity. 
		int prefListSize = readerCapacity == 0? 0:rand.nextInt(readerCapacity *3) + readerCapacity/2;
		
		// makes a preference list
		for (int i=0; i<prefListSize; i++){
			if (projectPreferenceList.isEmpty()){
                            if (unselectedProjects.isEmpty()){
                                break;
                            } else {
                                projectPreferenceList = (ArrayList<Integer>) projects.clone();
                                projectPreferenceList.removeAll(r.getPreferences());
                            }
                        }
			// selects a random project number from list
			int randomProjectIndex = rand.nextInt(projectPreferenceList.size()); 
			
			// creates a new project with the id from the list (and removes that id) and preference of loop index
			int id = projectPreferenceList.remove(randomProjectIndex);
			Project project;
			if( (project = this.getProject(id)) == null){
				project = new Project(id);
			}
			r.addPreference(project); 
			
		}
		
		return r;
	}
}
