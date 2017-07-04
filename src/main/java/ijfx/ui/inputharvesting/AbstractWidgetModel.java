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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import org.scijava.Context;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.widget.InputPanel;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author cyril
 */
public abstract class AbstractWidgetModel<T> implements WidgetModel {

    private static InputPanel<?, ?> panel = new DummyFXInputPanel();

    private final Class<T> type;

    private String label;

    private String style = "";

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
    
    public AbstractWidgetModel(Class<T> type) {
        this.type = type;
    }

    public AbstractWidgetModel<T> setWidgetLabel(String label) {
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
        return this.style.equals(style);
    }
    
  

    public AbstractWidgetModel<T> setStyle(String style) {
        this.style = style;
        return this;
    }

 
    
    
    
    public AbstractWidgetModel<T> setCallback(Runnable callback) {
        this.callback = callback;
        return this;
    }

    public AbstractWidgetModel<T> setPanel(InputPanel<?, ?> panel) {
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
        return type == String.class;
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
        return type == boolean.class || type == Boolean.class;
    }

    @Override
    public boolean isMultipleChoice() {
        return choices.size() > 0;
    }

    @Override
    public boolean isType(Class<?> type) {
        return this.type.isAssignableFrom(type);
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

}
