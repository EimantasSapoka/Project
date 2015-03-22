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
 * @param <Reader>
 */
public class ReaderNameColumn extends TableColumn<Reader, Reader> {
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public ReaderNameColumn(String name){
        super(name);
        setMinWidth(150);
        setMaxWidth(300);
       
       setCellValueFactory(features -> {
              Reader reader = (Reader) features.getValue();
              return new ReadOnlyObjectWrapper(reader.getName());
        });
    }
    
}
