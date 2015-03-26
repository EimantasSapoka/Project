/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.components;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import org.controlsfx.control.PopOver;

/**
 * a label class which has a popover with the information about
 * the object when clicked. 
 * @author Eimantas
 */
public class PopLabel extends Label {

    final PopOver pop;
    final Label popText;

    public PopLabel(String msg) {
        super(msg);
        
        this.getStyleClass().add("myLabel");
        
        pop = new PopOver();
        popText = new Label(msg);

        this.setOnContextMenuRequested(event -> {
                    popText.setTextFill(Color.BLACK);
                    popText.setPadding(new Insets(10, 10, 10, 10));
                    pop.setContentNode(popText);
                    pop.show((Node) event.getTarget(), event.getScreenX() + 10, event.getScreenY());
                    event.consume();
        });
        

        this.setOnMouseExited(event -> {
            if (pop.isShowing()){
                pop.hide();
            }
        });

    }

    public void setPopText(String text) {
        this.popText.setText(text);
    }
}
