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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import mcmfuserinterface.drag_drop_table.DragLabel;
import mcmfuserinterface.drag_drop_table.ListContextMenu;
import mcmfuserinterface.drag_drop_table.PopLabel;
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
public class ResultsViewController extends ViewController{
    
    @FXML
    Button infoButton;
    
    @FXML
    Button cancelButton;
    
    @FXML
    ListView<Project> unselectedList;
    
    @FXML
    CheckBox preferencesCheckBox;
    
    private  BarChart<String,Number> barChart;
    
    
    
    @Override
    public void initialize() {
    }
    
    @FXML
    private void export(){
        String output = createOutput();
        if (output != null){
             FileChooser fileChooser = new FileChooser();
            File desktopFolder = new File(MainViewController.DESKTOP_DIRECTORY);
            if (desktopFolder.exists()){
            	fileChooser.setInitialDirectory(desktopFolder);
            }
            fileChooser.setTitle("Save file");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("text files", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(table.getScene().getWindow());
            if (file != null) {
                try {
                    PrintWriter writer = new PrintWriter(file, "UTF-8");
                    writer.print(output);
                    writer.close();
                } catch (IOException ex) {
                    DialogUtils.createExceptionDialog(ex);
                }
            }
        }
        
    }
    
    private String createOutput() {
        Optional<List<String>> result =  createParameterDialogWindow();
        if (result.isPresent() && result.get() != null){
            List<String> parameters = result.get();
            return createOutputText(parameters);
        }
        return null;
    }

    private String createOutputText(List<String> parameters) {
        String resultString = "";
        for (Reader reader : model.getReaders()){
            if (parameters.contains("ids")){
                resultString += reader.getID() + ", ";
            }
            if(parameters.contains("names")){
                resultString += reader.getName() +", ";
            }
            if(parameters.contains("cap")){
                resultString += reader.getAssigned().size()+"/"+reader.getCapacity()+", ";
            }
            if (parameters.contains("supervised")){
                for (Integer project: reader.getSupervisorProjects()){
                    resultString+= project + " ";
                }
                resultString += ", ";
            }
            for (Project project : reader.getAssigned()){
                resultString += project.getId()+" ";
            }
            resultString+="\n";
        }
        return resultString;
    }

    private Optional<List<String>> createParameterDialogWindow() {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Choose export format");
        dialog.setHeaderText("Select parameters ");
        ButtonType exportButton = new ButtonType("Export", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(exportButton, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        CheckBox readerNames = new CheckBox("reader names");
        CheckBox readerID = new CheckBox("reader IDs");
        CheckBox readerSupervisedProjects = new CheckBox("reader supervised projects");
        CheckBox capacityAssigned = new CheckBox("assigned/Capacity");
        grid.add(readerNames, 1, 0);
        grid.add(readerID, 1, 1);
        grid.add(readerSupervisedProjects, 1, 2);
        grid.add(capacityAssigned, 1, 3);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if ( dialogButton == exportButton) {
                List<String> parameters = new ArrayList<String>();
                 if (readerID.isSelected()){
                    parameters.add("ids");
                }
                 if (readerNames.isSelected()){
                    parameters.add("names");
                }
                 if(capacityAssigned.isSelected()){
                    parameters.add("cap");
                }
               
                if(readerSupervisedProjects.isSelected()){
                    parameters.add("supervised");
                }
                
                return parameters;
            }
            return null;
        });
        return dialog.showAndWait();
    }
    
    @FXML
    private void showPreferencesToggle(){
        refreshTable();
    }
    
    void setModel(MCMFModel model) {
        this.model = model;
        createTableFromModel();
        createUnselectedProjectsList();
    }
    
    @Override
    protected void createTableColumns() {
        table.getColumns().add(new ReaderNameColumn("Reader name"));
        table.getColumns().add(new CapacityColumn("Cap"));
        table.getColumns().add(new AssignedProjectsCountColumn("#Assigned"));
        table.getColumns().add(new ListColumn("Projects Assigned", this));
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
        barChart.setTitle("How many readers got which preference");
        xAxis.setLabel("Reader preference");
        yAxis.setLabel("How many readers got it");
        yAxis.setTickUnit(1.0);
        yAxis.setMinorTickVisible(false);
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
                int preference = reader.getPreferences().indexOf(assigned);
                preference += preference == -1? reader.getPreferences().size()+2:1;
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
     * creates the table row factory which adds colors to the rows
     */
    @Override
    protected void setTableRowFactory() {
        table.setRowFactory(param -> {
                TableRow<TableObjectInterface> row = new TableRow<TableObjectInterface>(){
                    @Override
                    protected void updateItem(TableObjectInterface item, boolean empty) {
                        super.updateItem(item, empty); 
                        setUserData(item);
                        if (item != null){
                            
                            final Reader reader = (Reader) item;
                            reader.getAssignedCountStringProperty().addListener( (observable, oldValue,  newValue) -> {
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
                                    } else {
                                        setStyle("");
                                    }
                                });
                            int cap = ((Reader) getUserData()).getCapacity();
                            int assignedCount = reader.getAssigned().size();
                            if (reader.equals(getUserData())){
                                if (cap - 1 > assignedCount){
                                    setStyle("-fx-background-color: red;");
                                } else if (cap > assignedCount){
                                    setStyle("-fx-background-color: orange;");
                                } else {
                                    setStyle("");
                                }
                            } 
                        } else {
                            setStyle("");
                        }
                    }
                };
                return row;
            });
    }
    
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
        unselectedList.setCellFactory(param -> {
                final ListCell<Project> listCell =  new ListCell<Project>(){

                    @Override
                    protected void updateItem(Project item, boolean empty) {
                        super.updateItem(item, empty); 
                        if (item != null){
                            setUserData(item);
                            setStyle("-fx-background-color: red;");
                            setTooltip(new Tooltip(item.getName()));
                            setText(item.getId()+"");
                        } else {
                            setText("");
                            setVisible(false);
                            setStyle("");
                            setUserData(null);
                            setGraphic(null);
                        }
                    }               
                };
                
                listCell.setOnDragDetected(event -> {
                    if (listCell.getUserData() == null){
                        return;
                    }
                    Dragboard db = listCell.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent content = new ClipboardContent();

                    content.putString("test");
                    db.setContent(content);
                });
            return listCell;
        });

       unselectedList.setOnDragDone(event -> {
               refreshLowSelectedProjectList();
               if (preferencesCheckBox.isSelected()){
                   refresh();
               }
            });
       unselectedList.setOnDragOver(event ->{
           event.acceptTransferModes(TransferMode.MOVE);
       });
       unselectedList.setOnDragDropped(event -> {
            if (event.getAcceptedTransferMode().equals(TransferMode.MOVE)){
                Label sourceLabel = (Label) event.getGestureSource();
                HBox sourceHbox = (HBox) sourceLabel.getParent();
                Project projectToMove = (Project) sourceLabel.getUserData();
                Reader readerToRemoveFrom = (Reader) (sourceHbox).getUserData();
                
                removeProjectFromReader(readerToRemoveFrom, projectToMove);
                if (!preferencesCheckBox.isSelected()){
                    for (Node n : sourceHbox.getChildren()) {
                        if (n.getUserData().equals(projectToMove)) {
                            sourceHbox.getChildren().remove(n);
                            break;
                        }
                    }
                } 
                dragLabel.setVisible(false);
                refreshLowSelectedProjectList();
            }
       });
    }

