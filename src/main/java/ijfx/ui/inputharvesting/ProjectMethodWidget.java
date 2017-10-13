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

import ijfx.plugins.projection.ProjectionMethod;
import java.util.List;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author cyril
 */
@Plugin(type = InputWidget.class)
public class ProjectMethodWidget extends EasyInputWidget<ProjectionMethod>{

    
    ComboBox<ProjectionMethod> node;
    
    @Parameter
    PluginService pluginService;
    
    @Override
    public Property<ProjectionMethod> getProperty() {
        return node.valueProperty();
    }

    @Override
    public Node createComponent() {
        node = new ComboBox<>();
        
        List<ProjectionMethod> createInstancesOfType = pluginService.createInstancesOfType(ProjectionMethod.class);
        
        node.getItems().addAll(createInstancesOfType);
        
        
        return node;
    }

    @Override
    public boolean handles(WidgetModel model) {
        return model.isType(ProjectionMethod.class);
    }
    
}
