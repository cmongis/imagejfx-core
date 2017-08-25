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

import ijfx.core.image.DatasetUtilsService;
import ijfx.core.metadata.MetaData;
import ijfx.core.workflow.Workflow;
import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.main.ImageJFX;
import java.util.logging.Level;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class ExplorableSegmentationTask extends DefaultSegmentationOp {

    private final Explorable explorable;

    private boolean measureSource;

    @Parameter
    DatasetUtilsService datasetUtilsService;

    public ExplorableSegmentationTask(Explorable explorable, Workflow workflow) {

        super();
        this.explorable = explorable;
        setWorkflow(workflow);
        setMetaDataSet(explorable.getMetaDataSet());
    }

    @Override
    public void load() {
        
        System.out.println(explorable.getMetaDataSet());
        setInput(explorable.getDataset());
        
        try {
            setMeasuredDataset(datasetUtilsService.openSource(explorable, true));
        } catch (Exception e) {
            ImageJFX.getLogger().log(Level.SEVERE, "Couldn't open the source.", e);
            setMeasuredDataset(explorable.getDataset());
        }
        System.out.println("Datasets loaded");
    }

    public void dispose() {

        super.dispose();
        setInput(null);
        setMeasuredDataset(null);
        explorable.dispose();

    }

}
