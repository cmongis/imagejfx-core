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
package ijfx.core.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.scijava.Context;
import org.scijava.UIDetails;
import org.scijava.event.EventService;
import org.scijava.menu.MenuCreator;
import org.scijava.menu.ShadowMenu;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class MenuGenerator {

    
    @Parameter
    Context context;
    
    @Parameter
    private EventService eventService;

    @Parameter
    private ModuleService moduleService;

    /**
     * Menu tree structures. There is one structure per menu root.
     */
    private HashMap<String, ShadowMenu> rootMenus;

    private Class<?> toExclude;

    public MenuGenerator(Context context) {
        context.inject(this);
        
    }
    
    private Context getContext() {
        return context;
    }

    public ShadowMenu getMenu() {
        return getMenu(UIDetails.APPLICATION_MENU_ROOT);
    }

    public ShadowMenu getMenu(final String menuRoot) {
        return rootMenus().get(menuRoot);
    }

    public <T> T createMenus(final MenuCreator<T> creator, final T menu) {
        return createMenus(UIDetails.APPLICATION_MENU_ROOT, creator, menu);
    }

    public <T> T createMenus(final String menuRoot,
            final MenuCreator<T> creator, final T menu) {
        creator.createMenus(getMenu(menuRoot), menu);
        return menu;
    }
    
    public boolean isType(Class<?> type,ModuleInfo info) {
        try {
            
            Class<?> c = info.loadDelegateClass();
            
            return type != null && type.isAssignableFrom(c);
        } catch (ClassNotFoundException ex) {
            return false;
        }

    }

    // -- Helper methods --
    /**
     * Adds the given collection of modules to the menu data structure.
     * <p>
     * The menu data structure is created lazily via {@link #rootMenus()} if it
     * does not already exist. Note that this may result in a recursive call to
     * this method to populate the menus with the collection of modules
     * currently known by the {@link ModuleService}.
     * </p>
     */
    
    public  void addModules(final Collection<ModuleInfo> items,Class<?> exclude) { 
        List<ModuleInfo> collect = items
                .stream()
          
                .filter(info->!isType(exclude,info))
                .collect(Collectors.toList());
        
        addModules(collect);
    }
    
    
    public synchronized void addModules(final Collection<ModuleInfo> items) {
        addModules(items, rootMenus());
    }

    /**
     * As {@link #addModules(Collection)} adding modules to the provided menu
     * root.
     */
    private synchronized void addModules(final Collection<ModuleInfo> items,
            final Map<String, ShadowMenu> rootMenu) {
        // categorize modules by menu root
        final HashMap<String, ArrayList<ModuleInfo>> modulesByMenuRoot
                = new HashMap<>();
        for (final ModuleInfo info : items) {
            final String menuRoot = info.getMenuRoot();
            ArrayList<ModuleInfo> modules = modulesByMenuRoot.get(menuRoot);
            if (modules == null) {
                modules = new ArrayList<>();
                modulesByMenuRoot.put(menuRoot, modules);
            }
            modules.add(info);
        }

        // process each menu root separately
        for (final String menuRoot : modulesByMenuRoot.keySet()) {
            final ArrayList<ModuleInfo> modules = modulesByMenuRoot.get(menuRoot);
            ShadowMenu menu = rootMenu.get(menuRoot);
            if (menu == null) {
                // new menu root: create new menu structure
                menu = new ShadowMenu(getContext(), modules);
                rootMenu.put(menuRoot, menu);
            } else {
                // existing menu root: add to menu structure
                menu.addAll(modules);
            }
        }

    }

    /**
     * Lazily creates the {@link #rootMenus} data structure.
     * <p>
     * Note that the data structure is initially populated with all modules
     * available from the {@link ModuleService}, which is accomplished via a
     * call to {@link #addModules(Collection)}, which calls
     * {@link #rootMenus()}, which can result in a level of recursion. This is
     * intended.
     * </p>
     */
    private HashMap<String, ShadowMenu> rootMenus() {
        if (rootMenus == null) {
            initRootMenus();
        }
        return rootMenus;
    }

    /**
     * Initializes {@link #rootMenus}.
     */
    private synchronized void initRootMenus() {
        if (rootMenus != null) {
            return;
        }
        final HashMap<String, ShadowMenu> map = new HashMap<>();
        rootMenus = map;
    }

}
