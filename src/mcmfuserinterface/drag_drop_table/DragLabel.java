/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import mcmfuserinterface.ControllerInterface;
import model.Project;
import org.controlsfx.control.PopOver;

/**
 *
 * @author Eimantas
 */
public class DragLabel extends Label{
    
    final PopOver pop;
    final Label popText;
    
    public DragLabel(final Project project, final ControllerInterface controller){
        super(project.getId() + "");
        
        pop = new PopOver();
        popText = new Label();
        
        this.setUserData(project);
        this.setTooltip(new Tooltip(project.getName()));

        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY){
                    popText.setTextFill(Color.BLACK);
                    popText.setPadding(new Insets(10,10,10,10));
                    pop.setContentNode(popText);
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
    }
    
    public void setPopText(String text){
        this.popText.setText(text);
    }
}
