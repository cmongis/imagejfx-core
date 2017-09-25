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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.scijava.ItemVisibility;
import org.scijava.command.DynamicCommand;
import org.scijava.command.InteractiveCommand;
import org.scijava.plugin.Parameter;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author Cyril MONGIS
 */
public abstract class AbstractExplorableDisplayCommand extends DynamicCommand implements ExplorableDisplayCommand {

    @Parameter
    ExplorableDisplay display;

    /*
    @Parameter(required = false,visibility = ItemVisibility.INVISIBLE,autoFill = true)
    boolean warning = true;*/

    @Parameter
    UIService uiService;

    public static final String ALL_ITEMS = "All items";
    
    public static final String FILTERED_ITEMS = "Displayed items";
    
    public static final String SELECTED_ITEMS = "Selected items";
    
    
    @Parameter(persist = false,label = "Apply to...",choices = {SELECTED_ITEMS,ALL_ITEMS,FILTERED_ITEMS},initializer = "initApplyTo")
    private String applyTo = ALL_ITEMS;
    
    
    public void initApplyTo() {
        if(display == null) return;
        if(display.getSelectedItems().size() > 0) {
            applyTo = SELECTED_ITEMS;
        }
        
        if(display.getDisplayedItems().size() != display.getItems().size()) {
            applyTo = ALL_ITEMS;
        }
    }
    
    
    public void run() {

        List<Explorable> items;
       
        
        if(applyTo == ALL_ITEMS) {
            items = display.getItems();
        }
        else if(applyTo == SELECTED_ITEMS) {
            items = display.getSelectedItems();
        }
        else {
            items = display.getDisplayedItems();
        }
        
        
        try {
            run(items);
        } catch (Exception ex) {
            cancel("Error when executing");
            Logger.getLogger(AbstractExplorableDisplayCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public abstract void run(List<? extends Explorable> items) throws Exception;

    protected void initWithPossibleKeys(String field) {
        if(display == null) return;
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
