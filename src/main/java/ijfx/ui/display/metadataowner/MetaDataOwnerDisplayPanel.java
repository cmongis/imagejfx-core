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
package ijfx.ui.display.metadataowner;

import ijfx.commands.explorable.SaveMetaDataSetAsCsv;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.core.uiplugin.FXUiCommandService;
import ijfx.explorer.datamodel.MetaDataOwnerDisplay;
import ijfx.ui.display.image.AbstractFXDisplayPanel;
import ijfx.ui.display.image.FXDisplayPanel;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import mongis.utils.FXUtilities;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = FXDisplayPanel.class)
public class MetaDataOwnerDisplayPanel extends AbstractFXDisplayPanel<MetaDataOwnerDisplay> {

    BorderPane pane;

    TableView<MetaDataOwner> tableView;

    MetaDataOwnerHelper helper;

    HBox hbox;

    Button saveButton;

    @Parameter
    FXUiCommandService uiCommandService;

    @Parameter
    CommandService commandService;

    public MetaDataOwnerDisplayPanel() {
        super(MetaDataOwnerDisplay.class);
    }

    @Override
    public void pack() {
        // main pane
        pane = new BorderPane();
        pane.getStyleClass().add("metadataowner-display-panel");
        
        // initializing the TableView
        tableView = new TableView<>();
        helper = new MetaDataOwnerHelper(tableView);
        pane.setCenter(tableView);

        // initializing the button box
        hbox = new HBox();
        hbox.getStyleClass().add("button-box");
        pane.setBottom(hbox);
        
        
        // initializing the save button
        saveButton = uiCommandService.createButton(commandService.getCommand(SaveMetaDataSetAsCsv.class));
        hbox.getChildren().add(saveButton);


    }

    @Override
    public void redoLayout() {

    }

    @Override
    public void setLabel(String s) {
        getWindow().setTitle(s);
    }

    @Override
    public void redraw() {
        if (getDisplay().size() == 0) {
            return;
        }
        helper.setPriority(getDisplay().getKeyOrder());
        helper.setColumnsFromItems(getDisplay().get(0));

        helper.setItem(getDisplay().get(0));
    }

    @Override
    public Pane getUIComponent() {
        return pane;
    }

}
