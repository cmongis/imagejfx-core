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

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import org.scijava.plugin.Plugin;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author cyril
 */
@Plugin(type = InputWidget.class)
public class AxisTypeWidget extends AbstractFXInputWidget<AxisType> implements InputWidget<AxisType, Node> {

    ComboBox<AxisType> axisTypeComboBox;

    @Override
    public void set(WidgetModel model) {
        super.set(model);

        axisTypeComboBox = new ComboBox<>();
        axisTypeComboBox.getItems().addAll(Axes.X, Axes.Y, Axes.Z, Axes.TIME, Axes.CHANNEL, Axes.get("Serie"));

        bindProperty(axisTypeComboBox.valueProperty());

    }

    @Override
    public Node getComponent() {
       return axisTypeComboBox;
    }
    
    public boolean supports(WidgetModel model) {
        return super.supports(model) && model.isType(AxisType.class);
    }

}
