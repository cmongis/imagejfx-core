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
package ijfx.segmentation.core;

import javafx.scene.Node;
import net.imagej.ops.Initializable;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public interface InteractiveSegmentationUI<T extends InteractiveSegmentation> extends SciJavaPlugin,Initializable{
    
    //void process(ImageDisplay display);
    
    Node getContentNode();
    
    public Class<? extends InteractiveSegmentation> getSupportedType();
   
    public default boolean supports(InteractiveSegmentation segmentation) {
        if(segmentation == null) return false;
        return segmentation.getClass() == getSupportedType();
    }
  
    public T getCurrentSegmentation();
    
    void bind(T t);
    
    public default String getName() {
        return getClass().getAnnotation(Plugin.class).label();
    }
    
}
