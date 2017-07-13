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
package mongis.utils.panecell;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import mongis.utils.FXUtilities;

/**
 *
 * @author cyril
 */
public class PaneCellControllerFX<T> {
    
    ObservableList<T> items = FXCollections.observableArrayList();
    
    ObservableList<PaneCell<T>> cache = FXCollections.observableArrayList();
    
    ObservableList<PaneCell<T>> displayed = FXCollections.observableArrayList();
    
    final List<Node> nodeList;
    
    final Callable<PaneCell<T>> factory;

    public PaneCellControllerFX(List<Node> nodeList, Callable<PaneCell<T>> factory) {
        this.nodeList = nodeList;
        this.factory = factory;
        
        
        
    }
    
    
    public void update(List<T> items) {
        
        if(items.size() > cache.size()) {
            
            
            int toCreate = items.size() - cache.size();
            
            List<PaneCell<T>> newCells = IntStream
                    .of(toCreate)
                    .parallel()
                    .mapToObj(this::create)
                    .collect(Collectors.toList());
            
            
            cache.addAll(newCells);
        }
        
        
    }
    
    
    private PaneCell<T> create(int i) {
        try {
            return factory.call();
        } catch (Exception ex) {
            Logger.getLogger(PaneCellControllerFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    
    
    
    
}
