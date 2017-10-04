/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package ijfx.commands.assign;

import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.display.OverlayService;
import net.imagej.options.OptionsCompatibility;
import net.imagej.overlay.Overlay;
import net.imagej.plugins.commands.assign.InplaceUnaryTransform;
import net.imglib2.Cursor;
import net.imglib2.ops.operation.real.unary.RealInvert;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.menu.MenuConstants;
import org.scijava.options.OptionsService;
import org.scijava.plugin.Attr;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Fills an output Dataset by applying an inversion to an input Dataset's data
 * values. The inversion is relative to the minimum and maximum data values
 * present in the input Dataset. However when legacy compatibility mode is
 * enabled an unsigned 8-bit dataset is inverted relative to 0 and 255.
 *
 * @author Barry DeZonia
 * @author Cyril MONGIS : simplified version
 */
@Plugin(type = Command.class, menu = {
    @Menu(label = MenuConstants.EDIT_LABEL, weight = MenuConstants.EDIT_WEIGHT,
            mnemonic = MenuConstants.EDIT_MNEMONIC)
    ,
	@Menu(label = "Invert all planes", weight = 30, accelerator = "shift ^I")},
        headless = true, attrs = {
            @Attr(name = "no-legacy")}
        , description = "The inversion is relative to the"
                + " minimum and maximum data values present "
                + "in the input Dataset.")
public class InvertDataValues<T extends RealType<T>> extends ContextCommand {

  

    @Parameter
    private OptionsService optionsService;

    @Parameter(type = ItemIO.BOTH)
    private Dataset dataset;

    @Parameter
    private Overlay overlay;

    // -- other instance variables --
    private double min, max;

    // -- public interface --
    @Override
    public void run() {
        OptionsCompatibility options = optionsService.getOptions(OptionsCompatibility.class);

        if (options.isInvertModeLegacy() && dataset.isInteger()
                && !dataset.isSigned() && dataset.getType().getBitsPerPixel() == 8) {
            min = 0;
            max = 255;
        } else {
            calcValueRange(dataset);
        }

        final RealInvert<DoubleType, DoubleType> op
                = new RealInvert<DoubleType, DoubleType>(min, max);

        final InplaceUnaryTransform<T, DoubleType> transform;

        transform
                = new InplaceUnaryTransform<T, DoubleType>(
                        op, new DoubleType(), dataset, overlay);

        transform.run();
    }

    // -- private interface --
    /**
     * Finds the smallest and largest data values actually present in the input
     * image
     */
    private void calcValueRange(Dataset dataset) {
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;

        final Cursor<? extends RealType<?>> cursor = dataset.getImgPlus().cursor();

        while (cursor.hasNext()) {
            final double value = cursor.next().getRealDouble();

            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }
    }

}
