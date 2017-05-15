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
package ijfx.core.batch.item;

import ijfx.core.image.ImagePlaneService;
import ijfx.core.metadata.MetaDataService;
import ijfx.core.utils.DimensionUtils;
import net.imagej.Dataset;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class DatasetPlaneWrapper extends AbstractLoaderWrapper<Dataset>
{

    Dataset source;
    
    Dataset current;
    
    long[] position;
    
    @Parameter
    private ImagePlaneService imagePlaneService;
    
    @Parameter
    private MetaDataService metaDataService;
    
    public DatasetPlaneWrapper(Context context,Dataset dataset, long[] planePosition) {
        super(dataset);
        context.inject(this);
        source = dataset;
        metaDataService
                .extractMetaData(dataset)
                .mergeTo(getMetaDataSet());
        position = DimensionUtils.planarToAbsolute(planePosition);
        
    }
    
    @Override
    public void load() {
       setDataset(imagePlaneService.isolatePlane(source, position));
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public String getSourceFile() {
        return source.getSource();
    }
    
    public long[] getPositinon() {
        return position;
    }
    
    public String getDefaultSaveName() {
        return source.getName();
    }
    
}
