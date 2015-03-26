/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The main application class. runs the main window.
 * @author Eimantas
 */
public class UserInterface extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
    	// checks if JRE is supported
    	if (UserInterface.isJavaVersionCorrect()){
	        Parent root = FXMLLoader.load(getClass().getResource("fxml/FXMLMainView.fxml"));
	        Scene scene = new Scene(root);
	        String css = UserInterface.class.getResource("css/stylesheet.css").toExternalForm();
	        scene.getStylesheets().add(css);
	        stage.setScene(scene);
	        stage.setTitle("Reader Allocator 1.0");
    	} else { // if not, show a simple window with a message 
    		StackPane root = new StackPane();
    		root.getChildren().add(new Label("Usupported java version! Java 1.8.0_20 required, older versions are unsupported. "
    				+ "Your java version: " + System.getProperty("java.version")));
    		stage.setScene(new Scene(root));
    	}
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * parse java version and return if it is supported.
     * @return
     */
    private static boolean isJavaVersionCorrect(){
    	try {
    		String version = System.getProperty("java.version");
        	Double javaVersion = Double.parseDouble(version.substring(0, version.lastIndexOf('.')));
        	if (javaVersion < 1.8){
        		return false;
        	} else if (javaVersion == 1.8){
        		Double buildVersion = Double.parseDouble(version.substring(version.indexOf('_')+1));
        		if (buildVersion < 20){
        			return false; // has to be java 1.8 build higher than 20
        		} else {
        			return true;
        		}
        	} else {
        		return true;
        	}
    	} catch (Exception e){
    		System.out.println("WARNING! could not determine java version");
    		return true; // if can't figure out java version, just run it.
    	}
    }
    
    

}
