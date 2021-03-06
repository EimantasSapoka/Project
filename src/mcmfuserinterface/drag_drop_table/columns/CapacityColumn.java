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
 * a capacity column, which is editable. 
 * @author Eimantas
 */
public class CapacityColumn extends TableColumn<Reader, String>{
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public CapacityColumn(String name){
        super(name);
        setMinWidth(30);
        setPrefWidth(45);
        setMaxWidth(60);
        
        setCellValueFactory(features -> {
        	  setEditable(true);
              Reader reader = (Reader) features.getValue();
              return new ReadOnlyObjectWrapper(reader.getReaderTarget()+"");
        });
        
        setCellFactory(TextFieldTableCell.forTableColumn());
        
        setOnEditCommit(
            new EventHandler<CellEditEvent<Reader, String>>() {
                @Override
                public void handle(CellEditEvent<Reader, String> t) {
                	String newValue = t.getNewValue();
                	// if new value is not a two digit integer, do not modify anything
					if (newValue.matches("\\d{1,2}")){
                		int newMarkingTarget = Integer.parseInt(newValue);
                		t.getRowValue().setReaderTarget(newMarkingTarget);
                	}
					// refresh table 
					t.getTableColumn().setVisible(false);
					t.getTableColumn().setVisible(true);
                }
            }
        );
        
    }
    
}
