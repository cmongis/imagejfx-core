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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author cyril
 */
@Plugin(type = InputWidget.class, priority = Priority.VERY_LOW_PRIORITY)
public class NoWidgetFX extends EasyInputWidget<Object> {

    ObjectProperty<Object> property;

    StringBinding textValue;

    @Override
    public Property<Object> getProperty() {
        if (property == null) {
            property = new SimpleObjectProperty<Object>(null);
        }
        return property;
    }

    @Override
    public Node createComponent() {

        Button button = new Button();

        textValue = Bindings.createStringBinding(this::getButtonText, getProperty());

        button.textProperty().bind(textValue);
        button.getStyleClass().add("warning");
        return button;
        
    }

    protected String getButtonText() {
        if (property.getValue() == null) {
            return "No value (null)";
        }
        return property.getValue().toString();
    }

    @Override
    public boolean handles(WidgetModel model) {
        return false;
    }

}
