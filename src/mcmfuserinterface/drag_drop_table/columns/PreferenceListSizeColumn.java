/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.columns;

import java.util.Comparator;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class PreferenceListSizeColumn extends TableColumn<TableObjectInterface, TableObjectInterface>{
    
    public PreferenceListSizeColumn(String name){
        super(name);
        setMinWidth(30);
        setPrefWidth(50);
        setMaxWidth(60);
        
        setCellValueFactory(new Callback<CellDataFeatures<TableObjectInterface, TableObjectInterface>, ObservableValue<TableObjectInterface>>() {
            @Override public ObservableValue<TableObjectInterface> call(CellDataFeatures<TableObjectInterface, TableObjectInterface> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
          });
        
        setComparator(new Comparator<TableObjectInterface>(){
            @Override
            public int compare(TableObjectInterface o1, TableObjectInterface o2) {
                    return ((Reader) o1).getPreferences().size() - ((Reader) o2).getPreferences().size();
            }
        	
        });
        
        setCellFactory(new Callback<TableColumn<TableObjectInterface, TableObjectInterface>, TableCell<TableObjectInterface,TableObjectInterface>>() {
            @Override
            public TableCell<TableObjectInterface, TableObjectInterface> call(TableColumn<TableObjectInterface, TableObjectInterface> arg) {
                TableCell<TableObjectInterface, TableObjectInterface> cell = new TableCell<TableObjectInterface, TableObjectInterface>(){
                    private final Label label = new Label();

                    @Override
                    protected void updateItem(TableObjectInterface arg0, boolean arg1) {
                        super.updateItem(arg0, arg1);
                        if (arg0 != null){
                                label.textProperty().bind(((Reader)arg0).getPreferenceStringProperty());
                                setGraphic(label);
                        }
                    }
                };
                return cell;
            }
        });
        
    }
    
}
