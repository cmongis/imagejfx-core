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
package ijfx.ui.widgets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.utils.PathUtils;
import ijfx.core.uiplugin.Localization;
import ijfx.ui.RichMessageDisplayer;
import ijfx.ui.UiConfiguration;
import ijfx.ui.UiPlugin;
import ijfx.ui.main.ImageJFX;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import mongis.utils.CallbackTask;
import org.scijava.command.CommandService;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugins.commands.io.OpenFile;

/**
 *
 * @author cyril
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "test", localization = Localization.RIGHT, context = "test-open")
public class ModuleInfoDisplay extends BorderPane implements UiPlugin {

    private WebView webView;

    private final Property<ModuleInfo> moduleProperty = new SimpleObjectProperty<>();

    private RichMessageDisplayer messageDispayer;

    
    
    
    public ModuleInfoDisplay() {
       
    }

    private void installWebView(WebView view) {
        this.webView = view;

        view.prefWidthProperty().bind(widthProperty());
        view.prefHeightProperty().bind(heightProperty());
        setCenter(view);
        webView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);
        messageDispayer = new RichMessageDisplayer(webView);

        //setTop(new Label("Hello !"));
        updateWebView();
    }

    private void onModuleChanged(Object o, ModuleInfo oldValue, ModuleInfo newValue) {
        Platform.runLater(this::updateWebView);
    }
    
    private void onMouseClicked(MouseEvent event) {
        if(event.isPrimaryButtonDown() == false) {
            updateWebView();
        }
    }

    private void updateWebView() {

        if (webView == null) {
            return;
        }

        if (moduleProperty.getValue() == null) {
            return;
        }

        try {
            messageDispayer.setMessage(render(moduleProperty.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /*
        Accessors
    */
    
    public Property<ModuleInfo> moduleProperty() {
        return moduleProperty;
    }
    
    public void setModule(ModuleInfo infos) {
        moduleProperty().setValue(infos);
    }
    
    public ModuleInfo getModule() {
        return moduleProperty().getValue();
    }
    
    

   /*
        Template related methods
    */
    
    private String getClassName(String className) {
        
        
        String[] split = className.split("\\.");
        return split[split.length-1];
    }
    
    
   
    
    
    private String render(ModuleInfo infos) throws IOException {

        JarLoader loader = new JarLoader(this.getClass());

       

        loader.setPrefix("/ijfx/ui/widgets/");

        PebbleEngine engine = new PebbleEngine.Builder()
                .loader(loader)
                .build();

        StringWriter writer = new StringWriter();

        Map<String, Object> map = convert(infos);
        
        try {
            engine
                    .getTemplate("module.tpl.html")
                    .evaluate(writer, map);

            return writer.toString();

        } catch (PebbleException ex) {
            Logger.getLogger(ModuleInfoDisplay.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Something happened :-(";
    }

    private List<Map<String, String>> toList(Iterable<ModuleItem<?>> items) {

        return Lists.newArrayList(items)
                .parallelStream()
                .map(this::convert)
                .collect(Collectors.toList());

    }

    private Map<String, Object> convert(ModuleInfo module) {

        return Maps.newHashMap(ImmutableMap.<String, Object>builder()
                .put("className", getClassName(module.getDelegateClassName()))
                .put("class", module.getDelegateClassName())
                .put("inputs", toList(module.inputs()))
                .put("outputs", toList(module.outputs()))
                .build());
    }

    private Map<String, String> convert(ModuleItem<?> item) {

        return Maps.newHashMap(ImmutableMap.<String, String>builder()
                .put("name", item.getName())
                .put("ioType", item.getIOType().toString())
                .put("type", item.getType().getSimpleName())
                .build());

    }

   

    @Override
    public Node getUiElement() {
        return this;
    }

    @Parameter
    CommandService commandService;

    @Override
    public UiPlugin init() throws Exception {

        new CallbackTask<Void, WebView>()
                .call(WebView::new)
                .then(this::installWebView)
                .startInFXThread();

        setPrefWidth(300);
        setPrefHeight(400);
        moduleProperty.addListener(this::onModuleChanged);

        Platform.runLater(() -> {
            moduleProperty.setValue(commandService.getCommand(OpenFile.class));
        });
        return this;
    }

}