    @Override
    public String moveProject(Reader reader, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore) {
        return null; // method should not be used in the results table as reordering is not really necessary.
    }

    @Override
    public String moveProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove) {
        return model.moveAssignedProject(readerToAdd, readerToRemoveFrom, projectToMove);    
    }

    @Override
    public String addProjectToReader(Reader reader, Project projectToAdd) {
        return model.assignProjectToReader(reader, projectToAdd);
    }

    @Override
    public String addProjectToReader(Reader reader, Project projectToAdd, Project projectToAddBefore) {
        return model.assignProjectToReader(reader, projectToAdd, projectToAddBefore);
    }

    @Override
    public ContextMenu createContextMenu(Reader reader, Node container) {
        ListContextMenu menu = new ListContextMenu(reader, this, container);
        if (!preferencesCheckBox.isSelected()){
            menu.includeRemoveButton();
        }
        return menu;
    }
    
    @Override
    public Collection<Project> getReaderList(Reader reader) {
        if (preferencesCheckBox.isSelected()){
            List<Project> list = (List<Project>) reader.getPreferences().clone();
            for (Project project : reader.getAssigned()){
                if (!list.contains(project)){
                    list.add(project);
                }
            }
            return list;      
        } else {
            return reader.getAssigned();
        }
    }

    @Override
    public void removeProjectFromReader(Reader reader, Project project) {
        reader.removeAssignedProject(project);
    }

    @Override
    public Label createLabel(Reader reader, Project project, ControllerInterface controller) {
        Reader assignedReader = project.getAssignedReader();
        PopLabel label;
        
        if (assignedReader != null && reader.equals(assignedReader)) {
            label = new DragLabel(project, controller);
            if (preferencesCheckBox.isSelected()){
                label.getStyleClass().add("bordered");
            }
        } else {
            label = new PopLabel(project.getId() + "");
        }

        label.setPopText("Name: " + project.getName()
                + "\nID: " + project.getId()
                + "\nTimes selected: " + project.getSelectedCount()
                + "\nAssigned to: " + ((assignedReader == null)? "nobody":assignedReader.getName()));

        return label;
    }

	@Override
	public String canMoveProject(Reader readerToAdd, Project projectToAdd) {
		return canMoveProject(readerToAdd, null, projectToAdd);
	}

	@Override
	public String canMoveProject(Reader readerToAdd, Reader readerToRemoveFrom,Project projectToAdd) {
		errorPopOver.hide();
		return model.canAddAssignment(readerToAdd, readerToRemoveFrom, projectToAdd);
	}

	@Override
	protected boolean isReaderListComplete(Reader reader) {
		return reader.getAssigned().size() == reader.getCapacity();
	}
}
