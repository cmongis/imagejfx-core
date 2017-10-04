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

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import mongis.utils.FXUtilities;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.InputWidget;
import org.scijava.widget.NumberWidget;
import org.scijava.widget.TextWidget;
import org.scijava.widget.WidgetService;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = Command.class, menuPath = "Plugins > Test > Generic Input harvesting")
public class GenericInputHarvestingTest extends ContextCommand{

    @Parameter
    WidgetService widgetService;
    
    
    String value = "It works !";
    
    Boolean booleanValue;
    
    DoubleProperty doubleProperty = new SimpleDoubleProperty(20);
    
   
    
    
    
    @Override
    public void run() {
        
        Dialog dialog = FXUtilities.runAndWait(()->new Dialog());
       
        VBox vbox = new VBox();
        
        // we create the widget
        InputWidget<?, Node> textWidget = (InputWidget<?, Node>) widgetService.create(
                new SuppliedWidgetModel<>(String.class)
                .setGetter(this::getValue)
                .setSetter(this::setValue)
                .setWidgetStyle(TextWidget.FIELD_STYLE)
        );
        
        
        InputWidget<?,Node> numberWidget =  (InputWidget<?, Node>) widgetService.create(
                new SuppliedWidgetModel<>(Number.class)
                .setGetter(doubleProperty::getValue)
                .setSetter(doubleProperty::setValue)
                .setWidgetStyle(NumberWidget.SPINNER_STYLE)
            );
        
        InputWidget<?,Node> booleanWidget = (InputWidget<?, Node>) widgetService.create(
                new SuppliedWidgetModel<>(Boolean.class)
                .setGetter(()->booleanValue)
                .setSetter(value->booleanValue = value)
        );
                
                
        // refresh the view to correspond to the value
        // (will call the setter)
        textWidget.refreshWidget();
        numberWidget.refreshWidget();
        booleanWidget.refreshWidget();
        
        // adding the nodes to the view
        vbox.getChildren().addAll(textWidget.getComponent(),numberWidget.getComponent(),booleanWidget.getComponent());
        dialog.getDialogPane().setContent(vbox);
       
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        
        // opening the dialog
        Platform.runLater(dialog::show);
        

    }
    
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
        System.out.println("the value has changed to "+value);
    }
    
}
