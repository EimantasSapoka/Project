/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.components;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import mcmfuserinterface.DialogUtils;
import mcmfuserinterface.controllers.MainViewController;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;

/**
 * a label which supports dragging and dropping. It has a 
 * controller reference, which it calls out to modify the 
 * model. Depending on the controller, it behaves differently. 
 * @author Eimantas
 */
public class DragDropLabel extends DragLabel {

    public DragDropLabel(final Project project, final MainViewController controller) {
    	
        super(project, controller);
        
        this.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.MIDDLE) {
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
            setText(getText().replaceAll("\t", ""));
        });

        this.setOnDragDropped(event -> {
            HBox hbox = (HBox) getParent();
            Project projectToPlaceBefore = project;
            Reader readerToAdd = (Reader) hbox.getUserData();            
            Node sourceNode = (Node) event.getGestureSource();
            Project projectToAdd = (Project) sourceNode.getUserData();
            
            String errorMsg;
            
            // if move - that is from one list to another - move the project
            if (event.getTransferMode() == TransferMode.MOVE) {
                HBox sourceHbox = (HBox) sourceNode.getParent();
                Reader readerToRemoveFrom = (Reader) sourceHbox.getUserData();
                
                errorMsg = controller.moveProject(readerToAdd, readerToRemoveFrom, projectToAdd, projectToPlaceBefore);
            } else { // if not move - that is from the side list to the table - add the project instead
                errorMsg = controller.addProjectToReader(readerToAdd, projectToAdd, projectToPlaceBefore);
            }
            
            if (errorMsg != null) {
                DialogUtils.createErrorDialog(projectToAdd, errorMsg);
            } 
            
        	controller.refreshSideProjectList();
            event.consume();
        });
    }

}
