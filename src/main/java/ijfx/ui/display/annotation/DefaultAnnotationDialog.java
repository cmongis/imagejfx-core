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

import ijfx.explorer.datamodel.DefaultMapper;
import ijfx.explorer.datamodel.Mapper;
import ijfx.ui.service.AnnotationService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.scijava.plugin.Parameter;

/**
 *
 * @author sapho
 */

public class DefaultAnnotationDialog extends Dialog<Mapper> implements AnnotationDialog{
    
    @Parameter
    AnnotationService annotationService;
    
    @FXML
    Pane root, pane;
    
    @FXML    
    TextField oldKey, newKey;
    
    @FXML
    VBox vBox;

    Dialog<Mapper> dialog;
    String FXMLWAY = "/ijfx/ui/widgets/AnnotationDisplay.fxml";
    
    
    //Main List. This observableList observe this tow objects properties in each controller. If theses properties change, a notification is sended.
    private ObservableList <DataAnnotationController> oList = FXCollections.observableArrayList(c -> new ObservableValue[]{c.getValueTextProperty(), c.getNewValueTextProperty()});
    //Secondary list. Purpose : stock temporaly controller to know which controller can create new controller or not.
    private List <DataAnnotationController> updatedList = new ArrayList<>();
    

    Mapper mapper = new DefaultMapper();
    
    
    
    public DefaultAnnotationDialog() throws IOException {
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                
                loadFXML();
                
                firstUse();

                listenerTraitment();
                
                showAndWait();
  
            }
        });
    }
    
    /**
     * Traitment according properties state and kind of notification.
     */
    private void listenerTraitment (){
        oList.addListener((ListChangeListener.Change<? extends DataAnnotationController> change) -> { 
        while (change.next()) { 
                        
                        if(change.wasAdded()) {
                            vBox.getChildren().addAll(change.getAddedSubList());
                        }
                        if(change.wasRemoved()){
                            vBox.getChildren().removeAll(change.getRemoved());
                        }
                        if (change.wasUpdated()) {
                            if (!updatedList.contains(change.getList().get(change.getFrom()))){
                                updatedList.add(change.getList().get(change.getFrom()));
                                DataAnnotationController y = new DataAnnotationController();
                                oList.add(y);
                            }
                            if (change.getList().get(change.getFrom()).getState()){
                                oList.remove(change.getList().get(change.getFrom()));
                            }
                            
                            }
                            
                    }
                });
    }

    private void loadFXML(){
        FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource(FXMLWAY));
                loader.setController(DefaultAnnotationDialog.this);
                
                try {
                    loader.load();
                    
                } catch (IOException ex) {
                    Logger.getLogger(DefaultAnnotationDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

                Pane root = loader.getRoot();
                ///////////////////////////////////CSS PART
                //root.getStylesheets().add(getClass().getResource("/ijfx/ui/flatterfx.css").toExternalForm());
                //root.applyCss();
                ///////////////////////////////////
                getDialogPane().setContent(root);
                getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
                setResultConverter(DefaultAnnotationDialog.this::convert);
    }
       
    /**
     * First controller creation and put in the Observable List
     * @return 
     */
    @Override
    public ObservableList firstUse(){ //création du premier controlleur et mise dans la liste
        DataAnnotationController x = new DataAnnotationController();
        oList.add(x);
        vBox.getChildren().add(x);
        
        return oList;
    }
    
    

    /**
     * Keys retriever to mapper 
     */
    public void bindData (){
        
        Platform.runLater(()-> {
            mapper.setOldKey(oldKey.getText());
            mapper.setNewKey(newKey.getText());
        });
        
    }
    
    /**
     * Dialog need : methode run when the button is cliqued
     * @param button
     * @return 
     */
    public Mapper convert(ButtonType button) { //CA CA MARCHE
        
        if(button == ButtonType.OK) {
             return mapperAction();
         }
        else {
             return null;
         }

     }
    
    /**
     * Add new data in the mapper hasmap. 
     * @return 
     */
    public Mapper mapperAction(){ 
        
        Platform.runLater(()-> {
        bindData();
        
        for (DataAnnotationController item : oList){
            if (item.getNewValue() != " "){
                mapper.associatedValues(item.getValue(),item.getNewValue());
            }
        }
        System.out.println("mapper"+ mapper.getMapObject());
            
        });

        return mapper;
        
    }
    
}
