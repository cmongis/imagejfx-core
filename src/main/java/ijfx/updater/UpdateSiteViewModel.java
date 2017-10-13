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
package ijfx.updater;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import net.imagej.updater.FilesCollection;
import net.imagej.updater.UpdateSite;

/**
 *
 * @author cyril
 */
public class UpdateSiteViewModel {
    
    
    final FilesCollection collection;
    
    final String name;

    BooleanProperty activeProperty;
    
    StringProperty nameProperty;
    
    public UpdateSiteViewModel(FilesCollection collection, String name) {
        this.collection = collection;
        this.name = name;
        
        try {
            activeProperty = new JavaBeanBooleanPropertyBuilder()
                    .bean(this)
                    .name("active")
                    .build();
            
            
            
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(UpdateSiteViewModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void setActive(Boolean bool) {
    
        collection.getUpdateSite(name, true).setActive(true);
        
    }
    public Boolean isActive() {
        return collection.getUpdateSite(name, true).isActive();
    }
    
    public String getName() {
        return getUpdateSite().getName();
    }
    
    public void setName(String name) {
        getUpdateSite().setName(name);
    }
    
    private UpdateSite getUpdateSite() {
        return collection.getUpdateSite(name, true);
    }

    public FilesCollection getCollection() {
        return collection;
    }

    public BooleanProperty activeProperty() {
        return activeProperty;
    }
    
    
    
    
    
}
