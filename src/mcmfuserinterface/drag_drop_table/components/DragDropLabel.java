/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.components;

import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import mcmfuserinterface.DialogUtils;
import mcmfuserinterface.controllers.MainViewController;

/**
 *
 * @author Eimantas
 */
public class DragDropLabel extends DragLabel {

    public DragDropLabel(final Project project, final MainViewController controller) {
    	
        super(project, controller);
        
        this.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (controller instanceof MainViewController){
                        ((MainViewController) controller).setHighlightedProject((Project)this.getUserData());
                        controller.refreshTable();
                    }
                }
        });
       

        this.setOnDragEntered(event -> {
       		 if ( event.getGestureSource() != this) {
       			 setText("\t" + getText());
       		 }
        });

        this.setOnDragExited(event -> {
            setText(getText().trim());
        });

        this.setOnDragDropped(event -> {
            HBox hbox = (HBox) getParent();
            Project projectToPlaceBefore = project;
            Reader readerToAdd = (Reader) hbox.getUserData();            
            Node sourceNode = (Node) event.getGestureSource();
            Project projectToAdd = (Project) sourceNode.getUserData();
            
            String errorMsg;
            
            if (event.getTransferMode() == TransferMode.MOVE) {
                HBox sourceHbox = (HBox) sourceNode.getParent();
                Reader readerToRemoveFrom = (Reader) sourceHbox.getUserData();
                
                errorMsg = controller.moveProject(readerToAdd, readerToRemoveFrom, projectToAdd, projectToPlaceBefore);
            } else {
                errorMsg = controller.addProjectToReader(readerToAdd, projectToAdd, projectToPlaceBefore);
            }
            
            if (errorMsg != null) {
                DialogUtils.createErrorDialog(projectToAdd, errorMsg);
            } 
            
            event.consume();
        });
    }

}
