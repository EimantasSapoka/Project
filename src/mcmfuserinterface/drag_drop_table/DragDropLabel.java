/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;

import java.awt.Toolkit;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import mcmfuserinterface.ControllerInterface;
import mcmfuserinterface.MainViewController;
import mcmfuserinterface.ResultsViewController;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class DragDropLabel extends DragLabel {

    public DragDropLabel(final Project project, final ControllerInterface controller) {
        super(project, controller);
        
        this.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (controller instanceof MainViewController){
                        ((MainViewController) controller).setHighlightedProject((Project)this.getUserData());
                        controller.refresh();
                    }
                }
        });
        this.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
        });

        this.setOnDragEntered(event -> {
            if ((event.getGestureSource() != this)) {
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
            int indexToPlace;

            if (event.getTransferMode() == TransferMode.MOVE) {
                Label sourceLabel = (Label) event.getGestureSource();
                HBox sourceHbox = (HBox) sourceLabel.getParent();
                Project projectToMove = (Project) sourceLabel.getUserData();
                Reader readerToRemoveFrom = (Reader) sourceHbox.getUserData();

                indexToPlace = controller.moveProject(readerToAdd, readerToRemoveFrom, projectToMove, projectToPlaceBefore);
                if (indexToPlace == -1) {
                    createErrorDialog(projectToMove);
                } 
            } else {
                ListCell listCell = (ListCell) event.getGestureSource();
                Project projectToAdd = (Project) listCell.getUserData();

                indexToPlace = controller.addProjectToReader(readerToAdd, projectToAdd, projectToPlaceBefore);
                if (indexToPlace == -1) {
                   createErrorDialog(projectToAdd);
                } 
            }
            
            controller.refresh();
            event.consume();
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
