/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import java.util.Collection;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public interface ControllerInterface {
    public void refresh();
    public void refreshTable();
    public void refreshLowSelectedProjectList();
    public String moveProject(Reader reader, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore);
    public String moveProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove);
    public String addProjectToReader(Reader reader, Project projectToAdd);
    public String addProjectToReader(Reader reader, Project projectToAdd, Project projectToAddBefore);
    public ContextMenu createContextMenu(Reader reader, Node node);
    public Collection<Project> getProjects();
    public Collection<Project> getReaderList(Reader reader);
    public void removeProjectFromReader(Reader reader, Project project);
    public Label createLabel(Reader reader, Project project, ControllerInterface controller);
	public void showErrorPopOver(String errorMsg, Node parent);
	public String canMoveProject(Reader readerToAdd, Project projectToAdd);
	public String canMoveProject(Reader readerToAdd, Reader readerToRemoveFrom,Project projectToAdd);
}
