/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.components;

import ford_fulkerson.model.Project;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import mcmfuserinterface.controllers.ControllerInterface;

/**
 *
 * @author Eimantas
 */
public class DragLabel extends PopLabel {

    public DragLabel(final Project project, final ControllerInterface controller) {
        super(project.getID() + "");

        this.setUserData(project);
        this.setTooltip(new Tooltip(project.getName()));

        this.setOnDragDetected(mouseEvent -> { 
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            HBox hbox = (HBox) getParent();
            hbox.getChildren().get(0).setVisible(true);

            content.putString(project.getName());
            db.setContent(content);
           
        });
        
    }
}
