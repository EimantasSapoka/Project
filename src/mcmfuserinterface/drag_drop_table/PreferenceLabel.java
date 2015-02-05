/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;


import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import mcmfuserinterface.Controller;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class PreferenceLabel extends DragDropLabel {
    final private Controller controller;
    
    PreferenceLabel(final Project proj, Controller contr) {
        super(proj);
      
        this.controller = contr;
        
        this.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                HBox hbox = (HBox) getParent();
                Project projectToPlaceBefore = proj;
                Reader readerToAdd = (Reader) hbox.getUserData();
                int indexToPlace;

                if (event.getTransferMode() == TransferMode.MOVE){
                    Label sourceLabel = (Label) event.getGestureSource();
                    HBox sourceHbox = (HBox) sourceLabel.getParent();
                    Project projectToMove = (Project) sourceLabel.getUserData();
                    Reader readerToRemoveFrom = (Reader) sourceHbox.getUserData();

                    indexToPlace = controller.getModel().movePreference(readerToAdd, readerToRemoveFrom, projectToMove, projectToPlaceBefore);
                    if (indexToPlace != -1) {
                        sourceHbox.getChildren().remove(sourceLabel);
                        hbox.getChildren().add(indexToPlace, sourceLabel);
                    } else {
                        createErrorDialog(projectToMove);
                    }
                } else {
                    ListCell listCell = (ListCell) event.getGestureSource();
                    Project projectToAdd = (Project) listCell.getUserData();
                    
                    indexToPlace = controller.getModel().addProjectToReaderPreferences(readerToAdd, projectToAdd, projectToPlaceBefore);
                    if (indexToPlace != -1) {
                        hbox.getChildren().add(indexToPlace, new PreferenceLabel(projectToAdd, controller));
                    } else {
                        createErrorDialog(projectToAdd);
                    }
                    
                }
                
                event.setDropCompleted(indexToPlace != -1);
                event.consume();
            }
           
        });

    }
}
