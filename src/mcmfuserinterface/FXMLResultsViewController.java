/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.util.Callback;
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

    void setModel(MCMFModel model) {
        this.model = model;
        createTableFromModel();
        createUnselectedProjectsList();
    }
    
    private void createTableFromModel(){
        if (resultsTable.getColumns().isEmpty()){
            resultsTable.getColumns().add(new ReaderNameColumn("Reader name"));
            resultsTable.getColumns().add(new CapacityColumn("Cap"));
            resultsTable.getColumns().add(new AssignedProjectsCountColumn("#Assigned"));
            resultsTable.getColumns().add(new AssignedProjectsColumn("Projects Assigned", this));
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
        setTableRowFactory();
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
    
     /**
     * creates the table row factory which adds colors to the rows
     */
    private void setTableRowFactory() {
        resultsTable.setRowFactory(new Callback<TableView<TableObjectInterface>,TableRow<TableObjectInterface>>(){
            
            @Override
            public TableRow<TableObjectInterface> call(TableView<TableObjectInterface> param) {
                TableRow<TableObjectInterface> row = new TableRow<TableObjectInterface>(){
                    @Override
                    protected void updateItem(TableObjectInterface item, boolean empty) {
                        super.updateItem(item, empty); 
                        setUserData(item);
                        if (item != null){
                            
                            final Reader reader = (Reader) item;
                            reader.getPreferenceStringProperty().addListener(new ChangeListener<String>(){
                                
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                    if (getUserData()!= null){
                                        int cap = ((Reader) getUserData()).getCapacity();
                                        int assignedCount = Integer.parseInt(newValue);
                                        if (reader.equals(getUserData())){
                                            if (cap - 1 > assignedCount){
                                                setStyle("-fx-background-color: red;");
                                            } else if (cap > assignedCount){
                                                setStyle("-fx-background-color: orange;");
                                            } else {
                                                setStyle("");
                                            }
                                        } 
                                    }
                                }
                            });
                            int cap = ((Reader) getUserData()).getCapacity();
                            int assignedCount = reader.getAssignedProjectsFromGraph().size();
                            if (reader.equals(getUserData())){
                                if (cap - 1 > assignedCount){
                                    setStyle("-fx-background-color: red;");
                                } else if (cap > assignedCount){
                                    setStyle("-fx-background-color: orange;");
                                } else {
                                    setStyle("");
                                }
                            } 
                        }
                    }
                };
                return row;
            }
            
        });
    }
    
    @Override
    public void refreshTable() {
        resultsTable.getColumns().get(0).setVisible(false);
        resultsTable.getColumns().get(0).setVisible(true);
    }

    
    @Override
    public void refreshLowSelectedProjectList(){
        unselectedList.setVisible(false);
        unselectedList.setVisible(true);
    }
    /**
     * creates side list with lowest selected projects in ascending order
     */
    private void createUnselectedProjectsList() {
        
        ObservableList<Project> unselectedProjectList = FXCollections.observableArrayList();
        unselectedProjectList.addAll(model.getGraph().findUnassignedProjects());
        System.out.println(model);
        unselectedList.setItems(unselectedProjectList);
        unselectedList.setCellFactory(new Callback<ListView<Project>, ListCell<Project>>(){

            @Override
            public ListCell<Project> call(ListView<Project> param) {
                final ListCell<Project> listCell =  new ListCell<Project>(){

                    @Override
                    protected void updateItem(Project item, boolean empty) {
                        super.updateItem(item, empty); 
                        if (item != null){
                            setUserData(item);
                            setStyle("-fx-background-color: red;");
                            setTooltip(new Tooltip(item.getName()));
                            setText(item.getId()+"");
                        }
                    }               
                };
                
                listCell.setOnDragDetected(new EventHandler<MouseEvent>(){

                    @Override
                    public void handle(MouseEvent event) {
                        
                        Dragboard db = listCell.startDragAndDrop(TransferMode.COPY);
                        ClipboardContent content = new ClipboardContent();

                        content.putString("test");
                        db.setContent(content);
                    }            
                });
                return listCell;
            }
            
        });
        
       unselectedList.setOnDragDone(new EventHandler<DragEvent>(){
            @Override
            public void handle(DragEvent event) {
               refreshLowSelectedProjectList();
            }
           
       });
    }
}
