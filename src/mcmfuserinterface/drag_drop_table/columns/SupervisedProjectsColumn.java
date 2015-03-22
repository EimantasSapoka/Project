package mcmfuserinterface.drag_drop_table.columns;

import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import mcmfuserinterface.drag_drop_table.components.PopLabel;

public class SupervisedProjectsColumn extends TableColumn<TableObjectInterface, TableObjectInterface>{
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SupervisedProjectsColumn(String name){
        super(name);
        setMinWidth(100);
        setPrefWidth(500);
        setMaxWidth(700);
        
        setCellValueFactory(features -> {
            return new ReadOnlyObjectWrapper(features.getValue());
        });
        
        setCellFactory(features ->{
        	return new TableCell<TableObjectInterface, TableObjectInterface>() {
                @Override
                public void updateItem(final TableObjectInterface object, boolean empty) {
                    if(!empty && object != null){
                    	
                        HBox hbox = new HBox();
                        hbox.setSpacing(10);
                        hbox.setUserData(object);
                        
                        Reader reader = (Reader) object;
                        for (Project project : reader.getSupervisorProjects()) {
                            PopLabel label = new PopLabel(project.getID()+"");
                           
                            String thirdLine;
                            Reader assignedReader = project.getAssignedReader();
                            
							if (assignedReader == null){
                            	thirdLine = "Times selected: " +project.getSelectedCount();
                            } else {
                            	thirdLine = "Assigned to: "+ assignedReader.getName();
                            }
							
                            label.setPopText(String.format("Name: %s\nID: %d\n%s", 
                            		project.getName(), project.getID(), thirdLine));
                            
                            hbox.getChildren().add(label);
                        }
                        
                        ScrollPane scrollPane = new ScrollPane();
                        scrollPane.setContent(hbox);
                        
                        this.setGraphic(scrollPane);
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });
        
    }

}
