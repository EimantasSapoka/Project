package mcmfuserinterface.drag_drop_table;

import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import mcmfuserinterface.controllers.ControllerInterface;
import model.Reader;

public class TableRowFactory implements Callback<TableView<TableObjectInterface>, TableRow<TableObjectInterface>>{
	private final ControllerInterface controller;
	
	public TableRowFactory(final ControllerInterface contr){
		this.controller = contr;
	}
	
	@Override
	public TableRow<TableObjectInterface> call(TableView<TableObjectInterface> arg0) {
		TableRow<TableObjectInterface> row = new TableRow<TableObjectInterface>(){
            @Override
            protected void updateItem(TableObjectInterface item, boolean empty) {
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
