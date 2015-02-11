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
            ScrollPane scrollPane = new DroppableScrollPane(controller);
            HBox hbox = new HBox();
            
            {
                hbox.setSpacing(10);
                scrollPane.setContent(hbox);
            }

            @Override
            public void updateItem(final TableObjectInterface reader, boolean empty) {
                if (!empty && reader != null) {
                    hbox.getChildren().clear();
                    
                    for (Project project : controller.getReaderList((Reader) reader)) {
                        Label label = controller.createLabel(project, controller);
                        hbox.getChildren().add(label);
                    }
                    hbox.setUserData(reader);
                    scrollPane.setContextMenu(controller.createContextMenu((Reader)reader,hbox));
                    this.setGraphic(scrollPane);
                }
            }
        };
    }
}
