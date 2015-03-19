/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.controllers;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mcmfuserinterface.DialogUtils;
import mcmfuserinterface.UserInterface;
import mcmfuserinterface.drag_drop_table.columns.CapacityColumn;
import mcmfuserinterface.drag_drop_table.columns.ListColumn;
import mcmfuserinterface.drag_drop_table.columns.PreferenceListSizeColumn;
import mcmfuserinterface.drag_drop_table.columns.ReaderNameColumn;
import mcmfuserinterface.drag_drop_table.columns.SupervisedProjectsColumn;
import mcmfuserinterface.drag_drop_table.components.DragDropLabel;
import mcmfuserinterface.drag_drop_table.components.ListContextMenu;
import model.MCMFModel;
import model.Project;
import model.Reader;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import ford_fulkerson.MinCostMaxFlowAlgorithm;
import ford_fulkerson.ReaderShortlistException;

/**
 *
 * @author Eimantas
 */
public class MainViewController extends ViewController {
	
	private ResultsViewController resultsController;
    private Stage resultsStage; 
    private Project highlightedProject;
    
    @FXML    private MenuBar menuBar;
    @FXML    private Button runAlgorithmButton;
    @FXML    private CheckBox loadBalancedCheckbox;
    @FXML    private TextField projectLowSelectionLimitBox;
    @FXML    private Label trashBin;
   
    
    
    /******************  METHODS *********************/
   
    /**
    * shows a new window with the resulting assignments.
    */
    private void showResultsView() {
        if (!resultsStage.isShowing()){
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
        } else {
            resultsController.refresh();
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
    
    @FXML
    @Override
    protected void anchorPaneDragOver(DragEvent dragEvent) {
        super.anchorPaneDragOver(dragEvent);
        if (dragEvent.getTransferMode() == TransferMode.MOVE){
        	trashBin.setVisible(true);
        }
    }

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
        MCMFModel randomModel = DialogUtils.createRandomInstanceCreatorDialog();
        if (randomModel != null){
        	model = randomModel;
        	createTableFromModel();
        	createSideList();
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
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (file != null) {
            try {
                model = new MCMFModel(file);
                createTableFromModel();
                createSideList();
            } catch (Exception ex){
                DialogUtils.createExceptionDialog(ex);
            }
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
                refresh();
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

            refresh();
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
        
        SortedList<Project> sortedLowSelectedProjectList = new SortedList<Project>(lowSelectedProjectList, new Comparator<Project>() {
	        @Override
	        public int compare(Project o1, Project o2) {
	            return o1.getSelectedCount() - o2.getSelectedCount();
	        }
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
    public Collection<Project> getReaderList(Reader reader) {
        return reader.getPreferences();
    }

    @Override
    public void removeProjectFromReader(Reader reader, Project project) {
        reader.removePreference(project);
    }

    @Override
    public Label createLabel(Reader reader, Project project) {
        DragDropLabel label =  new DragDropLabel(project,this);
        
        if (project.equals(highlightedProject)){
            label.getStyleClass().add("highlighted");
        } else {
            label.getStyleClass().remove("highlighted");
        }
        
        if (reader.getPreferences().indexOf(project)< reader.getMarkingTarget()){
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
		return reader.getPreferences().size() >= reader.getMarkingTarget()*2;
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
    		output += r.getID() + ", " + r.getName() +", " + r.getMarkingTarget() + ", ";
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
