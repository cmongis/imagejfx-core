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
package ijfx.ui.inputharvesting;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.scijava.widget.AbstractInputPanel;
import org.scijava.widget.InputPanel;
import org.scijava.widget.InputWidget;

/**
 *
 * @author cyril
 */
public class ContextMenuInputPanel extends AbstractInputPanel<ContextMenu,Node>  {

    ContextMenu contextMenu;
    
    private final static String CSS_CLASS = "input-panel";
    
    
   @Override
   public boolean supports(InputWidget<?, ?> widget) {
       
       return Node.class.isAssignableFrom(widget.getComponentType());
   }
    
    @Override
    public void addWidget(InputWidget<?, Node> widget) {
        if(widget == null) return;
        super.addWidget(widget);
        
        MenuItem menuItem = new MenuItem();
        menuItem.setGraphic(widget.getComponent());
        
        getContextMenu().getItems().add(menuItem);
        
    }
    public ContextMenu getContextMenu() {
        if(contextMenu == null) {
            contextMenu = new ContextMenu();
            contextMenu.getStyleClass().add(CSS_CLASS);
        }
        return contextMenu;
    }

    @Override
    public Class<Node> getWidgetComponentType() {
        return Node.class;
    }

    @Override
    public ContextMenu getComponent() {
        
        
        
        return getContextMenu();
    }

    @Override
    public Class<ContextMenu> getComponentType() {
        return ContextMenu.class;
    }
    
   
}
