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
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import model.Reader;

/**
 *
 * @author Eimantas
 * @param <TableObjectInterface>
 */
public class ReaderNameColumn extends TableColumn<TableObjectInterface, TableObjectInterface> {
    
    public ReaderNameColumn(String name){
        super(name);
        setMinWidth(150);
        setMaxWidth(300);
        
       setCellValueFactory(new Callback<CellDataFeatures<TableObjectInterface, TableObjectInterface>, ObservableValue<TableObjectInterface>>() {
          @Override public ObservableValue<TableObjectInterface> call(CellDataFeatures<TableObjectInterface, TableObjectInterface> features) {
              Reader reader = (Reader) features.getValue();
              return new ReadOnlyObjectWrapper(reader.getName());
          }
        });
    }
    
}