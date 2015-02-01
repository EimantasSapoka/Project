/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;


import java.awt.Panel;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
                    VBox vbox = new VBox();
                    Label name = new Label("Name: " + project.getName());
                    name.setTextFill(Color.BLACK);
                    Label id = new Label("ID: " + project.getId());
                    id.setTextFill(Color.BLACK);
                    Label timesSelected = new Label("Times selected: " + project.getSelectedCount());
                    timesSelected.setTextFill(Color.BLACK);
                    vbox.getChildren().addAll(name, id, timesSelected);
                    vbox.setSpacing(3);
                    vbox.setPadding(new Insets(10,10,10,10));
                    pop.setContentNode(vbox);
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

                content.putString("test");
                db.setContent(content);
                
                controller.scaleTrashBin(1.5);
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

                Label sourceLabel = (Label) event.getGestureSource();
                HBox sourceHbox = (HBox) sourceLabel.getParent();
                Project projectToMove = (Project) sourceLabel.getUserData();
                Reader readerToRemoveFrom = (Reader) sourceHbox.getUserData();
                Project projectToPlaceBefore = project;
                HBox hbox = (HBox) label.getParent();
                Reader readerToAdd = (Reader) hbox.getUserData();

                int indexToPlace = controller.getModel().movePreference(readerToAdd, readerToRemoveFrom, projectToMove, projectToPlaceBefore);
                if (indexToPlace != -1) {
                    sourceHbox.getChildren().remove(sourceLabel);
                    hbox.getChildren().add(indexToPlace, sourceLabel);
                } else {
                    Dialogs.create()
                        .owner(label)
                        .title("Error")
                        .masthead("Cannot move preference")
                        .message("The reader already has project as preference!\nProject name: "
                                + projectToMove.getName() + ",\nID: " + projectToMove.getId())
                        .showError();
                }

                event.setDropCompleted(true);
                event.consume();
            }
        });
        
        label.setOnDragDone(new EventHandler<DragEvent>(){

            @Override
            public void handle(DragEvent event) {
                controller.scaleTrashBin(1.0);
            }
            
        });

    }

}
