/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import mcmfuserinterface.drag_drop_table.columns.AssignedProjectsColumn;
import mcmfuserinterface.drag_drop_table.columns.AssignedProjectsCountColumn;
import mcmfuserinterface.drag_drop_table.columns.CapacityColumn;
import mcmfuserinterface.drag_drop_table.columns.ReaderNameColumn;
import model.MCMFModel;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class FXMLResultsViewController implements Initializable, Controller{
    private MCMFModel model;
    
    @FXML
    Button cancelButton;
    
    @FXML
    TableView<TableObjectInterface> resultsTable;
    
    @FXML
    ListView<Project> unselectedList; 
    
    @FXML
    CheckBox zeroCapacityReaderCheckbox;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @Override
    public MCMFModel getModel() {
        return this.model;
    }

    @Override
    public void refreshTable() {
        resultsTable.getColumns().get(0).setVisible(false);
        resultsTable.getColumns().get(0).setVisible(true);
    }

    @Override
    public void refreshLowSelectedProjectList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void setModel(MCMFModel model) {
        this.model = model;
        createTableFromModel();
    }
    
    private void createTableFromModel(){
        if (resultsTable.getColumns().isEmpty()){
            resultsTable.getColumns().add(new ReaderNameColumn("Reader name"));
            resultsTable.getColumns().add(new CapacityColumn("Cap"));
            resultsTable.getColumns().add(new AssignedProjectsCountColumn("#Assigned"));
            resultsTable.getColumns().add(new AssignedProjectsColumn("Projects Assigned"));
        }
      
        ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
        if (zeroCapacityReaderCheckbox.isSelected()){
            items.addAll(model.getReaders());
        } else {
            for (Reader r : model.getReaders()){
                if (!(r.getCapacity() == 0)){
                    items.add(r);
                }
            }
        }
        
        refreshTable();
        resultsTable.setItems(items);
        resultsTable.setFixedCellSize(40);
//        setTableRowFactory();
    }
    
    
    
    /**
     * changes between showing and hiding readers with zero capacity
     */
    @FXML
    private void toggleShowZeroCapacityReaders() {
        ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
        if (zeroCapacityReaderCheckbox.isSelected()){
            items.addAll(model.getReaders());
        } else {
            for (Reader r : model.getReaders()){
                if (!(r.getCapacity() == 0)){
                    items.add(r);
                }
            }
        }
        resultsTable.setItems(null);
        resultsTable.setItems(items);
    }
}
