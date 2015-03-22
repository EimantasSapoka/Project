/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.columns;

import ford_fulkerson.model.Reader;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;

/**
 *
 * @author Eimantas
 */
public class CapacityColumn extends TableColumn<TableObjectInterface, TableObjectInterface>{
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public CapacityColumn(String name){
        super(name);
        setMinWidth(30);
        setPrefWidth(45);
        setMaxWidth(60);
        
        setCellValueFactory(features -> {
              Reader reader = (Reader) features.getValue();
              return new ReadOnlyObjectWrapper(reader.getMarkingTarget());
        });
        
    }
    
}
