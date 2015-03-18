/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import mcmfuserinterface.ControllerInterface;
import mcmfuserinterface.DialogUtils;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class DroppableScrollPane extends ScrollPane {
	
	private boolean dropAccepted;

    public DroppableScrollPane(final ControllerInterface controller) {
        super();

        setOnDragOver(event -> {
        	if (dropAccepted){
        		event.acceptTransferModes(TransferMode.ANY);
        	}
        });
        

        setOnDragEntered(event -> {
        	if (event.isAccepted()){
        		return;
        	}
            Reader readerToAdd = (Reader) getContent().getUserData();            
            Node sourceNode = (Node) event.getGestureSource();
            Project projectToAdd = (Project) sourceNode.getUserData();
            String errorMsg;
            
            if (event.getTransferMode() == TransferMode.MOVE) {
                Node sourceHbox = (Node) sourceNode.getParent();
                Reader readerToRemoveFrom = (Reader) (sourceHbox).getUserData();
                
                errorMsg = controller.canMoveProject(readerToAdd, readerToRemoveFrom, projectToAdd);
            } else {
                errorMsg = controller.canMoveProject(readerToAdd, projectToAdd);
            }
            
	       	if ( errorMsg == null){
	       		event.acceptTransferModes(TransferMode.ANY);
	       		dropAccepted = true;
	       	} else {
	       		dropAccepted = false;
	       		controller.showErrorPopOver(errorMsg, this);
	       		event.acceptTransferModes(TransferMode.NONE);
	       	}
        });
        

        setOnDragDropped(event -> {
        	Node hbox = getContent();
            if (hbox.getUserData() == null) {
                event.consume();
                return;
            }

            Reader readerToAdd = (Reader) hbox.getUserData();
            Node sourceNode = (Node) event.getGestureSource();
            Project projectToAdd = (Project) sourceNode.getUserData();
            
            String errorMsg;
            
            if (event.getTransferMode() == TransferMode.MOVE) {
                HBox sourceHbox = (HBox) sourceNode.getParent();
                Reader readerToRemoveFrom = (Reader) (sourceHbox).getUserData();

                errorMsg = controller.moveProject(readerToAdd, readerToRemoveFrom, projectToAdd);
            } else {
                errorMsg = controller.addProjectToReader(readerToAdd, projectToAdd);
            }
            
            if (errorMsg != null) {
                DialogUtils.createErrorDialog(errorMsg);
            }
            
            controller.refresh();
            event.setDropCompleted(true);
            event.consume();
        });
    }
    
   
    

}
