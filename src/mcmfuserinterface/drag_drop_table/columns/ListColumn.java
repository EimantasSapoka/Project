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
import mcmfuserinterface.ControllerInterface;
import mcmfuserinterface.drag_drop_table.TableCellWithListFactory;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;

/**
 *
 * @author Eimantas
 */
public class ListColumn  extends TableColumn<TableObjectInterface, TableObjectInterface>{
    
    public ListColumn(String name, ControllerInterface contr){
        super(name);
        setMinWidth(450);
        setPrefWidth(750);
        setMaxWidth(1000);
        
        setCellValueFactory(features -> {
              return new ReadOnlyObjectWrapper(features.getValue());
        });
        
        setCellFactory(new TableCellWithListFactory(contr));
    }
    
}
