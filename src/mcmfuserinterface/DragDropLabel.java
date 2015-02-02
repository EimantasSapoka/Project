/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;


import java.awt.Toolkit;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import model.Project;
import model.Reader;
import org.controlsfx.control.PopOver;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author Eimantas
 */
public class DragDropLabel extends Label {
    final PopOver pop;
    final Project project;
    final Label label;
    final private Controller controller;
    
   

    DragDropLabel(Project proj, Controller contr) {
        super(proj.getId()+"");
        
        this.project = proj;
        pop = new PopOver();
        label = this;
        this.controller = contr;
        
        this.setUserData(project);
        this.setTooltip(new Tooltip(project.getName()));

        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY){
                    Label name = new Label("Name: " + project.getName() +
                                           "\nID: " + project.getId() +
                                           "\nTimes selected: " + project.getSelectedCount());
                    name.setTextFill(Color.BLACK);
                    name.setPadding(new Insets(10,10,10,10));
                    pop.setContentNode(name);
                    pop.show((Node) event.getTarget(), event.getScreenX()+10, event.getScreenY());
                }
                
            }

        });
        
        label.setOnMouseExited(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                pop.hide(Duration.seconds(1.5));
            }
        
        });

        label.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Dragboard db = label.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                
                HBox hbox = (HBox) label.getParent();
                hbox.getChildren().get(0).setVisible(true);
                
                content.putString("test");
                db.setContent(content);
            }
        });

        label.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        });

        label.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ((event.getGestureSource() != label)) {
                    label.setOpacity(0.5);
                    label.setText("\t" + label.getText());
                }
            }
        });

        label.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                label.setText(label.getText().trim());
                label.setOpacity(1);
            }
        });

        label.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                HBox hbox = (HBox) label.getParent();
                Project projectToPlaceBefore = project;
                Reader readerToAdd = (Reader) hbox.getUserData();
                int indexToPlace;

                if (event.getTransferMode() == TransferMode.MOVE){
                    Label sourceLabel = (Label) event.getGestureSource();
                    HBox sourceHbox = (HBox) sourceLabel.getParent();
                    Project projectToMove = (Project) sourceLabel.getUserData();
                    Reader readerToRemoveFrom = (Reader) sourceHbox.getUserData();

                    indexToPlace = controller.getModel().movePreference(readerToAdd, readerToRemoveFrom, projectToMove, projectToPlaceBefore);
                    if (indexToPlace != -1) {
                        sourceHbox.getChildren().remove(sourceLabel);
                        hbox.getChildren().add(indexToPlace, sourceLabel);
                    } else {
                        createErrorDialog(projectToMove);
                    }
                } else {
                    ListCell listCell = (ListCell) event.getGestureSource();
                    Project projectToAdd = (Project) listCell.getUserData();
                    
                    indexToPlace = controller.getModel().addProjectToReaderPreferences(readerToAdd, projectToAdd, projectToPlaceBefore);
                    if (indexToPlace != -1) {
                        hbox.getChildren().add(indexToPlace, new DragDropLabel(projectToAdd, controller));
                    } else {
                        createErrorDialog(projectToAdd);
                    }
                    
                }
                
                event.setDropCompleted(indexToPlace != -1);
                event.consume();
            }

           
        });

    }
    
    
    private void createErrorDialog(Project projectToAdd) {
        final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
        if (runnable != null) {
            runnable.run();
        }
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot move preference");
        alert.setContentText("The reader already has project as preference!\nProject name: "
                        + projectToAdd.getName() + ",\nID: " + projectToAdd.getId());
        alert.showAndWait();
    }
}
