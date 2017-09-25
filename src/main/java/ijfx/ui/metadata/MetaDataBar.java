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
package ijfx.ui.metadata;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import ijfx.commands.explorable.ExplorableDisplayCommand;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.core.uiplugin.FXActionBarBuilder;
import ijfx.core.uiplugin.FXUiCommandService;
import ijfx.core.uiplugin.UiCommand;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import mongis.utils.FXUtilities;
import org.scijava.Context;
import org.scijava.command.CommandInfo;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginService;

/**
 *
 * @author Cyril MONGIS
 */
public class MetaDataBar extends HBox {

    private static String FIRST = "first";

    private static String LAST = "last";

    private Label label = new Label("Edit metadata");

    private HBox buttonVBox = new HBox();

    @Parameter
    private FXUiCommandService uiCommandService;

    @Parameter
    private CommandService commandService;

    @Parameter
    private PluginService pluginService;

    private final FXActionBarBuilder builder;

    public MetaDataBar(Context context) {
        getChildren().add(label);
        getStyleClass().addAll("hbox", "smaller", "metadata-bar");

        //buttonVBox.getStyleClass().add("hbox");
        context.inject(this);

        builder = new FXActionBarBuilder(context);

        getChildren().add(buttonVBox);

        addUiCommands();
    }

    public void addCommands(List<CommandInfo> infos) {
        builder.addCommands(infos);
    }

    public void addUiCommands() {
        builder.addUiCommands(uiCommandService
                .getAssociatedAction(this), this);
    }

    public void update() {

        builder
                .updateAsync(buttonVBox.getChildren())
                .then(o->FXUtilities.makeToggleGroup(buttonVBox, buttonVBox.getChildren()));

    }

}
