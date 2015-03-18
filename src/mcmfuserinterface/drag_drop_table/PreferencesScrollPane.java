/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;

import java.awt.Toolkit;

import javafx.scene.control.Alert;
import mcmfuserinterface.ControllerInterface;

/**
 *
 * @author Eimantas
 */
public class PreferencesScrollPane extends DroppableScrollPane {

    public PreferencesScrollPane(final ControllerInterface controller) {
        super(controller);

    }
    
  protected void createErrorDialog() {
        final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
        if (runnable != null) {
            runnable.run();
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot move!");
        alert.setContentText("The reader already has the project!");
        alert.showAndWait();
        
    }

}
