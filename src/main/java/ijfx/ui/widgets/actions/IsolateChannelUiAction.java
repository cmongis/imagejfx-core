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
package ijfx.ui.widgets.actions;

import ijfx.commands.axis.IsolateChannel;
import ijfx.ui.widgets.AbstractAdjusterUIAction;
import org.scijava.plugin.Plugin;
import ijfx.core.uiplugin.UiCommand;
import ijfx.ui.widgets.ImageDisplayAdjuster;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type= UiCommand.class,label = "Isolate this channel",iconPath="fa:remove",description="Create a new image with only the current channel")
public class IsolateChannelUiAction extends AbstractAdjusterUIAction{

    public IsolateChannelUiAction() {
        super(IsolateChannel.class);
    }
    
    public void run(ImageDisplayAdjuster adjuster) {
        commandService.run(IsolateChannel.class, true, "channel",adjuster.getCurrentChannel());
    }
}
