/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.columns;

import java.util.Comparator;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import mcmfuserinterface.drag_drop_table.TableObjectInterface;
import model.Reader;

/**
 *
 * @author Eimantas
 */
public class AssignedProjectsCountColumn extends TableColumn<TableObjectInterface, TableObjectInterface> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public AssignedProjectsCountColumn(String name) {
        super(name);

        setMinWidth(40);
        setPrefWidth(50);
        setMaxWidth(60);

        setCellValueFactory(features -> {
            return new ReadOnlyObjectWrapper(features.getValue());
        });

        setComparator(new Comparator<TableObjectInterface>() {
            @Override
            public int compare(TableObjectInterface o1, TableObjectInterface o2) {
                return ((Reader) o1).getAssigned().size() - ((Reader) o2).getAssigned().size();
            }

        });

        setCellFactory(arg -> {
            TableCell<TableObjectInterface, TableObjectInterface> cell = new TableCell<TableObjectInterface, TableObjectInterface>() {
                private final Label label = new Label();

                @Override
                protected void updateItem(TableObjectInterface arg0, boolean arg1) {
                    super.updateItem(arg0, arg1);
                    if (arg0 != null) {
                        label.textProperty().bind(((Reader) arg0).getAssignedCountProperty());
                        setGraphic(label);
                    } else {
                        setGraphic(null);
                    }
                }
            };
            return cell;
        });

    }

}
