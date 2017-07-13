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

import java.util.stream.Collectors;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author cyril
 */
@Plugin(type = InputWidget.class)
public class DatasetWidget extends EasyInputWidget<Dataset>{

    ComboBox<Dataset> comboBox;
    
    
    @Parameter
    ImageDisplayService imageDisplayService;
    
    @Override
    public Property<Dataset> getProperty() {
        return comboBox.valueProperty();
    }

    @Override
    public Node createComponent() {
        comboBox = new ComboBox<Dataset>();
        comboBox.setCellFactory(DatasetCell::new);
        
        comboBox
                .getItems()
                .addAll(imageDisplayService
                        .getImageDisplays()
                        .stream()
                        .map(imageDisplayService::getActiveDataset)
                        .collect(Collectors.toList()));
        
        return comboBox;
    }

    @Override
    public boolean handles(WidgetModel model) {
        return model.isType(Dataset.class);
    }
    
}
