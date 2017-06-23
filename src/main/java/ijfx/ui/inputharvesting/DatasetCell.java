/*
    This file is part of ImageJ FX.

    ImageJ FX is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ImageJ FX is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ImageJ FX.  If not, see <http://www.gnu.org/licenses/>. 
    
     Copyright 2015,2016 Cyril MONGIS, Michael Knop
	
 */
package ijfx.ui.inputharvesting;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import net.imagej.Dataset;

/**
 *
 * @author cyril
 */
public class DatasetCell extends ListCell<Dataset>{
    
    
    Label label = new Label();
    
    public DatasetCell() {
        super();
        
        itemProperty().addListener(this::onItemChanged);
        
    }
    
    public DatasetCell(ListView<Dataset> listView) {
        this();
    }
    
    private void onItemChanged(Object o, Dataset oldValue, Dataset newValue) {
        
        
        if(newValue == null) {
            setGraphic(null);
        }
        
        else {
            setGraphic(label);
        }
        
        label.setText(newValue.getName());
        
    }
    
    
}
