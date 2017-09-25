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
import com.mitchellbosecke.pebble.error.PebbleException;
import ijfx.ui.RichMessageDisplayer;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import mongis.utils.CallbackTask;
import org.apache.commons.lang3.ArrayUtils;
import org.scijava.plugin.PluginInfo;
import org.scijava.service.AbstractService;

/**
 *
 * @author Cyril MONGIS
 */
public class PluginInfoPane extends BorderPane{

    private WebView webView;

    private final Property<PluginInfo> moduleProperty = new SimpleObjectProperty<>();

    private RichMessageDisplayer messageDispayer;

    
    
    
    public PluginInfoPane() {
       
        // Webview is always created in the FX Thread
        new CallbackTask<Void, WebView>()
                .call(WebView::new)
                .then(this::installWebView)
                .startInFXThread();

        // Setting the minimum width and max width by default
        setPrefWidth(300);
        setPrefHeight(400);
        
        //add the right listeners
        moduleProperty.addListener(this::onModuleChanged);
        
    }

    private void installWebView(WebView view) {
        this.webView = view;

        view.prefWidthProperty().bind(widthProperty());
        view.prefHeightProperty().bind(heightProperty());
        setCenter(view);
        webView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);
        messageDispayer = new RichMessageDisplayer(webView);
        updateWebView();
    }

    private void onModuleChanged(Object o, PluginInfo oldValue, PluginInfo newValue) {
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
    
    public Property<PluginInfo> moduleInfoProperty() {
        return moduleProperty;
    }
    
    public void setModuleInfo(PluginInfo infos) {
        moduleInfoProperty().setValue(infos);
    }
    
    public PluginInfo getModuleInfo() {
        return moduleInfoProperty().getValue();
    }
    
    

   /*
        Template related methods
    */
    
    private String getClassName(String className) {
        
        
        String[] split = className.split("\\.");
        return split[split.length-1];
    }
    
    
   
    
    
    private String render(PluginInfo infos) throws IOException {

        JarLoader loader = new JarLoader(this.getClass());

       

        loader.setPrefix("/ijfx/ui/widgets/");

        PebbleEngine engine = new PebbleEngine.Builder()
                .loader(loader)
                .build();

        StringWriter writer = new StringWriter();

        Map<String, Object> map = convert(infos);
        
        try {
            engine
                    .getTemplate("PluginInfo.tpl.html")
                    .evaluate(writer, map);

            return writer.toString();

        } catch (PebbleException ex) {
            Logger.getLogger(PluginInfoPane.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Something happened :-(";
    }
/*
    private List<Map<String, String>> toList(Iterable<ModuleItem<?>> items) {

        return Lists.newArrayList(items)
                .parallelStream()
                .map(this::convert)
                .collect(Collectors.toList());

    }
    */
    private List<Map<String, Object>> toList(Method[] items) {

        return Lists.newArrayList(items)
                .parallelStream()
                .filter(item->!ArrayUtils.contains(AbstractService.class.getMethods(),item))
                .map(this::convert)
                .collect(Collectors.toList());

    }
    
    private List<Map<String, String>> toList( Parameter[] items) {

        return Lists.newArrayList(items)
                .parallelStream()
                .map(this::convert)
                .collect(Collectors.toList());

    }

    private Map<String, Object> convert(PluginInfo module) {

        return Maps.newHashMap(ImmutableMap.<String, Object>builder()
                .put("className", getClassName(module.getClassName()))
                .put("class", module.getClassName())
                .put("methods", toList(module.getPluginClass().getMethods()))
                .build());
    }

    private Map<String, Object> convert(Method item) {

        return Maps.newHashMap(ImmutableMap.<String, Object>builder()
                .put("name", item.getName())
                .put("inputs", toList(item.getParameters()))
                .put("output", item.getReturnType().getName())
                .build());
    }
    
    private Map<String, String> convert(Parameter item) {

        return Maps.newHashMap(ImmutableMap.<String, String>builder()
                .put("name", item.getName())
                .put("type", item.getType().getSimpleName())
                .build());

    }

  }
