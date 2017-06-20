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
import ijfx.ui.service.AnnotationService;
import ijfx.ui.widgets.ValueAnnotationDisplayController;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author sapho
 */

@Plugin (type = AnnotationDisplay.class)
public class DefaultAnnotationDisplay extends Pane implements AnnotationDisplay{
    
    @Parameter
    AnnotationService annotationService;
    
    Mapper mapper = new DefaultMapper();
    
    private Pane pane;
    private TextField oldKey;
    private TextField newKey;
        
    private Button cancel;
    private Button mapping;
    
    private final String CANCEL = "Cancel";
    private final String MAPPING = "Mapping";
    private final double MAX_HEIGHT = 300.0;
    private final double MIN_HEIGHT = 100.0;
    private final double PADDING = 4.0;
    
    private List <ValueAnnotationDisplayController> listLittleV;
    private List <MetaData> collect;
    
    

    public DefaultAnnotationDisplay() {
        
        // initialisation des valeurs requises
        cancel.setText(CANCEL);
        mapping.setText(MAPPING);
        this.setMinHeight(MIN_HEIGHT);
        this.setMaxHeight(MAX_HEIGHT);
        this.setPadding(new Insets(PADDING));
        
        
        //si la liste contenant les fxml secondaire est vide, créer un second fxml et l'introduire dans la liste
        if (listLittleV == null){
            ValueAnnotationDisplayController littleW = new ValueAnnotationDisplayController();
            listLittleV.add(littleW);
            
        addListener(littleW);
        
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
            ValueAnnotationDisplayController newV = new ValueAnnotationDisplayController(); //je suis pas sure de mon coup la
            addListener(newV); //mise à jour du listener
        
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
    
   
        
    
}
