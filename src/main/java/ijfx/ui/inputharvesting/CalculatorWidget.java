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

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.imagej.operator.CalculatorOp;
import net.imagej.operator.CalculatorService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author cyril
 */
@Plugin(type = InputWidget.class)
public class CalculatorWidget extends EasyInputWidget<CalculatorOp> {
     ComboBox<CalculatorOp> comboBox;
    
    
    @Parameter
    CalculatorService calculatorService;
    
    @Override
    public Property<CalculatorOp> getProperty() {
        return comboBox.valueProperty();
    }

    @Override
    public Node createComponent() {
        comboBox = new ComboBox<CalculatorOp>();
        
        comboBox
                .getItems()
                .addAll(
                        calculatorService
                                .getInstances()
                        
                    );
        
        return comboBox;
    }

    @Override
    public boolean handles(WidgetModel model) {
        return model.isType(CalculatorOp.class);
    }
}
