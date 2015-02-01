/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import model.Project;
import model.Reader;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

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
        
        final ContextMenu contextMenu = new ContextMenu();
        
        MenuItem add = new MenuItem("Add..");
        add.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                
                HBox hbox = (HBox) scrollPane.getContent();
                Reader reader = (Reader) hbox.getUserData();
                
                List<Project> choices = new ArrayList<Project>();
                choices.addAll(controller.getModel().getProjects());
                choices.removeAll(reader.getPreferences());
                choices.sort(null);
                
                Optional<Project> response = Dialogs.create()
                    .owner(scrollPane)
                    .title("Choose a project to add")
                    .message("Choose project (Times selected):")
                    .showChoices(choices);
                
                if (response.isPresent()){
                    reader.addPreference(response.get());
                    hbox.getChildren().add(new DragDropLabel(response.get(),controller));
                    
                }

            }
        });
        
        MenuItem remove = new MenuItem("Remove..");
        remove.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                
                HBox hbox = (HBox) scrollPane.getContent();
                Reader reader = (Reader) hbox.getUserData();
                
                List<Project> choices = new ArrayList<Project>();
                choices.addAll(reader.getPreferences());
                
                Optional<Project> response = Dialogs.create()
                    .owner(scrollPane)
                    .title("Choose a project to remove")
                    .message("Choose project (Times selected):")
                    .showChoices(choices);
                
                if (response.isPresent()){
                    reader.removePreference(response.get());
                    for (Node n : hbox.getChildren()){
                        if (n.getUserData().equals(response.get())){
                            hbox.getChildren().remove(n);
                            break;
                        }
                    }
                }

            }
        });
        
        contextMenu.getItems().addAll(add,remove);
        this.setContextMenu(contextMenu);
        
        scrollPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            }
        });
        
        
        scrollPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
               
                if(event.getTransferMode() == TransferMode.MOVE){
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
                    } else {
                         Dialogs.create()
                            .owner(scrollPane)
                            .title("Error")
                            .masthead("Cannot move preference")
                            .message("The reader already has project as preference!\n Project name: "
                                    + projectToMove.getName() + ",\nID: " + projectToMove.getId())
                            .showError();
                    }
                } else {
                    //TODO: list drop here
                }
                event.setDropCompleted(true);
                event.consume();
            }
        });
    }
    
    
    
}
