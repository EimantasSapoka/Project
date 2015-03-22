package mcmfuserinterface.drag_drop_table.columns;

import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import mcmfuserinterface.drag_drop_table.components.PopLabel;
import ford_fulkerson.model.Project;
import ford_fulkerson.model.Reader;

public class SupervisedProjectsColumn extends TableColumn<Reader, List<Project>>{
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SupervisedProjectsColumn(String name){
        super(name);
        setMinWidth(100);
        setPrefWidth(500);
        setMaxWidth(700);
        
        setCellValueFactory(features -> {
            return new ReadOnlyObjectWrapper(features.getValue().getSupervisorProjects());
        });
        
        setCellFactory(features ->{
        	return new TableCell<Reader, List<Project>>() {
        		
        		
        		
                @Override
                public void updateItem(final List<Project> list, boolean empty) {
                        
                	Reader reader = (Reader) getTableRow().getUserData();
                	
                	if(empty && list == null || reader == null){
                    	setGraphic(null);
                    	return;
                    }
                    
                	HBox hbox = new HBox();
        			hbox.setSpacing(10);
        			hbox.setUserData(reader);
                	
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
               
                }
            };
        });
        
    }

}
