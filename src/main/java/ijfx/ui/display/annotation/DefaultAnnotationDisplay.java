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
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Cell;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
    VBox vBox;

    Dialog<Mapper> dialog;

    private List <MetaData> listLittleV = new ArrayList<>(); //liste des fxml secondaires
    private List <ValueAnnotationDisplayController> list = new ArrayList<>();
    private final ObjectProperty valueTextProperty = new SimpleObjectProperty();
    private final ObjectProperty newValueTextProperty = new SimpleObjectProperty();
    private ObservableList <ValueAnnotationDisplayController> oList = FXCollections.observableArrayList();
    
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
                
                
            
                
                miseajour(); //rajoute le controller dans l'observablelist
                oList.addListener(new ListChangeListener<ValueAnnotationDisplayController>() {
                    @Override
                    public void onChanged(ListChangeListener.Change<? extends ValueAnnotationDisplayController> change) {
                      while (change.next()) {
                        for (ValueAnnotationDisplayController x : change.getList() ) { 
                            if(x.)
                            
                            
                        }
                        for (ValueAnnotationDisplayController x : oList) {
                          
                        }
                      }
                    }
                  });
                //addMapping();
                
                              
                showAndWait();
  
            }
        });
    }
    
    
    //necessaire au bon fonctionnement du dialog, c'est la fontion qui se déclenche quand on clique sur ok
    public Mapper convert(ButtonType button) {

         if(button == ButtonType.OK) {
             return mapperAction();
         }
         else {
             return null;
         }

     }

    
    private ObservableList miseajour(){
        ValueAnnotationDisplayController x = new ValueAnnotationDisplayController();
        oList.add(x);
        vBox.getChildren().add(x); //rajoute le controller dans le vbox
        return oList;
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
        
        for (ValueAnnotationDisplayController item : list){
            if (item.getNewValue() != " "){
                
                mapper.associatedValues(item.getValue(),item.getNewValue());
                
                
            }
            
        }
        System.out.println("mapper"+ mapper.getMapObject());
            
                });

        return mapper;
        
        
    }
    
    

}
