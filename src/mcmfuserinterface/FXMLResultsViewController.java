/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import mcmfuserinterface.drag_drop_table.DragLabel;
import mcmfuserinterface.drag_drop_table.ListContextMenu;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import mcmfuserinterface.drag_drop_table.columns.AssignedProjectsCountColumn;
import mcmfuserinterface.drag_drop_table.columns.CapacityColumn;
import mcmfuserinterface.drag_drop_table.columns.ListColumn;
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
    Button infoButton;
    
    @FXML
    Button cancelButton;
    
    @FXML
    TableView<TableObjectInterface> resultsTable;
    
    @FXML
    ListView<Project> unselectedList; 
    
    @FXML
    CheckBox zeroCapacityReaderCheckbox;
    
    private  BarChart<String,Number> barChart;
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    @FXML
    private void export(){
         FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save file");
            File file = fileChooser.showSaveDialog(resultsTable.getScene().getWindow());
            if (file != null) {
                try {
                    PrintWriter writer = new PrintWriter(file, "UTF-8");
                    writer.print(model.toString());
                    writer.close();
                } catch (IOException ex) {
                    Alert alert = new ExceptionDialog(ex);
                    alert.showAndWait();
                }
            }
    }
    
    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage) cancelButton.getScene().getWindow()).close();
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
            resultsTable.getColumns().add(new ListColumn("Projects Assigned", this));
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
    
    @FXML
    private void showInfo(){
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Information on assignments:");
        VBox box = new VBox();
        box.getChildren().add(new Label("Load balanced? : " + model.isLoadBalanced() + 
                                        "\tSaturating flow? : " + model.getGraph().isSaturatingFlow()));
        
        HBox hbox = new HBox();
        hbox.getChildren().add(new Label("Flow: " + model.getGraph().getFlow() +
                                        "\nWeight: " + model.getGraph().getWeight()));
        
        hbox.getChildren().add(new Label(String.format("Readers: %d\nProjects: %d\nAvg. reader target: %.2f", 
                model.getReaders().size(),model.getProjects().size(), model.getAverageReaderCapacity())));
        hbox.setSpacing(40);
        box.getChildren().add(hbox);
        
        Map<Integer, Integer> preferenceStatistics = calculatePreferenceAssignmentStatistics();
        box.getChildren().add(createPreferenceAssignedStatisticsTable(preferenceStatistics));
        if (barChart == null){
            createBarChart();
        } else {
            barChart.getData().clear();
        }
        XYChart.Series series1 = new XYChart.Series();    
        for (int preference : preferenceStatistics.keySet()){
            series1.getData().add(new XYChart.Data(preference+"", preferenceStatistics.get(preference)));
        }
        
        barChart.getData().add(series1);
        box.getChildren().add(barChart);
        box.setSpacing(20);
        info.getDialogPane().setContent(box);
        info.showAndWait();
    }

    private void createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        barChart = new BarChart<String,Number>(xAxis,yAxis);
        xAxis.setLabel("Preference");
        yAxis.setLabel("Projects assigned");
    }

    public GridPane createPreferenceAssignedStatisticsTable(Map<Integer, Integer> preferenceStatistics) {
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);
        grid.add(new Label("Preference:"), 0, 0);
        grid.add(new Label("# assigned"), 0, 1);
        grid.getColumnConstraints().add(new ColumnConstraints(100));
        int column = 1;
        for (int pref : preferenceStatistics.keySet()){
            grid.getColumnConstraints().add(new ColumnConstraints(30));
            grid.add(new Label("  "+ pref+""), column, 0);
            grid.add(new Label("  "+preferenceStatistics.get(pref)+""), column++, 1);
        }
        return grid;
    }

    private Map<Integer, Integer> calculatePreferenceAssignmentStatistics() {
        Map<Integer, Integer> preferenceToCount = new HashMap<Integer,Integer>();
        for (Reader reader: model.getReaders()){
            for (Project assigned : reader.getAssigned()){
                int preference = reader.getPreferences().indexOf(assigned) + 1;
                if (preferenceToCount.containsKey(preference)){
                    preferenceToCount.replace(preference, preferenceToCount.get(preference) + 1);
                } else {
                    preferenceToCount.put(preference, 1);
                }
            }
        }
        return preferenceToCount;
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
                            reader.getAssignedCountStringProperty().addListener(new ChangeListener<String>(){
                                
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
        ObservableList<Project> unselectedProjectList = FXCollections.observableArrayList();
        unselectedProjectList.addAll(model.getUnselectedProjects());
        unselectedList.setItems(unselectedProjectList);
        unselectedList.setVisible(false);
        unselectedList.setVisible(true);
    }
    /**
     * creates side list with lowest selected projects in ascending order
     */
    private void createUnselectedProjectsList() {
        
        ObservableList<Project> unselectedProjectList = FXCollections.observableArrayList();
        unselectedProjectList.addAll(model.getUnselectedProjects());
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

    @Override
    public int moveProject(Reader reader, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore) {
        return model.moveAssignedProject(reader, readerToRemoveFrom, projectToMove, projectToPlaceBefore);
    }

    @Override
    public boolean moveProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove) {
        return model.moveAssignedProject(readerToAdd, readerToRemoveFrom, projectToMove);    
    }

    @Override
    public boolean addProjectToReader(Reader reader, Project projectToAdd) {
        return model.assignProjectToReader(reader, projectToAdd);
    }

    @Override
    public int addProjectToReader(Reader reader, Project projectToAdd, Project projectToAddBefore) {
        return model.assignProjectToReader(reader, projectToAdd, projectToAddBefore);
    }

    @Override
    public ContextMenu createContextMenu(Reader reader, Node container) {
        ListContextMenu menu = new ListContextMenu(reader, this, container);
        menu.includeRemoveButton();
        return menu;
    }

    @Override
    public Collection<Project> getProjects() {
        return model.getProjects();
    }

    @Override
    public Collection<Project> getReaderList(Reader reader) {
        return reader.getAssigned();
    }

    @Override
    public void removeProjectFromReader(Reader reader, Project project) {
        reader.removeAssignedProject(project);
    }

    @Override
    public Label createLabel(Project project, Controller controller) {
        DragLabel label = new DragLabel(project, controller);
        label.setPopText( "Name: " + project.getName() +
                          "\nID: " + project.getId() +
                          "\nTimes selected: " + project.getSelectedCount());
        return label;
    }
}
