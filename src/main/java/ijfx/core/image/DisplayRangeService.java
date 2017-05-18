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
package ijfx.core.image;

import ijfx.core.IjfxService;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mongis.utils.DefaultUUIDMap;
import net.imagej.Dataset;
import net.imagej.lut.LUTService;
import net.imglib2.display.ColorTable;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 *
 * @author cyril
 */
@Plugin(type = Service.class)
public class DisplayRangeService extends AbstractService implements IjfxService {

    private static final String MINIMUM = "minimum";
    private static final String MAXIMUM = "maximum";
    private final DefaultUUIDMap<Double> datasetChannelMin = new DefaultUUIDMap();

    ObservableList<ColorTable> colorTableList;

    @Parameter
    LUTService lutService;

    public ObservableList<ColorTable> availableColorTableProperty() {
        if (colorTableList == null) {
            List<ColorTable> tables = lutService
                    .findLUTs()
                    .values()
                    .parallelStream()
                    .map(this::loadLUT)
                    .filter(lut -> lut != null)
                    .collect(Collectors.toList());

            colorTableList = FXCollections.observableArrayList();
            colorTableList.addAll(tables);
        }
        return colorTableList;

    }

    public Number getDatasetMinimum(Dataset dataset, int channel) {
        if (dataset == null) {
            return 0;
        }
        if (dataset.getType().getBitsPerPixel() <= 8) {
            return 0;
        }
        System.out.printf("DatasetMinMax : Minimum : %s,%d,%s\n", dataset.toString(), channel, datasetChannelMin.key(dataset, channel, MINIMUM).id().toString());
        return datasetChannelMin.key(dataset, channel, MINIMUM).getOrPut(dataset.getChannelMinimum(channel));
    }

    public Number getDatasetMaximum(Dataset dataset, int channel) {

        
        
        if (dataset == null || dataset.getType().getBitsPerPixel() == 1) {
            return 1;
        }
        if (dataset.getType().getBitsPerPixel() == 8) {
            return 255;
        }

        System.out.printf("DatasetMinMax : Maximum : %s,%d,%s\n", dataset.toString(), channel, datasetChannelMin.key(dataset, channel, MAXIMUM).id().toString());
        return datasetChannelMin.key(dataset, channel, MAXIMUM).getOrPut(dataset.getChannelMaximum(channel));
    }

    private ColorTable loadLUT(URL url) {
        try {
            return lutService.loadLUT(url);
        } catch (Exception e) {
            return null;
        }
    }

    public void saveDatasetMinimum(Dataset dataset, int channel, double value) {
        datasetChannelMin.key(dataset, channel, MINIMUM).put(value);
    }

    public void saveDatasetMaximum(Dataset dataset, int channel, double value) {
        datasetChannelMin.key(dataset, channel, MAXIMUM).put(value);
    }

    public ColorTable getEquivalentTable(ColorTable table) {

        return availableColorTableProperty()
                .stream()
                .filter(other->compare(table,other))
                .findFirst()
                .orElseGet(()->{
                    availableColorTableProperty().add(table);
                    return table;
                });

    }
    
     public boolean compare(ColorTable table1, ColorTable table2) {

        if (table1.getLength() != table2.getLength()) {
            return false;
        }

        for (int i = 0; i != table1.getLength(); i++) {
            for (int c = 0; c != 3; c++) {

                if (table1.get(c, i) != table2.get(c, i)) {
                    return false;
                }

            }
        }
        return true;

    }

}
