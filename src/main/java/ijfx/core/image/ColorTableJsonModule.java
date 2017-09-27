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
package ijfx.core.image;

import mongis.utils.CompressionUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.nio.ByteBuffer;
import net.imglib2.display.ColorTable16;
import net.imglib2.display.ColorTable8;

/**
 *
 * @author cyril
 */
public class ColorTableJsonModule extends SimpleModule{

    public ColorTableJsonModule() {
        
        addDeserializer(ColorTable8.class, new ColorTable8Deserializer());
        addSerializer(ColorTable8.class,new ColorTable8Serializer());
        addDeserializer(ColorTable16.class, new ColorTable16Deserializer());
        addSerializer(ColorTable16.class,new ColorTable16Serializer());

    }

   
    
    
    protected class ColorTable8Serializer extends JsonSerializer<ColorTable8> {

        @Override
        public void serializeWithType(ColorTable8 colorTable, JsonGenerator jg, SerializerProvider sp, TypeSerializer tp)  throws IOException, JsonProcessingException  {
            
            jg.writeStartObject();
            jg.writeStringField(tp.getPropertyName(), ColorTable8.class.getName());
            
            writeObject(colorTable, jg);
            jg.writeEndObject();
    //serialize(colorTable, jg, sp);
        }
        
        @Override
        public void serialize(ColorTable8 colorTable, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
            
            jg.writeStartObject();
           
            writeObject(colorTable, jg);
            
           
            
            jg.writeEndObject();
            
        }
        private void writeObject(ColorTable8 colorTable, JsonGenerator jg) throws IOException{
            jg.writeNumberField("components", colorTable.getComponentCount());
            
            for(int i = 0;i!=colorTable.getComponentCount();i++) {
                jg.writeBinaryField(""+i, colorTable.getValues()[i]);
            }
        }
        
    }
    
    
    
    
    protected class ColorTable8Deserializer extends JsonDeserializer<ColorTable8> {

       
        
        @Override
        public ColorTable8 deserialize(JsonParser jp, DeserializationContext arg1) throws IOException, JsonProcessingException {
            
           jp.nextValue();
            int components = jp.getIntValue();
            jp.nextValue();
            
            byte[][] values = new byte[components][];
            
            for(int i = 0;i!=components;i++) {
                values[i] = jp.getBinaryValue();
                jp.nextValue();
            }
            
            return new ColorTable8(values);
            
        }
        
    }
    
    
    /**
     * Color 16 Serializing / Deserializing
     */
    
    
    /*
        Utility methods
    */
    protected byte[] toBytes(short[] shorts) throws IOException {
        
        
       
        ByteBuffer allocate = ByteBuffer.allocate(shorts.length * 2);
        
        for(short s : shorts) {
            allocate.putShort(s);
        }
        
        return CompressionUtils.compress(allocate.array());
    }
    
    protected short[] toShorts(byte[] bytes) throws IOException{
        try {
        bytes = CompressionUtils.decompress(bytes);
        
        short[] shorts = new short[bytes.length / 2];
        
       
        int i = 0;
        ByteBuffer allocate = ByteBuffer
                .wrap(bytes);
        
        while(allocate.hasRemaining()) {
            shorts[i] = allocate.getShort();
            i++;
        }
        return shorts;
        }
        catch(Exception e) {
            return null;
        }
        
    }
    
    
    
    
    protected class ColorTable16Serializer extends JsonSerializer<ColorTable16> {

        @Override
        public void serializeWithType(ColorTable16 colorTable, JsonGenerator jg, SerializerProvider sp, TypeSerializer tp)  throws IOException, JsonProcessingException  {
            
            jg.writeStartObject();
            jg.writeStringField(tp.getPropertyName(), ColorTable16.class.getName());
            
            writeObject(colorTable, jg);
            jg.writeEndObject();
    //serialize(colorTable, jg, sp);
        }
        
        @Override
        public void serialize(ColorTable16 colorTable, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
            
            jg.writeStartObject();
           
            writeObject(colorTable, jg);
            
           
            
            jg.writeEndObject();
            
        }
        private void writeObject(ColorTable16 colorTable, JsonGenerator jg) throws IOException{
            jg.writeNumberField("components", colorTable.getComponentCount());
            
            for(int i = 0;i!=colorTable.getComponentCount();i++) {
                jg.writeBinaryField(""+i, toBytes(colorTable.getValues()[i]));
            }
        }
        
    }
    
    
    protected class ColorTable16Deserializer extends JsonDeserializer<ColorTable16> {

       
        
        @Override
        public ColorTable16 deserialize(JsonParser jp, DeserializationContext arg1) throws IOException, JsonProcessingException {
            
           jp.nextValue();
            int components = jp.getIntValue();
            jp.nextValue();
            
            short[][] values = new short[components][];
            
            for(int i = 0;i!=components;i++) {
                values[i] = toShorts(jp.getBinaryValue());
                jp.nextValue();
            }
            
            return new ColorTable16(values);
            
        }
        
    }
    
    
}
