/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import model.MCMFModel;
import model.Project;
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
            ScrollPane scrollPane = new ScrollPane();
            HBox hbox = new HBox();
            
            @Override 
            public void updateItem(final TableObjectInterface reader, boolean empty) {
                
                  if (!empty && reader != null){
                    hbox.getChildren().clear();
                    for (Project project : ((Reader) reader).getPreferences()){
                        Label label = new Label(project.toString());
                        label.setUserData(project);
                        hbox.getChildren().add(label);
                        hbox.setSpacing(10);
                    } 
                      
                     
                      
                      scrollPane.setContent(hbox);
                      this.setGraphic(scrollPane);
                  } 
            }
        };
      }
}
