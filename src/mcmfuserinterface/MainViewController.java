/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mcmfuserinterface.drag_drop_table.DragDropLabel;
import mcmfuserinterface.drag_drop_table.ListContextMenu;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import mcmfuserinterface.drag_drop_table.columns.CapacityColumn;
import mcmfuserinterface.drag_drop_table.columns.ListColumn;
import mcmfuserinterface.drag_drop_table.columns.PreferenceListSizeColumn;
import mcmfuserinterface.drag_drop_table.columns.ReaderNameColumn;
import mcmfuserinterface.drag_drop_table.columns.SupervisedProjectsColumn;
import model.MCMFModel;
import model.Project;
import model.Reader;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import test.graph_creator.RandomReaderAllocationModel;
import ford_fulkerson.Algorithm;
import ford_fulkerson.ReaderShortlistException;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Eimantas
 */
public class MainViewController extends ViewController {
	    
    private static final String READER_PREFERENCES_MAX = "readerPreferencesMax";
	private static final String READER_PREFERENCES_MIN = "readerPreferencesMin";
	private static final String READER_TARGET_MAX = "readerTargetMax";
	private static final String READER_TARGET_MIN = "readerTargetMin";
	private static final String PROJECT_COUNT = "projectCount";
	private static final String READER_COUNT = "readerCount";
	
	private ResultsViewController resultsController;
    private Stage resultsStage; 
    private Project highlightedProject;
    
    @FXML    private MenuBar menuBar;
    @FXML    private Button runAlgorithmButton;
    @FXML    private CheckBox loadBalancedCheckbox;
    @FXML    private TextField projectLowSelectionLimitBox;
    @FXML    private ListView<Project> lowSelectedList;
    @FXML    private Label trashBin;
   
    
    
    /******************    /
     METHODS
    /*********************/

    @Override
    public void initialize() {
        resultsStage = new Stage();
        resultsStage.setTitle("Assignments");
        
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
                    Algorithm.runUnbalancedAlgorithm(model.getGraph());
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
            resultsController.setModel(model);
            resultsController.refresh();
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
        if(checkIfTextFieldInputInteger(this.projectLowSelectionLimitBox)){
            if (model != null) {
                createLowSelectedProjectsList();
            }
        }
        
    }
    
    /**
     * generates the output to be exported in to a text file
     */
    protected String createOutput(){
    	if (model == null || (model.getProjects().isEmpty() && model.getReaders().isEmpty())){
    		return null;
    	} 
    	
    	String output = model.getProjects().size() + ", " + model.getReaders().size() + "\n";
    	for (Project p: model.getProjects()){
    		output += p.getId() + ", " + p.getName() + ", " + p.getSupervisorID()+"\n";
    	}
    	for (Reader r: model.getReaders()){
    		output += r.getID() + ", " + r.getName() +", " + r.getCapacity() + ", ";
    		for (Project p: r.getPreferences()){
    			output += p.getId() +" ";
    		}
    		output += "\n";
    	}
    	return output;
    	
    }

    /**
     * checks if the text field input is digits.
     */
    private boolean checkIfTextFieldInputInteger(TextField box){
        String text = box.getText();
        if (!text.matches("\\d+")
                || Integer.parseInt(text) < 1
                || Integer.parseInt(text) > 999) {
            box.setText("");
            return false;
        } else {
            return true;
        }
    }
    
    @FXML
    private void createRandomInstance(){
        Dialog<Map<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Create random instance");
        dialog.setHeaderText("Enter instance parameters");
        
        ButtonType createButtonType = new ButtonType("Create", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField readerCount = new TextField();
        readerCount.setPromptText("Number of readers");
        TextField projectCount = new TextField();
        projectCount.setPromptText("Number of projects");
        TextField readerTargetMin = new TextField();
        readerTargetMin.setPromptText("Min. Optional");
        TextField readerTargetMax = new TextField();
        readerTargetMax.setPromptText("Max. Optional");
        TextField readerPreferencesMin = new TextField();
        readerPreferencesMin.setPromptText("Min. Optional");
        TextField readerPreferencesMax = new TextField();
        readerPreferencesMax.setPromptText("Max. Optional");
        
        EventHandler<KeyEvent> acceptOnlyIntegers = new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				if (	!event.getCharacter().matches("\\d") || 
						((TextField)event.getSource()).getText().length() > 2 ){
                    event.consume();
                }
			}
        };
        
        
        readerCount.setOnKeyTyped(acceptOnlyIntegers);
        projectCount.setOnKeyTyped(acceptOnlyIntegers);
        readerTargetMax.setOnKeyTyped(acceptOnlyIntegers);
        readerTargetMin.setOnKeyTyped(acceptOnlyIntegers);
        readerPreferencesMax.setOnKeyTyped(acceptOnlyIntegers);
        readerPreferencesMin.setOnKeyTyped(acceptOnlyIntegers);
        
