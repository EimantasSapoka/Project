package test;

import static org.junit.Assert.*;
import mcmfuserinterface.UserInterfaceModel;

import org.junit.Before;
import org.junit.Test;

import ford_fulkerson.model.MCMFModel;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;

public class UserInterfaceModelTest {
	
	UserInterfaceModel model;
	Reader reader1,reader2,reader3;
	Project project1, project2, project3, project4, project5;
	
	@Before
	public void setUp(){
		model = new UserInterfaceModel(new MCMFModel()); // creates an empty model
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
		
		model.addReader(reader1);
		model.addReader(reader2);
		model.addReader(reader3);
	}
	
	
	@Test 
	public void testAddProjectToPreferences_Allowed(){
		assertFalse(reader1.getPreferences().contains(project5));
		String result = model.canAddPreference(reader1, null, project5);
		assertNull(result);
		assertFalse(reader1.getPreferences().contains(project5));
	}
	
	@Test 
	public void testAddProjectToPreferences_Added(){
		assertFalse(reader1.getPreferences().contains(project5));
		String result = model.addProjectToReaderPreferences(reader1, project5);
		assertNull(result);
		assertTrue(reader1.getPreferences().contains(project5));
	}
	
	@Test 
	public void testAddProjectToAssignments_Allowed(){
		assertFalse(reader1.getAssigned().contains(project5));
		String result = model.canAddAssignment(reader1, null, project5);
		assertNull(result);
		assertFalse(reader1.getAssigned().contains(project5));
	}
	
	@Test 
	public void testAddProjectToAssignments_Added(){
		assertFalse(reader1.getAssigned().contains(project5));
		String result = model.assignProjectToReader(reader1, project5);
		assertNull(result);
		assertTrue(reader1.getAssigned().contains(project5));
	}

	
	@Test
	public void testAddProjectInPreferenceList_NotAllowed(){
		reader1.addPreference(project5); // add the preference first
		
		String result = model.canAddPreference(reader1,null, project5);
		assertNotNull(result);
	}
	

	@Test
	public void testAddProjectInPreferenceList_NotAdded(){
		reader1.addPreference(project5); // add the preference first
		assertTrue(reader1.getPreferences().size() == 1);
		
		String result = model.addProjectToReaderPreferences(reader1, project5);
		assertNotNull(result);
		assertTrue(reader1.getPreferences().size() == 1);
	}
	
	@Test
	public void testAddProjectInAssignedList_NotAllowed(){
		reader1.assignProject(project5);
		
		String result = model.canAddAssignment(reader1, null, project5);
		assertNotNull(result);
	}
	
	@Test
	public void testAddProjectInAssignedList_NotAdded(){
		reader1.assignProject(project5);
		
		String result = model.assignProjectToReader(reader1, project5);
		assertNotNull(result);
	}
	
	@Test
	public void testAddSupervisedProjectToPreferences_NotAllowed(){
		assertTrue(reader1.getSupervisorProjects().contains(project1));
		
		String result = model.canAddPreference(reader1, null, project1);
		assertNotNull(result);
		assertFalse(reader1.getPreferences().contains(project1));
	}
	
	@Test
	public void testAddSupervisedProjectToPreferences_NotAdded(){
		assertTrue(reader1.getSupervisorProjects().contains(project1));
		
		String result = model.addProjectToReaderPreferences(reader1, project1);
		assertNotNull(result);
		assertFalse(reader1.getPreferences().contains(project1));
	}
	
	@Test
	public void testAddSupervisedProjectToAssignments_NotAllowed(){
		assertTrue(reader1.getSupervisorProjects().contains(project1));
		
		String result = model.canAddAssignment(reader1, null, project1);
		assertNotNull(result);
		assertFalse(reader1.getAssigned().contains(project1));
	}
	
	@Test
	public void testAddSupervisedProjectToAssignments_NotAdded(){
		assertTrue(reader1.getSupervisorProjects().contains(project1));
		
		String result = model.assignProjectToReader(reader1, project1);
		assertNotNull(result);
		assertFalse(reader1.getAssigned().contains(project1));
	}
	
	@Test
	public void testAddProjectToPreferencesZeroMarkingTarget_NotAllowed(){
		String result = model.canAddPreference(reader2, null, project5);
		assertNotNull(result);
		assertFalse(reader2.getPreferences().contains(project5));
	}
	
	@Test
	public void testAddProjectToPreferencesZeroMarkingTarget_NotAdded(){
		String result = model.addProjectToReaderPreferences(reader2, project5);
		assertNotNull(result);
		assertFalse(reader2.getPreferences().contains(project5));
	}
	
	@Test
	public void testAssingProjectZeroMarkingTarget_NotAllowed(){
		String result = model.canAddAssignment(reader2, null, project5);
		assertNotNull(result);
		assertFalse(reader2.getAssigned().contains(project5));
	}
	
	@Test
	public void testAssignProjectZeroMarkingTarget_NotAdded(){
		String result = model.assignProjectToReader(reader2, project5);
		assertNotNull(result);
		assertFalse(reader2.getAssigned().contains(project5));
	}
	
	@Test
	public void testCanRemovePreferenceInList_Removed(){
		reader1.addPreference(project5);
		assertTrue(reader1.getPreferences().contains(project5));
		
		model.removeProjectFromReaderPreferences(reader1, project5);
		assertFalse(reader1.getPreferences().contains(project5));
	}
	
	@Test
	public void testCanRemovePreferenceNotInList_NoEffect(){
		assertFalse(reader1.getPreferences().contains(project5));
		
		model.removeProjectFromReaderPreferences(reader1, project5);
		assertFalse(reader1.getPreferences().contains(project5));
	}
	
	
	@Test
	public void testCanMovePreference(){
		reader1.addPreference(project3);
		assertTrue(reader1.getPreferences().contains(project3));
		
		String result = model.movePreference(reader3, reader1, project3);
		assertNull(result);
		assertFalse(reader1.getPreferences().contains(project3));
		assertTrue(reader3.getPreferences().contains(project3));
	}
	
	@Test
	public void testCanMoveAssignment(){
		model.assignProjectToReader(reader1, project3);
		assertTrue(reader1.getAssigned().contains(project3));
		
		String result = model.moveAssignedProject(reader3, reader1, project3);
		assertNull(result);
		assertFalse(reader1.getAssigned().contains(project3));
		assertTrue(reader3.getAssigned().contains(project3));
	}
	

	@Test
	public void testAssginingAlreadyAssignedProjectToReader_DoesNothing(){
		model.assignProjectToReader(reader1, project5);
		assertTrue(reader1.getAssigned().contains(project5));
		
		String result = model.moveAssignedProject(reader1, reader1, project5);
		assertNull(result);
	}
	
}
