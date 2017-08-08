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
package ijfx.explorer.views;

import ijfx.explorer.ExplorableDisplay;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.views.ViewStateManager.ViewState;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Object responsible of synchronize the ExplorableDisplay state
 * with view states.
 * @author cyril
 */
public class ViewStateManager extends WeakHashMap<ExplorerView, ViewState>{
    
    
    
    
    private static int NOT_CALCULATED = -10;
    
    ViewState displayState;
    
    
    
    public void updateState(ExplorableDisplay display) {
        
        displayState = new ViewState(display);
        
    }
    
    public void checkView(ExplorerView view, ExplorableDisplay display) {
        
        if(containsKey(view) == false) {
            put(view,new ViewState());
        }
        
        ViewState viewState = get(view);
        ViewState displayState = this.displayState;
        // if the content of the view is differnet of the content
        // of the display, it has to set the items
       if(viewState.listContent != displayState.listContent) {
           
           view.setItems(display.getDisplayedItems());
           
       }
       // otherwise, if the content is the same but the elements
       // inside where modified
       else {
           if(viewState.data != displayState.data) {
               view.refresh();
           }
       }
       
       if(displayState.selection != viewState.selection){
           view.setSelectedItem(display.getSelected());
       }
       
       put(view,displayState.clone());
       
    }
    
    
    
    public class ViewState {
        
        int listContent = NOT_CALCULATED;
                
        int data = NOT_CALCULATED;

        int selection = NOT_CALCULATED;

        public ViewState() {
            
        }
        
        public ViewState(ExplorableDisplay display) {
            listContent = ExplorableList.listHash(display.getDisplayedItems());
            data = ExplorableList.contentHash(display.getDisplayedItems());
            selection = ExplorableList.listHashWithOrder(display.getSelected());        
        }
        
        public ViewState clone() {
            ViewState state = new ViewState();
            state.data = data;
            state.selection = selection;
            state.listContent = listContent;
            return state;
        }
    }
    
    
    
    
}
