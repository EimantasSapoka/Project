/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.columns;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;
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
        setMinWidth(350);
        setPrefWidth(450);
        setMaxWidth(2000);
        
        setCellValueFactory(features -> {
              return new ReadOnlyObjectWrapper(features.getValue());
        });
        
        setCellFactory(new TableCellWithListFactory(contr));
    }
    
}
