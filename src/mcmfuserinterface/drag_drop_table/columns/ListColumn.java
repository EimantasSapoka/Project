/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.columns;

import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import mcmfuserinterface.controllers.ControllerInterface;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import mcmfuserinterface.drag_drop_table.components.DroppableScrollPane;

/**
 *
 * @author Eimantas
 */
public class ListColumn  extends TableColumn<TableObjectInterface, TableObjectInterface>{
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public ListColumn(String name, ControllerInterface controller){
        super(name);
        setMinWidth(350);
        setPrefWidth(450);
        setMaxWidth(2000);
        
        setCellValueFactory(features -> {
              return new ReadOnlyObjectWrapper(features.getValue());
        });
        
        setCellFactory( item ->{
        	return new TableCell<TableObjectInterface, TableObjectInterface>() {
                @Override
                public void updateItem(final TableObjectInterface object, boolean empty) {
                    if(!empty && object != null){
                    	
                    	HBox hbox = new HBox();
                        hbox.setSpacing(10);
                        hbox.setUserData(object);
                        
                        Reader reader = (Reader) object;
                        for (Project project : controller.getReaderList(reader)) {
                            Label label = controller.createLabel(reader, project);
                            hbox.getChildren().add(label);
                        }
                        
                        ScrollPane scrollPane = new DroppableScrollPane(controller);
                        scrollPane.setContent(hbox);
                        scrollPane.setContextMenu(controller.createContextMenu((Reader)object,hbox));
                        
                        this.setGraphic(scrollPane);
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });
    }
    
}
