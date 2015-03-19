package mcmfuserinterface;

import java.awt.Toolkit;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ford_fulkerson.ReaderShortlistException;
import test.graph_creator.RandomReaderAllocationModel;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.MCMFModel;
import model.Project;
import model.Reader;

public class DialogUtils {
    private static final String READER_PREFERENCES_MAX = "readerPreferencesMax";
	private static final String READER_PREFERENCES_MIN = "readerPreferencesMin";
	private static final String READER_TARGET_MAX = "readerTargetMax";
	private static final String READER_TARGET_MIN = "readerTargetMin";
	private static final String PROJECT_COUNT = "projectCount";
	private static final String READER_COUNT = "readerCount";
	
	public static void createErrorDialog(String errorMsg) {
        final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
        if (runnable != null) {
            runnable.run();
        }
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot move!");
        alert.setContentText(errorMsg);
        alert.showAndWait();
    }
	
	public static void createErrorDialog(Project project, String errorMsg) {
        createErrorDialog(errorMsg + "\nProject name: "
                + project.getName() + ",  ID: " + project.getID());
    }
	
	public static void createExceptionDialog(Exception ex){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception Dialog");
		alert.setHeaderText("Exception has been thrown!");
		alert.setContentText(ex.getMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
	}
	
	/**
    * shows a dialog with errors and warnings and asks the user 
    * whether to proceed with the algorithm.
    * @param ex
    * @return 
    */
   public static boolean showErrorWarningDialog(ReaderShortlistException ex) {
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
    * shows a dialog box with inputs to configure the random 
    * instance generation. If all was correct, returns the random 
    * model instance. 
    * @return
    */
	public static MCMFModel createRandomInstanceCreatorDialog(){
		
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
        	
            return new RandomReaderAllocationModel(
            		input.get(READER_COUNT), 
            		input.get(PROJECT_COUNT), 
            		targetMin, targetMax, 
            		preferenceMin, preferenceMax);
           
        } else {
        	return null;
        }
        
	}
	
	/** 
	 * creates a parameter dialog window for results view exporting dialog.
	 * @return
	 */
	 public static Optional<List<String>> createParameterDialogWindow() {
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
	 
	 
	 
	 /**
	  * huge method to create the information dialog window for the results view.
	  * @param barChart
	  * @param model
	  */
	 public static void showInformationDialog(BarChart<String, Number> barChart, MCMFModel model) {
			
			Alert info = new Alert(Alert.AlertType.INFORMATION);
	        info.setHeaderText("Information on assignments:");
	        VBox box = new VBox();
	        box.getChildren().add(new Label("Load balanced? : " + model.isLoadBalanced() + 
	                                        "\tSaturating flow? : " + model.getNetwork().isSaturatingFlow()));
	        
	        HBox hbox = new HBox();
	        hbox.getChildren().add(new Label("Flow: " + model.getNetwork().getFlow() +
	                                        "\nWeight: " + model.getNetwork().getWeight()));
	        
	        hbox.getChildren().add(new Label(String.format("Readers: %d\nProjects: %d\nAvg. reader target: %.2f", 
	                model.getReaders().size(),model.getProjects().size(), model.getAverageReaderCapacity() )));
	        hbox.setSpacing(40);
	        box.getChildren().add(hbox);
	        
	        Map<Integer, Integer> preferenceStatistics = calculatePreferenceAssignmentStatistics(model);
	        box.getChildren().add(createPreferenceAssignedStatisticsTable(preferenceStatistics));
	        if (barChart == null){
	            CategoryAxis xAxis = new CategoryAxis();
		        NumberAxis yAxis = new NumberAxis();
		        barChart = new BarChart<String,Number>(xAxis,yAxis);
		        barChart.setTitle("How many readers got which preference");
		        xAxis.setLabel("Reader preference");
		        yAxis.setLabel("How many readers got it");
		        yAxis.setTickUnit(1.0);
		        yAxis.setMinorTickVisible(false);
	        } else {
	            barChart.getData().clear();
	        }
	        XYChart.Series<String, Number> series1 = new XYChart.Series<String,Number>();    
	        for (int preference : preferenceStatistics.keySet()){
	            series1.getData().add(new XYChart.Data<String, Number>(preference+"", preferenceStatistics.get(preference)));
	        }
	       
	        barChart.getData().add(series1);
	        box.getChildren().add(barChart);
	        box.setSpacing(20);
	        info.getDialogPane().setContent(box);
	        info.showAndWait();
		}


	    /**
	     * helper method which creates the profile table of assigned preferences.
	     * @param preferenceStatistics
	     * @return
	     */
	    private static GridPane createPreferenceAssignedStatisticsTable(Map<Integer, Integer> preferenceStatistics) {
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

	    /**
	     * helper method which calculates preference assigned statistics.
	     * @param model
	     * @return
	     */
	    private static Map<Integer, Integer> calculatePreferenceAssignmentStatistics(MCMFModel model) {
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
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 

}
