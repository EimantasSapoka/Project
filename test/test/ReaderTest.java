package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;

/**
 * a suite of tests to check that methods in the reader 
 * class perform as expected.
 * @author Eimantas
 *
 */
public class ReaderTest {
	
	Reader reader1,reader2,reader3;
	Project project1, project2, project3, project4, project5;
	
	@Before
	public void setUp(){
		reader1 = new Reader(1, 5);
		reader2 = new Reader(2, 0);
		reader3 = new Reader(3, 4);
		
		project1 = new Project("one", 1);
		project2 = new Project("two", 2);
		project3 = new Project("three", 3);
		project4 = new Project("four", 4);
		project5 = new Project("five", 5);
		
		reader1.addSupervisingProject(project1);
		reader1.addSupervisingProject(project2);
		reader2.addSupervisingProject(project3);
		reader3.addSupervisingProject(project4);
		reader3.addSupervisingProject(project5);
	}
	
	@Test
	public void testCanAssignProject(){
		reader1.assignProject(project5);
		assertTrue(reader1.getAssigned().contains(project5));
		assertTrue(project5.getAssignedReader() == reader1);
		
	}
	
	@Test
	public void testCanAddPreference(){
		reader1.addPreference(project5);
		assertTrue(reader1.getPreferences().contains(project5));
	}
	
	
	@Test
	public void testCanUnassignProject(){
		reader1.assignProject(project5);

		reader1.removeAssignedProject(project5);
		assertFalse(reader1.getPreferences().contains(project5));
		assertTrue(project1.getAssignedReader() == null);
	}
	
	
	@Test
	public void testCanRemoveAssignmentNotInList_NoEffect(){
		assertFalse(reader1.getAssigned().contains(project5));
		
		reader1.removeAssignedProject(project5);
		assertFalse(reader1.getPreferences().contains(project5));
	}
	
	@Test
	public void testCannotAssignSuprevisedProject(){
		reader1.assignProject(project1);
		assertFalse(reader1.getAssigned().contains(project1));
	}
	
	@Test
	public void testCannotAssignProjectTwice(){
		reader1.assignProject(project5);
		assertTrue(reader1.getAssigned().contains(project5));
		assertTrue(reader1.getAssigned().size() == 1);
		
		reader1.assignProject(project5);
		assertTrue(reader1.getAssigned().size() == 1);
	}
	
	@Test
	public void testCannotAddPreferenceSupervisingProject(){
		reader1.addPreference(project1);
		assertFalse(reader1.getPreferences().contains(project5));
	}
	
	@Test 
	public void cannotAddPreferenceTwice(){
		reader1.addPreference(project5);
		assertTrue(reader1.getPreferences().contains(project5));
		assertTrue(reader1.getPreferences().size() == 1);
		
		reader1.assignProject(project5);
		assertTrue(reader1.getPreferences().size() == 1);
	}
	

}
