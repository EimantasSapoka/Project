/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class DroppableScrollPane extends ScrollPane {
    
    final private ScrollPane scrollPane;
    final private Controller controller;
    
    public DroppableScrollPane(Controller contr){
        super();
        this.controller = contr;
        scrollPane = this;

        
        scrollPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
        
        scrollPane.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.consume();
            }
        });

        scrollPane.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.consume();
            }
        });
        
        
        scrollPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
               
                HBox hbox = (HBox) scrollPane.getContent();
                if (hbox.getUserData() == null){
                    event.consume();
                    return;
                }
               
                Label sourceLabel = (Label)event.getGestureSource();
                HBox sourceHbox = (HBox) sourceLabel.getParent();
                Project projectToMove = (Project) sourceLabel.getUserData();
                Reader readerToRemoveFrom = (Reader) (sourceHbox).getUserData();
                Reader readerToAdd = (Reader) hbox.getUserData();
                
                boolean success = controller.getModel().movePreference(readerToAdd, readerToRemoveFrom, projectToMove);
                if (success){
                    sourceHbox.getChildren().remove(sourceLabel);
                    hbox.getChildren().add(sourceLabel);
                }
                //controller.refreshTable();
                
                event.setDropCompleted(true);
                event.consume();
            }
        });
        
    }
    
    
    
}
