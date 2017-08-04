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
package ijfx.ui.bindings;

import ijfx.ui.main.ImageJFX;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class SideMenuBinding {

    private BooleanProperty show = new SimpleBooleanProperty(false);

    private final Node node;

    private double xWhenHidden = 20;
    
    public SideMenuBinding(Node node) {
        this.node = node;
        
        show.addListener(this::onShowChanged);
        
         
        
    }
    
    public void refresh() {
        onShowChanged(show, Boolean.FALSE, showProperty().getValue());
    }

    public BooleanProperty showProperty() {
        return show;
    }

    public SideMenuBinding setxWhenHidden(double xWhenHidden) {
        this.xWhenHidden = xWhenHidden;
        return this;
    }
    
    

    private void onShowChanged(Observable obs, Boolean oldValue, Boolean newValue) {

        final Timeline timeline = new Timeline();

        if (newValue) {
            KeyValue kv = new KeyValue(node.translateXProperty(), 0, Interpolator.LINEAR);
            KeyFrame kf = new KeyFrame(ImageJFX.getAnimationDuration(), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
        } else {
            KeyValue kv = new KeyValue(node.translateXProperty(), -1 * (node.getBoundsInParent().getWidth() + xWhenHidden), Interpolator.LINEAR);
            KeyFrame kf = new KeyFrame(ImageJFX.getAnimationDuration(), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
        }
    }
    
    

}
