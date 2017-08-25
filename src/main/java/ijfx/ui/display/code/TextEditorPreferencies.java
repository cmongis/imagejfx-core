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

import ijfx.ui.main.ImageJFX;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author florian
 */
public class TextEditorPreferencies implements Preferencies {
    
    private StringProperty themeProperty = new SimpleStringProperty("darkTheme");
    private List<String> listOfTheme = new ArrayList<>();
    //@org.scijava.plugin.Parameter(label = "enable autocompletion")
    private boolean autocompletion = true;
    //@org.scijava.plugin.Parameter(label = "enable side panel")
    private boolean sidePanel = true;
    
    private ObjectProperty<File> customCssProperty = new SimpleObjectProperty<>();

    public TextEditorPreferencies() {
        this.listOfTheme.add("darkTheme");
        this.listOfTheme.add("lightTheme");
       
    }
    
    public String getTheme() {
        //System.out.println(themeProperty.get());
        return themeProperty.get();
    }

    public List<String> getListOfTheme() {
        return listOfTheme;
    }

    public boolean isAutocompletion() {
        return autocompletion;
    }

    public boolean isSidePanel() {
        return sidePanel;
    }

    public File getCustomCSS() {
        return customCssProperty.get();
    }

    public void setTheme(String theme) {
        this.themeProperty.set(theme);
    }

    public void setListOfTheme(List<String> listOfTheme) {
        this.listOfTheme = listOfTheme;
    }

    public void setAutocompletion(boolean autocompletion) {
        this.autocompletion = autocompletion;
    }

    public void setSidePanel(boolean sidePanel) {
        this.sidePanel = sidePanel;
    }

    public void setCustomCSS(File customCSS) {
        // copy of the new css file in the config directory
        String[] newCssName = customCSS.getAbsolutePath().split(File.separator);
        String newCssPath = ImageJFX.getConfigDirectory() + "/ScriptEditorConfig/" + newCssName[newCssName.length-1];
        File newCss = new File(newCssPath.replaceAll("/", File.separator));
        try {
            copyFile(customCSS, newCss);
        } catch (IOException IOException) {
            System.out.println("Error while copying imported CSS");
        }
        String[] themeName = newCssName[newCssName.length-1].split("\\.");
        if (!listOfTheme.contains(themeName[0])){
            listOfTheme.add(themeName[0]);
        }
        this.customCssProperty.set(newCss);
    }
    
    public void copyFile(File src, File dest)throws IOException{
        
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);

        byte[] buffer = new byte[1024];

        int length;
        //copy the file content in bytes
        while ((length = in.read(buffer)) > 0){
           out.write(buffer, 0, length);
        }

        in.close();
        out.close();
            
    }
    
    
}
