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

import com.sun.deploy.uitoolkit.impl.fx.ui.FXAboutDialog;
import ijfx.core.image.ImagePlaneService;
import ijfx.core.image.PreviewService;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.metadata.MetaDataSetUtils;
import ijfx.core.overlay.OverlayDrawingService;
import ijfx.core.overlay.OverlayUtilsService;
import ijfx.explorer.datamodel.AbstractExplorable;
import ijfx.ui.loading.LoadingScreenService;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import mongis.utils.CallbackTask;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.ColorTable8;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class SegmentedObjectExplorerWrapper extends AbstractExplorable {

    private final SegmentedObject object;

    @Parameter
    private ImagePlaneService imagePlaneService;

    @Parameter
    private OverlayDrawingService overlayDrawingService;

    @Parameter
    private PreviewService previewService;

    @Parameter
    private OverlayUtilsService overlayUtilsService;

    @Parameter
    private LoadingScreenService loadingScreenService;

    private Dataset source;

    private WeakReference<ImageDisplay> imageDisplay = new WeakReference<>(null);

    Image image;

    public SegmentedObjectExplorerWrapper(SegmentedObject object) {
        this.object = object;
        object.getOverlay().context().inject(this);
    }

    @Override
    public String getTitle() {
        return object.getOverlay().getName();
    }

    @Override
    public String getSubtitle() {
        return object.getMetaDataSet().get(MetaData.FILE_NAME).getStringValue();
    }

    @Override
    public String getInformations() {
        return "";
    }

    public void setSource(Dataset source) {
        this.source = source;
    }

    public void setImageDisplay(ImageDisplay imageDisplay) {
        this.imageDisplay = new WeakReference<>(imageDisplay);
    }

    public ImageDisplay getImageDisplay() {
        return this.imageDisplay.get();
    }

    public void load() {

    }

    @Override
    public synchronized Image getImage() {
        

        if (image == null) {
            try {

                RandomAccessibleInterval<? extends RealType> extractedObject;
                if (object.getPixelSource() != null) {

                    extractedObject = overlayDrawingService.extractObject(object.getOverlay(), object.getPixelSource());

                } else {

                    // we get the position the overlay was extracted from
                    long[] nonPlanarPosition = MetaDataSetUtils.getNonPlanarPosition(getMetaDataSet());

                    // we open the image virtually just in case
                    File imageFile = new File(object.getMetaDataSet().get(MetaData.ABSOLUTE_PATH).getStringValue());

                    Dataset dataset = imagePlaneService.openVirtualDataset(imageFile);
                    // the pixels are extracted
                    extractedObject = overlayDrawingService.extractObject(object.getOverlay(), dataset, nonPlanarPosition);

                }
                double min = object.getMetaDataSet().get(MetaData.STATS_PIXEL_MIN).getDoubleValue();
                double max = object.getMetaDataSet().get(MetaData.STATS_PIXEL_MAX).getDoubleValue();
                Image image = previewService.datasetToImage((RandomAccessibleInterval<? extends RealType>) extractedObject, new ColorTable8(), min, max);
                Double sampleFactor = 100 * 100 / image.getWidth() / image.getHeight();
                sampleFactor = sampleFactor < 1 ? 1 : sampleFactor;
                this.image = resample(image, sampleFactor);

            } catch (IOException ioe) {
                logger.log(Level.SEVERE, "Error when accessing file " + getFile().getAbsolutePath(), ioe);
                return null;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error when getting image for " + getTitle(), e);
                return null;
            }
        }
        return image;
    }

    private Image resample(Image input, double scaleFactor) {
        final int W = (int) input.getWidth();
        final int H = (int) input.getHeight();
        final double S = scaleFactor;
        final int HSample = (int) (H * scaleFactor) + 1;
        final int WSample = (int) (W * scaleFactor) + 1;

        WritableImage output = new WritableImage(
                WSample,
                HSample
        );

        PixelReader reader = input.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                final int argb = reader.getArgb(x, y);
                for (int dy = 0; dy < S; dy++) {
                    for (int dx = 0; dx < S; dx++) {
                        writer.setArgb((int) (x * S) + dx, (int) (y * S) + dy, argb);
                    }
                }
            }
        }

        return output;
    }

    @Override
    public void open() throws Exception {
        new CallbackTask<File, Void>()
                .setInput(getFile())
                .callback(f -> {
                    overlayUtilsService.openOverlay(f, object.getOverlay());
                    return null;
                })
                .submit(loadingScreenService)
                .setName("Opening file and object...")
                .setInitialProgress(0.5)
                .start();
    }

    @Override
    public Dataset getDataset() {
        return null;
    }

    @Override
    public MetaDataSet getMetaDataSet() {
        return object.getMetaDataSet();
    }

    public SegmentedObject getObject() {
        return object;
    }

    public void dispose() {

    }
}
