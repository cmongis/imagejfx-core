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
package ijfx.core.io;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ijfx.core.metadata.MetaDataJsonModule;
import ijfx.core.overlay.io.OverlayIOService;
import ijfx.core.prefs.JsonPreferenceService;
import ijfx.explorer.datamodel.Explorable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = Service.class)
public class DefaultExplorableIOService extends AbstractService implements ExplorableIOService {

    @Parameter
    JsonPreferenceService prefService;

    ObjectMapper mapper = new ObjectMapper();

    @Parameter
    OverlayIOService overlayIOService;

    @Override
    public void initialize() {

        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
       
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(overlayIOService.getOverlayJsonModule());
        mapper.registerModule(new MetaDataJsonModule());

    }

    public ObjectMapper getJsonMapper() {
        return mapper;
    }

    @Override
    public List<? extends Explorable> loadAll(File file) throws IOException {

        List<? extends Explorable> readValue = mapper.readValue(getCompressedInputStream(file), mapper.getTypeFactory().constructCollectionType(List.class, Explorable.class));

        readValue.forEach(tag -> tag.inject(getContext()));
        
        return readValue;
    }

    @Override
    public void saveAll(List<? extends Explorable> explorableList, File file) throws IOException {

        mapper.writeValue(getCompressedOutputStream(file), explorableList);

    }

   
    @Override
    public void saveOne(Explorable taggable, File target) throws IOException {

        mapper.writeValue(getCompressedOutputStream(target), taggable);
    }

    @Override
    public Explorable loadOne(File file) throws IOException {
        Explorable expl =  mapper.readValue(getCompressedInputStream(file), Explorable.class);
        expl.inject(getContext());
        return expl;
    }

    public InputStream getCompressedInputStream(File file) throws IOException {
        
        
        FileInputStream fis = new FileInputStream(file);
        
        GZIPInputStream gis = new GZIPInputStream(fis);
        
        return gis;
        
        
    }
    
    public OutputStream getCompressedOutputStream(File file) throws IOException{
        
        FileOutputStream fos = new FileOutputStream(file);
        
        GZIPOutputStream gos = new GZIPOutputStream(fos);
        
        return gos;
        
        
    }
    
    
    
}
