/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class TableCellWithListFactory implements Callback<TableColumn<TableObjectInterface, TableObjectInterface>, TableCell<TableObjectInterface, TableObjectInterface>> {
    private final Controller controller;

    public TableCellWithListFactory(Controller controller) {
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
                    
                    for (Project project : ((Reader) reader).getPreferences()) {
                        Label label = new DragDropLabel(project.getId()+"", controller);
                        label.setUserData(project);
                        label.setTooltip(new Tooltip(project.toString()));
                        hbox.getChildren().add(label);
                    }
                    hbox.setUserData(reader);
                    this.setGraphic(scrollPane);
                }
            }
        };
    }
}