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
package ijfx.core.uiplugin;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import ijfx.core.usage.Usage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import mongis.utils.CallbackTask;
import net.mongis.usage.UsageLocation;
import org.scijava.Context;
import org.scijava.command.CommandInfo;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class FXActionBarBuilder {

    private List<LabelledAction> actionList = new ArrayList<>();

    private String moreButtonText = "More";

    private FontAwesomeIcon moreIcon = FontAwesomeIcon.BARS;

    private UsageLocation usageLocation = UsageLocation.GENERAL;

    @Parameter
    private FXUiCommandService uiCommandService;

    private int limit = 3;

    private List<Button> buttons = new ArrayList<>();
    private List<MenuItem> items = new ArrayList<>();
    private MenuButton menuButton = new MenuButton();

    public FXActionBarBuilder(Context context) {

        context.inject(this);

    }

    public FXActionBarBuilder setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public FXActionBarBuilder setActionList(List<LabelledAction> actionList) {
        this.actionList = actionList;
        return this;
    }

    public FXActionBarBuilder setMoreButtonText(String modeButtonText) {
        this.moreButtonText = modeButtonText;
        return this;
    }

    public FXActionBarBuilder setUsageLocation(String usageLocation) {
        this.usageLocation = new UsageLocation(usageLocation);
        return this;
    }

    public FXActionBarBuilder addCommands(List<CommandInfo> info) {
        return addActionList(uiCommandService.wrap(info));

    }

    public <T> FXActionBarBuilder addUiCommands(List<UiCommand<T>> commandList, T acceptor) {
        return addActionList(uiCommandService.wrap(commandList, acceptor));
    }

    public FXActionBarBuilder addActionList(List<? extends LabelledAction> action) {
        actionList.addAll(action);
        return this;
    }

    public FXActionBarBuilder update(List<Node> list) {

        list.addAll(buttons);
        list.add(menuButton);
        return this;
    }

    
    
    public FXActionBarBuilder build() {

        menuButton.setText(moreButtonText);

        menuButton.setGraphic(new FontAwesomeIconView(moreIcon));

        actionList.sort((o1, o2) -> Double.compare(o1.priority(), o2.priority()));

        int len = actionList.size();
        int limit = len > this.limit ? this.limit : len;

        buttons = actionList
                .subList(0, limit)
                .stream()
                .map(uiCommandService::createButton)
                .peek(button -> Usage.listenButton(button, usageLocation))
                .collect(Collectors.toList());

        if (len > limit) {
            List<MenuItem> items = actionList
                    .subList(limit, len)
                    .stream()
                    .map(uiCommandService::createMenuItem)
                    .peek(item -> Usage.listenClick(item, usageLocation))
                    .collect(Collectors.toList());
            menuButton.getItems().addAll(items);

        }

        return this;
    }
    
    
    public void updateAsync(List<Node> node) {
        
        
        new CallbackTask<>()
                .run(this::build)
                .then(o->{
                    node.clear();
                    update(node);
                })
                .start();
        
        
    }

}
