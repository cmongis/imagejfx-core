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
package ijfx.core.icon;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import ijfx.core.utils.SciJavaUtils;
import ijfx.ui.main.ImageJFX;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import org.apache.commons.io.IOUtils;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 *
 * @author cyril
 */
@Plugin(type = Service.class)
public class DefaultFXIconService extends AbstractService implements FXIconService {

    Map<String, String> fontawesomeIconEquivalent = new HashMap<>();

    Logger logger = ImageJFX.getLogger();

    public void initialize() {

        try {
            String toString = IOUtils.toString(getClass().getResourceAsStream("/ijfx/ui/icons/equivalents"), "utf8");

            for (String line : toString.split("\n")) {

                String[] split = line.split("=");

                if (split.length != 2) {
                    continue;
                }

                registerEquivalent(split[0].trim(), split[1].trim());

            }

        } catch (IOException ex) {
            Logger.getLogger(DefaultFXIconService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public Node getIconAsNode(SciJavaPlugin plugin) {

        String menuPath = SciJavaUtils.getIconPath(plugin);

        if (menuPath == null) {
            return null;
        }

        if (fontawesomeIconEquivalent.containsKey(plugin.getClass().getName())) {
            return getIconAsNode(fontawesomeIconEquivalent.get(plugin.getClass().getName()));
        } else if (menuPath.startsWith("fa:") == false) {
            return getIconAsNode(plugin.getClass().getResource(menuPath).toExternalForm());
        } else {
            return getIconAsNode(menuPath);
        }
    }

    @Override
    public Node getIconAsNode(String iconPath) {

        if (iconPath == null) {
            return null;
        }
        if ("".equals(iconPath)) {
            return null;
        }

        if (fontawesomeIconEquivalent.containsKey(iconPath)) {
            return getIconAsNode(fontawesomeIconEquivalent.get(iconPath));
        } else if (iconPath.startsWith("fa:")) {
            try {
                return new FontAwesomeIconView(getIcon(iconPath));
            } catch (Exception e) {
                logger.log(Level.WARNING, String.format("Couldn't load FA icon : %s", iconPath.substring(3).toUpperCase()));
                return new FontAwesomeIconView(FontAwesomeIcon.REMOVE);
            }
        } else {

            ImageView imageView = new ImageView(iconPath);
            return imageView;
        }

    }

    public void registerEquivalent(String classPath, String fontawesomeId) {
        fontawesomeIconEquivalent.put(classPath, fontawesomeId);
    }

    @Override
    public void registerEquivalent(Class<?> clazz, String fontawesomeId) {

        registerEquivalent(clazz.getName(), fontawesomeId);

    }

    @Override
    public FontAwesomeIcon getIcon(String iconPath) {
        try {
            return FontAwesomeIcon.valueOf(iconPath.substring(3).toUpperCase());
        } catch (IllegalArgumentException ex) {
            return FontAwesomeIcon.QUESTION;
        }
    }

}
