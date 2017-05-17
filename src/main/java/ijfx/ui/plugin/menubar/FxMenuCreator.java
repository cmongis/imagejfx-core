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

import ijfx.core.usage.Usage;
import ijfx.ui.main.ImageJFX;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.mongis.usage.UsageType;
import org.scijava.Context;

import org.scijava.menu.AbstractMenuCreator;
import org.scijava.menu.ShadowMenu;
import org.scijava.plugin.Parameter;
import org.scijava.thread.ThreadService;
import static ucar.nc2.util.net.EasyX509TrustManager.logger;

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
    
    Map<ShadowMenu,MenuItem> menuMap = new HashMap<>();
    
    
    Logger logger = ImageJFX.getLogger();
    
    @Override
    protected void addLeafToMenu(ShadowMenu sm, Menu m) {
        MenuItem item = new MenuItem(sm.getName());
        
        menuMap.put(sm, item);
        
        item.addEventHandler(ActionEvent.ANY, event->{
            Usage
                    .factory()
                    .createUsageLog(UsageType.CLICK, sm.getName(), Usage.MENUBAR)
                    .setValue(sm.getName())
                    .send();
        });
        
        item.setOnAction(event->{
            //ImageJFX.getThreadPool().submit(sm);
            
//            context.inject(sm);
            threadService.run(sm);
            
            
        });
        m.getItems().add(item);
    }

    @Override
    protected void addLeafToTop(ShadowMenu sm, MenuBar t) {
        
        t.getMenus().add(new Menu(sm.getName()));
    }

    @Override
    protected Menu addNonLeafToMenu(ShadowMenu sm, Menu m) {
        Menu newMenu = new Menu(sm.getName());
        m.getItems().add(newMenu);
        menuMap.put(sm, newMenu);
        return newMenu;

    }

    @Override
    protected Menu addNonLeafToTop(ShadowMenu sm, MenuBar t) {

        Menu newMenu = new Menu(sm.getName());
        menuMap.put(sm, newMenu);
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
        logger.info("Removing "+sm.getName());
        MenuItem item = menuMap.get(sm);
        if(item == null) return;
        Menu parent = item.getParentMenu();
        if(parent != null) {
            parent.getItems().remove(item);
        }
    }
    
    public void addMenu(ShadowMenu sm) {
        Menu menu = (Menu) menuMap.get(sm.getParent());
        if(sm.isLeaf()) {
            addLeafToMenu(sm, menu);
        }
        else {
            addNonLeafToMenu(sm, menu);
        }
    }

}
