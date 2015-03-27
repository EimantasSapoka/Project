/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.controllers;

import java.io.File;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mcmfuserinterface.DialogUtils;
import mcmfuserinterface.UserInterface;
import mcmfuserinterface.UserInterfaceModel;
import mcmfuserinterface.drag_drop_table.columns.CapacityColumn;
import mcmfuserinterface.drag_drop_table.columns.ListColumn;
import mcmfuserinterface.drag_drop_table.columns.PreferenceListSizeColumn;
import mcmfuserinterface.drag_drop_table.columns.ReaderNameColumn;
import mcmfuserinterface.drag_drop_table.columns.SupervisedProjectsColumn;
import mcmfuserinterface.drag_drop_table.components.DragDropLabel;
import mcmfuserinterface.drag_drop_table.components.ListContextMenu;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import ford_fulkerson.MinCostMaxFlowAlgorithm;
import ford_fulkerson.ReaderShortlistException;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;

/**
 * A class which builds on top of the TableViewController class
 * and extends it with main view specific functionality and components.
 * @author Eimantas
 */
public class MainViewController extends TableViewController {
	
	// the results view controller
	private ResultsViewController resultsController;
	
	// results stage
    private Stage resultsStage;
    
    // project which is highlighted in the table
    private Project highlightedProject;
    
    @FXML    private CheckBox loadBalancedCheckbox;
    @FXML    private TextField projectLowSelectionLimitBox;
    @FXML    private Label trashBin;
   
    
    
    /******************  METHODS *********************/
   
