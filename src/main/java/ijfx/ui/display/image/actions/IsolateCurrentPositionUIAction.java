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
package ijfx.ui.display.image.actions;

import ijfx.commands.axis.Isolate;
import ijfx.core.uiplugin.AbstractUiCommand;
import ijfx.ui.display.image.AxisSlider;
import net.imagej.display.ImageDisplay;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import ijfx.core.uiplugin.UiCommand;

/**
 *
 * @author cyril
 */
@Plugin(type = UiCommand.class,label = "Isolate the current {0}",iconPath="fa:clone")
public class IsolateCurrentPositionUIAction extends AbstractUiCommand<AxisSlider> {

    public IsolateCurrentPositionUIAction() {
        super(AxisSlider.class);
    }

    @Parameter
    CommandService commandService;
    
    @Override
    public void run(AxisSlider t) {

        ImageDisplay display = t.getDisplay();
        Long position = display.getLongPosition(t.getAxisId()) + 1;

       commandService.run(Isolate.class, true, "axisType", t.getAxisType(), "position", t.getPosition());

    }

}
