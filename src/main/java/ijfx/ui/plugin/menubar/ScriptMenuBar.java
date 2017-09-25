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
package ijfx.ui.plugin.menubar;

import ijfx.commands.script.ScriptCommand;
import ijfx.core.menu.MenuGenerator;
import ijfx.core.uiplugin.Localization;
import ijfx.ui.UiConfiguration;
import ijfx.ui.UiPlugin;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import org.scijava.Context;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "script-menu-bar",context = "script-open",localization = Localization.TOP_LEFT)
public class ScriptMenuBar implements UiPlugin{

    MenuBar menuBar;
    
    @Parameter
    Context context;
    
    @Parameter
    PluginService pluginService;
    
    @Parameter
    ModuleService moduleService;
    
    @Override
    public Node getUiElement() {
        return menuBar;
    }

    @Override
    public UiPlugin init() {
        
        menuBar = new MenuBar();
        
        MenuGenerator menuGenerator = new MenuGenerator(context);
        
        
        menuGenerator.addModules(moduleService
                .getModules()
                .stream()
                .filter(info->menuGenerator.isType(ScriptCommand.class,info))
                .collect(Collectors.toList()));
        
        FxMenuCreator creator = new FxMenuCreator(context);
        
        menuGenerator.createMenus(creator, menuBar);
        
        return this;
    }
    
   
    
}
