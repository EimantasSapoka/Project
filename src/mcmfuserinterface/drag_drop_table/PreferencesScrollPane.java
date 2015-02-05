/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import mcmfuserinterface.Controller;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class PreferencesScrollPane extends DroppableScrollPane {

    public PreferencesScrollPane(final Controller controller) {
        super();

        final ContextMenu contextMenu = new ContextMenu();

        MenuItem add = new MenuItem("Add..");
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                HBox hbox = (HBox) getContent();
                Reader reader = (Reader) hbox.getUserData();

                if (reader.getCapacity() == 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Cannot add preference!");
                    alert.setContentText("Reader has capacity of zero!");
                    alert.showAndWait();
                    return;
                }

                List<Project> choices = new ArrayList<Project>();
                choices.addAll(controller.getModel().getProjects());
                choices.removeAll(reader.getPreferences());
                choices.sort(null);

                ChoiceDialog<Project> dialog = new ChoiceDialog<>(choices.get(0), choices);
                dialog.setTitle("Choose a project to add");
                dialog.setContentText("Choose project (Times selected):");

                Optional<Project> result = dialog.showAndWait();
                if (result.isPresent()) {
                    reader.addPreference(result.get());
                    hbox.getChildren().add(new PreferenceLabel(result.get(), controller));
                }
            }
        });

        MenuItem remove = new MenuItem("Remove..");
        remove.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                HBox hbox = (HBox) getContent();
                Reader reader = (Reader) hbox.getUserData();

                if (reader.getPreferences().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Reader does not have any preferences");
                    alert.showAndWait();
                    return;
                }

                List<Project> choices = new ArrayList<Project>();
                choices.addAll(reader.getPreferences());

                ChoiceDialog<Project> dialog = new ChoiceDialog<>(choices.get(0), choices);
                dialog.setTitle("Choose a project to remove");
                dialog.setContentText("Choose project (Times selected):");

                Optional<Project> result = dialog.showAndWait();
                if (result.isPresent()) {
                    reader.removePreference(result.get());
                    for (Node n : hbox.getChildren()) {
                        if (n.getUserData().equals(result.get())) {
                            hbox.getChildren().remove(n);
                            break;
                        }
                    }
                }
            }
        });

        contextMenu.getItems().addAll(add, remove);
        this.setContextMenu(contextMenu);

        setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
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

                    success = controller.getModel().movePreference(readerToAdd, readerToRemoveFrom, projectToMove);
                    if (success) {
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
                        hbox.getChildren().add(new PreferenceLabel(projectToAdd, controller));
                    } else {
                        createErrorDialog();
                    }

                }
                event.setDropCompleted(success);
                event.consume();
            }

        });

    }

}
