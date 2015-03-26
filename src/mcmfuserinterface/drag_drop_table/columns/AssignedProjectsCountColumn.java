/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.columns;

import javafx.scene.control.TableColumn;
import ford_fulkerson.model.Reader;

/**
 * 
 * @author Eimantas
 */
public class AssignedProjectsCountColumn extends TableColumn<Reader, Integer> {

	public AssignedProjectsCountColumn(String name) {
        super(name);

        setMinWidth(40);
        setPrefWidth(50);
        setMaxWidth(60);

        setCellValueFactory(features -> {
            return features.getValue().getAssignedCountProperty().asObject();
        });

    }

}
