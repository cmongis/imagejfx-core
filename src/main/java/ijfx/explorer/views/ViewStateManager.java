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

import ijfx.explorer.ExplorableViewModel;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.views.ViewStateManager.ViewState;
import ijfx.ui.filters.metadata.TaggableFilterPanel;
import java.util.List;
import java.util.WeakHashMap;
import mongis.utils.task.ProgressHandler;

/**
 * Object responsible of synchronize the ExplorableViewModel state with view
 * states.
 *
 * @author Cyril MONGIS
 */
public class ViewStateManager extends WeakHashMap<ExplorerView, ViewState> {

    private static int NOT_CALCULATED = -10;

    private ViewState displayState;

    private int itemState = NOT_CALCULATED;

    private TaggableFilterPanel taggleFilterPanel;

    public void setTaggleFilterPanel(TaggableFilterPanel taggleFilterPanel) {
        this.taggleFilterPanel = taggleFilterPanel;
    }

    
    
    
    public void updateState(ExplorableViewModel model) {

        displayState = new ViewState(model);
        checkFilterPanel(model);

    }

    
    
    public void checkFilterPanel(ExplorableViewModel model) {

        if (taggleFilterPanel != null) {

            int newItemState = ExplorableList.contentHash(model.getItems());

            if (newItemState != this.itemState) {

                itemState = newItemState;
                taggleFilterPanel.updateFilters(ProgressHandler.check(null), model.getItems());
            }

        }
    }

    public synchronized void checkView(ExplorerView view, ExplorableViewModel display) {

        if (displayState == null || display == null) {
            return;
        }
        if (containsKey(view) == false) {
            put(view, new ViewState());
        }

        ViewState viewState = get(view);
        ViewState displayState = this.displayState;
        // if the content of the view is differnet of the content
        // of the display, it has to set the items
        if (viewState.listContent != displayState.listContent) {

            view.setItems(display.getDisplayedItems());

        } // otherwise, if the content is the same but the elements
        // inside where modified
        else {
            if (viewState.displayedData != displayState.displayedData) {
                view.refresh();
            }
        }

        if (displayState.selection != viewState.selection) {
            view.setSelectedItem(display.getSelectedItems());
        }

        put(view, displayState.clone());

    }

    public class ViewState {

        int listContent = NOT_CALCULATED;

        int displayedData = NOT_CALCULATED;

        int selection = NOT_CALCULATED;

        public ViewState() {

        }

        public ViewState(ExplorableViewModel display) {
            listContent = ExplorableList.listHash(display.getDisplayedItems());
            displayedData = ExplorableList.contentHash(display.getDisplayedItems());
            selection = ExplorableList.listHashWithOrder(display.getSelectedItems());
        }

        public ViewState clone() {
            ViewState state = new ViewState();
            state.displayedData = displayedData;
            state.selection = selection;
            state.listContent = listContent;
            return state;
        }
    }

}
