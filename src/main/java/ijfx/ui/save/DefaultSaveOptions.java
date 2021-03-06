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
package ijfx.ui.save;

import ijfx.ui.widgets.messagebox.DefaultMessageBox;
import ijfx.ui.widgets.messagebox.MessageBox;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.fxml.FXML;
import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import mongis.utils.FXUtilities;

import mongis.utils.bindings.FileButtonBinding;

/**
 *
 * @author Pierre BONNEAU
 */
public class DefaultSaveOptions extends VBox implements SaveOptions{
    
    private Property<SaveType> saveType;
    private Property<String>  suffix;
    private Property<File> folder;
    
   // private String CSS_FILE = getClass().getResource("/ijfx/ui/main/flatterfx.css").toExternalForm();
    
    @FXML
    private Label title;
    
    @FXML
    private RadioButton replaceFilesBtn;
    
    @FXML
    private RadioButton newFilesBtn;
    
    @FXML
    private TextField newSuffix;
    
    @FXML
    private Label extension;
    
    @FXML
    private Button destinationFolderBtn;
    

    
    private MessageBox messageBox;
        
    public DefaultSaveOptions() {

        try {
            FXUtilities.injectFXML(this);
            
            saveType = new SimpleObjectProperty<>(SaveType.NEW);
            suffix = new SimpleStringProperty();
            folder = new SimpleObjectProperty<>();
            
            ToggleGroup toggleGroup = new ToggleGroup();
            
            replaceFilesBtn.setToggleGroup(toggleGroup);
            newFilesBtn.setToggleGroup(toggleGroup);
            
            replaceFilesBtn.selectedProperty().setValue(Boolean.FALSE);
            newFilesBtn.selectedProperty().setValue(Boolean.TRUE);
            
            ObjectBinding<SaveType> obinding = Bindings.createObjectBinding(this::updateSaveType,
                    toggleGroup.selectedToggleProperty()
            );
            
            saveType().bind(obinding);
            
            StringBinding sbinding = Bindings.createStringBinding(()->{
                return newSuffix.textProperty().getValue();
            },
                    newSuffix.textProperty());
            
            suffix().bind(sbinding);
            
            newSuffix.disableProperty().bind(replaceFilesBtn.selectedProperty());
            destinationFolderBtn.disableProperty().bind(replaceFilesBtn.selectedProperty());
            
            FileButtonBinding fbinding = new FileButtonBinding(destinationFolderBtn);
            
            folder().bind(fbinding.fileProperty());
            
            
            messageBox = new DefaultMessageBox();
            
           
        } catch (IOException ex) {
            Logger.getLogger(DefaultSaveOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public Property<SaveType> saveType(){
        return this.saveType;
    }
    
    @Override
    public Property<String> suffix(){
        return this.suffix;
    }
    
    @Override
    public Property<File> folder(){
        return this.folder;
    }
    
    @Override
    public Node getContent(){
        return this;
    }
    
    
    public SaveType updateSaveType(){
        
        SaveType newSaveType = SaveType.NEW;
        
        if(replaceFilesBtn.selectedProperty().getValue()){
            newSaveType = SaveType.REPLACE;
        }
        else if(newFilesBtn.selectedProperty().getValue()){
            newSaveType = SaveType.NEW;
        }
        
        return newSaveType;
    }
}
