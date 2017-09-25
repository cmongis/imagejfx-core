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
package ijfx.ui.mainwindow;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import mongis.utils.transition.TransitionBinding;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Cyril MONGIS
 */
public class SideMenuButton extends HBox {

        String appToOpen;

        Label label = new Label();
        Node iconNode;
        FontAwesomeIconView iconView;
        
        private BooleanProperty extendedProperty = new SimpleBooleanProperty();
        
        private StringProperty initialText = new SimpleStringProperty();
        
        public SideMenuButton() {

            super();
           
            getStyleClass().add("side-menu-button");
            setMaxWidth(Double.MAX_VALUE);
            iconView = new FontAwesomeIconView(FontAwesomeIcon.QUESTION);
            iconNode = new StackPane(iconView);
            iconNode.getStyleClass().add("side-menu-icon");

            getChildren().addAll(iconNode, label);

            new TransitionBinding<Number>(0d, 1d)
                    .bind(extendedProperty(), label.opacityProperty())
                    .setDuration(Duration.millis(150));

            label.textProperty().bind(Bindings.createStringBinding(this::getText, extendedProperty(),initialText));

        }
        
        public SideMenuButton(String name) {
            this();
            setName(name);
        }
        
        
         public SideMenuButton(String name, FontAwesomeIcon icon) {
            this();
            setName(name);
            setIcon(icon);
        }
        
        public SideMenuButton(String name, String appToOpen) {
            this(name);

            this.appToOpen = appToOpen;

        }

       
       

        public SideMenuButton setIcon(FontAwesomeIcon icon) {
            //GlyphsDude.createIcon(icon);

            iconView.setIcon(icon);
            return this;
        }
        

        public void setName(String name) {
            
            initialText.set(name);
        }
        
        public BooleanProperty extendedProperty() {
            return extendedProperty;
        }
        
        public String getText() {
            if (extendedProperty().getValue()) {
                return initialText.getValue();
            } else {
                return "";
            }
        }

        
       
    } 
