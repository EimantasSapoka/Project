/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.columns;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import ford_fulkerson.model.Reader;

/**
 *
 * @author Eimantas
 * @param <Reader>
 */
public class ReaderNameColumn extends TableColumn<Reader, String> {
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public ReaderNameColumn(String name){
        super(name);
        setMinWidth(150);
        setMaxWidth(300);
       
       setCellValueFactory(features -> {
    	   	  setEditable(true);
              Reader reader = (Reader) features.getValue();
              return new ReadOnlyObjectWrapper(reader.getName());
        });
       
       setCellFactory(TextFieldTableCell.forTableColumn());
       setOnEditCommit(
           new EventHandler<CellEditEvent<Reader, String>>() {
               @Override
               public void handle(CellEditEvent<Reader, String> t) {
            	   t.getRowValue().setName(t.getNewValue());
               }
           }
       );
    }
    
}
