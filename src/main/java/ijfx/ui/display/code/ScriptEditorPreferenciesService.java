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
import ijfx.ui.main.ImageJFX;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import net.imagej.ImageJService;
import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.scijava.widget.WidgetService;


/**
 *
 * @author florian
 *  
 */
@Plugin(type = Service.class,priority = Priority.VERY_LOW_PRIORITY)
public class ScriptEditorPreferenciesService extends AbstractService implements ImageJService, ScriptEditorPreferencies{
    @Parameter
    JsonPreferenceService jsonPreferenceService;
    @Parameter
    WidgetService widgetService;
    
    private String fileName = "ScriptEdtirorPreferences";
    private String nanorcDirectory = "ScriptEditorConfig";
    private TextEditorPreferencies preferencies= new TextEditorPreferencies();
    private File configDirectory = ImageJFX.getConfigDirectory(); 
    private String separator = File.separator;

    public ScriptEditorPreferenciesService() {
        File file = new File(this.configDirectory + separator + nanorcDirectory);
        //List<String> text = Files.readAllLines(file.toPath(), Charset.defaultCharset());
        if (file.exists()){
            try {
                List<String> text = Files.readAllLines(file.toPath(), Charset.defaultCharset());
            } catch (Exception IOException) {
                
                throw new UnsupportedOperationException("Not supported yet.");
                //System.out.println("/!\\ /!\\ !!! " + e.getMessage());
            }
        }
        else{
            createnanorcDirectory();
        }
        
    }
    
    public void createnanorcDirectory(){
        File target = new File(this.configDirectory + separator + nanorcDirectory);
        File source = new File(getClass().getResource("/ijfx/ui/display/code").getPath());
        try {
            copyFolder(source, target);
        } catch (Exception IOException) {
            System.out.println(IOException);
            System.out.println("Error: nanorcFiles not found");
        }
        
    }
    
    public void copyFolder(File src, File dest) throws IOException{
        Boolean test = src.isDirectory();
        String test2 = src.getPath();
    	if(src.isDirectory()){

            //if directory not exists, create it
            if(!dest.exists()){
               dest.mkdir();
               System.out.println("Directory copied from "+ src + "  to " + dest);
            }

            //list all the directory contents
            String files[] = src.list();

            for (String file : files) {
               //construct the src and dest file structure
                
               File srcFile = new File(src, file);
               File destFile = new File(dest, file);
               //recursive copy
               copyFolder(srcFile,destFile);
                
               
            }

    	}else{
            //if file, then copy it
            //Use bytes stream to support all file types
            if (src.getName().endsWith(".nanorc")|| src.getName().endsWith(".css")) {
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
                System.out.println("File copied from " + src + " to " + dest);
            }
    	}
    }
    
    
    @Override
    public void loadPreferencies(){
        this.preferencies = jsonPreferenceService.loadFromJson(fileName, preferencies);
                
    }
    
    @Override
    public void savePreferencies(){
        jsonPreferenceService.savePreference(preferencies, fileName);
    }

    @Override
    public Preferencies getPreferencies(){
        return this.preferencies;
    }
    
   
    @Override
    public void initialize(){
        loadPreferencies();
        
    }

}
