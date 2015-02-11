/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

/**
 *
 * @author Eimantas
 */
public class PopLabel extends Label {

    final PopOver pop;
    final Label popText;

    public PopLabel(String msg) {
        super(msg);

        pop = new PopOver();
        popText = new Label();

        this.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    popText.setTextFill(Color.BLACK);
                    popText.setPadding(new Insets(10, 10, 10, 10));
                    pop.setContentNode(popText);
                    pop.show((Node) event.getTarget(), event.getScreenX() + 10, event.getScreenY());
                }
        });

        this.setOnMouseExited(event -> {
                pop.hide(Duration.seconds(1.5));
        });

    }

    public void setPopText(String text) {
        this.popText.setText(text);
    }
}
