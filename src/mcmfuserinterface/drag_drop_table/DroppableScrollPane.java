/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;

import java.awt.Toolkit;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import mcmfuserinterface.ControllerInterface;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class DroppableScrollPane extends ScrollPane {

    public DroppableScrollPane(final ControllerInterface controller) {
        super();

        setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
        });

        setOnDragDropped(event -> {
            HBox hbox = (HBox) getContent();
            if (hbox.getUserData() == null) {
                event.consume();
                return;
            }
            boolean success;

            Reader readerToAdd = (Reader) hbox.getUserData();

            if (event.getTransferMode() == TransferMode.MOVE) {
                Label sourceLabel = (Label) event.getGestureSource();
                HBox sourceHbox = (HBox) sourceLabel.getParent();
                Project projectToMove = (Project) sourceLabel.getUserData();
                Reader readerToRemoveFrom = (Reader) (sourceHbox).getUserData();

                success = controller.moveProject(readerToAdd, readerToRemoveFrom, projectToMove);
                if (success) {
                    sourceHbox.getChildren().remove(sourceLabel);
                    hbox.getChildren().add(sourceLabel);
                } else {
                    createErrorDialog();
                }
            } else {
                ListCell listCell = (ListCell) event.getGestureSource();
                Project projectToAdd = (Project) listCell.getUserData();

                success = controller.addProjectToReader(readerToAdd, projectToAdd);
                if (success) {
                    hbox.getChildren().add(new DragDropLabel(projectToAdd, controller));
                } else {
                    createErrorDialog();
                }

            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    protected void createErrorDialog() {
        final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
        if (runnable != null) {
            runnable.run();
        }
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot move!");
        alert.setContentText("Either the reader already has project or cannot take any projects!");
        alert.showAndWait();
    }
}
