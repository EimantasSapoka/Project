/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import mcmfuserinterface.ControllerInterface;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class TableCellWithListFactory implements Callback<TableColumn<TableObjectInterface, TableObjectInterface>, TableCell<TableObjectInterface, TableObjectInterface>> {
    private final ControllerInterface controller;
    
    public TableCellWithListFactory(ControllerInterface controller) {
        this.controller = controller;
    }

    @Override
    public TableCell<TableObjectInterface, TableObjectInterface> call(TableColumn<TableObjectInterface, TableObjectInterface> btnCol) {
        return new TableCell<TableObjectInterface, TableObjectInterface>() {
            ScrollPane scrollPane;
            HBox hbox;
    
            @Override
            public void updateItem(final TableObjectInterface object, boolean empty) {
                if(!empty && object != null){
                	
                    scrollPane = new DroppableScrollPane(controller);
                    hbox = new HBox();
                    hbox.setSpacing(10);
                    scrollPane.setContent(hbox);
                    
                    Reader reader = (Reader) object;
                    for (Project project : controller.getReaderList(reader)) {
                        Label label = controller.createLabel(reader, project);
                        
                        hbox.getChildren().add(label);
                    }
                    hbox.setUserData(object);
                    scrollPane.setContextMenu(controller.createContextMenu((Reader)object,hbox));
                    this.setGraphic(scrollPane);
                } else {
                    setGraphic(null);
                }
            }
        };
    }
}
