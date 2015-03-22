/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mcmfuserinterface.DialogUtils;
import mcmfuserinterface.SideListCellFactory;
import mcmfuserinterface.UserInterfaceModel;
import mcmfuserinterface.drag_drop_table.TableRowFactory;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;

/**
 *
 * @author Eimantas
 */
public abstract class ViewController implements Initializable, ControllerInterface {
	
	public static String DESKTOP_DIRECTORY;

    protected UserInterfaceModel model;
    protected Label dragLabel;
    protected PopOver errorPopOver;
    protected int scroll = 10;
    
    protected FileChooser fileChooser;
    
    @FXML     protected AnchorPane anchorPane;
    @FXML     protected TableView<Reader> table;
    @FXML     protected CheckMenuItem zeroCapacityReaderCheckbox;
    @FXML     protected CheckMenuItem completeListReaderCheckBox;
    @FXML     protected ListView<Project> sideProjectListView;
    
	/**
	 * creates side list controller specified list of projects
	 */
	protected void createSideList() {
		sideProjectListView.setItems(getSideListProjects());
		sideProjectListView.setCellFactory(new SideListCellFactory(this));
		sideProjectListView.setOnDragDone(event -> {
			refreshSideProjectList();
		});
	}

	private void initFileChooser() {
		fileChooser = new FileChooser();
        File desktopDir = new File(DESKTOP_DIRECTORY);
        if (desktopDir.exists()){
        	fileChooser.setInitialDirectory(desktopDir);
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
	}
    
    
    private void createDragLabel() {
        dragLabel = new Label("");
        dragLabel.getStyleClass().add("myLabel");
        dragLabel.getStyleClass().add("shadow");
        dragLabel.setMouseTransparent(true);
        dragLabel.setVisible(false);
        dragLabel.toFront();
        
        anchorPane.getChildren().add(dragLabel);
    }
    
    /**
     * creates a table with reader information and preferences list.
     */
    protected void createTableFromModel() {
    	
        if (model == null) { 
            /* should not happen as this is only called locally after
               the model is instanciated */
            System.err.println("model instance empty");
            return;
        }
        
        // if the table has no columns, create them
        if (table.getColumns().isEmpty()){
            createTableColumns();
        }
        
        ObservableList<Reader> items = FXCollections.observableArrayList();
        items.addAll(model.getReaders());
        
        this.zeroCapacityReaderCheckbox.setSelected(true);
        this.completeListReaderCheckBox.setSelected(true);
        
       
        table.setItems(items);
        table.setRowFactory(new TableRowFactory(this));
        refreshTable();
    }
    
    
    public void showErrorPopOver(String errorMsg, Node parent){
		((Label) errorPopOver.getContentNode()).setText(errorMsg);
        
		if (!errorPopOver.isShowing() || parent != errorPopOver.getOwnerNode()){
    		 errorPopOver.show(parent);
    	}
	}
    
    public void hideErrorPopOver(){
    	errorPopOver.hide();
    }
    
    /******************************* OVERRIDE METHODS ******************/
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	DESKTOP_DIRECTORY = System.getProperty("user.home") + File.separator + "Desktop" + File.separator;
    	errorPopOver = new PopOver();
    	Label popText = new Label();
		popText.setTextFill(Color.BLACK);
        popText.setPadding(new Insets(10, 10, 10, 10));
        errorPopOver.setContentNode(popText);
        errorPopOver.setArrowLocation(ArrowLocation.RIGHT_CENTER);
        createDragLabel();
        initFileChooser();
        initialize();      
    }
     
    @Override
    public void refresh(){
        refreshTable();
        refreshSideProjectList();
        hideScreenFloats();
    }
    
    @Override
	public void hideScreenFloats() {
		this.dragLabel.setVisible(false);
        this.errorPopOver.hide();
	}
    
    @Override
    public void refreshTable() {
        table.getColumns().get(0).setVisible(false);
        table.getColumns().get(0).setVisible(true);
    }
   
    @Override
    public Collection<Project> getProjects() {
        return model.getProjects();
    }
    
    /******************************* FXML METHODS *********************/

    @FXML
    protected void closeWindow(ActionEvent event) {
        ((Stage) table.getScene().getWindow()).close();
    }
    
    
    
    @FXML
    public void export(){
        String output = createOutput();
        saveTextToFile(output);
    }

