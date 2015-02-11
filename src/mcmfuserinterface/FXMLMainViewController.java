/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import ford_fulkerson.Algorithm;
import ford_fulkerson.ReaderShortlistException;
import model.MCMFModel;
import model.Reader;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import mcmfuserinterface.drag_drop_table.DragDropLabel;
import mcmfuserinterface.drag_drop_table.ListContextMenu;
import mcmfuserinterface.drag_drop_table.columns.CapacityColumn;
import mcmfuserinterface.drag_drop_table.columns.PreferenceListSizeColumn;
import mcmfuserinterface.drag_drop_table.columns.ListColumn;
import mcmfuserinterface.drag_drop_table.columns.ReaderNameColumn;
import model.Project;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

/**
 *
 * @author Eimantas
 */
public class FXMLMainViewController extends ViewController {
    
    FXMLResultsViewController resultsController;
    
    Stage resultsStage;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Button runAlgorithmButton;
    
    @FXML 
    private CheckBox loadBalancedCheckbox;
    
    @FXML
    private TextField projectLowSelectionLimitBox;
    
    @FXML
    private ListView<Project> lowSelectedList;
    
    @FXML
    private Label trashBin;
    
    @FXML
    Button extendPrefListButton;
    
    /******************    /
     METHODS
    /*********************/

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resultsStage = new Stage();
        resultsStage.setTitle("Assignments");
        
