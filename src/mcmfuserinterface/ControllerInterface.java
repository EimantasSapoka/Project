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
    public void refreshTable();
    public void refreshLowSelectedProjectList();
    public int moveProject(Reader reader, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore);
    public boolean moveProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove);
    public boolean addProjectToReader(Reader reader, Project projectToAdd);
    public int addProjectToReader(Reader reader, Project projectToAdd, Project projectToAddBefore);
    public ContextMenu createContextMenu(Reader reader, Node node);
    public Collection<Project> getProjects();
    public Collection<Project> getReaderList(Reader reader);
    public void removeProjectFromReader(Reader reader, Project project);
    public Label createLabel(Reader reader, Project project, ControllerInterface controller);
}
