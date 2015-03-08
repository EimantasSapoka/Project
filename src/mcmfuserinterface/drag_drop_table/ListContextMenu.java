/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import mcmfuserinterface.ControllerInterface;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class ListContextMenu extends ContextMenu {

    final private Reader reader;
    final private ControllerInterface controller;
    final private HBox hbox;

    public ListContextMenu(final Reader reader, final ControllerInterface controller, final Node container) {
        super();

        this.reader = reader;
        this.controller = controller;
        hbox = (HBox) container;
    }

    /**
     * inserts the "Remove..." Option into the context menu
     *
     * @param controller1
     * @param reader1
     */
    public void includeRemoveButton() {
        MenuItem remove = new MenuItem("Remove..");
        remove.setOnAction(e -> {
            if (controller.getReaderList(reader).isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Reader does not have any preferences");
                alert.showAndWait();
                return;
            }
            List<Project> choices = new ArrayList<Project>();
            choices.addAll(controller.getReaderList(reader));
            ChoiceDialog<Project> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Choose a project to remove");
            dialog.setContentText("Choose project (ID):");
            Optional<Project> result = dialog.showAndWait();
            if (result.isPresent()) {
                controller.removeProjectFromReader(reader, result.get());
                for (Node n : hbox.getChildren()) {
                    if (n.getUserData().equals(result.get())) {
                        hbox.getChildren().remove(n);
                        break;
                    }
                }
                controller.refreshLowSelectedProjectList();
            }
        });
        getItems().add(remove);
    }

    /**
     * inserts the "Add.." option into the contextMenu
     */
    public void includeAddButton() {
        MenuItem add = new MenuItem("Add..");
        add.setOnAction(e -> {

            if (reader.getCapacity() == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Cannot add preference!");
                alert.setContentText("Reader has capacity of zero!");
                alert.showAndWait();
                return;
            }

            List<Project> choices = new ArrayList<Project>();
            choices.addAll(controller.getProjects());
            choices.removeAll(controller.getReaderList(reader));
            for (int i = 0; i< choices.size(); i++){
            	if(reader.getSupervisorProjects().contains(choices.get(i).getId())){
            		choices.remove(i--);
            	}
            }
            choices.sort(null);
            if (choices.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Reader has no projects he can get added");
                alert.showAndWait();
                return;
            }
            ChoiceDialog<Project> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Choose a project to add");
            dialog.setContentText("Choose project (ID):");

            Optional<Project> result = dialog.showAndWait();
            if (result.isPresent()) {
                controller.addProjectToReader(reader, result.get());
                hbox.getChildren().add(new DragDropLabel(result.get(), controller));
                controller.refreshLowSelectedProjectList();
            }
        });
        getItems().add(add);
    }

}
