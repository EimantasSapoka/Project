package mcmfuserinterface.drag_drop_table;

import ford_fulkerson.model.Reader;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import mcmfuserinterface.controllers.TableControllerInterface;

public class TableRowFactory implements Callback<TableView<Reader>, TableRow<Reader>>{
	private final TableControllerInterface controller;
	
	public TableRowFactory(final TableControllerInterface contr){
		this.controller = contr;
	}
	
	@Override
	public TableRow<Reader> call(TableView<Reader> arg0) {
		TableRow<Reader> row = new TableRow<Reader>(){
            @Override
            protected void updateItem(Reader item, boolean empty) {
                super.updateItem(item, empty); 
                setUserData(item);
                if (item != null){
                	Reader reader = (Reader) item;
                	styleProperty().bind(controller.getTableRowStyleProperty(reader));
                } else {
                	styleProperty().unbind();
                	setStyle("");
                }
            }
        };
        return row;
	}

}
