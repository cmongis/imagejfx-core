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
package ijfx.ui.plugin.annotation;

import ijfx.core.metadata.MetaDataService;
import ijfx.core.uiplugin.Localization;
import ijfx.explorer.ExplorableDisplay;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.Tag;
import ijfx.explorer.datamodel.Taggable;
import ijfx.ui.UiConfiguration;
import ijfx.ui.UiPlugin;
import ijfx.ui.widgets.TaggablePane;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import org.scijava.Context;
import org.scijava.display.event.DisplayActivatedEvent;
import org.scijava.display.event.DisplayUpdatedEvent;
import org.scijava.event.EventHandler;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "tag-bar", localization = Localization.RIGHT, context = "explorable-open")
public class TagEditingBar implements UiPlugin {

    private TaggablePane taggablePane;

    @Parameter
    private Context context;

    @Parameter
    private MetaDataService metaDataService;

    private ExplorableDisplay display;

    private IntegerProperty limit = new SimpleIntegerProperty(5);
    
    @Override
    public Node getUiElement() {
        return taggablePane.getContentPane();
    }

    @Override
    public UiPlugin init() throws Exception {

        taggablePane = new TaggablePane(context,TaggablePane.Orientation.VERTICAL)
                .setOnAdd(this::addTag)
                .setOnRemove(this::removeTag);
        
        
        return this;
    }
    
    public void addTag(Taggable taggable, Tag tag) {
        List<Explorable> selected = display.getSelectedItems();
                selected.forEach(explorable->explorable.addTag(tag));
        display.update();
    }
    
    public void removeTag(Taggable taggable, Tag tag) {
        display
                .getSelectedItems()
                .forEach(explorable->explorable.deleteTag(tag));
        
        display.update();      
    }

    @EventHandler
    public void onDisplayChanged(DisplayActivatedEvent event) {

        if (event.getDisplay() instanceof ExplorableDisplay) {

            display = (ExplorableDisplay) event.getDisplay();
            updateView();
        }

    }

    @EventHandler
    public void onDisplayUpdated(DisplayUpdatedEvent event) {

        if (display == null) {
            return;
        }

        updateView();

    }

    public void updateView() {
        if(display == null) return;
        if(display.size() == 0) return;
        
        taggablePane.setTaggle(display.getSelectedItems().stream().findFirst().orElse(null));
        
        taggablePane.setTags(display
                .getSelectedItems()
                .stream()
                .flatMap(explorable -> explorable.getTagList().stream())
                .collect(Collectors.toSet()));

        taggablePane.setPossibleTags(
                limitToLast(
                display.getItems()
                        .stream()
                        .flatMap(explorable -> explorable.getTagList().stream())
                .collect(Collectors.toSet()
                ),limit.get()));
        
        //taggablePane.refresh();
    }
    
    
    private <T> Collection <T> limitToLast(Collection<T> collection, int limit) {
        
        if(collection.size() >  limit) {
            return new ArrayList(collection).subList(collection.size()-6, collection.size()-1);
        }
        else {
            return new ArrayList(collection);
        }
        
    }

}
