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
package ijfx.ui.activity;

import ijfx.core.activity.Activity;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import jfxtras.scene.control.window.Window;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;

/**
 *
 * @author cyril
 */
@Plugin(type = Activity.class, name = "imagej")
public class DisplayContainer extends StackPane implements Activity {

    public AnchorPane anchorPane = new AnchorPane();

    public DisplayContainer() {
        getChildren().add(anchorPane);
    }

    public void addWindow(Window window) {

        Platform.runLater(() -> {
            
            anchorPane.getChildren().add(window);
            window.setPrefSize(500, 400);
        });
    }

    @Override
    public Node getContent() {
        return this;
    }

    @Override
    public Task updateOnShow() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void showContextMenu(String menuRoot, Display<?> display, int x, int y) {
        
    }

}
