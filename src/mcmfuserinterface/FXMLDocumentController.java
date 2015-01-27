/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

import model.MCMFModel;
import ford_fulkerson.graph.Graph;
import model.Reader;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Project;

/**
 *
 * @author Eimantas
 */
public class FXMLDocumentController implements Initializable {
    
    MCMFModel model;
    
    @FXML
    private MenuBar menuBar;
    
    @FXML
    TreeView<TreeObjectInterface> treeView;
    
    
    @FXML
    private void exitSystem(ActionEvent event){
        ((Stage) menuBar.getScene().getWindow()).close();
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Input File");
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (file != null){
            System.out.println(file);
            model = new MCMFModel(file); 
            createTreeViewFromGraph(model);
        }        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeTreeCells();
        Reader root = new Reader("root", -1);
        TreeItem<TreeObjectInterface> rootItem = new TreeItem<TreeObjectInterface>(root);
        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);
       
        
    }

    private void initializeTreeCells() {
        treeView.setCellFactory(new Callback<TreeView<TreeObjectInterface>, TreeCell<TreeObjectInterface>>() {
                    @Override
                    public TreeCell<TreeObjectInterface> call(TreeView<TreeObjectInterface> stringTreeView) {
                        final TreeCell<TreeObjectInterface> treeCell = new TreeCell<TreeObjectInterface>() {
                            @Override
                            protected void updateItem(TreeObjectInterface item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    setText(item.toString());
                                }
                            }
                        };

                        treeCell.setOnDragDetected(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                System.out.println("Dragging!!");
                               TreeItem<TreeObjectInterface> treeItem = treeCell.getTreeItem();
                                if (!treeItem.isLeaf()){
                                    mouseEvent.consume();
                                    return;
                                } 
                                Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);
                                
                                /* put a string on dragboard */
                                ClipboardContent content = new ClipboardContent();
                                content.putString(((Project)treeItem.getValue()).getId()+"");
                                db.setContent(content);
                                mouseEvent.consume();
                            }
                        });
                        
                        treeCell.setOnDragOver(new EventHandler <DragEvent>() {
                            @Override
                            public void handle(DragEvent event) {
                                /* data is dragged over the treeCell */
                                System.out.println("onDragOver");

                                /* accept it only if it is  not dragged from the same node 
                                 * and if it has a string data */
                                
                               TreeItem<TreeObjectInterface> treeItem = treeCell.getTreeItem();
                                if (treeItem != null && !treeItem.isLeaf()){
                                    for(TreeItem t: treeItem.getParent().getChildren()){
                                        t.setExpanded(false);
                                    }
                                    treeItem.setExpanded(true);
                                    
                                }
                                if (event.getGestureSource() != treeCell &&
                                        event.getDragboard().hasString()) {
                                    /* allow for both copying and moving, whatever user chooses */
                                    event.acceptTransferModes(TransferMode.MOVE);
                                }

                                event.consume();
                            }
                        });
                        
                        
                        
                    treeCell.setOnDragEntered(new EventHandler <DragEvent>() {
                        @Override
                        public void handle(DragEvent event) {
                            /* the drag-and-drop gesture entered the treeCell */
                            System.out.println("onDragEntered");
                            /* show to the user that it is an actual gesture treeCell */
                            if (event.getGestureSource() != treeCell &&
                                    event.getDragboard().hasString()) {
                              
                            } 
                           TreeItem<TreeObjectInterface> treeItem = treeCell.getTreeItem();
                            if (treeItem != null && treeItem.isLeaf()){
                                treeCell.setOpacity(0.5);
                            }
                            event.consume();
                        }
                    });

                    treeCell.setOnDragExited(new EventHandler <DragEvent>() {
                        @Override
                        public void handle(DragEvent event) {
                            /* mouse moved away, remove the graphical cues */
                           
                            treeCell.setOpacity(1);
                            event.consume();
                        }
                    });

                    treeCell.setOnDragDropped(new EventHandler <DragEvent>() {
                        @Override
                        public void handle(DragEvent event) {
                            /* data dropped */
                            System.out.println("onDragDropped");
                            /* if there is a string data on dragboard, read it and use it */
                            Dragboard db = event.getDragboard();
                           TreeItem<TreeObjectInterface> treeItem = treeCell.getTreeItem();
                           boolean success = false;
                           if (treeItem.isLeaf()){
                               
                           } else {
                               Reader reader = (Reader) treeItem.getValue();
                               Project projectToMove = model.getProject(Integer.parseInt(db.getString()));
                               success = model.movePreference(model.getReader(reader), projectToMove );
                           }
                            createTreeViewFromGraph(model);
                            /* let the source know whether the string was successfully 
                             * transferred and used */
                            System.out.println(success);
                            event.setDropCompleted(success);

                            event.consume();
                        }
                    });

                    treeCell.setOnDragDone(new EventHandler <DragEvent>() {
                        @Override
                        public void handle(DragEvent event) {
                            /* the drag-and-drop gesture ended */
                            System.out.println("onDragDone");
                            /* if the data was successfully moved, clear it */
                            if (event.getTransferMode() == TransferMode.MOVE) {
                                treeCell.setText("");
                            }

                            event.consume();
                        }
                    });

                        return treeCell;
                    }
                });
    }

    private void createTreeViewFromGraph(MCMFModel model) {
        if (model.getGraph() == null){
            System.out.println("Graph instance empty");
            return;
        } 
        this.treeView.getRoot().getChildren().clear();
        
        for (Reader reader : model.getReaders()){
            TreeItem<TreeObjectInterface> readerNode = new TreeItem<TreeObjectInterface>(reader);
            for (Project preference: reader.getPreferences()){
                 TreeItem<TreeObjectInterface> projectNode = new TreeItem<TreeObjectInterface>(preference);
                 readerNode.getChildren().add(projectNode);
            }
            
            this.treeView.getRoot().getChildren().add(readerNode);
        }
    }
}
