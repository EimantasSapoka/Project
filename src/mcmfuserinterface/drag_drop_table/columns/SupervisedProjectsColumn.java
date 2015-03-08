package mcmfuserinterface.drag_drop_table.columns;

import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import model.Reader;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;

public class SupervisedProjectsColumn extends TableColumn<TableObjectInterface, TableObjectInterface>{
	
	public SupervisedProjectsColumn(String name){
        super(name);
        setMinWidth(300);
        setPrefWidth(500);
        setMaxWidth(700);
        
        setCellValueFactory(features -> {
              Reader reader = (Reader) features.getValue();
              String supervisedProjects = "";
              for (int projID : reader.getSupervisorProjects()){
            	  supervisedProjects += projID + ",  ";
              }
              return new ReadOnlyObjectWrapper(supervisedProjects);
        });
        
    }

}
