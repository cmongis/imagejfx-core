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
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;


/**
 * FXML Controller class
 *
 * @author sapho
 */
public class ValueAnnotationDisplayController extends ListCell<MetaData>  { //avant extends Pane

    //@FXML
    private final GridPane pane = new GridPane(); //oui mon gridpane s'appelle pane

    //@FXML
    TextField value = new TextField();
    TextField newValue = new TextField();
    
    
    
    private final AnchorPane content = new AnchorPane();
             
    
    private final double MIN_HEIGHT = 120.0;//hauteur
    private final double MIN_WIDTH = 30.0; //largeur
    private final double HEIGHT = 40.0; //hauteur
    private final double WIDTH = 244.0; //largeur
    private final double PADDING = 4.0;
    private final double COLUMN_WIDTH = 112.0;//largeur 
    private final double TEXT_WIDTH = 50.0;
    private final double TEXT_HEIGTH = 30.0;
    
    private final ObjectProperty<String> valueTextProperty = new SimpleObjectProperty(value.getText());
    private final ObjectProperty<String> newValueTextProperty = new SimpleObjectProperty(newValue.getText());
    
    
    
    private final ObservableValue<Boolean> fillState = Bindings.createObjectBinding(this:: getTextfieldState);
    private final BooleanProperty isValues = new SimpleBooleanProperty();
    
    

    public ValueAnnotationDisplayController() {
        
        Platform.runLater(()-> {
        
        pane.setPrefSize(WIDTH, HEIGHT);
        value.setPromptText("value");
        newValue.setPromptText("new value");
        
        
        //pane.setPadding(Insets.PADDING);
        
        
        value.setPrefSize(TEXT_WIDTH, TEXT_HEIGTH);
        value.setPadding(new Insets (5,5,5,15));
        value.setAlignment(Pos.CENTER);
        GridPane.setConstraints(value, 0, 0);
        //
        newValue.setPrefSize(TEXT_WIDTH, TEXT_HEIGTH);
        newValue.setPadding(new Insets (5,15,5,0)); //top droite bas gauche
        newValue.setAlignment(Pos.CENTER);
        GridPane.setConstraints(newValue, 1, 0);
        
        //pane.setPadding(new Insets(5, 5, 5, 5));
        pane.getColumnConstraints().add(new ColumnConstraints(COLUMN_WIDTH));
        pane.getColumnConstraints().add(new ColumnConstraints(COLUMN_WIDTH));
        
        pane.getChildren().setAll(value, newValue);
        content.getChildren().add(pane);
        
        System.out.println("pouet");
        init();
        setGraphic(content);
        });

    }
    
    
    
   @Override 
   public void updateItem(MetaData item, boolean empty) {
        super.updateItem(item, empty);
        
        if (empty){
            setGraphic(null);
        }
        if (!empty && item != null){
            value.setPromptText("value");
            newValue.setPromptText("new value");
            setGraphic(content);
            
        }
   }
    

    public void init(){
        
    }
    
    
    public ObjectProperty getValueTextProperty(){
        return valueTextProperty;
    }
    
    public ObjectProperty getNewValueTextProperty(){
        return newValueTextProperty;
    }
    
    //permet la récupération des données
    public String getValue(){
        return value.getText();
    }
    
    public String getNewValue(){
        return newValue.getText();
    }
        
    //récupération public de la property
    public BooleanProperty getIsValues(){
        return isValues;
    }

   //défini les conditions de l'observableValue.
    public Boolean getTextfieldState(){
        if (this.value.getText() != null && this.newValue.getText() != null){
            return true;
        }
        return false;
    }
    
    
}
