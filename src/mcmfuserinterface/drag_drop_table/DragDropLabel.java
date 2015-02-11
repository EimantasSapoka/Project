/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;

import java.awt.Toolkit;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import mcmfuserinterface.ControllerInterface;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class DragDropLabel extends DragLabel {
    
    public DragDropLabel(final Project project, final ControllerInterface controller) {
        super(project, controller);

        this.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        });

        this.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ((event.getGestureSource() != this)) {
                    setText("\t" + getText());
                }
            }
        });

        this.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                setText(getText().trim());
            }
        });
        
        
        this.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                HBox hbox = (HBox) getParent();
                Project projectToPlaceBefore = project;
                Reader readerToAdd = (Reader) hbox.getUserData();
                int indexToPlace;

                if (event.getTransferMode() == TransferMode.MOVE){
                    Label sourceLabel = (Label) event.getGestureSource();
                    HBox sourceHbox = (HBox) sourceLabel.getParent();
                    Project projectToMove = (Project) sourceLabel.getUserData();
                    Reader readerToRemoveFrom = (Reader) sourceHbox.getUserData();

                    indexToPlace = controller.moveProject(readerToAdd, readerToRemoveFrom, projectToMove, projectToPlaceBefore);
                    if (indexToPlace != -1) {
                        sourceHbox.getChildren().remove(sourceLabel);
                        hbox.getChildren().add(indexToPlace, sourceLabel);
                    } else {
                        createErrorDialog(projectToMove);
                    }
                } else {
                    ListCell listCell = (ListCell) event.getGestureSource();
                    Project projectToAdd = (Project) listCell.getUserData();
                    
                    indexToPlace = controller.addProjectToReader(readerToAdd, projectToAdd, projectToPlaceBefore);
                    if (indexToPlace != -1) {
                        hbox.getChildren().add(indexToPlace, new DragDropLabel(projectToAdd, controller));
                    } else {
                        createErrorDialog(projectToAdd);
                    }
                    
                }
                
                event.setDropCompleted(indexToPlace != -1);
                event.consume();
            }
           
        });
    }
    
    
    protected void createErrorDialog(Project projectToAdd) {
        final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
        if (runnable != null) {
            runnable.run();
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot move");
        alert.setContentText("The reader already has this project!\nProject name: "
                        + projectToAdd.getName() + ",  ID: " + projectToAdd.getId());
        alert.setResizable(true);
        alert.showAndWait();
    }
    
}
