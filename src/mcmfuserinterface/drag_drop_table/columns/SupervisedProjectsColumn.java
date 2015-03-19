package mcmfuserinterface.drag_drop_table.columns;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import model.Project;
import model.Reader;

public class SupervisedProjectsColumn extends TableColumn<TableObjectInterface, TableObjectInterface>{
	
	public SupervisedProjectsColumn(String name){
        super(name);
        setMinWidth(100);
        setPrefWidth(500);
        setMaxWidth(700);
        
        setCellValueFactory(features -> {
              Reader reader = (Reader) features.getValue();
              String supervisedProjects = "";
              for (Project proj : reader.getSupervisorProjects()){
            	  supervisedProjects += proj.getID() + ",  ";
              }
              supervisedProjects = supervisedProjects.isEmpty() ? "":supervisedProjects.substring(0, supervisedProjects.length() -3);
              return new ReadOnlyObjectWrapper(supervisedProjects);
        });
        
    }

}
