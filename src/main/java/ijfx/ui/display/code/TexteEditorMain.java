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
package ijfx.ui.display.code;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.script.TextEditor;

/**
 *
 * @author florian
 */
@Plugin(type = Command.class, menuPath = "Plugins > script editor ImageJFX")
public class TexteEditorMain implements Command {
    @Parameter
    private Context context;
    
    @Override
    public void run() {
        
        Parent root = null;
        try {
            root = new TextEditorController();
        } catch (IOException ex) {
            Logger.getLogger(TexteEditorMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        Scene scene = new Scene(root);
        Platform.runLater( () ->{

            Stage stage = new  Stage();
            stage.setScene(scene);
            stage.show();
        });

    }
    
}
