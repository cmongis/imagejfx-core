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
package ijfx.ui.test;

import ijfx.core.uiextra.ChoiceDialog;
import ijfx.explorer.datamodel.DefaultMapper;
import ijfx.explorer.datamodel.Mapper;
import ijfx.ui.display.annotation.DefaultAnnotationDisplay;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import mongis.utils.FXUtilities;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Plugin;
import org.scijava.widget.InputHarvester;

/**
 *
 * @author sapho
 */

//@Plugin(type = PreprocessorPlugin.class, menuPath="Plugins > Test > Generate Dummy Something", priority = InputHarvester.PRIORITY)

@Plugin(type = Command.class, menuPath="Plugins > Test > Generate Dummy Something")
public class DummySomething extends ContextCommand{
    
    Dialog dialog;

@Override
    public void run() {
        
        Platform.runLater( ()-> {
             DefaultAnnotationDisplay annot = null;
        try {
           annot = new DefaultAnnotationDisplay();
        } catch (IOException ex) {
            Logger.getLogger(DummySomething.class.getName()).log(Level.SEVERE, null, ex);
        }
        
                
        final Dialog<Mapper> dialog = new Dialog<>();
        final Mapper finalMapper = annot.mapperAction();
        
        
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
        
        
        dialog.setResultConverter(button->{
            if(button == ButtonType.OK) {
                return finalMapper;    
            }
            return null;
        });
        
        
    });
    }
        
    }
        
 
    

