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
package ijfx.ui.display.code;

import ijfx.core.prefs.JsonPreferenceService;
import ijfx.ui.inputharvesting.AbstractWidgetModel;
import ijfx.ui.inputharvesting.SuppliedWidgetModel;
import java.io.File;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import net.imagej.ImageJService;
import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.scijava.widget.ChoiceWidget;
import org.scijava.widget.FileWidget;
import org.scijava.widget.InputWidget;
import org.scijava.widget.TextWidget;
import org.scijava.widget.WidgetService;

/**
 *
 * @author florian
 *  
 */
@Plugin(type = Service.class,priority = Priority.VERY_LOW_PRIORITY)
public class ScriptEditorPreferenciesService extends AbstractService implements ImageJService{
    @Parameter
    JsonPreferenceService jsonPreferenceService;
    @Parameter
    WidgetService widgetService;
    
    private String fileName = "ScriptEdtirorPreferences";
    private TextEditorPreferencies preferencies;

    public ScriptEditorPreferenciesService() {
        
    }
    
    public void loadPreferencies(){
        try {
            this.preferencies = jsonPreferenceService.loadFromJson(fileName, preferencies);
        } catch (Exception e) {
            this.preferencies = new TextEditorPreferencies();
        }
        
        
    }
    
    public void savePreferencies(){
        jsonPreferenceService.savePreference(preferencies, fileName);
    }

    public TextEditorPreferencies getPreferencies(){
        return this.preferencies;
    }
    
    public Node generatepreferenciesWidget(){
        VBox preferenciesBox = new VBox();
        preferenciesBox.setPadding(Insets.EMPTY);
         InputWidget<?, Node> textWidget = (InputWidget<?, Node>) widgetService.create(
                new SuppliedWidgetModel<>(String.class)
                .setGetter(preferencies::getTheme)
                .setSetter(preferencies::setTheme)
                .setStyle(TextWidget.FIELD_STYLE)
        );
        InputWidget<?,Node> booleanWidget = (InputWidget<?, Node>) widgetService.create(
                new SuppliedWidgetModel<>(Boolean.class)
                .setGetter(preferencies::isAutocompletion)
                .setSetter(preferencies::setAutocompletion)
                       
                .setWidgetLabel("Enable autocompletion")
                
        );
        InputWidget<?,Node> sidePanelActivator = (InputWidget<?, Node>) widgetService.create(
                new SuppliedWidgetModel<>(Boolean.class)
                .setGetter(preferencies::isAutocompletion)
                .setSetter(preferencies::setAutocompletion)
                .setWidgetLabel("Enable side panel")
                
        );
        InputWidget<?,Node> setStyleWidget = (InputWidget<?, Node>) widgetService.create(
                new SuppliedWidgetModel<>(String.class)
                .setGetter(preferencies::getTheme)
                .setSetter(preferencies::setTheme)
                .setStyle(ChoiceWidget.LIST_BOX_STYLE)
                .setWidgetLabel("Choose style")
                
        );
        InputWidget<?,Node> customCssWidget = (InputWidget<?, Node>) widgetService.create(
                new SuppliedWidgetModel<>(File.class)
                .setGetter(preferencies::getCustomCSS)
                .setSetter(preferencies::setCustomCSS)
                .setStyle(FileWidget.DIRECTORY_STYLE)
                .setWidgetLabel("Choose style")
                
        );
        AbstractWidgetModel styleWidget = (AbstractWidgetModel) setStyleWidget.get();
        for (String theme : preferencies.getListOfTheme()){
            styleWidget.addChoice(theme);
        }
        setStyleWidget = (InputWidget<?, Node>) widgetService.create(styleWidget);
        textWidget.refreshWidget();
        booleanWidget.refreshWidget();
        sidePanelActivator.refreshWidget();
        setStyleWidget.refreshWidget();
        customCssWidget.refreshWidget();
        preferenciesBox.getChildren().addAll(textWidget.getComponent(), setStyleWidget.getComponent(), booleanWidget.getComponent(), sidePanelActivator.getComponent(), customCssWidget.getComponent());
        return preferenciesBox;
    }
   
    @Override
    public void initialize(){
        loadPreferencies();
        
    }

}
