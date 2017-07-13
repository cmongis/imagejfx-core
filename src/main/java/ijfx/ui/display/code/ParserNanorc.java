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

/**
 *
 * @author florian
 */
public class ParserNanorc {
    
    public void nanorcParser(File file){
        Hashtable keywords = new Hashtable(); // creation de la table de hashage
        if (file.equals(null)){
            return;
        }
        List<String> text = new ArrayList<>();
        try {
            text = Files.readAllLines(file.toPath(), Charset.defaultCharset());// lecture du fichier, tout est mis dans la list text
                    } catch (IOException ex) {
            Logger.getLogger(ParserNanorc.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (String line : text){
            String[] splitedLine = line.split(" ");
            if (splitedLine[0].equals("color")){
                List<String> words = new ArrayList<>(); // creation d'une entree dans la table, la valeur est une liste qui contiendra les mots
                String chain = splitedLine[2].substring(4,splitedLine[2].indexOf(")\\>"));
                for (String word : chain.split("|")){
                    words.add(word);
                }
                keywords.put(splitedLine[1], words);
            }
            
        }
    }
}
