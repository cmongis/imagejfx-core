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
package ijfx.ui.inputharvesting;

import ijfx.ui.main.ImageJFX;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.scijava.Context;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.widget.InputPanel;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author Cyril MONGIS
 */
public abstract class AbstractWidgetModel implements WidgetModel {

    private static InputPanel<?, ?> panel = new DummyFXInputPanel();

    private Class<?> type;
    
    private String label;

    private String widgetStyle = "";

    private Runnable callback;

    private Context context;

    private boolean initialized = true;

    private Number min = 0;

    private Number max = 100;

    private Number stepSize = 1;

    private Number softMin = 0;

    private Number softMax = 100;

    private List<String> choices = new ArrayList<>();
    
    private boolean isMessage = false;
    
    private String text;
    
    
    public AbstractWidgetModel() {
        
    }
    
    protected void setType(String type) {
        try {
            setType(Class.forName(type));
        }
        catch(Exception e) {
            ImageJFX.getLogger().warning("Couldn't find type :"+type);
        }
    }
    
    protected void setType(Class<?> type) {
        this.type = type;
    }
    
    protected Class<?> type() {
        if(this.type == null && getValue()!=null) {
            this.type = getValue().getClass();
        }
        return this.type;
    }
    
    public AbstractWidgetModel(Object value) {
        type = value.getClass();
    }
    
    
    public AbstractWidgetModel(Class<?> type) {
        this.type = type;
    }

    public AbstractWidgetModel setWidgetLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public InputPanel<?, ?> getPanel() {
        return panel;
    }

    @Override
    public Module getModule() {
        return null;
    }

    @Override
    public ModuleItem<?> getItem() {
        return null;
    }

    @Override
    public List<?> getObjectPool() {
        return new ArrayList<>();
    }

    @Override
    public String getWidgetLabel() {
        return label;
    }

    @Override
    public boolean isStyle(String style) {
        return this.widgetStyle.equals(style);
    }
    
  

    public AbstractWidgetModel setWidgetStyle(String style) {
        this.widgetStyle = style;
        return this;
    }

 
    
    
    
    public AbstractWidgetModel setCallback(Runnable callback) {
        this.callback = callback;
        return this;
    }

    public AbstractWidgetModel setPanel(InputPanel<?, ?> panel) {
        this.panel = panel;
        return this;
    }

    
   

    @Override
    public void callback() {
        callback.run();
    }

    @Override
    public Number getMin() {
        return min;
    }

    @Override
    public Number getMax() {
        return max;

    }

    @Override
    public Number getSoftMin() {
        return softMin;
    }

    @Override
    public Number getSoftMax() {
        return softMax;
    }

    @Override
    public Number getStepSize() {
        return stepSize;
    }

    @Override
    public String[] getChoices() {
        return choices.toArray(new String[choices.size()]);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean isMessage() {
        return isMessage;
    }

    @Override
    public boolean isText() {
        return type() == String.class;
    }

    @Override
    public boolean isCharacter() {
        return false;

    }

    @Override
    public boolean isNumber() {
        return Number.class.isAssignableFrom(type);
    }

    @Override
    public boolean isBoolean() {
        return type() == boolean.class || type() == Boolean.class;
    }

    @Override
    public boolean isMultipleChoice() {
        return choices.size() > 0;
    }

    @Override
    public boolean isType(Class<?> type) {
        
        if(type() == null) {
            new IllegalArgumentException("Impossible to initialize WidgetModel type");
        }
        
        return type().isAssignableFrom(type);
    }

    @Override
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public Context context() {
        return context;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
    
    public AbstractWidgetModel addChoice(String choice){
        this.choices.add(choice);
        return this;
    }

    public AbstractWidgetModel setMin(Number min) {
        this.min = min;
        return this;
    }

    public AbstractWidgetModel setMax(Number max) {
        this.max = max;
        return this;
    }

    public AbstractWidgetModel setSoftMin(Number softMin) {
        this.softMin = softMin;
        return this;
    }

    public AbstractWidgetModel setSoftMax(Number softMax) {
        this.softMax = softMax;
        return this;
    }

    public AbstractWidgetModel setLabel(String label) {
        this.label = label;
        return this;
    }

    public AbstractWidgetModel setStepSize(Number stepSize) {
        this.stepSize = stepSize;
        return this;
    }
    public AbstractWidgetModel setChoices(List<?> choices) {
        
        if(choices == null) {
            return this;
        }
        
        this.choices.addAll(choices.stream().map(o->o.toString()).collect(Collectors.toList()));
        return this;
    }
    
}
