/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import model.MCMFModel;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public interface Controller {
    public MCMFModel getModel();
    public void refreshTable();
    public void refreshLowSelectedProjectList();
    public int moveProject(Reader reader, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore);
    public boolean moveProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove);
    public boolean addProjectToReader(Reader reader, Project projectToAdd);
    public int addProjectToReader(Reader reader, Project projectToAdd, Project projectToAddBefore);
}
