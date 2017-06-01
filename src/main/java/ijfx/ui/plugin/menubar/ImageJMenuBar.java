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
package ijfx.ui.plugin.menubar;

import ijfx.commands.script.ScriptCommand;
import ijfx.ui.UiPlugin;
import ijfx.core.legacy.ImageJ1PluginService;
import ijfx.core.menu.MenuGenerator;
import ijfx.ui.main.ImageJFX;

import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import org.scijava.Context;
import org.scijava.command.CommandService;
import org.scijava.menu.MenuService;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;
import ijfx.ui.UiConfiguration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.MouseEvent;
import mongis.utils.transition.TransitionBinding;
import org.scijava.event.EventHandler;
import org.scijava.menu.ShadowMenu;
import org.scijava.menu.event.MenusAddedEvent;
import org.scijava.menu.event.MenusRemovedEvent;

/**
 *
 * @author Cyril MONGIS, 2015
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "imagej-menu-bar", context = "imagej+visualize -script-open", localization = "topCenterHBox")
public class ImageJMenuBar extends MenuBar implements UiPlugin {

    @Parameter
    private MenuService menuService;

    //@Parameter
    //LegacyService legacyService;

    @Parameter
    private ModuleService moduleService;

    @Parameter
    private PluginService pluginService;

    @Parameter
    private ImageJ1PluginService ij1PluginService;

    @Parameter
    private CommandService commandService;

    @Parameter
    private Context context;
    
    
    private FxMenuCreator creator;
    
    @Override
    public Node getUiElement() {
        return this;
    }

    final Logger logger = ImageJFX.getLogger();

   

    public static final String CSS_IJ1_CMD = "ij1-command";
    
    BooleanProperty isHover = new SimpleBooleanProperty();
    
    @Override
    public UiPlugin init() {
        creator = new FxMenuCreator(context);
        
        
        MenuGenerator menuGenerator = new MenuGenerator(context);
        
        
        menuGenerator.addModules(moduleService.getModules(),ScriptCommand.class);
        menuGenerator.createMenus(creator, this);
       
        addEventHandler(MouseEvent.MOUSE_ENTERED,event->isHover.setValue(true));
        addEventHandler(MouseEvent.MOUSE_EXITED,event->isHover.setValue(false));
        
        new TransitionBinding<Double>(0.5, 1.0)
                .bind(isHover,opacityProperty().asObject());
        
      
        
        
        return this;

    }

    @EventHandler
    private void onMenusAddedEvent(MenusAddedEvent event) {
        
        ShadowMenu parent = event.getItems().get(0).getParent();
        
        event.getItems().forEach(creator::addMenu);
        
    }
    
    @EventHandler
    private void onMenusRemovedEvent(MenusRemovedEvent event) {
        event.getItems().forEach(creator::removeMenu);
    }
    
    
    
    public Menu getParentMenu(String path) {
        String[] folders = path.split("/");

        int depth = 0;
        final String topFolder = folders[depth];
        Menu parentMenu = getMenus().stream().filter(m -> m.getText().equals(topFolder)).findFirst().get();
        Menu childMenu = parentMenu;

        if (parentMenu == null) {
            parentMenu = new Menu(topFolder);
            this.getMenus().add(parentMenu);
        }

        while (depth < folders.length - 2 && parentMenu != null) {

            depth++;

            final String currentFolder = folders[depth];
            //System.out.println(String.format("#%s : folder = %s, depth = %d, parrent menu = %s", path, currentFolder, depth, parentMenu.getText()));
            try {
                childMenu = (Menu) parentMenu.getItems().stream().filter(m -> currentFolder.equals(m.getText())).findFirst().orElseThrow(null);
            } catch (Exception e) {
                
                childMenu = null;

            }
            if (childMenu == null) {
                //System.out.println(String.format("#%s : child menu '%s' doesn't exist. Creating one", path, currentFolder));
                childMenu = new Menu(currentFolder);
                parentMenu.getItems().add(childMenu);
            }
            parentMenu = childMenu;

        }

        return childMenu;
    }

  

    
    public class LegacyCommandInfo {

        public String path;
        public String command;
    }

}
