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
package ijfx.ui.display.image;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import jfxtras.scene.control.window.Window;
import org.scijava.Context;
import org.scijava.display.Display;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginService;
import org.scijava.ui.viewer.DisplayPanel;
import org.scijava.ui.viewer.DisplayWindow;

/**
 *
 * @author cyril
 */

public class DisplayWindowFX extends Window implements DisplayWindow{

    @Parameter
    PluginService pluginService;
    
    FXDisplayPanel panel;
    
    public DisplayWindowFX(Display<?> display) {
        
        display.getContext().inject(this);
        
        panel = pluginService
                .createInstancesOfType(FXDisplayPanel.class)
                .stream()
                .filter(plugin->plugin.canView(display))
                .findFirst()
                .orElse(null);
        
        panel.view(this,display);
        
        if(panel != null) {
            panel.pack();
            panel.display(display);
            setContentPane(panel.getUIComponent());
        }
        else {
            setContentPane(new StackPane(new Label("No Display plugin :-(")));
        }
        
        
    }
    
    
    public FXDisplayPanel getDisplayPanel() {
        return panel;
    }
    
  
    
    
    @Override
    public void setContent(DisplayPanel panel) {
       
    }

    @Override
    public void pack() {
       
    }

    @Override
    public void showDisplay(boolean visible) {
        setVisible(visible);
    }

    @Override
    public int findDisplayContentScreenX() {
       return 0;
    }

    @Override
    public int findDisplayContentScreenY() {
      return 0;
    }
    
}
