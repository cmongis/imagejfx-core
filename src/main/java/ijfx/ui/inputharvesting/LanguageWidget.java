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
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptService;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = InputWidget.class)
public class LanguageWidget extends EasyInputWidget<ScriptLanguage> {

    @Parameter
    private ScriptService scriptService;

    private ComboBox<ScriptLanguage> combox;
    
    @Override
    public Property<ScriptLanguage> getProperty() {
        return combox.valueProperty();
    }

    @Override
    public Node createComponent() {
        combox = new ComboBox<>();
        combox.getItems().addAll(scriptService.getLanguages());
        
        return combox;
    }

     
    
    @Override
    public boolean handles(WidgetModel model) {
        return model.isType(ScriptLanguage.class);
    }

}
