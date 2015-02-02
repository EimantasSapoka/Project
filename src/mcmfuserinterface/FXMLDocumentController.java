/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import ford_fulkerson.Algorithm;
import ford_fulkerson.ReaderShortlistException;
import model.MCMFModel;
import model.Reader;

import java.io.File;
import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
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
import model.Project;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

/**
 *
 * @author Eimantas
 */
public class FXMLDocumentController implements Initializable, Controller {

    MCMFModel model;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Button runAlgorithmButton;
    
    @FXML 
    private CheckBox loadBalancedCheckbox;
            
    @FXML
    private CheckBox zeroCapacityReaderCheckbox;
    
    @FXML
    private TextField projectLowSelectionLimitBox;
    
    @FXML
    TableView<TableObjectInterface> tableView;
    
    @FXML
    private Button printPref;
    
    @FXML
    private ListView<Project> lowSelectedList;
    
    @FXML
    private Label trashBin;
    
    Label dragLabel;
    
    @FXML
    AnchorPane anchorPane;
    
    @FXML
    Button extendPrefListButton;
    
    /******************    /
     METHODS
    /*********************/

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        dragLabel = new Label("");
        dragLabel.setMouseTransparent(true);
        dragLabel.setVisible(false);
        dragLabel.toFront();
        registerRootPaneDragEvents();
        
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        trashBin.setGraphic(fontAwesome.create(FontAwesome.Glyph.TRASH_ALT));
        addTrashBinListeners(trashBin);
        
        setZeroCapacityCheckboxListeners();
        setTextBoxListeners();
        setRunAlgorithmButtonListeners();
        setExtendPrefListButtonListener();
        
