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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;
import org.scijava.widget.WidgetService;

/**
 *
 * @author florian
 */
public class PreferencesChoserGenerator implements PreferencePanelGenerator {

    private final WidgetService widgetService;
    private final GridPane gridPane;

    Map<String, List<WidgetModel>> widgetMap = new HashMap<>();
    List<String> categories = new ArrayList<>();
    boolean hasChanged = false;

    public PreferencesChoserGenerator(WidgetService widgetService) {
        this.widgetService = widgetService;
        gridPane = new GridPane();
        gridPane.getStyleClass().add("pref-pane");
    }

    private List<WidgetModel> getCategory(String category) {
        if (widgetMap.containsKey(category) == false) {
            widgetMap.put(category, new ArrayList<>());
            categories.add(category);
            hasChanged = true;
        }

        return widgetMap.get(category);
    }

    @Override
    public void addCategory(String category) {
        getCategory(category);
    }

    @Override
    public void addWidget(String category, WidgetModel model) {
        getCategory(category).add(model);
        hasChanged = true;
    }

    @Override
    public Node getPanel() {
        if (hasChanged) {
            gridPane.getChildren().clear();

            int rowCount = 0;

            for (String cat : categories) {

                Node categoryNode = createCategoryNode(cat);

                gridPane.add(categoryNode, 0, rowCount++, 2, 1);

                for (WidgetModel model : getCategory(cat)) {
                    gridPane.add(createWidgetLabel(model), 0, rowCount);
                    gridPane.add(createWidget(model), 1, rowCount);
                    rowCount++;
                }

            }
        }
        return gridPane;

    }

    private Node createCategoryNode(String name) {
        /*
        The good way to use this function is that the nodes in the list are the nodes returned by the method createWidget()
        But you can put what you want.
         */
        //VBox category = new VBox();
        Label label = new Label(name);
        label.getStyleClass().add("pref-category");
        return label;
    }

    public Node createWidgetLabel(WidgetModel model) {
        Label label = new Label(model.getWidgetLabel());
        label.getStyleClass().add("widget-label");
        return label;
    }

    public Node createWidget(WidgetModel widgetModel) {

        InputWidget<?, Node> newWidget = (InputWidget<?, Node>) this.widgetService.create(widgetModel);
        newWidget.refreshWidget();
        return newWidget.getComponent();
    }
}
