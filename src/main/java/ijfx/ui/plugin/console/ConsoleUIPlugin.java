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
package ijfx.ui.plugin.console;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import ijfx.core.FXUserInterface;
import ijfx.core.uicontext.UiContextService;
import ijfx.core.uicontext.UiContextUpdatedEvent;
import ijfx.core.uiplugin.FXUiCommandService;
import ijfx.core.uiplugin.Localization;
import ijfx.ui.UiConfiguration;
import ijfx.ui.UiPlugin;
import ijfx.ui.main.ImageJFX;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.controlsfx.control.PopOver;
import org.scijava.console.ConsoleService;
import org.scijava.console.OutputEvent;
import org.scijava.event.EventHandler;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;
import org.scijava.ui.console.ConsolePane;

/**
 *
 * @author cyril
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "console-plugin", context = "always", localization = Localization.TOP_RIGHT)
public class ConsoleUIPlugin implements UiPlugin, ConsolePane<Node> {

    @FXML
    TextArea consoleTextArea;

    @FXML
    Pane root;

    @FXML
    MenuButton debugButton;

    @FXML
    FlowPane uiContextFlowPane;

    @FXML
    TextField uiContextTextField;

    HBox hbox = new HBox();

    ToggleButton toggleButton;

    Button cssButton = new Button("CSS");

    PopOver popOver;

    @Parameter
    UIService uiService;

    @Parameter
    UiContextService uiContextService;

    @Parameter
            FXUiCommandService commandService;
    
    @Parameter
    ConsoleService consoleService;
    
    Set<String> lastContextList;

    @Override
    public Node getUiElement() {
        return hbox;
    }

    @Override
    public UiPlugin init() throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("ConsoleUIPlugin.fxml"));

        loader.setController(this);

        loader.load();

        hbox.getStyleClass().addAll("toggle-group");

        cssButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.RECYCLE));
        cssButton.getStyleClass().add("first");
        cssButton.setOnAction(event->reloadCss());
        uiContextTextField.addEventHandler(KeyEvent.KEY_RELEASED, this::onKeyPressed);
        popOver = new PopOver(root);
        toggleButton = new ToggleButton("Console");
        toggleButton.getStyleClass().add("last");
        toggleButton.selectedProperty().bind(popOver.showingProperty());
        //toggleButton.addEventFilter(MouseEvent.MOUSE_CLICKED,this::onMousePressed);
        toggleButton.addEventFilter(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        
        hbox.getChildren().addAll(cssButton,toggleButton);
        
        debugButton.getItems().addAll(commandService
                .getAssociatedAction(this.getClass())
                .stream()
                .map(commandService::createMenuItem)
                .collect(Collectors.toList()));
        
        consoleService.addOutputListener(this);
        
        return this;

    }

    public void onMousePressed(MouseEvent event) {
        popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_BOTTOM);

        if (popOver.isShowing()) {
            popOver.hide();
        } else {
            popOver.show(toggleButton);
        }

        event.consume();
    }

    public void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            uiContextService.enter(uiContextTextField.getText().split(" "));
            uiContextService.update();
            uiContextTextField.clear();
        }
    }

    @Override
    public void append(OutputEvent event) {
        consoleTextArea.appendText("\n");
        consoleTextArea.appendText(event.getOutput());
    }

    @Override
    public void show() {

        popOver.show(toggleButton);
    }

    @Override
    public Node getComponent() {
        return consoleTextArea;
    }

    @Override
    public Class<Node> getComponentType() {
        return Node.class;
    }

    @Override
    public void outputOccurred(OutputEvent event) {
        append(event);
    }

    @FXML
    public void reloadCss() {
        ((FXUserInterface) uiService.getUI(ImageJFX.UI_NAME)).reloadCss();
    }

    /*
        SciJava Event
     */
    @EventHandler
    public void on(UiContextUpdatedEvent event) {

        Platform.runLater(this::refreshContext);

    }

    private List<Button> getButtonList() {
        return uiContextFlowPane
                .getChildren()
                .stream()
                .map(node -> (Button) node)
                .collect(Collectors.toList());
    }

    private void refreshContext() {

        Set<String> context = uiContextService.getContextList();

        Set<String> current = getButtonList()
                .stream()
                .map(Button::getText)
                .collect(Collectors.toSet());

        List<Button> toRemove = getButtonList()
                .stream()
                .filter(button -> context.contains(button.getText()) == false)
                .collect(Collectors.toList());

        List<Button> toAdd = context
                .stream()
                .filter(c -> current.contains(c) == false)
                .map(this::createButton)
                .collect(Collectors.toList());

        uiContextFlowPane.getChildren().removeAll(toRemove);
        uiContextFlowPane.getChildren().addAll(toAdd);

    }

    private Button createButton(String uiContext) {
        Button button = GlyphsDude.createIconButton(FontAwesomeIcon.REMOVE, uiContext);
        button.setOnAction(event -> {

            uiContextService.leave(uiContext);
            uiContextService.update();
        });
        return button;
    }

}
