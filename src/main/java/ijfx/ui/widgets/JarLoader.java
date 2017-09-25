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
package ijfx.ui.widgets;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.utils.PathUtils;
import ijfx.ui.main.ImageJFX;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

/**
 *
 * @author Cyril MONGIS
 */
public class JarLoader implements Loader<String> {
    
    Logger logger = ImageJFX.getLogger();
    private String prefix;
    private String suffix;
    private String charset = "UTF-8";
    private char expectedSeparator = '/';
    private final Class rcl;

    public JarLoader(Class rcl) {
        this.rcl = rcl;
    }

    @Override
    public Reader getReader(String templateName) throws LoaderException {
        InputStreamReader isr = null;
        Reader reader = null;
        InputStream is = null;
        // append the prefix and make sure prefix ends with a separator character
        StringBuilder path = new StringBuilder(128);
        if (getPrefix() != null) {
            path.append(getPrefix());
            // we do NOT use OS dependent separators here; getResourceAsStream
            // explicitly requires forward slashes.
            if (!getPrefix().endsWith(Character.toString(expectedSeparator))) {
                path.append(expectedSeparator);
            }
        }
        path.append(templateName);
        if (getSuffix() != null) {
            path.append(getSuffix());
        }
        String location = path.toString();
        logger.fine(String.format("Looking for template in %s.", location));
        // perform the lookup
        is = rcl.getResourceAsStream(location);
        if (is == null) {
            throw new LoaderException(null, "Could not find template \"" + location + "\"");
        }
        try {
            isr = new InputStreamReader(is, charset);
            reader = new BufferedReader(isr);
        } catch (UnsupportedEncodingException e) {
        }
        return reader;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getCharset() {
        return charset;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String resolveRelativePath(String relativePath, String anchorPath) {
        return PathUtils.resolveRelativePath(relativePath, anchorPath, expectedSeparator);
    }

    @Override
    public String createCacheKey(String templateName) {
        return templateName;
    }
    
}