        printPref.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
               for(Reader r : model.getReaders()){
                   System.out.println("reader " + r.getID() + " : " + r.getPreferences());
               }
            }
        });   
    }

    /**
     * adds the button listener to the run algorithm button
    */
    private void setRunAlgorithmButtonListeners() {
        runAlgorithmButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if (model == null){
                    openFileButtonAction(event);
                } else {
                    boolean runAlgorithm = true;
                    try {
                        model.createGraph();
                    } catch (ReaderShortlistException ex) {
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
                        alert.setContentText(ex.getMessage());

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == proceed){
                            runAlgorithm = true;
                        }
                    }
                    if (runAlgorithm){
                        if (loadBalancedCheckbox.isSelected()){
                            Algorithm.runLoadBalancedAlgorithm(model);
                        } else {
                            Algorithm.runUnbalancedAlgorithm(model);
                        }
                        System.out.println(model);
                    }
                }
            }
            
        });
    }

    /**
     * sets the listeners on the input text box which changes
     * the items in the low selected project list. 
     */
    private void setTextBoxListeners() {
        projectLowSelectionLimitBox.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                TextField textField = (TextField) event.getSource();
                if (!textField.getText().matches("\\d+")
                        || Integer.parseInt(textField.getText()) < 2
                        || Integer.parseInt(textField.getText()) > 99){
                    textField.setText("");
                } else {
                    if (model != null){
                        createLowSelectedProjectsList();
                    }
                }
            }
        });
    }

    /**
     * sets the listeners to zero capacity check box.
     */
    private void setZeroCapacityCheckboxListeners() {
        zeroCapacityReaderCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                zeroCapacityReaderCheckbox.setSelected(newValue);
                ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
                if (newValue){
                    items.addAll(model.getReaders());
                } else {
                    for (Reader r : model.getReaders()){
                        if (!(r.getCapacity() == 0)){
                            items.add(r);
                        }
                    }
                }
                tableView.setItems(null);
                tableView.setItems(items);
            }
        });
    }

    
    
    public boolean showZeroCapacityReaders(){
        return zeroCapacityReaderCheckbox.isSelected();
    }
    
    @FXML
    private void exitSystem(ActionEvent event) {
        ((Stage) menuBar.getScene().getWindow()).close();
    }

    /**
     * 
     * @param event 
     */
    @FXML
    private void openFileButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Input File");
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (file != null) {
            System.out.println(file);
            model = new MCMFModel(file);
            createTableViewFromGraph(model);
            createLowSelectedProjectsList();
        }
    }

    
    @Override
    public MCMFModel getModel(){
        return model;
    }
    
    
    /**
     * creates a table with reader information and preferences list.
     * @param model 
     */
    private void createTableViewFromGraph(MCMFModel model) {
        if (model == null) { 
            /* should not happen as this is only called locally after
               the model is instanciated */
            System.err.println("model instance empty");
            return;
        }
        
        // if the table has no columns, create them
        if (tableView.getColumns().isEmpty()){
            tableView.getColumns().add(createReaderColumn());
            tableView.getColumns().add(createCapacityColumn());
            tableView.getColumns().add(createPreferenceListSizeColumn());
            tableView.getColumns().add(createPreferencesColumn());
        }
        
        ObservableList<TableObjectInterface> items = FXCollections.observableArrayList();
        if (this.showZeroCapacityReaders()){
            items.addAll(model.getReaders());
        } else {
            for (Reader r : model.getReaders()){
                if (!(r.getCapacity() == 0)){
                    items.add(r);
                }
            }
        }
        
        refreshTable();
        tableView.setItems(items);
        tableView.setFixedCellSize(40);
        setTableRowFactory();
    }

    /**
     * registers drag events for the root anchorPane, which 
 show and move the drag label across the anchorPane.
     */
    private void registerRootPaneDragEvents() {
        anchorPane.getChildren().add(dragLabel);
        anchorPane.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) { 
                System.out.println("DRAGGING");
                
                
                dragLabel.relocate(
                        (int) (t.getSceneX() - dragLabel.getBoundsInLocal().getWidth() / 2),
                        (int) (t.getSceneY() - dragLabel.getBoundsInLocal().getHeight() / 2));
            }
        });
        
        anchorPane.setOnDragOver(new EventHandler<DragEvent>(){
            @Override
            public void handle(DragEvent event) {
                if (!dragLabel.isVisible()){
                    dragLabel.setVisible(true);
                    dragLabel.toFront();
                    Project project;
                    if (event.getGestureSource() instanceof DragDropLabel){
                        project = (Project) ((Label) event.getGestureSource()).getUserData();
                    } else {
                        project = (Project) ((ListCell)event.getGestureSource()).getUserData();
                    }
                    dragLabel.setText(project.getId() + "");
                }
                dragLabel.relocate(
                        (int) (event.getSceneX() - dragLabel.getBoundsInLocal().getWidth() / 2),
                        (int) (event.getSceneY() - dragLabel.getBoundsInLocal().getHeight() / 2));
            }
        });
        
        anchorPane.setOnDragDone(new EventHandler<DragEvent>(){
            @Override
            public void handle(DragEvent event) {
                dragLabel.setVisible(false);
            }
            
        });
    }

    /**
     * creates the table row factory which adds colors to the rows
     */
    private void setTableRowFactory() {
        tableView.setRowFactory(new Callback<TableView<TableObjectInterface>,TableRow<TableObjectInterface>>(){
            
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
    
    /**
     * creates the reader column for the tableView.
     * @return table column
     */
    private TableColumn<TableObjectInterface, TableObjectInterface> createReaderColumn(){
        TableColumn<TableObjectInterface, TableObjectInterface> readerColumn = new TableColumn<TableObjectInterface, TableObjectInterface>("Reader name");
        readerColumn.setMinWidth(150);
        readerColumn.setMaxWidth(300);
        
        readerColumn.setCellValueFactory(new Callback<CellDataFeatures<TableObjectInterface, TableObjectInterface>, ObservableValue<TableObjectInterface>>() {
          @Override public ObservableValue<TableObjectInterface> call(CellDataFeatures<TableObjectInterface, TableObjectInterface> features) {
              Reader reader = (Reader) features.getValue();
              return new ReadOnlyObjectWrapper(reader.getName());
          }
        });
        return readerColumn;
    }
    
    /**
     * creates the preferences column and modifies so the cells contain lists
     * of preferences.
     * @return table column
     */
    private TableColumn<TableObjectInterface, TableObjectInterface> createPreferencesColumn(){
        TableColumn<TableObjectInterface, TableObjectInterface> preferencesColumn  = new TableColumn<TableObjectInterface, TableObjectInterface>("Preferences");
        preferencesColumn.setMinWidth(300);
        preferencesColumn.setCellValueFactory(new Callback<CellDataFeatures<TableObjectInterface, TableObjectInterface>, ObservableValue<TableObjectInterface>>() {
          @Override public ObservableValue<TableObjectInterface> call(CellDataFeatures<TableObjectInterface, TableObjectInterface> features) {
              return new ReadOnlyObjectWrapper(features.getValue());
          }
        });
        
        preferencesColumn.setCellFactory(new TableCellWithListFactory(this));
        return preferencesColumn;
    }
    
    /**
     * creates a column which shows the reader's capacity
     * @return 
     */
    private TableColumn<TableObjectInterface, TableObjectInterface> createCapacityColumn(){
        TableColumn<TableObjectInterface, TableObjectInterface> capacityColumn  = new TableColumn<TableObjectInterface, TableObjectInterface>("Cap");
        capacityColumn.setMinWidth(30);
        capacityColumn.setPrefWidth(50);
        capacityColumn.setMaxWidth(60);
        
        capacityColumn.setCellValueFactory(new Callback<CellDataFeatures<TableObjectInterface, TableObjectInterface>, ObservableValue<TableObjectInterface>>() {
          @Override public ObservableValue<TableObjectInterface> call(CellDataFeatures<TableObjectInterface, TableObjectInterface> features) {
              Reader reader = (Reader) features.getValue();
              return new ReadOnlyObjectWrapper(reader.getCapacity());
          }
        });
        
        return capacityColumn;
    }
    
    /**
     * creates the column with the preference sizes. It changes dynamically as the 
     * preferences are added and removed. Hence the huge method. 
     * @return 
     */
     private TableColumn<TableObjectInterface, TableObjectInterface> createPreferenceListSizeColumn(){
         
        TableColumn<TableObjectInterface, TableObjectInterface> preferenceCountColumn  = new TableColumn<TableObjectInterface, TableObjectInterface>("#Pref");
        preferenceCountColumn.setMinWidth(30);
        preferenceCountColumn.setPrefWidth(50);
        preferenceCountColumn.setMaxWidth(60);
        
        preferenceCountColumn.setCellValueFactory(new Callback<CellDataFeatures<TableObjectInterface, TableObjectInterface>, ObservableValue<TableObjectInterface>>() {
            @Override public ObservableValue<TableObjectInterface> call(CellDataFeatures<TableObjectInterface, TableObjectInterface> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
          });
        
        preferenceCountColumn.setComparator(new Comparator<TableObjectInterface>(){
            @Override
            public int compare(TableObjectInterface o1, TableObjectInterface o2) {
                    return ((Reader) o1).getPreferences().size() - ((Reader) o2).getPreferences().size();
            }
        	
        });
        
        preferenceCountColumn.setCellFactory(new Callback<TableColumn<TableObjectInterface, TableObjectInterface>, TableCell<TableObjectInterface,TableObjectInterface>>() {
            @Override
            public TableCell<TableObjectInterface, TableObjectInterface> call(TableColumn<TableObjectInterface, TableObjectInterface> arg) {
                TableCell<TableObjectInterface, TableObjectInterface> cell = new TableCell<TableObjectInterface, TableObjectInterface>(){
                    private final Label label = new Label();

                    @Override
                    protected void updateItem(TableObjectInterface arg0, boolean arg1) {
                        super.updateItem(arg0, arg1);
                        if (arg0 != null){
                                label.textProperty().bind(((Reader)arg0).getPreferenceStringProperty());
                                setGraphic(label);
                        }
                    }
                };
                return cell;
            }
        });
        
        return preferenceCountColumn;
    }
     
    public void refreshTable(){
        tableView.getColumns().get(2).setVisible(false);
        tableView.getColumns().get(2).setVisible(true);
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
     * adds the listeners for drag over and drops for the trash bin icon.
     * @param trashBin 
     */
      private void addTrashBinListeners(final Label trashBin) {
        trashBin.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() instanceof DragDropLabel) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    trashBin.setScaleY(1.5);
                    trashBin.setScaleX(1.5);
                }
            }

        });

        trashBin.setOnDragExited(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() instanceof DragDropLabel) {
                    trashBin.setScaleX(1);
                    trashBin.setScaleY(1);
                }
            }

        });

        trashBin.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
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
        });
        
        
    }

    private void setExtendPrefListButtonListener() {
        extendPrefListButton.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent event) {
                if (model == null){
                    openFileButtonAction(event);
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
            
        });
    }

}
