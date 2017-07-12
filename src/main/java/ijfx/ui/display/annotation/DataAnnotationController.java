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

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;


/**
 * FXML Controller class
 *
 * @author sapho
 */
public class DataAnnotationController extends GridPane  { 
    
    Boolean wasModify = false;

    private final GridPane pane = new GridPane();

    TextField oldValue = new TextField();
    TextField newValue = new TextField();
    
    private final double HEIGHT = 40.0; //hauteur
    private final double WIDTH = 244.0; //largeur
    private final double PADDING = 4.0;
    private final double COLUMN_WIDTH = 112.0;//largeur 
    private final double TEXT_WIDTH = 40.0;
    private final double TEXT_HEIGTH = 30.0;
    private final int HGAP = 28;
   
    public final ObservableValue<Boolean> modifyState = Bindings.createObjectBinding(this::getState, oldValue.textProperty(), newValue.textProperty());
    
    public DataAnnotationController() {
        
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setHgap(HGAP);
        oldValue.setPromptText("value");
        newValue.setPromptText("new value");
        //
        oldValue.setPrefSize(TEXT_WIDTH, TEXT_HEIGTH);
        oldValue.setAlignment(Pos.CENTER);
        GridPane.setConstraints(oldValue, 0, 0);
        //
        newValue.setPrefSize(TEXT_WIDTH, TEXT_HEIGTH);
        newValue.setPadding(new Insets (5,15,5,0)); //top right bot left
        newValue.setAlignment(Pos.CENTER);
        GridPane.setConstraints(newValue, 1, 0);
        
        pane.setPadding(new Insets(5, 5, 5, 6));
        pane.getColumnConstraints().add(new ColumnConstraints(COLUMN_WIDTH+5));
        pane.getColumnConstraints().add(new ColumnConstraints(COLUMN_WIDTH));
        pane.getChildren().setAll(oldValue, newValue);
        
        this.getChildren().add(pane); //Don't forget this line overwise the class is not a node !
        System.out.println("coucou");
              
    }
    /**
     *If textfields are empty again, return true : this controller can be delete.
     * @return 
     */
    public Boolean getState (){
        if (oldValue.getText().equals("") && newValue.getText().equals("")){
            return true;
        }
        return false;
    }
   
    //Property retriever
    public StringProperty getValueTextProperty(){        
        return oldValue.textProperty();
    }
    
    public StringProperty getNewValueTextProperty(){
        return newValue.textProperty();
    }
    
    //Getters and setters
    public String getValue(){
        return oldValue.getText();
    }
    
    public String getNewValue(){
        return newValue.getText();
    }
    
    public void setValue(String value){
        oldValue.setText(value);
    }
    
    public void setNewValue(String value){
        newValue.setText(value);
    }
    
    //Boolean Observer to state retriever
    public ObservableValue<Boolean> getWasModifyState (){
       return modifyState;
       
   }
}
