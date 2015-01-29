/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import model.Project;
import model.Reader;


/**
 *
 * @author Eimantas
 */
public class DragDropLabel extends Label{
    final Label label;
    final private Controller controller;
    
    
    DragDropLabel(String s, Controller contr){
        super(s);
        label = this;
        this.controller = contr;
        
       
        
        label.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Dragboard db = label.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();

                content.putString("test");
                db.setContent(content);
                mouseEvent.consume();
            }
        });
        
        label.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
        
        label.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ((event.getGestureSource() != label)) {
                    label.setOpacity(0.5);
                    label.setText("\t" + label.getText());
                }
                event.consume();
            }
        });

        label.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                label.setText(label.getText().trim());
                label.setOpacity(1);
                event.consume();
            }
        });
        
        
        label.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                
                Label sourceLabel = (Label)event.getGestureSource();
                Project projectToMove = (Project) sourceLabel.getUserData();
                Reader readerToRemoveFrom = (Reader) ((HBox) sourceLabel.getParent()).getUserData();
                Project projectToPlaceBefore = (Project) label.getUserData();
                HBox hbox = (HBox) label.getParent();
                Reader readerToAdd = (Reader) hbox.getUserData();
                
                
                controller.getModel().movePreference(readerToAdd, readerToRemoveFrom, projectToMove, projectToPlaceBefore);
                controller.refreshTable();
                
                event.setDropCompleted(true);
                event.consume();
            }
        });
        


    }
    
    
    
}
