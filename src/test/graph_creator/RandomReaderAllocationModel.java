package test.graph_creator;

import java.util.ArrayList;
import java.util.Random;

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
	private ArrayList<Project> projectList;
    
    private int targetMin, targetMax, preferenceMin, preferenceMax;
	Random rand;
	
	/**
	 * creates a random reader allocation graph with a random 
	 * number of readers up to the reader limit. 
	 * also creates number of projects equal to the number of readers multiplied 
	 * by the projectMultiplier. 
        * @param readerCount
        * @param projectCount
        * @param readerMaxCapacity
	 */
	public RandomReaderAllocationModel(int readerCount, int projectCount,
			int targetMin, int targetMax, int preferenceMin, int preferenceMax){
		super();
		
		if (preferenceMin > projectCount){
			preferenceMin = projectCount;
		}
		if (preferenceMax < preferenceMin){
			preferenceMax = 0;
		}
		
		if (targetMax< targetMin){
			targetMax = 0;
		}
		
		this.targetMin = targetMin;
		this.targetMax = targetMax == 0? targetMin*2: targetMax;
		this.preferenceMin = preferenceMin;
		this.preferenceMax = preferenceMax == 0? preferenceMin*2:preferenceMax;
		
		rand = new Random();
		READER_COUNT = readersLeft = readerCount;
		PROJECT_COUNT = projectsLeft = projectCount;
        
		projectList = new ArrayList<Project>(PROJECT_COUNT);
		
		for (int i=0; i<PROJECT_COUNT; i++){
            Project project = new Project("Project " + i, i + READER_COUNT, rand.nextInt(readerCount)+1 );
            projectList.add(project);
            this.addProject(project);
		}
		
		for (int i=0; i<READER_COUNT; i++){
            this.addReader(generateReader());
		}
		
	}
	
	public RandomReaderAllocationModel(int readerCount, int projectCount){
		this(readerCount, projectCount, 0,0,0,0);
	}
	
	/**
	 * constructor taking reader count limit. 
	 * will create graph with random number of readers up to the limit
	 * and number of projects twice the reader count. 
	 * @param readerCount
	 */
	public RandomReaderAllocationModel(int readerCount){
		this(readerCount, readerCount*4, 0, 0, 0, 0);
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
            
		ArrayList<Project> projectPreferenceList = (ArrayList<Project>) projectList.clone();
         
        int readerCapacityMin = targetMin == 0? 2:targetMin;
        int readerCapacityMax = targetMax == 0? (PROJECT_COUNT/READER_COUNT)*2 : (targetMax-readerCapacityMin);

        
		int readerCapacity = readerCapacityMax == 0? readerCapacityMin:(rand.nextInt(readerCapacityMax+1) + readerCapacityMin);
                
        projectsLeft -= readerCapacity;
        readersLeft--;
		
		Reader r = new Reader(readerIDcounter++, readerCapacity);
		
		for (Project p: projects){
			if (p.getSupervisorID() == r.getID()){
				r.addSupervisingProject(p.getId());
			}
		}
		
		int readerPreferencesMax = preferenceMax == 0? readerCapacity *2 : preferenceMax-preferenceMin;

		int prefListSize;
		if (readerCapacity == 0){
			prefListSize = 0;
		} else {
			prefListSize = readerPreferencesMax == 0? preferenceMin:(rand.nextInt(readerPreferencesMax+1) + preferenceMin);
		}

		// makes a preference list
		for (int i=0; i<prefListSize;){
			if (projectPreferenceList.isEmpty()){
                break;
            }
			// selects a random project number from list
			int randomProjectIndex = rand.nextInt(projectPreferenceList.size()); 
			
			// creates a new project with the id from the list (and removes that id) and preference of loop index
			Project project = projectPreferenceList.remove(randomProjectIndex);
			if (r.addPreference(project)){;
				i++;
			}
		}
		
		return r;
	}
}
