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
package ijfx.plugins.flatfield;

import ijfx.core.assets.AssetService;
import ijfx.core.assets.FlatfieldAsset;
import ijfx.core.batch.CommandRunner;
import java.io.File;
import net.imagej.Dataset;
import net.imagej.axis.Axes;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.scijava.Context;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = Command.class, menuPath = "Process > Correction > Flatfield correction")
public class FlatFieldCorrection extends ContextCommand {

    @Parameter(label = "Flatfield image")
    File flatfield;

    @Parameter(label = "Darkfield image (for the flatfield)", required = false, description = "The darkfield image will be only applied to the flatfield image.")
    File darkfield;

    @Parameter(label = "Convert original type")
    boolean keepType = true;

    @Parameter
    AssetService assetService;

    @Parameter(type = ItemIO.BOTH)
    Dataset dataset;

    @Parameter
    Context context;

    @Parameter(label = "Channel to correct", style = "Channel", description = "Channel id from 0 to n-1. (indicate -1 to correcto all channels)")
    Integer channel = 0;

    @Override
    public void run() {

        Dataset flatfieldData = assetService.load(new FlatfieldAsset(flatfield, darkfield));

        if (keepType == false) {
            dataset = new CommandRunner(context)
                    .set("dataset", dataset)
                    .runSync(ConvertTo32Bits.class)
                    .getOutput("dataset");
        }

        double value;
        double coeff;

        RandomAccessibleInterval target;

        if (channel == -1 || dataset.dimension(Axes.CHANNEL) <= 0 || dataset.dimensionIndex(Axes.CHANNEL) == -1) {
            target = dataset;
        } else {
            int channelAxisId = dataset.dimensionIndex(Axes.CHANNEL);
            target = (RandomAccessibleInterval) Views.hyperSlice(dataset, channelAxisId, channel);
        }
        correct(target, (RandomAccessibleInterval) flatfieldData);
    }

    private static double limit(double min, double max, double number) {
        return Math.max(Math.min(max, number), min);
    }

    public static <T extends RealType<T>, U extends RealType<U>> void correct(T t, U u, double min, double max) {
        final double value = t.getRealDouble();
        final double coeff = u.getRealDouble();
        t.setReal(limit(min, max, value / coeff));
    }
    
    public static <T extends RealType<T>, U extends RealType<U>> void correct(RandomAccessibleInterval<T> dataset, RandomAccessibleInterval<U> flatfield) {
    
        final double minValue = dataset.randomAccess().get().getMinValue();
        final double maxValue = dataset.randomAccess().get().getMaxValue();

        Cursor<T> datasetCursor = Views.iterable(dataset).cursor();
        RandomAccess<U> flatfieldRai = flatfield.randomAccess();
        datasetCursor.reset();

        long[] xy = new long[2];
        while (datasetCursor.hasNext()) {
            datasetCursor.fwd();
            xy[0] = datasetCursor.getLongPosition(0);
            xy[1] = datasetCursor.getLongPosition(1);
            flatfieldRai.setPosition(xy);
            FlatFieldCorrection.correct(datasetCursor.get(), flatfieldRai.get(), minValue, maxValue);

        }

    }

}
