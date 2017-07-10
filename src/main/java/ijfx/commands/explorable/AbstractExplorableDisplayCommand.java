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
package ijfx.commands.explorable;

import ijfx.explorer.ExplorableDisplay;
import ijfx.explorer.datamodel.Explorable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.scijava.command.DynamicCommand;
import org.scijava.command.InteractiveCommand;
import org.scijava.plugin.Parameter;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;

/**
 *
 * @author cyril
 */
public abstract class AbstractExplorableDisplayCommand extends DynamicCommand implements ExplorableDisplayCommand {

    @Parameter
    ExplorableDisplay display;

    @Parameter
    boolean warning = true;

    @Parameter
    UIService uiService;

    public void run() {

        List<Explorable> items;

        if (display.getSelected().size() == 0 && warning) {

            DialogPrompt.Result result = uiService.showDialog("Do you want to apply this action to all items ?", DialogPrompt.MessageType.QUESTION_MESSAGE, DialogPrompt.OptionType.YES_NO_OPTION);

            if (result == DialogPrompt.Result.YES_OPTION) {
                items = display.getItems();
            } else {
                items = new ArrayList<>();
            }

        } else {
            items = display.getSelected();
        }
        run(items);
    }

    public abstract void run(List<? extends Explorable> items);

    protected void initWithPossibleKeys(String field) {

        getInfo()
                .getMutableInput(field, String.class)
                .setChoices(new ArrayList<>(
                        display
                                .getItems()
                                .parallelStream()
                                .flatMap(exp -> exp.getMetaDataSet().keySet().stream())
                                .collect(Collectors.toSet()))
                );

    }

    protected void initWithPossibleValues(String field) {
        getInfo()
                .getMutableInput(field, String.class)
                
                .setChoices(
                        new ArrayList<>(
                                display
                                        .getItems()
                                        .parallelStream()
                                        .flatMap(exp -> exp.getMetaDataSet().values().stream())
                                        .map(Object::toString)
                                        .collect(Collectors.toSet()))
                );
        
       
    }

}
