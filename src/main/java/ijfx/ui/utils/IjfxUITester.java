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
package ijfx.ui.utils;

import ijfx.ui.main.ImageJFX;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mongis.utils.UITesterBase;
import net.imagej.ImageJ;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public abstract class IjfxUITester extends UITesterBase {

    BorderPane borderPane = new BorderPane();

    ToolBar toolbar = new ToolBar();

    Stage primaryStage;
    
    @Parameter
    Context context;
    
    
    protected Context getContext() {
        return context;
    }
    
    public IjfxUITester() {

       super();
       ImageJ ij = new ImageJ();
        
       ij.context().inject(this);
    
     
    }

   
    @Override
    public String getStyleSheet() {
        return ImageJFX.getStylesheet();
    }
    
    
}