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
package ijfx.ui.display.code;

import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.scijava.plugin.Parameter;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;
import org.scijava.widget.WidgetService;

/**
 *
 * @author florian
 */
public class PreferencesChoserGenerator implements PreferencePanelGenerator {
    WidgetService widgetService;
    private VBox widgetBox;

    public PreferencesChoserGenerator(WidgetService widgetService) {
        this.widgetService = widgetService;
        this.widgetBox = new VBox();
    }
    
    
    @Override
    public Node getPanel(){
        return this.widgetBox;
    }
    
    @Override
    public void addCategory( List<Node> widgets, String name){
        /*
        The good way to use this function is that the nodes in the list are the nodes returned by the method createWidget()
        But you can put what you want.
        */
        VBox category = new VBox();
        category.getChildren().add(new Label(name));
        for (Node widget : widgets){
            category.getChildren().add(widget);
        }
        this.widgetBox.getChildren().add(category);
    }
    
    @Override
    public void addWidget(WidgetModel widgetModel, String name){
        this.widgetBox.getChildren().add(createWidget(widgetModel, name));
    }
    
    @Override
    public void addWidget(WidgetModel widgetModel){
        InputWidget<?,Node> newWidget =(InputWidget<?, Node>) widgetService.create(widgetModel);
        newWidget.refreshWidget();
        this.widgetBox.getChildren().add(newWidget.getComponent());
    }
    
    @Override
    public Node createWidget(WidgetModel widgetModel, String name){
         HBox newBox = new HBox();
        newBox.setPadding(new Insets(20));
        Label label = new Label(name);
        label.setPadding(new Insets(20));
        newBox.getChildren().add(label);
        InputWidget<?,Node> newWidget =(InputWidget<?, Node>) this.widgetService.create(widgetModel);
        newWidget.refreshWidget();
        newBox.getChildren().add(newWidget.getComponent());
        return newBox;
    }
}
