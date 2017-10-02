/*
 * /*
 *     This file is part of ImageJ FX.
 *
 *     ImageJ FX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ImageJ FX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ImageJ FX.  If not, see <http://www.gnu.org/licenses/>. 
 *
 * 	Copyright 2015,2016 Cyril MONGIS, Michael Knop
 *
 */
package ijfx.ui.plugin.menubar.toolbar;

import ijfx.core.icon.FXIconService;
import ijfx.core.module.ModuleWrapper;
import ijfx.core.uicontext.UiContextService;
import ijfx.core.uiextra.UIExtraService;
import ijfx.core.uiplugin.FXUiCommandService;
import ijfx.core.uiplugin.Localization;
import ijfx.core.uiplugin.UiPluginService;
import ijfx.ui.UiPlugin;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import org.scijava.Context;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;
import ijfx.ui.UiConfiguration;
import ijfx.ui.inputharvesting.ContextMenuInputPanel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import jfxtras.scene.control.ToggleGroupValue;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.module.ModuleItem;
import org.scijava.module.ModuleService;
import org.scijava.tool.Tool;
import org.scijava.tool.ToolService;
import org.scijava.tool.event.ToolActivatedEvent;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;
import org.scijava.widget.WidgetService;

/**
 *
 * @author Cyril MONGIS, 2015
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "imagej-toolbar", context = "imagej+visualize+image-open", localization = Localization.LEFT)
public class ImageJToolBar extends VBox implements UiPlugin {

    @Parameter
    Context context;

    @Parameter
    UiContextService contextService;

    @Parameter
    UiPluginService loaderService;

    @Parameter
    PluginService pluginService;

    @Parameter
    ToolService toolService;

    @Parameter
    ModuleService moduleService;

    @Parameter
    FXIconService fxIconService;

    @Parameter
    EventService eventService;

    @Parameter
    UIExtraService uiExtraService;
    
    @Parameter
    FXUiCommandService fXUiCommandService;
    

    //ToggleGroup toggleGroup = new ToggleGroup();
    ToggleGroupValue<Tool> currentTool = new ToggleGroupValue<>();

    @Override
    public Node getUiElement() {

        return this;
    }

    @Override
    public UiPlugin init() {

        getStyleClass().addAll("toggle-group", "vertical");

        List<ToggleButton> buttonList = toolService
                .getTools()
                .stream()
                .filter(tool -> tool.getClass().getSimpleName().contains("Swing") == false)
                .filter(tool -> !tool.isAlwaysActive())
                .map(this::createButton)
                .collect(Collectors.toList());

        getChildren().addAll(buttonList);

        getChildren().get(0).getStyleClass().add("first");
        getChildren().get(getChildren().size() - 1).getStyleClass().add("last");

        currentTool.setValue(toolService.getActiveTool());

        return this;
    }

    public ToggleButton createButton(Tool tool) {

        StackPane iconPane = new StackPane();
        iconPane.getStyleClass().add("icon-pane");
        iconPane.getChildren().add(fxIconService.getIconAsNode(tool));
        ToggleButton button = new ToggleButton("", iconPane);
        
        String description = new StringBuilder()
                .append(tool.getClass().getSimpleName())
                .append(("( "))
                .append(tool.getDescription())
                .append(" )")
                .toString();
        
        

        button.setContextMenu(createContextMenu(tool));
        fXUiCommandService.attacheDescription(button, description);
        currentTool.add(button, tool);

        ToolListener listener = new ToolListener(tool);

        button.addEventFilter(MouseEvent.MOUSE_PRESSED, listener);
        button.addEventFilter(MouseEvent.MOUSE_PRESSED, listener);
        
        //toggleGroup.getToggles().add(button);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        //button.selectedProperty().addListener(new ToolListener(tool));
        return button;
    }

    @EventHandler
    public void onToolActivated(ToolActivatedEvent event) {
        currentTool.setValue(event.getTool());
    }

    @Parameter
    WidgetService widgetService;

    private ContextMenu createContextMenu(Tool tool) {

        ModuleWrapper<Tool> wrapper = new ModuleWrapper<>(tool);
        ContextMenuInputPanel inputPanel = new ContextMenuInputPanel();

        for (ModuleItem<?> input : wrapper.getInfo().inputs()) {
            WidgetModel model = widgetService.createModel(inputPanel, wrapper, input, new ArrayList<>());
            InputWidget<?, ?> widget = widgetService.find(model);
            if (widget == null) {
                continue;
            }
            widget.set(model);
            inputPanel.addWidget((InputWidget<?, Node>) widget);

        }

        return inputPanel.getComponent();

    }

    private class ToolListener implements javafx.event.EventHandler<MouseEvent> {

        private final Tool tool;

        public ToolListener(Tool tool) {
            this.tool = tool;
        }

        @Override
        public void handle(MouseEvent event) {
            if (event.isPrimaryButtonDown()) {
                toolService.setActiveTool(tool);
                event.consume();
            }
        }

    }

}
