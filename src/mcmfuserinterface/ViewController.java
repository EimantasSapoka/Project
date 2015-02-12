/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mcmfuserinterface.drag_drop_table.DragDropLabel;
import mcmfuserinterface.drag_drop_table.DragLabel;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import model.MCMFModel;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public abstract class ViewController implements Initializable, ControllerInterface {

    protected MCMFModel model;
    protected Label dragLabel;
    @FXML 
    protected AnchorPane anchorPane;
    @FXML
    protected TableView<TableObjectInterface> table;
    @FXML
    protected CheckBox zeroCapacityReaderCheckbox;
    
    
    @Override
    public abstract void initialize(URL location, ResourceBundle resources);

    @Override
    public void refreshTable() {
        table.getColumns().get(0).setVisible(false);
        table.getColumns().get(0).setVisible(true);
    }
    
     protected void createDragLabel() {
        dragLabel = new Label("");
        dragLabel.setMouseTransparent(true);
        dragLabel.setVisible(false);
        dragLabel.toFront();
        
        anchorPane.getChildren().add(dragLabel);
    }
    
    @Override
    public Collection<Project> getProjects() {
        return model.getProjects();
    }
    
    @FXML
    protected void closeWindow(ActionEvent event) {
        ((Stage) table.getScene().getWindow()).close();
    }
    
    /**
     * changes between showing and hiding readers with zero capacity
     */
    @FXML
    protected void toggleShowZeroCapacityReaders() {
        ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
        if (zeroCapacityReaderCheckbox.isSelected()){
            items.addAll(model.getReaders());
        } else {
            for (Reader r : model.getReaders()){
                if (!(r.getCapacity() == 0)){
                    items.add(r);
                }
            }
        }
        table.setItems(null);
        table.setItems(items);
    }
    
    
    /**
     * creates a table with reader information and preferences list.
     */
    protected void createTableFromModel() {
        if (model == null) { 
            /* should not happen as this is only called locally after
               the model is instanciated */
            System.err.println("model instance empty");
            return;
        }
        
        // if the table has no columns, create them
        if (table.getColumns().isEmpty()){
            createTableColumns();
        }
        
        ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
        if (this.zeroCapacityReaderCheckbox.isSelected()){
            items.addAll(model.getReaders());
        } else {
            for (Reader r : model.getReaders()){
                if (!(r.getCapacity() == 0)){
                    items.add(r);
                }
            }
        }
        
       
        table.setItems(items);
        table.setFixedCellSize(40);
        setTableRowFactory();
        refreshTable();
    }
    
    /**************************** ANCHOR PANE EVENTS ********************/
    
    @FXML
    protected void anchorPaneDragDetected(MouseEvent t) {
        dragLabel.relocate(
                (int) (t.getSceneX() - dragLabel.getBoundsInLocal().getWidth() / 2),
                (int) (t.getSceneY() - dragLabel.getBoundsInLocal().getHeight() / 2));
    }

    @FXML
    protected void anchorPaneDragOver(DragEvent event) {
        if (!dragLabel.isVisible()) {
            dragLabel.setVisible(true);
            dragLabel.toFront();
            Project project;
            if (event.getGestureSource() instanceof DragLabel) {
                project = (Project) ((Label) event.getGestureSource()).getUserData();
            } else {
                project = (Project) ((ListCell) event.getGestureSource()).getUserData();
            }
            dragLabel.setText(project.getId() + "");
        }
        dragLabel.relocate(
                (int) (event.getSceneX() - dragLabel.getBoundsInLocal().getWidth() / 2),
                (int) (event.getSceneY() - dragLabel.getBoundsInLocal().getHeight() / 2));
    }

    @FXML
    protected void anchorPaneDragDone(DragEvent event) {
        dragLabel.setVisible(false);
    }
    
    protected abstract void setTableRowFactory();
    protected abstract void createTableColumns();

    @Override
    public abstract void refreshLowSelectedProjectList();

    @Override
    public abstract int moveProject(Reader reader, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore);

    @Override
    public abstract boolean moveProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove);

    @Override
    public abstract boolean addProjectToReader(Reader reader, Project projectToAdd);

    @Override
    public abstract int addProjectToReader(Reader reader, Project projectToAdd, Project projectToAddBefore);

    @Override
    public abstract ContextMenu createContextMenu(Reader reader, Node node);
    
    @Override
    public abstract Collection<Project> getReaderList(Reader reader);

    @Override
    public abstract void removeProjectFromReader(Reader reader, Project project);

    @Override
    public abstract Label createLabel(Reader reader, Project project, ControllerInterface controller);
    
}
