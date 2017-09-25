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
package ijfx.explorer.display;

import ijfx.explorer.ExplorableViewModel;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.views.DataClickEvent;
import java.util.function.Consumer;

/**
 *
 * @author Cyril MONGIS
 */
public class DataClickEventListener implements Consumer<DataClickEvent<Explorable>> {
    
    final ExplorableViewModel model;

    public DataClickEventListener(ExplorableViewModel model) {
        this.model = model;
    }

    @Override
    public void accept(DataClickEvent<Explorable> event) {
        int selected = model.getSelectedItems().size();
        Explorable clicked = event.getData();
        if (event.getEvent() == null) {
            model.selectOnly(clicked);
            model.update();
            return;
        }
        boolean isShiftDown = event.getEvent() != null ? event.getEvent().isShiftDown() : false;
        boolean isAlreadySelected = model.getSelectedItems().contains(clicked);
        if (isShiftDown && selected > 0) {
            model.selectUntil(clicked);
        } else if (isAlreadySelected && selected == 1) {
            model.getSelectedItems().remove(clicked);
        } else {
            model.selectOnly(clicked);
        }
        model.update();
    }
    
}
