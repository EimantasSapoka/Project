/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.columns;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import mcmfuserinterface.Controller;
import mcmfuserinterface.drag_drop_table.TableCellWithAssignmentsFactory;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;

/**
 *
 * @author Eimantas
 */
public class AssignedProjectsColumn extends TableColumn<TableObjectInterface, TableObjectInterface>{
    
    public AssignedProjectsColumn(String name, Controller contr){
        super(name);
        
        setMinWidth(300);
        
        setCellValueFactory(new Callback<CellDataFeatures<TableObjectInterface, TableObjectInterface>, ObservableValue<TableObjectInterface>>() {
          @Override public ObservableValue<TableObjectInterface> call(CellDataFeatures<TableObjectInterface, TableObjectInterface> features) {
              return new ReadOnlyObjectWrapper(features.getValue());
          }
        });
        
        setCellFactory(new TableCellWithAssignmentsFactory(contr));
    }
    
}
