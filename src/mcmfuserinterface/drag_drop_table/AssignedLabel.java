/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import mcmfuserinterface.Controller;
import model.Project;

/**
 *
 * @author Eimantas
 */
public class AssignedLabel extends DragDropLabel{
    
    public AssignedLabel(Project proj, Controller contr){
        super(proj);
        this.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                System.out.println("dropped");
            }
        });
    }
    
}
