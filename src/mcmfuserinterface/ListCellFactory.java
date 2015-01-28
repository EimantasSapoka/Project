/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import model.MCMFModel;
import model.Project;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class ListCellFactory implements Callback<ListView<TableObjectInterface>, ListCell<TableObjectInterface>>{
    private final Controller controller;
    private final MCMFModel model;
    
    ListCellFactory(MCMFModel model, Controller controller) {
        this.controller = controller;
        this.model = model;
    }

    @Override
    public ListCell<TableObjectInterface> call(ListView<TableObjectInterface> param) {
        final ListCell<TableObjectInterface> listCell = new ListCell<TableObjectInterface>() {
            
            @Override protected void updateItem(TableObjectInterface item,boolean empty){
                super.updateItem(item, empty);
                if (item != null){
                    this.setText(item.toString());
                    this.updateSelected(false);
                }
            }
        };
        
        listCell.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("Dragging!!");

                Dragboard db = listCell.startDragAndDrop(TransferMode.MOVE);
                
                ClipboardContent content = new ClipboardContent();

                Project project = (Project) listCell.getItem();
                ListView<TableObjectInterface> list = listCell.getListView();
                TableCell<TableObjectInterface, TableObjectInterface> tableCell = (TableCell) list.getParent();
                Reader reader = (Reader) tableCell.getItem();
                
                if (reader != null && project != null){
                    content.putString(reader.getID()+"/"+project.getId());
                    db.setContent(content);
                }
                mouseEvent.consume();
            }
        });

        listCell.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data is dragged over the listCell */
                System.out.println("onDragOver");

                if (event.getGestureSource() != listCell
                        && event.getDragboard().hasString()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.MOVE);
                }

                event.consume();
            }
        });

        listCell.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture entered the listCell */
                System.out.println("onDragEntered");
                /* show to the user that it is an actual gesture listCell */
                if (event.getGestureSource() != listCell
                        && event.getDragboard().hasString()) {
                    listCell.setTextFill(Color.BLUE);
                    listCell.setOpacity(0.5);
                }

                event.consume();
            }
        });

        listCell.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* mouse moved away, remove the graphical cues */
                listCell.setTextFill(Color.BLACK);
                listCell.setOpacity(1);
                event.consume();
            }
        });

        listCell.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                System.out.println("DROPPED!!!!");
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                
                if (listCell.getItem() == null){
                    event.consume();
                    return;
                }
                String clipboard = db.getString();
                System.out.println(clipboard);
                
                int readerID = Integer.parseInt(clipboard.split("/")[0]);
                int projectID = Integer.parseInt(clipboard.split("/")[1]);
                
               
                ListCell sourceCell = (ListCell) event.getGestureSource();
                ListView<TableObjectInterface> sourceList = sourceCell.getListView();
                ListView<TableObjectInterface> targetList = listCell.getListView();
                
                Project projectToMove = model.getProject(projectID);
                Reader readerToRemoveFrom = model.getReader(readerID);
                Project projectToPlaceBefore = (Project) listCell.getItem();
                Reader readerToAdd = (Reader) ((TableCell) targetList.getParent()).getItem();
                
                model.movePreference(readerToAdd, readerToRemoveFrom, projectToMove, projectToPlaceBefore);
                
                sourceList.getItems().remove(projectToMove);
                int indexToPlace = targetList.getItems().indexOf(projectToPlaceBefore);
                targetList.getItems().add(indexToPlace, projectToMove);
                
                ObservableList<TableObjectInterface> targetListItems = targetList.getItems();
                ObservableList<TableObjectInterface> sourceListItems = sourceList.getItems();
                
                sourceList.setItems(null);
                targetList.setItems(null);
                sourceList.setItems(sourceListItems);
                targetList.setItems(targetListItems);
                
                System.out.println("source list: " + sourceListItems);
                System.out.println("target list: " + targetListItems);

                event.setDropCompleted(true);
                event.consume();
            }
        });
        
        return listCell;
    }
                

    
}
