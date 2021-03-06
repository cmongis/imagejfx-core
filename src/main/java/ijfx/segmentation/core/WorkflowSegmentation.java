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
package ijfx.segmentation.core;

import ijfx.commands.assign.InvertDataValues;
import ijfx.core.segmentation.SegmentationService;
import ijfx.core.workflow.DefaultWorkflow;
import ijfx.core.workflow.Workflow;
import ijfx.core.workflow.WorkflowBuilder;
import ijfx.core.workflow.WorkflowStep;
import ijfx.ui.loading.LoadingScreenService;
import java.util.List;
import net.imagej.plugins.commands.binary.Binarize;
import net.imagej.plugins.commands.binary.DilateBinaryImage;
import net.imagej.plugins.commands.binary.ErodeBinaryImage;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import org.scijava.Context;
import org.scijava.plugin.Parameter;
import org.scijava.ui.UIService;

/**
 *
 * @author Cyril MONGIS
 */
public class WorkflowSegmentation extends AbstractSegmentation {

    @Parameter
    Context context;

    @Parameter
    LoadingScreenService loadingService;

    @Parameter
    UIService uiService;

    @Parameter
    SegmentationService segmentationService;

    public List<WorkflowStep> stepList;

    public WorkflowSegmentation() {

    }

    public List<WorkflowStep> getStepList() {
        if (stepList == null) {
            stepList = new WorkflowBuilder(context)
                    .addStep(Binarize.class)
                    .addStep(InvertDataValues.class)
                    .addStep(ErodeBinaryImage.class)
                    .addStep(DilateBinaryImage.class)
                    .buildList();
        }
        return stepList;
    }

    @Override
    public <T extends RealType<?>> void preview(RandomAccessibleInterval<T> example) {
        setExample(example);
        reprocess(getStepList());
    }

    @Override
    public Workflow getWorkflow() {
        return new DefaultWorkflow(stepList);
    }

    public void reprocess(List<WorkflowStep> steps) {
        if (example == null) {
            return;
        }
        segmentationService
                .createSegmentation()
                .setWorkflow(steps)
                .addInterval(example)
                .getAsMask()
                .executeAsync()
                .submit(loadingService)
                .then(this::onProcessedFinished);

    }

    private void onProcessedFinished(List<Img<BitType>> masks) {

        if (masks.size() == 0) {
            uiService.showDialog("Your workflow should produce a binary image.");

        } else {
            Img<BitType> get = masks.get(0);
            maskProperty().setValue(get);
        }

    }

    public void refresh() {
        reprocess(getStepList());
    }

}
