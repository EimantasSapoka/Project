/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import model.Project;
import model.Reader;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

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
                HBox hbox = (HBox) scrollPane.getContent();
                if (hbox.getUserData() == null){
                       event.consume();
                       return;
                 }
                boolean success;
                
                Reader readerToAdd = (Reader) hbox.getUserData();
                 
                if(event.getTransferMode() == TransferMode.MOVE){
                    Label sourceLabel = (Label)event.getGestureSource();
                    HBox sourceHbox = (HBox) sourceLabel.getParent();
                    Project projectToMove = (Project) sourceLabel.getUserData();
                    Reader readerToRemoveFrom = (Reader) (sourceHbox).getUserData();

                    success = controller.getModel().movePreference(readerToAdd, readerToRemoveFrom, projectToMove);
                    if (success){
                        sourceHbox.getChildren().remove(sourceLabel);
                        hbox.getChildren().add(sourceLabel);
                    } else {
                         createErrorDialog();
                    }
                } else {
                    ListCell listCell = (ListCell) event.getGestureSource();
                    Project projectToAdd = (Project) listCell.getUserData();
                   
                    
                   success = controller.getModel().addProjectToReaderPreferences(readerToAdd, projectToAdd);
                    if (success) {
                        hbox.getChildren().add(new DragDropLabel(projectToAdd, controller));
                    } else {
                        createErrorDialog();
                    }
                    
                }
                event.setDropCompleted(success);
                event.consume();
            }

            private void createErrorDialog() {
                final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
                if (runnable != null) {
                    runnable.run();
                }
                
                Dialogs.create()
                        .owner(scrollPane)
                        .title("Error")
                        .masthead("Cannot move preference")
                        .message("Either the reader already has project as preference or he has capacity of 0!")
                        .showError();
               
            }
        });
    }    
}
