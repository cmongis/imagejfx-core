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
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.viewer.DisplayWindow;

/**
 *
 * @author sapho
 */

//@Plugin(type = FXDisplayPanel.class)
public class DefaultAnnotationDisplay extends Dialog<Mapper> implements AnnotationDisplay{
    
    @Parameter
    AnnotationService annotationService;
    
    @Parameter
    Scene scene;
    
      
    @FXML
    Pane root;
    
    @FXML    
    TextField oldKey, newKey;

    @FXML
    Button cancel, mapping;
    
    Context context;
    
    Display display;
    
    private final String CANCEL = "Cancel";
    private final String MAPPING = "Mapping";
    private final double MAX_HEIGHT = 300.0;
    private final double MIN_HEIGHT = 100.0;
    private final double PADDING = 4.0;
    
    private List <ValueAnnotationDisplayController> listLittleV; //liste des fxml secondaires
    private List <MetaData> collect; //je crois que ça sert à rien
    
    Mapper mapper = new DefaultMapper();
    
    public DefaultAnnotationDisplay() throws IOException {
        
        FXMLLoader loader = new FXMLLoader();
         loader.setLocation(getClass().getResource("/fxml/AnnotationDisplay.fxml"));

         loader.setController(this);

         loader.load();

         Pane root = loader.getRoot();

         getDialogPane().setContent(root);

        getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);

         setResultConverter(this::convert);
         
         //si la liste contenant les fxml secondaire est vide, créer un second fxml et l'introduire dans la liste
        if (listLittleV == null){
            addMapping();
        }

     

        }
    

     public Mapper convert(ButtonType button) {

         if(button == ButtonType.OK) {
             //
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
                addMapping();
            }
        });
    }
    
        
    private void addMapping(){ //rajouter un fxm de cases values dès que le précédent est rempli // ET met à jour le listener
            //ValueAnnotationDisplayController newV = new ValueAnnotationDisplayController(); //je suis pas sure de mon coup la
            //addListener(newV); //mise à jour du listener
            //listLittleV.add(newV); //ajoute le fxml fraichement crée dans la liste des fxml secondaires.
            //this.add(newV); //ajoute le fxml à la scene ?? ca marche pas
        
    }

    //je pense qu'on se passe de commentaire
    private void bindData (){
        mapper.setOldKey(oldKey.getText());
        mapper.setNewKey(newKey.getText());
        
        

    }
    
    private void mapperAction(){ //lier au FXML AnnotationDisplay.fxml  avec SceneBuilder ;
        bindData();
        listLittleV.stream().map(c -> mapper.map(MetaData.create(c.getValue(), c.getNewValue())));
        
        
    }
    
    public static void main(String... args) throws IOException {

         Mapper map = new DefaultAnnotationDisplay().showAndWait().get();


     }
    
  
}
