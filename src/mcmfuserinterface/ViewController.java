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
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import model.MCMFModel;
import model.Project;
import model.Reader;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

/**
 *
 * @author Eimantas
 */
public abstract class ViewController implements Initializable, ControllerInterface {
	
	public static final String DESKTOP_DIRECTORY = System.getProperty("user.home") + File.separator + "Desktop" + File.separator;

    protected MCMFModel model;
    protected Label dragLabel;
    protected PopOver errorPopOver;
    int scroll = 10;
    
    @FXML     protected AnchorPane anchorPane;
    @FXML     protected TableView<TableObjectInterface> table;
    @FXML     protected CheckMenuItem zeroCapacityReaderCheckbox;
    @FXML     protected CheckMenuItem completeListReaderCheckBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	errorPopOver = new PopOver();
        createDragLabel();
        initialize();      
    }
    
    @Override
    public void refresh(){
        refreshTable();
        refreshLowSelectedProjectList();
        this.dragLabel.setVisible(false);
        this.errorPopOver.hide();
    }
    
   
    @Override
    public void refreshTable() {
        table.getColumns().get(0).setVisible(false);
        table.getColumns().get(0).setVisible(true);
    }
    
     protected void createDragLabel() {
        dragLabel = new Label("");
        dragLabel.getStyleClass().add("myLabel");
        dragLabel.getStyleClass().add("shadow");
        dragLabel.setMouseTransparent(true);
        dragLabel.setVisible(false);
        dragLabel.toFront();
        
        anchorPane.getChildren().add(dragLabel);
    }
    
    @Override
    public Collection<Project> getProjects() {
        return model.getProjects();
    }
    
    @FXML
    protected void closeWindow(ActionEvent event) {
        ((Stage) table.getScene().getWindow()).close();
    }
    
   
    /**
     * changes between showing and hiding readers with zero capacity
     */
    @FXML
    protected void toggleShowZeroCapacityReaders() {
        if (model != null && completeListReaderCheckBox.isSelected()){
            ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
            if (zeroCapacityReaderCheckbox.isSelected()){
                items.addAll(model.getReaders());
            } else {
                addNonZeroCapacityReaders(items);
            }
            table.setItems(null);
            table.setItems(items);
        }
    }

	private void addNonZeroCapacityReaders(ObservableList<TableObjectInterface> items) {
		for (Reader r : model.getReaders()){
		    if (!(r.getCapacity() == 0)){
		        items.add(r);
		    }
		}
	}
    
    
    /**
     * changes between showing and hiding readers with complete lists
     * - that is sufficiently good lists.
     */
    @FXML
    protected void toggleShowReadersWithCompleteLists(){
    	if (model != null ){
    		 ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
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
    
    protected void addIncompleteReaders(ObservableList<TableObjectInterface> items){
    	for (Reader r : model.getReaders()){
		    if (!isReaderListComplete(r)){
		        items.add(r);
		    }
		}
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
        
        ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
        items.addAll(model.getReaders());
        
        this.zeroCapacityReaderCheckbox.setSelected(true);
        this.completeListReaderCheckBox.setSelected(true);
        
       
        table.setItems(items);
        setTableRowFactory();
        refreshTable();
    }
    
    
    public void showErrorPopOver(String errorMsg, Node parent){
    	errorPopOver = new PopOver();
		Label popText = new Label(errorMsg);
		popText.setTextFill(Color.BLACK);
        popText.setPadding(new Insets(10, 10, 10, 10));
        errorPopOver.setContentNode(popText);
        errorPopOver.setArrowLocation(ArrowLocation.RIGHT_CENTER);
        errorPopOver.show(parent);
	}
    
    
    @FXML
    public void export(){
        String output = createOutput();
        if (output != null){
            FileChooser fileChooser = new FileChooser();
            File desktopFolder = new File(DESKTOP_DIRECTORY);
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
            dragLabel.toFront();
            Project project = (Project) ((Node) dragEvent.getGestureSource()).getUserData();
            dragLabel.setText(project.getId() + "");
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
    	errorPopOver.hide();
        dragLabel.setVisible(false);
    }
    
    /**
     * resolving a javafx bug...
     * @param event
     */
    @FXML 
    protected void hide(MouseEvent event){
    	anchorPaneDragDone(null);
    }
    
    
    protected abstract String createOutput();
    protected abstract boolean isReaderListComplete(Reader reader);
    protected abstract void initialize();
    protected abstract void setTableRowFactory();
    protected abstract void createTableColumns();
}
