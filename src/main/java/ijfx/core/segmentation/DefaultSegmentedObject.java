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
package ijfx.core.segmentation;

import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.metadata.MetaDataSetType;
import ijfx.core.overlay.OverlayStatService;
import ijfx.core.overlay.OverlayStatistics;
import net.imagej.overlay.Overlay;
import org.scijava.plugin.Parameter;
/**
 *
 * @author Cyril MONGIS, 2016
 */
public class DefaultSegmentedObject implements SegmentedObject {

    
    
    private Overlay overlay;

    
    private MetaDataSet set = new MetaDataSet();
    
    
    @Parameter
    private OverlayStatService overlayStatsService;
    
    
    public DefaultSegmentedObject() {
        set.setType(MetaDataSetType.OBJECT);
    }

    public void setOverlay(Overlay overlay) {
        this.overlay = overlay;
        set.putGeneric(MetaData.NAME, overlay.getName());
    }
    
    public void setMetaDataSet(MetaDataSet set) {
        this.set = set;
    }
    
    public DefaultSegmentedObject(Overlay overlay, OverlayStatistics overlayStatistics) {
        this();
        overlay.getContext().inject(this);
        setOverlay(overlay);
        
        set.merge(overlayStatsService.getStatisticsAsMap(overlayStatistics));
        
    }

    @Override
    public Overlay getOverlay() {
        return overlay;
    }

    @Override
    public MetaDataSet getMetaDataSet() {
        return set;
    }
}
