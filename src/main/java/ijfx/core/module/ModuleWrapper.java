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
package ijfx.core.module;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.scijava.SciJava;
import org.scijava.module.AbstractModule;
import org.scijava.module.ModuleInfo;
import org.scijava.plugin.SciJavaPlugin;

/**
 *
 * @author cyril
 */
public class ModuleWrapper<T extends SciJavaPlugin> extends AbstractModule {

    final T plugin;

    WrappedModuleInfo infos;

    public T getPlugin() {
        return plugin;
    }

    public ModuleWrapper(T plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setInput(String name, Object o) {
        super.setInput(name, o);
        

    }

    @Override
    public ModuleInfo getInfo() {
        if (infos == null) {
            infos = new WrappedModuleInfo(this);
        }
        return infos;
    }

    @Override
    public void run() {
        if (plugin instanceof Runnable) {
            ((Runnable) plugin).run();
        }
    }

}
