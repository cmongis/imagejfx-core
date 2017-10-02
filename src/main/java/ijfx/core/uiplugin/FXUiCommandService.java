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
package ijfx.core.uiplugin;

import ijfx.core.IjfxService;
import ijfx.core.icon.FXIconService;
import ijfx.core.uiextra.UIExtraService;
import ijfx.core.usage.Usage;
import ijfx.core.utils.SciJavaUtils;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.collections.SetChangeListener;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import net.mongis.usage.UsageLocation;
import org.scijava.Priority;
import org.scijava.command.CommandInfo;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.SciJavaPlugin;
import org.scijava.service.Service;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = Service.class, priority = Priority.NORMAL_PRIORITY)
public class FXUiCommandService extends DefaultUiCommandService implements IjfxService {

    @Parameter
    FXIconService fxIconService;

    @Parameter
    CommandService commandService;

    @Parameter
    UIExtraService uiExtraService;

    public <T> MenuItem createMenuItem(UiCommand<T> action) {
        return createMenuItem(action, null);
    }

    public <T> MenuItem createMenuItem(UiCommand<T> action, T object) {

        return createMenuItem(new UiCommandWrapper(action, action));

    }

    public MenuItem createMenuItem(LabelledAction action) {

        
        return createMenuItem(action.label(), action.description(), fxIconService.getIconAsNode(action.iconPath()), event->action.runner().accept(action.data()));
        
        /*
        MenuItem menuItem = new MenuItem(action.label(), fxIconService.getIconAsNode(action.iconPath()));
       
        
        
        menuItem.getPseudoClassStates().addListener((Observable change) -> {
            System.out.println(change);
        });
        menuItem.getPseudoClassStates().addListener(new DescriptAttachedToPseudoClass(action.description()));
        
        //button.setTooltip(new Tooltip(action.description()));
        menuItem.setOnAction(event -> {
            action.runner().accept(action.data());
        });

        return menuItem;*/

    }

    public Button createButton(LabelledAction action) {
        
        
        
        Button button = new Button(action.label(), fxIconService.getIconAsNode(action.iconPath()));

        button.setTooltip(new Tooltip(action.description()));

        attacheDescription(button, action.description());

        button.setOnAction(event -> {
            action.runner().accept(action.data());
        });

        return button;

    }

    public Button createButton(SciJavaPlugin plugin) {
        Button button = new Button(SciJavaUtils.getLabel(plugin), fxIconService.getIconAsNode(plugin));
        button.setTooltip(new Tooltip(SciJavaUtils.getDescription(plugin)));
        attacheDescription(button, SciJavaUtils.getDescription(plugin));
        return button;
    }

    public Button createButton(PluginInfo<?> infos) {
        Button button = new Button(infos.getLabel(), fxIconService.getIconAsNode(infos.getIconPath()));

        attacheDescription(button, infos.getDescription());

        //button.setTooltip(new Tooltip(infos.getDescription()));
        return button;
    }

    public <T> Button createButton(UiCommand<T> action, T object) {
        return createButton(new UiCommandWrapper(action, action));
    }

    public MenuItem createMenuItem(String label, final String description, Node icon, EventHandler event) {

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add("fx-menu-item");
       
        borderPane.setCenter(new Label(label));
        
        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("icon");
        if(icon != null) {
            iconContainer.getChildren().add(icon);
        }
        
        borderPane.setLeft(iconContainer);
        
        borderPane.addEventHandler(MouseEvent.MOUSE_ENTERED, ev -> {
            uiExtraService.showDescriptoin(description);

        });
        borderPane.addEventHandler(MouseEvent.MOUSE_EXITED, ev -> {
            uiExtraService.showDescriptoin(null);
        });
        MenuItem menuItem = new MenuItem(null, borderPane);
        menuItem.setOnAction(event);
        return menuItem;

    }

    public <T> List<LabelledAction> wrap(List<UiCommand<T>> actions, T acceptor) {
        return actions
                .stream()
                .map(action -> new UiCommandWrapper<>(action, acceptor))
                .collect(Collectors.toList());
    }

    public <T> List<LabelledAction> wrap(List<CommandInfo> commandInfoList) {
        return commandInfoList
                .stream()
                .map(info -> new CommandInfoWrapper(info))
                .collect(Collectors.toList());
    }

