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

import ijfx.core.metadata.GenericMetaData;
import ijfx.core.metadata.MetaData;
import ijfx.explorer.datamodel.DefaultMapper;
import ijfx.explorer.datamodel.Mapper;
import ijfx.ui.service.AnnotationService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.scijava.plugin.Parameter;

/**
 *
 * @author sapho
 */

public class DefaultAnnotationDisplay extends Dialog<Mapper> implements AnnotationDisplay{ //extend Pane or Dialog<Mapper>
    
    @Parameter
    AnnotationService annotationService;
    
    @FXML
    Pane root, pane;
    
    @FXML    
    TextField oldKey, newKey;
    
    @FXML
    ListView<ValueAnnotationDisplayController> listView;

    Dialog<Mapper> dialog;

    private List <ValueAnnotationDisplayController> listLittleV = new ArrayList<>(); //liste des fxml secondaires
    private List <ValueAnnotationDisplayController> collect = new ArrayList<>(); //je crois que ça sert à rien
    
    private final ObjectProperty<Text> valueTextProperty = new SimpleObjectProperty();
    private final ObjectProperty<Text> newValueTextProperty = new SimpleObjectProperty();
    
    
    Mapper mapper = new DefaultMapper();
    
    public DefaultAnnotationDisplay() throws IOException {
        
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/ijfx/ui/widgets/AnnotationDisplay.fxml"));
                loader.setController(DefaultAnnotationDisplay.this);
                
                try {
                    loader.load();
                    
                } catch (IOException ex) {
                    Logger.getLogger(DefaultAnnotationDisplay.class.getName()).log(Level.SEVERE, null, ex);
                }

                Pane root = loader.getRoot();
                ///////////////////////////////////CSS PART
                //root.getStylesheets().add(getClass().getResource("/ijfx/ui/flatterfx.css").toExternalForm());
                //root.applyCss();
                ///////////////////////////////////
                getDialogPane().setContent(root);
                getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
                setResultConverter(DefaultAnnotationDisplay.this::convert);
                
                
                
                //si la liste contenant les Metadata est vide, créer un premier metadata sans spécificités
                if (listLittleV.isEmpty() ){
                    addMapping();
                }
                
                refresh();
                                
                showAndWait();
  
            }
        });
    }
    
    
    //necessaire au bon fonctionnement du dialog, c'est la fontion qui se déclenche quand on clique sur ok
    public Mapper convert(ButtonType button) {

         if(button == ButtonType.OK) {
             mapperAction();
             System.out.println(mapper.getMapObject());
             return mapper;
         }
         else {
             return null;
         }

     }

    
    /*
    //ajoute le listener au fxml demandé
    private void addListener2 (ValueAnnotationDisplayController name){
        name.getIsValues().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newBoolean) -> {
            if (newBoolean){
                    addMapping();
                
            }
        });
    }
    */
    
        
    private void addMapping() { //rajouter un fxm de cases values dès que le précédent est rempli // ET met à jour le listener
        
        Platform.runLater(()-> {
            //GenericMetaData m = new GenericMetaData();
            ValueAnnotationDisplayController v = new ValueAnnotationDisplayController();
            listLittleV.add(v); //ajoute le futur métadata dans la liste des métadata à ajouter au mapper.
            
            refresh();
        });
        
        
            
            
        
    }

    //Récupère les clefs necessaires au fonctionnement du mapper.
    private void bindData (){ //CA CA MARCHE
        Platform.runLater(()-> {
            mapper.setOldKey(oldKey.getText());
            mapper.setNewKey(newKey.getText());
        });
        
        
        

    }
    
    public Mapper mapperAction(){ //permet de d'ajouter les nouveaux metadata dans le map et renvoi le hashmap
        Platform.runLater(()-> {
        bindData();
        
        collect = (List) listView.getItems().stream().collect(Collectors.toList());
            for (ValueAnnotationDisplayController item : collect){
                System.out.println("liste des controleurs secondaires" +item.getValue());
                /*
                for (MetaData m : listLittleV){
                    m.setName(item.getValueTextProperty().toString());
                    m.setValue(item.getNewValueTextProperty());
                    //annotationService.addMapper(item, mapper);
        
        }
            */
        
                
            }
                
                });
            
        
        
        
        return mapper;
        
        
    }
    
    
    public void refresh(){ //permet la corrélation entre les metadata présents dans la liste et l'affichage de la listview.
        
            /*
            for (MetaData item : listLittleV){
                //listView.setCellFactory(lv  -> new ValueAnnotationDisplayController());
                listView.getItems().add(new ValueAnnotationDisplayController());
            }
            
            */
            //ValueAnnotationDisplayController v = new ValueAnnotationDisplayController(); NE MARCHE PAS

            listView.setCellFactory(lv  -> new ValueAnnotationDisplayController());
            
            listView.getItems().clear();
            listView.getItems().setAll(listLittleV);
            
            
            
        
    }
  

}
