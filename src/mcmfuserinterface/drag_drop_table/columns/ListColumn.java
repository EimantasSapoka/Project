/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.columns;

import java.util.List;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import mcmfuserinterface.controllers.TableControllerInterface;
import mcmfuserinterface.controllers.ResultsViewController;
import mcmfuserinterface.drag_drop_table.components.DroppableScrollPane;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;

/**
 *
 * @author Eimantas
 */
public class ListColumn  extends TableColumn<Reader, ObservableList<Project>>{
    
	public ListColumn(String name, TableControllerInterface controller){
        super(name);
        setMinWidth(350);
        setPrefWidth(450);
        setMaxWidth(2000);
        
        setCellValueFactory(features -> {
			ObservableList<Project> readerList = controller.getObservableList(features.getValue());
			return  new SimpleListProperty<Project>(readerList);
        });
        
        
        
        setCellFactory( item ->{
        	return new TableCell<Reader,ObservableList<Project>>() {
        		
                @Override
                public void updateItem(final ObservableList<Project> list, boolean empty) {
                	Reader reader = (Reader) getTableRow().getUserData();
                	
                    if(empty && list == null || reader == null){
                    	setGraphic(null);
                    	return;
                    }
                    HBox hbox = new HBox();
        			hbox.setSpacing(10);
                	hbox.setUserData(reader);
                	
                	ScrollPane scrollPane = new DroppableScrollPane(controller);
                    scrollPane.setContent(hbox);
        			scrollPane.setContextMenu(controller.createContextMenu(reader,hbox));
                    
        			List<Project> listToDisplay = list;
        			
        			/*
        			 *  workaround due to the fact that the results view can display different lists -
        			 *  one just assignments, the other assignments with preferences mixed. Although 
        			 *  ugly, improves performance significantly!
        			 */
        			
        			if (controller instanceof ResultsViewController){
        				listToDisplay = ((ResultsViewController) controller).getDisplayList(reader);
        			}
        			
					for (Project project : listToDisplay) {
                        Label label = controller.createLabel(reader, project);
                        hbox.getChildren().add(label);
                    }
                    
                    this.setGraphic(scrollPane);
                }
            };
        });
    }
    
}
