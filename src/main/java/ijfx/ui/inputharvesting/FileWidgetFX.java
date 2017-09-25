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
package ijfx.ui.inputharvesting;

import java.io.File;
import javafx.scene.Node;
import javafx.scene.control.Button;
import mongis.utils.FileButtonBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.scijava.plugin.Plugin;
import org.scijava.widget.FileWidget;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = InputWidget.class)
public class FileWidgetFX extends AbstractFXInputWidget<File> implements FileWidget<Node> {

    Button button;

    FileButtonBinding binding;

    @Override
    public void set(WidgetModel model) {
        super.set(model);
        
        button = new Button();
        binding = new FileButtonBinding(button);
        
        FileButtonBinding.Mode mode = null;

        // first we try to get the adaptive widget style
        // to get the type and configure the file dialog
        if (model.getItem() != null) {

            String style = model.getItem().getWidgetStyle();

            if (style != null) {
                style = style.toLowerCase();
                if (style.contains("open")) {
                    
                    
                    mode = FileButtonBinding.Mode.OPEN;
                    binding.setExtensions(extractExtensions(style));
                } else if (style.contains("save")) {
                    mode = FileButtonBinding.Mode.SAVE;
                    binding.setExtensions(extractExtensions(style));
                }
            }

        }

        // otherwise
        if (mode == null) {
            if (model.isStyle(DIRECTORY_STYLE)) {
                mode = FileButtonBinding.Mode.FOLDER;
            } else if (model.isStyle(SAVE_STYLE)) {
                mode = FileButtonBinding.Mode.SAVE;
            } else {
                mode = FileButtonBinding.Mode.OPEN;

            }
        }
        binding.setMode(mode);

        bindProperty(binding.fileProperty());

    }

    @Override
    public Node getComponent() {
        return button;
    }

    public boolean supports(WidgetModel model) {
        return super.supports(model) && model.isType(File.class
        );
    }

    /**
     * Extract the list of extensions from a widget style string
     * e.g. save csv png --> ['csv','png']
     * @param style
     * @return an array containing the list of exention
     */
    private String[] extractExtensions(String style) {
        
        if(style.contains(" ") == false) {
            return new String[0];
        }
        
        String[] exts = style.split(" ");
        exts = ArrayUtils.subarray(exts, 1, exts.length);
        
        return exts;
        
        
    }
    
    
}