        grid.add(new Label("Readers:"), 0, 0);
        grid.add(readerCount, 1, 0);
        grid.add(new Label("Projects:"), 0, 1);
        grid.add(projectCount, 1, 1);
        grid.add(new Label("Reader Target:"), 0, 2);
        grid.add(readerTargetMin, 1, 2);
        grid.add(readerTargetMax, 2, 2);
        grid.add(new Label("Reader Pref. List size"), 0, 3);
        grid.add(readerPreferencesMin, 1, 3);
        grid.add(readerPreferencesMax, 2, 3);
       
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if ( dialogButton == createButtonType &&
                !readerCount.getText().isEmpty() &&
                !projectCount.getText().isEmpty() ) {
               
            	Map<String, Integer> input = new HashMap<String,Integer>();
                input.put(READER_COUNT, Integer.parseInt(readerCount.getText()) );
                input.put(PROJECT_COUNT, Integer.parseInt(projectCount.getText()) );
                
                if (!readerTargetMin.getText().isEmpty()){
                	input.put(READER_TARGET_MIN, Integer.parseInt(readerTargetMin.getText()) );
                }
                
                if (!readerTargetMax.getText().isEmpty()){
                	input.put(READER_TARGET_MAX, Integer.parseInt(readerTargetMax.getText()) );
                }
               
                if (!readerPreferencesMin.getText().isEmpty()){
                	input.put(READER_PREFERENCES_MIN, Integer.parseInt(readerPreferencesMin.getText()) );
                }
                
                if (!readerPreferencesMax.getText().isEmpty()){
                	input.put(READER_PREFERENCES_MAX, Integer.parseInt(readerPreferencesMax.getText()) );
                }
                
                return input;
            }
            return null;
        });

        Optional<Map<String, Integer>> result = dialog.showAndWait();
        if (result.isPresent() && result.get() != null){
        	Map<String, Integer> input = result.get();
        	
        	int targetMin = input.containsKey(READER_TARGET_MIN)? input.get(READER_TARGET_MIN):0;
        	int targetMax = input.containsKey(READER_TARGET_MAX)? input.get(READER_TARGET_MAX):0;
        	int preferenceMin = input.containsKey(READER_PREFERENCES_MIN)? input.get(READER_PREFERENCES_MIN):0;
        	int preferenceMax = input.containsKey(READER_PREFERENCES_MAX)? input.get(READER_PREFERENCES_MAX):0;
        	
            model = new RandomReaderAllocationModel(
            		input.get(READER_COUNT), 
            		input.get(PROJECT_COUNT), 
            		targetMin, targetMax, 
            		preferenceMin, preferenceMax);
            createTableFromModel();
            createLowSelectedProjectsList();
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
        File desktopDir = new File(DESKTOP_DIRECTORY);
        if (desktopDir.exists()){
        	fileChooser.setInitialDirectory(desktopDir);
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (file != null) {
            try {
                model = new MCMFModel(file);
                createTableFromModel();
                createLowSelectedProjectsList();
            } catch (Exception ex){
                DialogUtils.createExceptionDialog(ex);
            }
        }
    }

    @Override
    protected void createTableColumns() {
        table.getColumns().add(new ReaderNameColumn("Reader name"));
        table.getColumns().add(new CapacityColumn("Cap"));
        table.getColumns().add(new PreferenceListSizeColumn("#Pref"));
        table.getColumns().add(new ListColumn("Preferences", this));
        table.getColumns().add(new SupervisedProjectsColumn("Supervised Projects"));
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
                            reader.getPreferenceStringProperty().addListener((observable, oldValue, newValue)-> {
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
                                    } else {
                                        setStyle("");
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
                        } else {
                            setStyle("");
                        }
                    }
                };
                return row;
            });
    }
    
    public void refreshLowSelectedProjectList(){
        SortedList<Project> list = (SortedList<Project>) lowSelectedList.getItems();
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
        SortedList<Project> sortedLowSelectedProjectList = new SortedList<Project>(lowSelectedProjectList, new Comparator<Project>() {

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
        lowSelectedList.setCellFactory(param -> {
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
                        }else {
                            setUserData(null);
                            setVisible(false);
                        }
                    }               
                };
                listCell.setOnMouseClicked(event -> {
                    this.setHighlightedProject((Project)((Node)event.getSource()).getUserData());
                    refresh();
                });
                listCell.setOnDragDetected(event ->{
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
        
       lowSelectedList.setOnDragDone(event -> {
               refreshLowSelectedProjectList();
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
                refresh();
                refreshLowSelectedProjectList();
            }
        }
    }
    
    /************************* TRASHBIN EVENTS ************************/
    
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
            refreshLowSelectedProjectList();
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
        
        if (reader.getPreferences().indexOf(project)< reader.getCapacity()){
        	label.getStyleClass().add("topChoice");
        } else {
        	label.getStyleClass().remove("topChoice");
        }
        label.setPopText("Name: " + project.getName() +
                         "\nID: " + project.getId() +
                         "\nTimes selected: " + project.getSelectedCount());
         
        return label;
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

	public String canMoveProject(Reader readerToAdd, Project projectToAdd) {
		return canMoveProject(readerToAdd, null, projectToAdd);
	}
	
	public String canMoveProject(Reader readerToAdd, Reader readerToRemoveFrom, Project projectToAdd){
		errorPopOver.hide();
		return model.canAddPreference(readerToAdd, readerToRemoveFrom, projectToAdd);
	}

	@Override
	protected boolean isReaderListComplete(Reader reader) {
		return reader.getPreferences().size() >= reader.getCapacity()*2;
	}
        
        @FXML
        @Override
        protected void anchorPaneDragOver(DragEvent dragEvent) {
            super.anchorPaneDragOver(dragEvent);
            trashBin.setVisible(true);
        }

        @FXML
        @Override
        protected void anchorPaneDragDone(DragEvent event) {
            super.anchorPaneDragDone(event);
            trashBin.setVisible(false);
        }

}
