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
import mcmfuserinterface.Controller;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class TableCellWithAssignmentsFactory implements Callback<TableColumn<TableObjectInterface, TableObjectInterface>, TableCell<TableObjectInterface, TableObjectInterface>> {
    private final Controller controller;
    
    public TableCellWithAssignmentsFactory(Controller controller) {
        this.controller = controller;
        
    }

    @Override
    public TableCell<TableObjectInterface, TableObjectInterface> call(TableColumn<TableObjectInterface, TableObjectInterface> btnCol) {
        return new TableCell<TableObjectInterface, TableObjectInterface>() {
            ScrollPane scrollPane = new AssignmentsScrollPane(controller);
            HBox hbox = new HBox();
            
            {
                hbox.setSpacing(10);
                scrollPane.setContent(hbox);
            }

            @Override
            public void updateItem(final TableObjectInterface reader, boolean empty) {
                if (!empty && reader != null) {
                    hbox.getChildren().clear();
                    
                    for (Project project : ((Reader) reader).getFinalisedAssignedProjectList()) {
                        Label label = new PreferenceLabel(project, controller);
                        hbox.getChildren().add(label);
                    }
                    hbox.setUserData(reader);
                    this.setGraphic(scrollPane);
                }
            }
        };
    }
}
