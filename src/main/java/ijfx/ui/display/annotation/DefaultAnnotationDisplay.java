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
import ijfx.explorer.datamodel.DefaultMapper;
import ijfx.explorer.datamodel.Mapper;
import ijfx.ui.display.image.AbstractFXDisplayPanel;
import ijfx.ui.display.image.FXDisplayPanel;
import ijfx.ui.service.AnnotationService;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.scijava.Context;
import org.scijava.command.CommandService;
import org.scijava.display.Display;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.viewer.DisplayWindow;
import org.scijava.widget.InputHarvester;

/**
 *
 * @author sapho
 */

public class DefaultAnnotationDisplay extends Dialog<Mapper> implements AnnotationDisplay{ //extend Pane or Dialog<Mapper>
    
    @Parameter
    AnnotationService annotationService;
    
    @FXML
    Pane root;
    
    @FXML    
    TextField oldKey, newKey;

    //@FXML
    //Button cancel, mapping;
    
    Dialog<Mapper> dialog;
    
    private final String CANCEL = "Cancel";
    private final String MAPPING = "Mapping";
    private final double MAX_HEIGHT = 300.0;
    private final double MIN_HEIGHT = 100.0;
    private final double PADDING = 4.0;
    
    private List <ValueAnnotationDisplayController> listLittleV; //liste des fxml secondaires
    private List <MetaData> collect; //je crois que ça sert à rien
    
    Mapper mapper = new DefaultMapper();
    
    public DefaultAnnotationDisplay() throws IOException {
        
        //Pane root = new Pane();
        //dialog = new Dialog<>();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/ijfx/ui/widgets/AnnotationDisplay.fxml"));
                loader.setController(DefaultAnnotationDisplay.this);
                
                //getRoot2(loader);
                Pane root = loader.getRoot();
                //loader.setRoot(root);
                //root = loader.getRoot();
                getDialogPane().setContent(root);
                getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
                setResultConverter(DefaultAnnotationDisplay.this::convert);
                try {
                    loader.setRoot(root);
                    loader.load();
                    
                } catch (IOException ex) {
                    Logger.getLogger(DefaultAnnotationDisplay.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                /*
                //si la liste contenant les fxml secondaire est vide, créer un second fxml et l'introduire dans la liste
                if (listLittleV == null){
                try {
                addMapping();
                } catch (IOException ex) {
                Logger.getLogger(DefaultAnnotationDisplay.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
                */
            }
        });
    }
    
    public Node getRoot2 (){
        return root;
    }

    public Mapper convert(ButtonType button) {

         if(button == ButtonType.OK) {
             //mapperAction();
             return mapper;
         }
         else {
             return null;
         }

     }

    
    
    //ajoute le listener au fxml demandé
    private void addListener (ValueAnnotationDisplayController name){
        name.getIsValues().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newBoolean) -> {
            if (newBoolean){
                try {
                    addMapping();
                } catch (IOException ex) {
                    Logger.getLogger(DefaultAnnotationDisplay.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
        
    private void addMapping() throws IOException{ //rajouter un fxm de cases values dès que le précédent est rempli // ET met à jour le listener
            ValueAnnotationDisplayController newV = new ValueAnnotationDisplayController(); //je suis pas sure de mon coup la
            addListener(newV); //mise à jour du listener
            listLittleV.add(newV); //ajoute le fxml fraichement crée dans la liste des fxml secondaires.
            
        
    }

    //je pense qu'on se passe de commentaire
    private void bindData (){
        mapper.setOldKey(oldKey.getText());
        mapper.setNewKey(newKey.getText());
        
        

    }
    
    public Mapper mapperAction(){ //lier au FXML AnnotationDisplay.fxml  avec SceneBuilder ;
        //bindData();
        //listLittleV.stream().map(c -> mapper.map(MetaData.create(c.getValue(), c.getNewValue())));
        return mapper;
        
        
    }
    
    public static void main(String... args) throws IOException {

         Mapper map = new DefaultAnnotationDisplay().showAndWait().get();


     }
    
  
}
