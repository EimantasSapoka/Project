/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
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
    
    @FXML    Button infoButton;
    @FXML    Button cancelButton;
    @FXML    CheckMenuItem preferencesCheckBox;
    
    private  BarChart<String,Number> barChart;
    
    
    
    @Override
    public void initialize(){
    	
    }
    
    @Override
    protected String createOutput() {
        Optional<List<String>> result =  DialogUtils.createParameterDialogWindow();
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
                resultString += reader.getAssigned().size()+"/"+reader.getMarkingTarget()+", ";
            }
            if (parameters.contains("supervised")){
                for (Project project: reader.getSupervisorProjects()){
                    resultString+= project.getID() + " ";
                }
                resultString += ", ";
            }
            for (Project project : reader.getAssigned()){
                resultString += project.getID()+" ";
            }
            resultString+="\n";
        }
        return resultString;
    }


    
    @FXML
    private void showPreferencesToggle(){
        refreshTable();
    }
    
    protected void setModel(MCMFModel model) {
        this.model = model;
        createTableFromModel();
        createSideList();
    }
    
    @Override
    protected void createTableColumns() {
        table.getColumns().add(new ReaderNameColumn("Reader name"));
        table.getColumns().add(new CapacityColumn("Target"));
        table.getColumns().add(new AssignedProjectsCountColumn("#Assigned"));
        table.getColumns().add(new ListColumn("Projects Assigned", this));
    }
    
    @FXML
    private void showInfo(){
        DialogUtils.showInformationDialog(barChart,model);
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
                            reader.getAssignedCountProperty().addListener( (observable, oldValue,  newValue) -> {
                                    if (getUserData()!= null){
                                        int cap = ((Reader) getUserData()).getMarkingTarget();
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
                            int cap = ((Reader) getUserData()).getMarkingTarget();
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
    
    @Override
    public void refreshSideProjectList(){
        ObservableList<Project> unselectedProjectList = FXCollections.observableArrayList();
        unselectedProjectList.addAll(model.getUnselectedProjects());
        sideProjectListView.setItems(unselectedProjectList);
    }
   
	@FXML
	protected void onSideListDragOver(DragEvent event) {
		event.acceptTransferModes(TransferMode.MOVE);
	}

	@FXML
	protected void onSideListDragDropped(DragEvent event) {
		if (event.getAcceptedTransferMode().equals(TransferMode.MOVE)) {
			Label sourceLabel = (Label) event.getGestureSource();
			Project projectToMove = (Project) sourceLabel.getUserData();
			Reader readerToRemoveFrom = (Reader) sourceLabel.getParent().getUserData();

			removeProjectFromReader(readerToRemoveFrom, projectToMove);
			refresh();
		}
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
    public ContextMenu createContextMenu(Reader reader, Node container) {
        ListContextMenu menu = new ListContextMenu(reader, this, container);
        if (!preferencesCheckBox.isSelected()){
            menu.includeRemoveButton();
        }
        return menu;
    }
    
    @Override
    @SuppressWarnings("unchecked")
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
    public Label createLabel(Reader reader, Project project) {
        Reader assignedReader = project.getAssignedReader();
        PopLabel label;
        
        if (assignedReader != null && reader.equals(assignedReader)) {
            label = new DragLabel(project, this);
            if (preferencesCheckBox.isSelected()){
                label.getStyleClass().add("bordered");
            }
        } else {
            label = new PopLabel(project.getID() + "");
        }

        label.setPopText("Name: " + project.getName()
                + "\nID: " + project.getID()
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
		return reader.getAssigned().size() == reader.getMarkingTarget();
	}
	
	@Override
	protected ObservableList<Project> getSideListProjects(){
    	ObservableList<Project> list = FXCollections.observableArrayList();
    	list.addAll( model.getUnselectedProjects());
		return list;		
	}
	
	@Override
	public String getListItemText(Project item){
		return item.getID()+"";
	}
	
	@Override
	public String getListCellStyle(Project item){
		return "-fx-background-color: red;";
	}
}
