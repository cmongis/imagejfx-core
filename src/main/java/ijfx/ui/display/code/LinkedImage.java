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

/**
 *
 * @author florian
 */
import javafx.scene.Node;
import org.fxmisc.richtext.model.Codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface LinkedImage<S> {
    static <S> Codec<LinkedImage<S>> codec(Codec<S> styleCodec) {
        return new Codec<LinkedImage<S>>() {

            @Override
            public String getName() {
                return "LinkedImage<" + styleCodec.getName() + ">";
            }

            @Override
            public void encode(DataOutputStream os, LinkedImage<S> i) throws IOException {
                // don't encode EmptyLinkedImage objects
                if (i.getStyle() != null) {
                    // external path rep should use forward slashes only
                    String externalPath = i.getImagePath().replace("\\", "/");
                    Codec.STRING_CODEC.encode(os, externalPath);
                    styleCodec.encode(os, i.getStyle());
                }
            }

            @Override
            public RealLinkedImage<S> decode(DataInputStream is) throws IOException {
                // Sanitize path - make sure that forward slashes only are used
                String imagePath = Codec.STRING_CODEC.decode(is);
                imagePath = imagePath.replace("\\",  "/");
                S style = styleCodec.decode(is);
                return new RealLinkedImage<>(imagePath, style);
            }

        };
    }

    LinkedImage<S> setStyle(S style);

    S getStyle();

    /**
     * @return The path of the image to render.
     */
    String getImagePath();

    Node createNode();
}
