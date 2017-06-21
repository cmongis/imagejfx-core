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

import com.google.common.collect.ImmutableMap;
import ijfx.commands.axis.DeleteDataFX;
import ijfx.core.uiplugin.UiAction;
import ijfx.ui.display.image.AxisSlider;
import java.util.Map;
import org.scijava.plugin.Plugin;

/**
 *
 * @author cyril
 */
@Plugin(type=UiAction.class,label = "Delete this {0}",iconPath="fa:remove")
public class DeleteThisPositionUiAction extends AbstractAxisSliderUiAction {
    @Override
    public void run(AxisSlider t) {

        int axisId = t.getAxisId();

        Long position = t.getDisplay().getLongPosition(axisId) + 1;

        Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("position", position)
                .put("quantity", 1)
                .put("axisType", t.getAxisType())
                .build();

        commandService.run(DeleteDataFX.class, true, params);
    }

}
