/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.columns;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;
import ford_fulkerson.model.Reader;

/**
 *
 * @author Eimantas
 */
public class CapacityColumn extends TableColumn<Reader, Integer>{
    
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
