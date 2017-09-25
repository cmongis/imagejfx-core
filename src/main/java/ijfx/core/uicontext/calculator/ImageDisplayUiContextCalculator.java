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
package ijfx.core.uicontext.calculator;

import ijfx.core.overlay.OverlaySelectionService;
import ijfx.core.overlay.OverlayUtilsService;
import ijfx.core.utils.AxisUtils;
import net.imagej.Dataset;
import net.imagej.axis.Axes;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.overlay.BinaryMaskOverlay;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = UiContextCalculator.class)
public class ImageDisplayUiContextCalculator extends AbstractUiContextCalculator<ImageDisplay>{

    
     public final static String CTX_OVERLAY_SELECTED = "overlay-selected";
    public final static String CTX_RGB_IMAGE = "rgb-img";
    public final static String CTX_MULTI_Z_IMAGE = "multi-z-img";
    public final static String CTX_MULTI_CHANNEL_IMG = "multi-channel-img";
    public final static String CTX_MULTI_N_IMG = "multi-n-img";
    public final static String CTX_MULTI_TIME_IMG = "multi-time-img";
    public final static String CTX_TABLE_DISPLAY = "table-open";
    public final static String CTX_IMAGE_DISPLAY = "image-open";
    public final static String CTX_IMAGE_BINARY = "binary";
    public final static String CTX_MEASURE_DISPLAY = "measure-open";
    public final static String CTX_ANY_DISPLAY = "any-display-open";
    public final static String CTX_MASK = "mask";
    
    public ImageDisplayUiContextCalculator() {
        super(ImageDisplay.class);
    }

    @Parameter
    OverlayUtilsService overlayUtilsService;
    
    @Parameter
    ImageDisplayService imageDisplayService;
    
    @Parameter
    OverlaySelectionService overlaySelectionService;
    
    @Override
    public void calculate(ImageDisplay imageDisplay) {

        
                toggle(CTX_OVERLAY_SELECTED, imageDisplay != null && overlaySelectionService.getSelectedOverlays(imageDisplay).size() > 0);

                toggle(CTX_MULTI_Z_IMAGE, imageDisplay != null && AxisUtils.hasAxisType(imageDisplay, Axes.Z));

                toggle(CTX_MULTI_CHANNEL_IMG, imageDisplay != null && AxisUtils.hasAxisType(imageDisplay, Axes.CHANNEL));

                toggle(CTX_MULTI_TIME_IMG, imageDisplay != null && AxisUtils.hasAxisType(imageDisplay, Axes.TIME));

                toggle(CTX_RGB_IMAGE, imageDisplay != null && imageDisplayService.getActiveDataset(imageDisplay).isRGBMerged());

                toggle(CTX_IMAGE_BINARY, imageDisplay != null && imageDisplayService.getActiveDataset(imageDisplay).getValidBits() == 1);

                toggle(CTX_MULTI_N_IMG, imageDisplay != null && imageDisplay.numDimensions() > 2);

                toggle(CTX_MASK, imageDisplay != null && overlayUtilsService.findOverlayOfType(imageDisplay, BinaryMaskOverlay.class) != null);

                Dataset dataset = null;
                if (imageDisplay != null) {
                    dataset = (Dataset) imageDisplay.getActiveView().getData();
                    for (int i = 1; i <= 32; i *= 2) {
                        toggle(String.valueOf(dataset.getValidBits()) + "-bits", dataset.getValidBits() == i);
                    }
                } else {
                    for (int i = 1; i <= 32; i *= 2) {
                        toggle(String.valueOf(i) + "-bits", false);
                    }
                }

    }
    
}