        createDragLabel();
        
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        trashBin.setGraphic(fontAwesome.create(FontAwesome.Glyph.TRASH_ALT));
    }

    /**
     * runs the algorithm if the model is not null
     */
    @FXML
    private void runAlgorithm() {
        if (model == null) {
            openFileButtonAction();
        } else {
            boolean runAlgorithm = true;
            try {
                model.createGraph();
            } catch (ReaderShortlistException ex) {
                runAlgorithm = showErrorWarningDialog(ex);
            }
            if (runAlgorithm) {
                if (loadBalancedCheckbox.isSelected()) {
                    Algorithm.runLoadBalancedAlgorithm(model);
                } else {
                    Algorithm.runUnbalancedAlgorithm(model);
                }
                showResultsView();
            }
        }
    }

    /**
    * shows a new window with the resulting assignments.
    */
    private void showResultsView() {
        if (!resultsStage.isShowing()){
            Parent myPane = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/FXMLResultsView.fxml"));
                myPane = (Parent) loader.load();
                resultsController = (FXMLResultsViewController) loader.getController();
                resultsController.setModel(model);
                Scene scene = new Scene(myPane);
                resultsStage.setScene(scene);
                
                resultsStage.show();
            } catch (Exception ex) {
                Alert alert = new ExceptionDialog(ex);
                alert.showAndWait();
            }
        } else {
            resultsController.setModel(model);
            resultsController.refreshTable();
        }
    }

   /**
    * shows a dialog with errors and warnings and asks the user 
    * whether to proceed with the algorithm.
    * @param ex
    * @return 
    */
   private boolean showErrorWarningDialog(ReaderShortlistException ex) {
       boolean runAlgorithm;
       runAlgorithm = false;
       Alert alert = new Alert(AlertType.CONFIRMATION);
       ButtonType proceed = new ButtonType("Proceed anyway");
       ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
       alert.getButtonTypes().setAll(proceed, cancel);
       if (ex.isErrorMessage()){
           alert.setTitle("Error");
           alert.setHeaderText("There are errors in data.\n"
                   + "Continuing is NOT recommended and WILL\n"
                   + "lead to unbalanced or poor project assignment.");
       } else {
           alert.setTitle("Warning");
           alert.setHeaderText("There are warnings in data.\nResulting "
                   + "assignment may not be the optimal. ");
       }
       alert.getDialogPane().setContent(new Label(ex.getMessage()));
       Optional<ButtonType> result = alert.showAndWait();
       if (result.get() == proceed){
           runAlgorithm = true;
       }
       return runAlgorithm;
   }

    /**
     * sets the listeners on the input text box which changes the items in the
     * low selected project list.
     */
    @FXML
    private void setLowSelectedProjectsThreshold() {
        String text = this.projectLowSelectionLimitBox.getText();
        if (!text.matches("\\d+")
                || Integer.parseInt(text) < 1
                || Integer.parseInt(text) > 99) {
            projectLowSelectionLimitBox.setText("");
        } else {
            if (model != null) {
                createLowSelectedProjectsList();
            }
        }
    }

    /**
     * 
     * @param event 
     */
    @FXML
    private void openFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Input File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (file != null) {
            try {
                model = new MCMFModel(file);
                createTableFromModel();
                createLowSelectedProjectsList();
            } catch (Exception ex){
                Alert alert = new ExceptionDialog(ex);
                alert.showAndWait();
            }
        }
    }

    @Override
    protected void createTableColumns() {
        table.getColumns().add(new ReaderNameColumn("Reader name"));
        table.getColumns().add(new CapacityColumn("Cap"));
        table.getColumns().add(new PreferenceListSizeColumn("#Pref"));
        table.getColumns().add(new ListColumn("Preferences", this));
    }

    /**
     * creates the table row factory which adds colors to the rows
     */
    @Override
    protected void setTableRowFactory() {
        table.setRowFactory(new Callback<TableView<TableObjectInterface>,TableRow<TableObjectInterface>>(){
            
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
                                        int numPref = Integer.parseInt(newValue);
                                        if (reader.equals(getUserData())){
                                            if (numPref < cap){
                                                setStyle("-fx-background-color: red;");
                                            } else if (numPref < cap*2){
                                                setStyle("-fx-background-color: orange;");
                                            } else {
                                                setStyle("");
                                            }
                                        } 
                                    }
                                }
                            });
                            int cap = reader.getCapacity();
                            int numPref = reader.getPreferences().size();

                            if (numPref < cap){
                                setStyle("-fx-background-color: red;");
                            } else if (numPref < cap*2){
                                setStyle("-fx-background-color: orange;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                };
                return row;
            }
            
        });
    }
    
    @Override
    public void refreshLowSelectedProjectList(){
        SortedList list = (SortedList) lowSelectedList.getItems();
        list.setComparator(new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                return o1.getSelectedCount() - o2.getSelectedCount();
            }
        });
        lowSelectedList.setItems(null);
        lowSelectedList.setItems(list.sorted());      
    }
    /**
     * creates side list with lowest selected projects in ascending order
     */
    private void createLowSelectedProjectsList() {
        
        ObservableList<Project> lowSelectedProjectList = FXCollections.observableArrayList();
        SortedList sortedLowSelectedProjectList = new SortedList(lowSelectedProjectList, new Comparator<Project>() {

            @Override
            public int compare(Project o1, Project o2) {
                return o1.getSelectedCount() - o2.getSelectedCount();
            }
        });

        for (Project p : model.getProjects()) {
            if (p.getSelectedCount() < Integer.parseInt(projectLowSelectionLimitBox.getText())) {
                lowSelectedProjectList.add(p);
            }
        }

        
        lowSelectedList.setItems(sortedLowSelectedProjectList.sorted());
        lowSelectedList.setCellFactory(new Callback<ListView<Project>, ListCell<Project>>(){

            @Override
            public ListCell<Project> call(ListView<Project> param) {
                final ListCell<Project> listCell =  new ListCell<Project>(){

                    @Override
                    protected void updateItem(Project item, boolean empty) {
                        super.updateItem(item, empty); 
                        if (item != null){
                            setUserData(item);
                            if (item.getSelectedCount() == 0){
                                setStyle("-fx-background-color: red;");
                            } else if (item.getSelectedCount() < 3){
                                setStyle("-fx-background-color: orange;");
                            } else {
                                setStyle("");
                            }
                            setTooltip(new Tooltip(item.getName()));
                            setText(item.getId() + "\t\t("+item.getSelectedCount()+")");
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
        
       lowSelectedList.setOnDragDone(new EventHandler<DragEvent>(){
            @Override
            public void handle(DragEvent event) {
               refreshLowSelectedProjectList();
            }
           
       });
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
                refreshTable();
                refreshLowSelectedProjectList();
            }
        }
    }
    
    /************************* TRASHBIN EVENTS ************************/
    
    @FXML
    private void onTrashBinDragOver(DragEvent event) {
        if (event.getGestureSource() instanceof DragDropLabel) {
            event.acceptTransferModes(TransferMode.MOVE);
            trashBin.setScaleY(1.5);
            trashBin.setScaleX(1.5);
        }
    }
    
    @FXML
    private void onTrashBinDragExit(DragEvent event) {
        if (event.getGestureSource() instanceof DragDropLabel) {
            trashBin.setScaleX(1);
            trashBin.setScaleY(1);
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
            sourceHbox.getChildren().remove(sourceLabel);

            event.setDropCompleted(true);
            dragLabel.setVisible(false);
            refreshLowSelectedProjectList();
        }
    }
    

    @Override
    public int moveProject(Reader reader, Reader readerToRemoveFrom, Project projectToMove, Project projectToPlaceBefore) {
        return model.movePreference(reader, readerToRemoveFrom, projectToMove, projectToPlaceBefore);
    }

    @Override
    public boolean moveProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToMove) {
        return model.movePreference(readerToAdd, readerToRemoveFrom, projectToMove);
    }

    @Override
    public boolean addProjectToReader(Reader reader, Project projectToAdd) {
       return model.addProjectToReaderPreferences(reader, projectToAdd);
    }

    @Override
    public int addProjectToReader(Reader reader, Project projectToAdd, Project projectToAddBefore) {
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
    public Label createLabel(Reader reader, Project project, ControllerInterface controller) {
        DragDropLabel label =  new DragDropLabel(project,controller);
        label.setPopText("Name: " + project.getName() +
                         "\nID: " + project.getId() +
                         "\nTimes selected: " + project.getSelectedCount());
         
        return label;
    }
}
