package mcmfuserinterface;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import mcmfuserinterface.controllers.MainViewController;
import mcmfuserinterface.controllers.TableControllerInterface;
import mcmfuserinterface.drag_drop_table.components.PopLabel;
import ford_fulkerson.model.Project;

/**
 * A cell factory which changes the way side list cells are presented. 
 * Adds drag and drop fuctionality to them.
 * @author Eimantas
 *
 */
public class SideListCellFactory implements Callback<ListView<Project>, ListCell<Project>> {

	private final TableControllerInterface controller;
	    
    public SideListCellFactory(TableControllerInterface controller) {
        this.controller = controller;
    }
	
	@Override
	public ListCell<Project> call(ListView<Project> arg0) {
		 final ListCell<Project> listCell =  new ListCell<Project>(){

             @Override
             protected void updateItem(Project item, boolean empty) {
                 super.updateItem(item, empty); 
                 if (item != null){
                     setUserData(item);
                     setTooltip(new Tooltip(item.getName()));
                     setStyle(controller.getListCellStyle(item));
                     PopLabel label = new PopLabel(controller.getListItemText(item));
                     label.setPopText("Name: " + item.getName() +
                    		 		  "\nID: " +item.getID()+
                    		 		  "\nTimes selected: " + item.getSelectedCount());
                     setGraphic(label);
                 }else {
                	 setGraphic(null);
                	 setTooltip(null);
                     setUserData(null);
                     setVisible(false);
                     setStyle(null);
                 }
             }               
         };
         if (controller instanceof MainViewController){
        	 listCell.setOnMouseClicked(event -> {
	             ((MainViewController) controller).setHighlightedProject((Project)((Node)event.getSource()).getUserData());
	             controller.refreshTable();
        	 });
         }
         
         listCell.setOnDragDetected(event ->{
         		Project data = (Project) listCell.getUserData();
                 if (data == null){
                     return;
                 }
                 Dragboard db = listCell.startDragAndDrop(TransferMode.COPY);
                 ClipboardContent content = new ClipboardContent();

                 content.putString(data.getName());
                 db.setContent(content);
         });
         return listCell;
     }

}
