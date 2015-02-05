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
import org.controlsfx.control.PopOver;

/**
 *
 * @author Eimantas
 */
public class DragDropLabel extends Label {
    
    final PopOver pop;
    
    DragDropLabel(final Project project) {
        
        super(project.getId()+"");
        pop = new PopOver();
        
        this.setUserData(project);
        this.setTooltip(new Tooltip(project.getName()));

        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
        
        this.setOnMouseExited(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                pop.hide(Duration.seconds(1.5));
            }
        
        });

        this.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                
                HBox hbox = (HBox) getParent();
                hbox.getChildren().get(0).setVisible(true);
                
                content.putString("test");
                db.setContent(content);
            }
        });

        this.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        });

        this.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if ((event.getGestureSource() != this)) {
                    setText("\t" + getText());
                }
            }
        });

        this.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                setText(getText().trim());
            }
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
