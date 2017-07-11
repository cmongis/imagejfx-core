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
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


/**
 * FXML Controller class
 *
 * @author sapho
 */
public class ValueAnnotationDisplayController extends GridPane  { 
    
    Boolean wasModify = false;

    private final GridPane pane = new GridPane(); //oui mon gridpane s'appelle pane

    TextField oldValue = new TextField();
    TextField newValue = new TextField();
    
    private final double HEIGHT = 40.0; //hauteur
    private final double WIDTH = 244.0; //largeur
    private final double PADDING = 4.0;
    private final double COLUMN_WIDTH = 112.0;//largeur 
    private final double TEXT_WIDTH = 50.0;
    private final double TEXT_HEIGTH = 30.0;
   
    public final ObservableValue<Boolean> modifyState = Bindings.createObjectBinding(this::getState, oldValue.textProperty(), newValue.textProperty());
    
    public ValueAnnotationDisplayController() {
        
        pane.setPrefSize(WIDTH, HEIGHT);
        oldValue.setPromptText("value");
        newValue.setPromptText("new value");
        
        oldValue.setPrefSize(TEXT_WIDTH, TEXT_HEIGTH);
        oldValue.setPadding(new Insets (5,5,5,15));
        oldValue.setAlignment(Pos.CENTER);
        GridPane.setConstraints(oldValue, 0, 0);
        //
        newValue.setPrefSize(TEXT_WIDTH, TEXT_HEIGTH);
        newValue.setPadding(new Insets (5,15,5,0)); //top droite bas gauche
        newValue.setAlignment(Pos.CENTER);
        GridPane.setConstraints(newValue, 1, 0);
        
        //pane.setPadding(new Insets(5, 5, 5, 5));
        pane.getColumnConstraints().add(new ColumnConstraints(COLUMN_WIDTH));
        pane.getColumnConstraints().add(new ColumnConstraints(COLUMN_WIDTH));
        
        pane.getChildren().setAll(oldValue, newValue);
        
        this.getChildren().add(pane); //NE PAS OUBLIER CETTE LIGNE SINON LA CLASSE N'EST PAS UN NODE
        System.out.println("pouet");
              
    }
    
    public Boolean getState (){
        if (oldValue.textProperty().isEmpty() && newValue.textProperty().isEmpty()){
            return true;
        }
        return true;
    }
   
    //permet la récupération des property;
    public StringProperty getValueTextProperty(){        
        return oldValue.textProperty();
    }
    
    public StringProperty getNewValueTextProperty(){
        return newValue.textProperty();
    }
    
    public TextField getTextField(){
        return newValue;
    }
    
    //permet la récupération des données
    public String getValue(){
        return oldValue.getText();
    }
    
    public String getNewValue(){
        return newValue.getText();
    }

   public Boolean getWasModify(){
       return this.wasModify;
   }
   
   public ObservableValue<Boolean> getWasModifyState (){
       return modifyState;
       
   }
}
