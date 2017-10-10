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
/**
 *
 * @author cyril
 */
package ijfx.ui.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HelpConfigurationTest {
    
    
    private static final String KEYWORD = "eminem";
    
    @Test
    public void test() throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        
        HelpConfiguration conf = mapper.readValue(new File("src/test/resources/HelpConfigurationExample.json"), HelpConfiguration.class);
        
        
        Assert.assertTrue("description", conf.getDescriptions().containsKey(KEYWORD));
        Assert.assertEquals("hints number",2,conf.getHintList().size());
        Assert.assertTrue("hint content",conf.getHintList().get(0).getTarget().contains(KEYWORD));
    }
    
}
