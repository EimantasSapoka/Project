/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import model.MCMFModel;
import model.Reader;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author Eimantas
 */
public class FXMLDocumentController implements Initializable, Controller {

    MCMFModel model;

    @FXML
    private MenuBar menuBar;

    @FXML
    private CheckBox zeroCapacityReaderCheckbox;
    
    @FXML
    TableView<TableObjectInterface> tableView;
    
    
    /******************    /
     METHODS
    /*********************/

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        zeroCapacityReaderCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                zeroCapacityReaderCheckbox.setSelected(newValue);
                 ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
                if (newValue){
                    items.addAll(model.getReaders());
                } else {
                    for (Reader r : model.getReaders()){
                        if (!(r.getCapacity() == 0)){
                            items.add(r);
                        }
                    }
                }
                tableView.setItems(null);
                tableView.setItems(items);
            }
        });
    }
    
    public boolean showZeroCapacityReaders(){
        return zeroCapacityReaderCheckbox.isSelected();
    }
    
    @FXML
    private void exitSystem(ActionEvent event) {
        ((Stage) menuBar.getScene().getWindow()).close();
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Input File");
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (file != null) {
            System.out.println(file);
            model = new MCMFModel(file);
            createTableViewFromGraph(model);
        }
    }

    
    @Override
    public MCMFModel getModel(){
        return model;
    }
    
    
    
    public void createTableViewFromGraph(MCMFModel model) {
        if (model.getGraph() == null) {
            System.out.println("Graph instance empty");
            return;
        }
     
        if (tableView.getColumns().isEmpty()){
            tableView.getColumns().add(createReaderColumn());
            tableView.getColumns().add(createCapacityColumn());
            tableView.getColumns().add(createPreferenceListSizeColumn());
            tableView.getColumns().add(createPreferencesColumn());
        }
        
        ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
        if (this.showZeroCapacityReaders()){
            items.addAll(model.getReaders());
        } else {
            for (Reader r : model.getReaders()){
                if (!(r.getCapacity() == 0)){
                    items.add(r);
                }
            }
        }
        
        tableView.setItems(items);
        tableView.setStyle(".table-row-cell {-fx-cell-size: 50px;}");
        tableView.setFixedCellSize(40);
        
        this.refreshTable();
       
    }
    
    /**
     * creates the reader column for the tableView.
     * @return table column
     */
    private TableColumn<TableObjectInterface, TableObjectInterface> createReaderColumn(){
        TableColumn<TableObjectInterface, TableObjectInterface> readerColumn = new TableColumn<TableObjectInterface, TableObjectInterface>("Reader name");
        readerColumn.setMinWidth(150);
        readerColumn.setMaxWidth(300);
        
        readerColumn.setCellValueFactory(new Callback<CellDataFeatures<TableObjectInterface, TableObjectInterface>, ObservableValue<TableObjectInterface>>() {
          @Override public ObservableValue<TableObjectInterface> call(CellDataFeatures<TableObjectInterface, TableObjectInterface> features) {
              Reader reader = (Reader) features.getValue();
              return new ReadOnlyObjectWrapper(reader.getName());
          }
        });
        return readerColumn;
    }
    
    /**
     * creates the preferences column and modifies so the cells contain lists
     * of preferences.
     * @return table column
     */
    private TableColumn<TableObjectInterface, TableObjectInterface> createPreferencesColumn(){
        TableColumn<TableObjectInterface, TableObjectInterface> preferencesColumn  = new TableColumn<TableObjectInterface, TableObjectInterface>("Preferences");
        preferencesColumn.setMinWidth(300);
        preferencesColumn.setCellValueFactory(new Callback<CellDataFeatures<TableObjectInterface, TableObjectInterface>, ObservableValue<TableObjectInterface>>() {
          @Override public ObservableValue<TableObjectInterface> call(CellDataFeatures<TableObjectInterface, TableObjectInterface> features) {
              return new ReadOnlyObjectWrapper(features.getValue());
          }
        });
        
        preferencesColumn.setCellFactory(new TableCellWithListFactory(this));
        return preferencesColumn;
    }
    
    private TableColumn<TableObjectInterface, TableObjectInterface> createCapacityColumn(){
        TableColumn<TableObjectInterface, TableObjectInterface> capacityColumn  = new TableColumn<TableObjectInterface, TableObjectInterface>("Cap");
        capacityColumn.setMinWidth(30);
        capacityColumn.setPrefWidth(50);
        capacityColumn.setMaxWidth(60);
        
        capacityColumn.setCellValueFactory(new Callback<CellDataFeatures<TableObjectInterface, TableObjectInterface>, ObservableValue<TableObjectInterface>>() {
          @Override public ObservableValue<TableObjectInterface> call(CellDataFeatures<TableObjectInterface, TableObjectInterface> features) {
              Reader reader = (Reader) features.getValue();
              return new ReadOnlyObjectWrapper(reader.getCapacity());
          }
        });
        
        return capacityColumn;
    }
    
     private TableColumn<TableObjectInterface, TableObjectInterface> createPreferenceListSizeColumn(){
        TableColumn<TableObjectInterface, TableObjectInterface> preferenceCountColumn  = new TableColumn<TableObjectInterface, TableObjectInterface>("#Pref");
        preferenceCountColumn.setMinWidth(30);
        preferenceCountColumn.setPrefWidth(50);
        preferenceCountColumn.setMaxWidth(60);
        
        preferenceCountColumn.setCellValueFactory(new Callback<CellDataFeatures<TableObjectInterface, TableObjectInterface>, ObservableValue<TableObjectInterface>>() {
          @Override public ObservableValue<TableObjectInterface> call(CellDataFeatures<TableObjectInterface, TableObjectInterface> features) {
              Reader reader = (Reader) features.getValue();
              return new ReadOnlyObjectWrapper(reader.getPreferences().size());
          }
        });
        
        return preferenceCountColumn;
    }
     
    public void refreshTable(){
        tableView.getColumns().get(0).setVisible(false);
        tableView.getColumns().get(0).setVisible(true);
    }

       
}
