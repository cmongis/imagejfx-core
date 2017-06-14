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

import ijfx.commands.script.RunScript;
import ijfx.core.formats.Script;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.IndexRange;
import javax.script.ScriptException;
import org.scijava.Prioritized;
import org.scijava.Priority;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.event.EventService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptService;

/**
 * TODO : change to DefaultScriptDisplay
 * @author florian
 */
@Plugin(type = Display.class, priority = Priority.HIGH_PRIORITY)
public class DefaultScriptDisplay extends AbstractDisplay<Script> implements ScriptDisplay {
    @Parameter
    EventService eventService;
    @Parameter
    ScriptService scriptService;
    
    
    private String copiedText;
    
    public DefaultScriptDisplay() {
        super(Script.class);
        
        textProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                get(0).setCode(newValue);
            }
        });
        
    }

    private Script getScript() {
        return get(0);
    }

    @Override
    public String getIdentifier() {
        return getScript().getSourceFile();
    }

    @Override
    public ScriptLanguage getLanguage() {
        return get(0).getLanguage();
    }

    @Override
    public void setLanguage(ScriptLanguage language) {
        get(0).setLanguage(language);
    }

    @Override
    public int compareTo(Prioritized o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LogService log() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void copyText() {
        
        this.copiedText=selectedTextProperty.getValue();
    }

    @Override
    public void pasteText() {
        StringBuilder sb = new StringBuilder(get(0).getCode());
        sb.replace(selectionProperty.getValue().getStart(),selectionProperty.getValue().getEnd(), copiedText);
        get(0).setCode(sb.toString());
    }

    @Override
    public void setSelectedText(String text) {
        selectedTextProperty.set(text);
    }

    @Override
    public StringProperty selectedTextProperty() {
        return selectedTextProperty;
    }


    @Override
    public final StringProperty textProperty() {
        return textProperty;
    }

    @Override
    public ObjectProperty<IndexRange> selectionProperty() {
        return selectionProperty;
    }

    @Override
    public final void setText(ObservableValue textValue) {
        textProperty.setValue(textValue.getValue().toString());
    }

    @Override
    public final String getText() {
        return textProperty.getValue();
    }

    @Override
    public void editText(String newValue) {
        get(0).setCode(newValue);
    }

    @Override
    public void setSelection(IndexRange indexRange) {
        selectionProperty.set(indexRange);
    }

    @Override
    public void undo() {
        eventService.publish(new UndoEvent());
    }

    @Override
    public void redo() {
        eventService.publish(new RedoEvent());
    }

    @Override
    public void runScript() {
        String path = get(0).getSourceFile();
        File scriptFile = new File(path);
        try {
            scriptService.run(scriptFile, true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RunScript.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScriptException ex) {
            Logger.getLogger(RunScript.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
