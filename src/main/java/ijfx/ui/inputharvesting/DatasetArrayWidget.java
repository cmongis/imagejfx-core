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

import ijfx.ui.widgets.SelectionList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author cyril
 */
@Plugin(type = InputWidget.class)
public class DatasetArrayWidget extends EasyInputWidget<Dataset[]> {

    Property<Dataset[]> datasetArray = new SimpleObjectProperty<>();

    @Parameter
    ImageDisplayService imageDisplayService;

    @Override
    public Property<Dataset[]> getProperty() {
        return datasetArray;
    }

    @Override
    public Node createComponent() {
        datasetArray = new SimpleObjectProperty<>();
        SelectionList<Dataset> listView = new SelectionList();
        listView.prefHeight(100);
        listView.getSelectedItems().addListener(this::onSelectionChanged);
        listView
                .getItems()
                .addAll(
                        imageDisplayService
                                .getImageDisplays()
                                .stream()
                                .map(display -> imageDisplayService.getActiveDataset(display))
                                .collect(Collectors.toList())
                );
        return listView;
    }

    @Override
    public boolean handles(WidgetModel model) {
        return model.isType(Dataset[].class);
    }

    private void onSelectionChanged(ListChangeListener.Change<? extends Dataset> change) {
        while (change.next());
        List<? extends Dataset> selected = change.getList();

        Dataset[] array = new Dataset[selected.size()];
        datasetArray.setValue(selected.toArray(array));
    }
}
