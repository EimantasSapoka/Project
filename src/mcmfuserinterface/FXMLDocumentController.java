/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import ford_fulkerson.Algorithm;
import model.MCMFModel;
import model.Reader;

import java.io.File;
import java.net.URL;
import java.util.Comparator;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
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
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Project;
import org.controlsfx.dialog.Dialogs;
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
    
    /******************    /
     METHODS
    /*********************/

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        trashBin.setGraphic(fontAwesome.create(FontAwesome.Glyph.TRASH_ALT));
        addTrashBinListeners(trashBin);
                
        setZeroCapacityCheckboxListeners();
        setProjectLowSelectionLimitBoxListeners();
        setRunAlgorithmButtonListeners();
        
        printPref.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
               for(Reader r : model.getReaders()){
                   System.out.println("reader " + r.getID() + " : " + r.getPreferences());
               }
            }
        });   
    }

    private void setRunAlgorithmButtonListeners() {
        runAlgorithmButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if (loadBalancedCheckbox.isSelected()){
                    Algorithm.runLoadBalancedAlgorithm(model);
                } else {
                    Algorithm.runUnbalancedAlgorithm(model);
                }
                System.out.println(model);
            }
            
        });
    }

    private void setProjectLowSelectionLimitBoxListeners() {
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

    @FXML
    private void handleButtonAction(ActionEvent event) {
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
    
    
    
    public void createTableViewFromGraph(MCMFModel model) {
        if (model.getGraph() == null) {
            System.out.println("Graph instance empty");
            return;
        }
        
        
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
        
        this.refreshTable();
        tableView.setItems(items);
        tableView.setStyle(".table-row-cell {-fx-cell-size: 50px;}");
        tableView.setFixedCellSize(40);
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
                        event.consume();
                    }            
                });
                return listCell;
            }
            
        });
        
       lowSelectedList.setOnDragDone(new EventHandler<DragEvent>(){

            @Override
            public void handle(DragEvent event) {
               
                SortedList list = (SortedList) lowSelectedList.getItems();
                list.setComparator(new Comparator<Project>() {
                    @Override
                    public int compare(Project o1, Project o2) {
                        return o1.getSelectedCount() - o2.getSelectedCount();
                    }
                });
                lowSelectedList.setItems(list.sorted());
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
                    event.consume();
                }
            }
        });
        
        
    }

}