    /**
    * shows a new window with the resulting assignments.
    */
    private void showResultsView() {
        Parent myPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mcmfuserinterface/fxml/FXMLResultsView.fxml"));
            myPane = (Parent) loader.load();
            resultsController = (ResultsViewController) loader.getController();
            resultsController.setModel(model);
            Scene scene = new Scene(myPane);
            String css = UserInterface.class.getResource("css/stylesheet.css").toExternalForm();
            scene.getStylesheets().add(css);
            resultsStage.setScene(scene);

            resultsStage.show();
        } catch (Exception ex) {
        	DialogUtils.createExceptionDialog(ex);
        }
    }    
 
    
    @FXML
    @Override
    public void refreshSideProjectList(){
    	if (model != null){
    		sideProjectListView.setItems(this.getSideListProjects());    
    	}
    }
    
    /**
     * highlights all occurrences of the project. 
     * if the project is already highlighted, it is unhighlighted - 
     * method works as a toggle if the same object is passed. 
     * @param project
     */
    public void setHighlightedProject(Project project) {
    	if (project != null && project.equals(this.highlightedProject)){
    		this.highlightedProject = null;
    	} else {
    		this.highlightedProject = project;
    	}
    }
    
    
    
    
    /******************* FXML METHODS *******************/
    
    /**
     * extends the anchor pane drag over with displaying the 
     * trash bin icon if the project is being moved (that is 
     * not from the side list)
     * @param dragEvent
     */
    @FXML
    @Override
    protected void anchorPaneDragOver(DragEvent dragEvent) {
        super.anchorPaneDragOver(dragEvent);
        if (dragEvent.getTransferMode() == TransferMode.MOVE){
        	trashBin.setVisible(true);
        }
    }

    /**
     * extends the anchor pane drag done event with
     * hiding of the trash bin
     * @param event
     */
    @FXML
    @Override
    protected void anchorPaneDragDone(DragEvent event) {
        super.anchorPaneDragDone(event);
        trashBin.setVisible(false);
    }


    /**
     * runs the algorithm if the model is not null
     */
    @FXML
    private void runAlgorithm() {
        if (model == null) {
            openFileButtonAction();
            return;
        } 
        
        if (resultsStage != null && resultsStage.isShowing()){
        	resultsStage.close();
        }
        
        boolean runAlgorithm = true;
        
        try {
            model.createNetwork();
        } catch (ReaderShortlistException ex) {
        	// show errors and warnings check if the user still wants to proceed
            runAlgorithm = DialogUtils.showErrorWarningDialog(ex);
        }
        
        if (runAlgorithm) {
            if (loadBalancedCheckbox.isSelected()) {
                MinCostMaxFlowAlgorithm.runLoadBalancedAlgorithm(model);
            } else {
                MinCostMaxFlowAlgorithm.runUnbalancedAlgorithm(model.getNetwork());
            }
            showResultsView();
        }

    }

    
    /**
     * shows a dialog window which allows to configure 
     * a random instance and displays it. 
     */
    @FXML
    private void createRandomInstance(){
        UserInterfaceModel randomModel = DialogUtils.createRandomInstanceCreatorDialog();
        if (randomModel != null){
        	loadModel(randomModel);
        }
    }
    
    /**
     * sets the listeners on the input text box which changes the items in the
     * low selected project list.
     */
    @FXML
    private void onLowSelectedProjectsThresholdChange(KeyEvent event) {
    	String text = ((TextField)event.getSource()).getText();
    	String character = event.getCharacter();
    	if ( !character.matches("\\d") ||  text.length() > 1 ){
            event.consume();
        }
    }

    /**
     * opens a file to import from the file system. 
     * @param event 
     */
    @FXML
    private void openFileButtonAction() {
        fileChooser.setTitle("Open Input File");
        File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            try {
            	loadModel(new UserInterfaceModel(file));
            } catch (Exception ex){
                DialogUtils.createExceptionDialog(ex);
            }
        }
    }


    private void loadModel(UserInterfaceModel userInterfaceModel) {
    	 model = userInterfaceModel;
		 createTableFromModel();
         createSideList();
         if (resultsStage != null && resultsStage.isShowing()){
         	resultsStage.close();
         }
	}


	/**
     * extends the preference lists of readers
     */
    @FXML
    private void extendPreferenceLists() {
        if (model == null){
            openFileButtonAction();
        } else {
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Are you sure you want to automatically extend preference lists?");
            confirmation.setContentText("The automatic extension is semi-random. It may result in some readers "
                    + "being assigned unwanted projects or projects not in their field of study");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.get() == ButtonType.OK){
                model.extendPreferenceLists();
                refreshSideProjectList();
            }
        }
    }
    
    /************************* TRASHBIN FXML METHODS ************************/
    
    @FXML
    private void onTrashBinDragOver(DragEvent event) {
        if (event.getGestureSource() instanceof DragDropLabel) {
            event.acceptTransferModes(TransferMode.MOVE);
            trashBin.setScaleY(3.5);
            trashBin.setScaleX(3.5);
        }
    }
    
    @FXML
    private void onTrashBinDragExit(DragEvent event) {
        if (event.getGestureSource() instanceof DragDropLabel) {
            trashBin.setScaleX(2.5);
            trashBin.setScaleY(2.5);
        }
    }
    
    @FXML
    private void onTrashBinDragDropped(DragEvent event) {
        if (event.getGestureSource() instanceof DragDropLabel) {
            Label sourceLabel = (Label) event.getGestureSource();
            HBox sourceHbox = (HBox) sourceLabel.getParent();
            Project projectToRemove = (Project) sourceLabel.getUserData();
            Reader readerToRemoveFrom = (Reader) sourceHbox.getUserData();

            model.removeProjectFromReaderPreferences(readerToRemoveFrom, projectToRemove);
            hideScreenFloats();
        }
    }
    
    /***************** OVERRIDE METHODS ****************/
    
    @Override
    public void initialize() {
        resultsStage = new Stage();
        resultsStage.setTitle("Assignments");
        
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        trashBin.setGraphic(fontAwesome.create(FontAwesome.Glyph.TRASH_ALT));
    }
    
    @Override
    protected void createTableColumns() {
        table.getColumns().add(new ReaderNameColumn("Reader name"));
        table.getColumns().add(new CapacityColumn("Target"));
        table.getColumns().add(new PreferenceListSizeColumn("#Pref"));
        table.getColumns().add(new ListColumn("Preferences", this));
        table.getColumns().add(new SupervisedProjectsColumn("Supervised Projects"));
    }
    
   
    /**
     * gets a list of projects to put into the side list
     */
    @Override
    protected ObservableList<Project> getSideListProjects(){
    	ObservableList<Project> lowSelectedProjectList = FXCollections.observableArrayList();
    	
    	String lowerThresholdString = projectLowSelectionLimitBox.getText();
        int lowerThreshold = lowerThresholdString.isEmpty()? 0:Integer.parseInt(projectLowSelectionLimitBox.getText());
        for (Project p : model.getProjects()) {
	       if (p.getSelectedCount() <= lowerThreshold) {
	          lowSelectedProjectList.add(p);
	       }
	    } 
        
        // sort the project by least selected first
        SortedList<Project> sortedLowSelectedProjectList = new SortedList<Project>(lowSelectedProjectList, (p1,p2) ->{
	            return p1.getSelectedCount() - p2.getSelectedCount();
        });
    	
    	return sortedLowSelectedProjectList.sorted();
    }
    
    
    @Override
    public String getListItemText(Project item){
    	return item.getID() + "\t\t("+item.getSelectedCount()+")";
    }
    
    
    @Override
	public String getListCellStyle(Project item){
		if (item.getSelectedCount() == 0){
		    return "-fx-background-color: red;";
		} else if (item.getSelectedCount() < 3){
		    return "-fx-background-color: orange;";
		} else {
		    return "";
		}
    }
    
    public String moveProject(Reader reader, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore) {
        return model.movePreference(reader, readerToRemoveFrom, projectToMove, projectToPlaceBefore);
    }

    @Override
    public String moveProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove) {
        return model.movePreference(readerToAdd, readerToRemoveFrom, projectToMove);
    }

    @Override
    public String addProjectToReader(Reader reader, Project projectToAdd) {
       return model.addProjectToReaderPreferences(reader, projectToAdd);
    }

    
    public String addProjectToReader(Reader reader, Project projectToAdd, Project projectToAddBefore) {
        return model.addProjectToReaderPreferences(reader, projectToAdd, projectToAddBefore);
    }

    @Override
    public ContextMenu createContextMenu(Reader reader, Node container) {
        ListContextMenu menu =  new ListContextMenu(reader, this, container );
        menu.includeAddButton();
        menu.includeRemoveButton();
        return menu;
    }
    
	@Override
    public ObservableList<Project> getObservableList(Reader reader) {
        return (ObservableList<Project>) reader.getPreferences();
    }

    @Override
    public void removeProjectFromReader(Reader reader, Project project) {
        reader.removePreference(project);
    }

    /**
     * creates a label which is used in this view's table.
     * it accounts for highlighted projects.
     */
    @Override
    public Label createLabel(Reader reader, Project project) {
        DragDropLabel label =  new DragDropLabel(project,this);
        
        if (project.equals(highlightedProject)){
            label.getStyleClass().add("highlighted");
        } else {
            label.getStyleClass().remove("highlighted");
        }
        
        if (reader.getPreferences().indexOf(project)< reader.getReaderTarget()){
        	label.getStyleClass().add("topChoice");
        } else {
        	label.getStyleClass().remove("topChoice");
        }
        label.setPopText("Name: " + project.getName() +
                         "\nID: " + project.getID() +
                         "\nTimes selected: " + project.getSelectedCount());
         
        return label;
    }


    @Override
	public String canMoveProject(Reader readerToAdd, Project projectToAdd) {
		return canMoveProject(readerToAdd, null, projectToAdd);
	}
	
	@Override
	public String canMoveProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToAdd){
		return model.canAddPreference(readerToAdd, readerToRemoveFrom, projectToAdd);
	}

	@Override
	protected boolean isReaderListComplete(Reader reader) {
		return reader.getPreferences().size() >= reader.getReaderTarget()*2;
	}
	
	  /**
     * generates the output to be exported in to a text file
     */
    @Override
    protected String createOutput(){
    	if (model == null || (model.getProjects().isEmpty() && model.getReaders().isEmpty())){
    		return null;
    	} 
    	
    	String output = model.getProjects().size() + ", " + model.getReaders().size() + "\n";
    	for (Project p: model.getProjects()){
    		output += p.getID() + ", " + p.getName() + ", " + p.getSupervisorID()+"\n";
    	}
    	for (Reader r: model.getReaders()){
    		output += r.getID() + ", " + r.getName() +", " + r.getReaderTarget() + ", ";
    		for (Project p: r.getPreferences()){
    			output += p.getID() +" ";
    		}
    		output += "\n";
    	}
    	return output;
    	
    }


	@Override
	public SimpleStringProperty getTableRowStyleProperty(Reader reader) {
		return reader.getReaderPreferenceShortlistStyleProperty();
	}

        
}
