/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface.drag_drop_table.columns;

import javafx.scene.control.TableColumn;
import ford_fulkerson.model.Reader;

/**
 *
 * @author Eimantas
 */
public class PreferenceListSizeColumn extends TableColumn<Reader, Integer>{
    
	public PreferenceListSizeColumn(String name){
        super(name);
        setMinWidth(40);
        setPrefWidth(40);
        setMaxWidth(60);
        
        setCellValueFactory(features -> {
                return features.getValue().getPreferenceCountProperty().asObject();
          });
        
    }
    
}