    public void generateButtonBar(String acceptorUsageId, List<LabelledAction> actionLIst, List<Node> buttonAcceptor, List<MenuItem> itemAcceptor) {

        actionLIst.sort(LabelledAction::compare);

        int len = actionLIst.size();
        int limit = len > 3 ? 3 : len;

        List<Button> buttons = actionLIst
                .subList(0, limit)
                .stream()
                .map(this::createButton)
                .peek(button -> listenButton(button, acceptorUsageId))
                .collect(Collectors.toList());

        buttonAcceptor.addAll(buttons);

        if (len > limit) {
            List<MenuItem> items = actionLIst
                    .subList(limit, len)
                    .stream()
                    .map(this::createMenuItem)
                    .collect(Collectors.toList());

            itemAcceptor.addAll(items);

        }
    }

    public <T> void generateButtonBar(T acceptor, String acceptorUsageId, List<UiCommand<T>> actionLIst, List<Node> buttonAcceptor, List<MenuItem> itemAcceptor) {

        int len = actionLIst.size();
        int limit = len > 3 ? 3 : len;

        actionLIst.sort(UiCommand::compare);

        List<Button> buttons = actionLIst
                .subList(0, limit)
                .stream()
                .map(action -> createButton(action, acceptor))
                .peek(item -> listenButton(item, acceptorUsageId))
                .collect(Collectors.toList());

        buttonAcceptor.addAll(buttons);

        if (len > limit) {
            List<MenuItem> items = actionLIst
                    .subList(limit, len)
                    .stream()
                    .map(action -> createMenuItem(action, acceptor))
                    .collect(Collectors.toList());

            itemAcceptor.addAll(items);

        }

    }

    private void listenButton(Button item, String location) {

        Usage.listenButton(item, new UsageLocation(location), item.getText());
    }

    public void attacheDescription(Node node, String description) {
        if (description != null) {
            node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                uiExtraService.showDescriptoin(description);
            });

            node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                uiExtraService.showDescriptoin(null);
            });
        }
    }

    public class UiCommandWrapper<T> implements LabelledAction<T> {

        final private UiCommand<T> command;
        final T acceptor;
        final double priority;

        public UiCommandWrapper(UiCommand<T> t, T acceptor) {
            this.command = t;
            this.acceptor = acceptor;
            priority = SciJavaUtils.getPriority(t);
        }

        @Override
        public String description() {
            return SciJavaUtils.getDescription(command);
        }

        @Override
        public T data() {
            return acceptor;
        }

        @Override
        public String label() {
            return SciJavaUtils.getLabel(command);
        }

        @Override
        public String iconPath() {
            return SciJavaUtils.getIconPath(command);
        }

        @Override
        public Consumer<T> runner() {
            return command::run;
        }

        @Override
        public double priority() {
            return priority;
        }
    }

    public class CommandInfoWrapper implements LabelledAction<CommandInfo> {

        final CommandInfo info;

        public CommandInfoWrapper(CommandInfo info) {
            this.info = info;
        }

        @Override
        public CommandInfo data() {
            return info;
        }

        @Override
        public String label() {
            return info.getLabel();
        }

        @Override
        public String description() {
            return info.getDescription();
        }

        @Override
        public String iconPath() {
            return info.getIconPath();
        }

        @Override
        public Consumer<CommandInfo> runner() {
            return this::run;
        }

        @Override
        public double priority() {
            return info.getPriority();
        }

        public void run(CommandInfo info) {
            commandService.run(info, true);
        }

    }
    
    static final PseudoClass HOVER = PseudoClass.getPseudoClass("hover");
    
    private class DescriptAttachedToPseudoClass implements SetChangeListener<PseudoClass> {

        
        
        final String description;

        public DescriptAttachedToPseudoClass(String description) {
            this.description = description;
        }
        
        
        
        @Override
        public void onChanged(Change<? extends PseudoClass> change) {
            
           if(change.getSet().contains(HOVER)) {
               uiExtraService.showDescriptoin(description);
           }
           else {
               uiExtraService.showDescriptoin(null);
           }

        }
        
    }

}
