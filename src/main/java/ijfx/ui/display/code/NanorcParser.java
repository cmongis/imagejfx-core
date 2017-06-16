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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scijava.script.ScriptLanguage;

/**
 *
 * @author florian
 */
public class NanorcParser implements LanguageKeywords{
    
    private Hashtable keywordsTable;
    private ScriptLanguage language;
    private String nanorcFile;

    public NanorcParser(ScriptLanguage language) {
        keywordsTable = new Hashtable();
        this.language = language;
    }

    public NanorcParser() {
        keywordsTable = new Hashtable();
    }
    
    
    
    @Override
    public Hashtable getKeywords() {
        return this.keywordsTable;
    }

    @Override
    public void setLanguage(ScriptLanguage language) {
        this.language = language;
        run();
    }

    @Override
    public void run() {
        try {
            this.nanorcFile = getClass().getResource(findFileLanguage(language)).getFile();
            nanoRcParseV2(this.nanorcFile);
        } catch (NullPointerException e) {
            System.out.println("No nanorc file for this language");
            this.keywordsTable = new Hashtable();
        }
        
        
    }
    
    public static String findFileLanguage(ScriptLanguage language) {
       return String.format("/ijfx/ui/display/code/%s.nanorc",language.getLanguageName().toLowerCase().replace(" ", ""));
    }
    
    public void nanoRcParseV2(String path){
        List<String> text = new ArrayList<>();
        File file = new File(path);
        try {
            text = Files.readAllLines(file.toPath(), Charset.defaultCharset());// lecture du fichier, tout est mis dans la list textProperty
                    } 
        catch (IOException ex) {
            Logger.getLogger(DefaultTextArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String line : text){
            String[] splitedLine = line.split(" ");
            if (splitedLine.length <= 1) continue;
            else {
                if (splitedLine[0].equals("color")){
                    if (splitedLine[2].matches("\"\\\\\\<\\(.*")){                                       // check if the regex start with ""\<(" = keywords in nanorc
                        if (this.keywordsTable.containsKey(splitedLine[1])){
                                String pattern = (String) this.keywordsTable.get(splitedLine[1]);
                                pattern = pattern.replace(")\\b", "|"+splitedLine[2]);
                                pattern = pattern.concat(")\\b");
                                this.keywordsTable.put(splitedLine[1], pattern);
                            }
                        else {
                            String pattern = splitedLine[2];
                            pattern= pattern.replace("\"\\<(", "\\b(");
                            pattern = pattern.replace(")\\>\"", ")\\b");
                            this.keywordsTable.put(splitedLine[1], pattern); //                                  adding the list in the hash table
                        }
                    }
                    
                }
            }
        }
    }
}