    /**
     * saves output text into a user selected file.
     * @param output
     */
	protected void saveTextToFile(String output) {
		if (output != null) {
			
			fileChooser.setTitle("Save file");
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
    
    /**
     * changes between showing and hiding readers with zero capacity
     */
    @FXML
    protected void toggleShowZeroCapacityReaders() {
        if (model != null && completeListReaderCheckBox.isSelected()){
            ObservableList<Reader> items = FXCollections.observableArrayList();
            if (zeroCapacityReaderCheckbox.isSelected()){
                items.addAll(model.getReaders());
            } else {
                addNonZeroCapacityReaders(items);
            }
            table.setItems(null);
            table.setItems(items);
        }
    }
    
    /**
     * changes between showing and hiding readers with complete lists
     * - that is sufficiently good lists.
     */
    @FXML
    protected void toggleShowReadersWithCompleteLists(){
    	if (model != null ){
    		 ObservableList<Reader> items = FXCollections.observableArrayList();
             if (completeListReaderCheckBox.isSelected()){
            	 if (zeroCapacityReaderCheckbox.isSelected()){
	                 items.addAll(model.getReaders());
	             } else {
	                 addNonZeroCapacityReaders(items);
	             }
             } else {
            	 addIncompleteReaders(items);
             }
             table.setItems(null);
             table.setItems(items);
    	}
    }
    
    	/**
    	 * helper method which adds non zero capacity readers to the items list
    	 * @param items
    	 */
		private void addNonZeroCapacityReaders(ObservableList<Reader> items) {
			for (Reader r : model.getReaders()){
			    if (!(r.getMarkingTarget() == 0)){
			        items.add(r);
			    }
			}
		}
	    
		/**
		 * helper method which adds all incomplete readers to the items list
		 * @param items
		 */
	    private void addIncompleteReaders(ObservableList<Reader> items){
	    	for (Reader r : model.getReaders()){
			    if (!isReaderListComplete(r)){
			        items.add(r);
			    }
			}
	    }
	      
    
    /**************************** ANCHOR PANE EVENTS ********************/

    @FXML
    protected void anchorPaneDragOver(DragEvent dragEvent) {
    	double sceneY = dragEvent.getSceneY();
    	double sceneX = dragEvent.getSceneX();
    	double tableMaxY = table.getBoundsInParent().getMaxY();
    	double tableMinY = table.getBoundsInParent().getMinY();
    	double tableMinX = table.getBoundsInLocal().getMinX();
		    	
        if (!dragLabel.isVisible()) {
            dragLabel.setVisible(true);
            Project project = (Project) ((Node) dragEvent.getGestureSource()).getUserData();
            dragLabel.setText(project.getID() + "");
        }
        dragLabel.relocate(
                (int) (dragEvent.getSceneX() - dragLabel.getBoundsInLocal().getWidth() / 2),
                (int) (sceneY - dragLabel.getBoundsInLocal().getHeight() / 2));
        
        if (sceneY > tableMaxY-30 && sceneY < tableMaxY+20 && sceneX > tableMinX+240){
    		table.scrollTo(scroll/2);
    		if (scroll < table.getItems().size()*2){
    			scroll++;
    		}
    	} else if (sceneY < tableMinY+50 && sceneY > tableMinY && sceneX > tableMinX+240){
    		table.scrollTo(scroll/2);
    		if (scroll > 0){
    			scroll--;
    		}
    	}
    }

    @FXML
    protected void anchorPaneDragDone(DragEvent event) {
    	hideScreenFloats();
    	refreshSideProjectList();
    }
    
    /**
     * resolving a javafx bug...
     * @param event
     */
    @FXML 
    protected void hide(MouseEvent event){
    	hideScreenFloats();
    }
    
    
    
    /********************* ABSTRACT METHODS **************************/
    
    /**
     * creates view specific output. 
     * @return
     */
    protected abstract String createOutput();
    
    /**
     * depending on table view, checks if the reader is complete. 
     * @param reader
     * @return
     */
    protected abstract boolean isReaderListComplete(Reader reader);
    
    /**
     * initializes each controller specific items
     */
    protected abstract void initialize();
    
    /**
     * creates and adds table specific columns
     */
    protected abstract void createTableColumns();
    
    /**
     * creates and returns a list of projects to be added to the side list
     */
    protected abstract ObservableList<Project> getSideListProjects();
}
