/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import javafx.event.EventHandler;
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
                /* data is dragged over the listCell */
                System.out.println("onDragOver");
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
        
        scrollPane.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture entered the listCell */
                System.out.println("onDragEntered");
                /* show to the user that it is an actual gesture listCell */
                event.consume();
            }
        });

        scrollPane.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* mouse moved away, remove the graphical cues */
                event.consume();
            }
        });
        
        
        scrollPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                System.out.println("DROPPED!!!!");
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                
                HBox hbox = (HBox) scrollPane.getContent();
                if (hbox.getUserData() == null){
                    event.consume();
                    return;
                }
                String clipboard = db.getString();
                System.out.println(clipboard);
                
                int readerID = Integer.parseInt(clipboard.split("/")[0]);
                int projectID = Integer.parseInt(clipboard.split("/")[1]);
                
                Project projectToMove = controller.getModel().getProject(projectID);
                Reader readerToRemoveFrom = controller.getModel().getReader(readerID);
                Reader readerToAdd = (Reader) hbox.getUserData();
                
                System.out.println("adding to reader " + readerToAdd + " project " + projectToMove);
                controller.getModel().movePreference(readerToAdd, readerToRemoveFrom, projectToMove);
                controller.refreshTable();
                
                event.setDropCompleted(true);
                event.consume();
            }
        });
        
    }
    
    
    
}
