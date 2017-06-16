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
package ijfx.ui.display.annotation;

import ijfx.core.metadata.MetaData;
import ijfx.ui.service.AnnotationService;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.scijava.plugin.Parameter;

/**
 *
 * @author sapho
 */
public class DefaultAnnotationDisplay extends Pane implements AnnotationDisplay{
    
    @Parameter
    AnnotationService annotationService;
    
    private Pane pane;
    private TextField key;
    private TextField new_key;
    private TextField value;
    private TextField new_value;
    private Button cancel;
    private Button add_more;
    private Button mapping;
    
    private String CANCEL = "Cancel";
    private String ADD_MORE = "+";
    private String MAPPING = "Mapping";
    

    public DefaultAnnotationDisplay() {
        
    }
    
    private void bindButton (){
        cancel.setText(CANCEL);
        add_more.setText(ADD_MORE);
        mapping.setText(MAPPING);
        
        
        mapping.setOnAction(annotationService.addMapper(MetaData.NULL));
    }
    
    
}
