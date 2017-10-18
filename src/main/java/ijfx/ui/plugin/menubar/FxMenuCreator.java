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

import ijfx.core.icon.FXIconService;
import ijfx.core.uiplugin.FXUiCommandService;
import ijfx.core.usage.Usage;
import ijfx.ui.main.ImageJFX;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import mongis.utils.uuidmap.DefaultUUIDMap;
import mongis.utils.uuidmap.UUIDMap;
import net.mongis.usage.UsageType;
import org.scijava.Context;

import org.scijava.menu.AbstractMenuCreator;
import org.scijava.menu.ShadowMenu;
import org.scijava.plugin.Parameter;
import org.scijava.thread.ThreadService;

/**
 * This class generates allows generation of JavaFX Menu by ImageJ
 *
 * @author MONGIS Cyril
 */
public class FxMenuCreator extends AbstractMenuCreator<MenuBar, Menu> {

    @Parameter
    ThreadService threadService;

    @Parameter
    Context context;

    @Parameter
    FXIconService fxIconService;

    @Parameter
    FXUiCommandService fxUiCommandService;

    UUIDMap<MenuItem> menuMap = new DefaultUUIDMap<>();

    Logger logger = ImageJFX.getLogger();

    public FxMenuCreator(Context context) {
        context.inject(this);
    }

    @Override
    protected void addLeafToMenu(ShadowMenu sm, Menu m) {

        final String label = sm.getName();
        final String iconPath = sm.getModuleInfo().getIconPath();
        final String description = sm.getModuleInfo().getDescription();
        final EventHandler onAction = event -> {
            Usage
                    .factory()
                    .createUsageLog(UsageType.CLICK, sm.getName(), Usage.MENUBAR)
                    .setValue(sm.getName())
                    .send();
            
            
           threadService.run(sm);

        };
        
        Node icon = fxIconService.getIconAsNode(iconPath);

        MenuItem item = fxUiCommandService.createMenuItem(label, description, icon, onAction);
        
        menuMap
                .key(new Integer(sm.getMenuDepth()), sm.getName())
                .put(item);

       
        m.getItems().add(item);
    }

    @Override
    protected void addLeafToTop(ShadowMenu sm, MenuBar t) {

        t.getMenus().add(new Menu(sm.getName().toUpperCase()));
    }

    @Override
    protected Menu addNonLeafToMenu(ShadowMenu sm, Menu m) {
        
        final String label = sm.getName();
     
        final Node icon = null; //fxIconService.getIconAsNode("fa:ellipsis_v");
        MenuItem menuItem = fxUiCommandService.createMenuItem(label, null, icon, null);
        Menu newMenu = new Menu(null,menuItem.getGraphic());
        m.getItems().add(newMenu);
        menuMap
                .key(new Integer(sm.getMenuDepth()), sm.getName())
                .put(m);
        return newMenu;

    }

    @Override
    protected Menu addNonLeafToTop(ShadowMenu sm, MenuBar t) {

        Menu newMenu = new Menu(sm.getName());
      
        t.getMenus().add(newMenu);
        return newMenu;

    }

    @Override
    protected void addSeparatorToMenu(Menu m) {
        m.getItems().add(new SeparatorMenuItem());
    }

    @Override
    protected void addSeparatorToTop(MenuBar t) {
        ImageJFX.getLogger().warning("A seperator should have been inserted but was not.");
    }

    public void removeMenu(ShadowMenu sm) {
        logger.info("Removing " + sm.getName());
        MenuItem item = menuMap
                .key(new Integer(sm.getMenuDepth()), sm.getName())
                .get();
        if (item == null) {
            return;
        }
        Menu parent = item.getParentMenu();
        if (parent != null) {
            parent.getItems().remove(item);
        }
    }

    public void addMenu(ShadowMenu sm) {
        Menu menu = (Menu) menuMap
                .key(new Integer(sm.getMenuDepth()), sm.getName())
                .get();

        if (menu == null) {
            return;
        }

        if (sm.isLeaf()) {
            addLeafToMenu(sm, menu);
        } else {
            addNonLeafToMenu(sm, menu);
        }
    }

}
