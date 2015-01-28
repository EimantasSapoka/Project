/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import model.MCMFModel;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class TableCellWithListFactory implements Callback<TableColumn<TableObjectInterface, TableObjectInterface>, TableCell<TableObjectInterface, TableObjectInterface>> {
    private final MCMFModel model;
    private final Controller controller;
    
    public TableCellWithListFactory(MCMFModel model, Controller controller) {
        this.model = model;
        this.controller = controller;
    }


    @Override 
    public TableCell<TableObjectInterface, TableObjectInterface> call(TableColumn<TableObjectInterface, TableObjectInterface> btnCol) {
        return new TableCell<TableObjectInterface, TableObjectInterface>() {
          final ListView<TableObjectInterface> list = new ListView<TableObjectInterface>(); {
             list.setOrientation(Orientation.HORIZONTAL);
             list.setCellFactory(new ListCellFactory(model, controller));
             list.setMaxHeight(50);
             
          }
          final ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
          @Override public void updateItem(final TableObjectInterface reader, boolean empty) {
            super.updateItem(reader, empty);
            if (!empty && reader != null && items.size() == 0){
                
                items.addAll(((Reader) reader).getPreferences());
                list.setItems(items);
                this.setGraphic(list);
            } else {
                list.setItems(items);
            }
          }
        };
      }
}
