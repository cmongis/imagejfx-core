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

import ijfx.commands.explorable.ExplorableDisplayCommand;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.core.uiplugin.FXUiCommandService;
import ijfx.core.uiplugin.UiCommand;
import static java.awt.SystemColor.info;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.scijava.Context;
import org.scijava.command.CommandInfo;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.PluginService;
import org.scijava.plugin.SciJavaPlugin;

/**
 *
 * @author cyril
 */
public class MetaDataBar extends HBox{
    
    private static String FIRST = "first";
    
    private static String LAST = "last";
    
    private Label label = new Label("Edit metadata");
    
    private HBox buttonVBox = new HBox();
    
    @Parameter
    FXUiCommandService uiCommandService;
    
    @Parameter
    CommandService commandService;
    
    @Parameter
    PluginService pluginService;
    
    public MetaDataBar(Context context) {
        getChildren().add(label);
        getStyleClass().addAll("hbox","smaller","metadata-bar");

        buttonVBox.getStyleClass().add("hbox");
        context.inject(this);
        
        List<Button> buttons = commandService
                .getCommandsOfType(ExplorableDisplayCommand.class)
                
                .stream()
                .map(this::createButton)
                .collect(Collectors.toList());
        buttonVBox.getChildren().addAll(buttons);
        
        getChildren().add(buttonVBox);
        
    }
    
    public Button createButton(CommandInfo infos) {
        
        Button button = uiCommandService.createButton(infos);
        
        button.setOnAction(event->{
            commandService.run(infos,true);
        });
        
        return button;
        
    }
    
    public void addUiCommands(List<UiCommand<MetaDataBar>> commands) {
        
        List<Button> buttons = commands
                .stream()
                .map(uiCommand->uiCommandService.createButton(uiCommand,this))
                .collect(Collectors.toList());
                
        buttonVBox.getChildren().addAll(buttons);
        
        updateClasses();
        
    }
    
    
    public void updateClasses() {
        
        // deleting the last class
        buttonVBox.getChildren()
                .stream()
                .filter(node->node instanceof Button)
                .peek(node->node.getStyleClass().remove(LAST))
                .peek(node->node.getStyleClass().remove(FIRST));
        
        
        buttonVBox.getChildren().get(getChildren().size()).getStyleClass().add(LAST);
        buttonVBox.getChildren().get(0).getStyleClass().add(FIRST);
        
        
    }
    
    
    public void setItem(List<? extends MetaDataOwner> items) {
        
    }
    
    public void getItem(List<? extends MetaDataOwner> items){
        
    }
    
    
    
    
    
}
