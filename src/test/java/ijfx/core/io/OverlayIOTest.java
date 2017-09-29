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

import com.fasterxml.jackson.databind.ObjectMapper;
import ijfx.core.IjfxTest;
import ijfx.core.overlay.io.OverlayIOService;
import ijfx.core.overlay.io.PolygonOverlayBuilder;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import junit.framework.Assert;
import net.imagej.overlay.LineOverlay;
import net.imagej.overlay.Overlay;
import net.imagej.overlay.PolygonOverlay;
import net.imagej.overlay.RectangleOverlay;
import org.junit.Test;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class OverlayIOTest extends IjfxTest {

    @Parameter
    ExplorableIOService explorableIOService;

    @Parameter
    OverlayIOService overlayIOService;

    protected RectangleOverlay createRectangle() {

        RectangleOverlay overlay = new RectangleOverlay(getContext());
        overlay.setName("My rectangle");

        overlay.setOrigin(20, 0);
        overlay.setOrigin(20, 1);
        overlay.setExtent(40, 0);
        overlay.setExtent(50, 1);

        return overlay;

    }

    protected LineOverlay createLine() {
        LineOverlay overlay = new LineOverlay(getContext());

        overlay.setLineStart(new double[]{2, 4});
        overlay.setLineEnd(new double[]{5, 8});

        overlay.setName("My line");
        return overlay;
    }

    protected PolygonOverlay createPolygon() {

        PolygonOverlay polygonOverlay = new PolygonOverlayBuilder(getContext())
                .addVertex(0, 0)
                .addVertex(1, 1)
                .addVertex(2, 1)
                .addVertex(4, 1)
                .addVertex(1, 4)
                .addVertex(2, 2)
                .build();

        polygonOverlay.setName("My polygon");

        return polygonOverlay;

    }

    protected File createTempFile(String id) throws IOException {
        return File.createTempFile(id, ".json");
    }

    protected <T extends Overlay> Object writeAndRead(ObjectMapper mapper, T overlay) throws IOException {

        File tmpFile = createTempFile(overlay.getName());
        mapper.writeValue(tmpFile, overlay);

        displayFile(tmpFile);

        return mapper.readValue(tmpFile, Object.class);
    }

    @Test
    public void testWithExplorableIOService() throws IOException {

        testMapper(explorableIOService.getJsonMapper());

    }

    @Test
    public void testWithOverlayIOService() throws IOException {
        testMapper(overlayIOService.getJsonMapper());
    }

    protected void testMapper(ObjectMapper mapper) throws IOException {
        new Tester<PolygonOverlay>(mapper)
                .create(this::createPolygon)
                .doBasicTests()
                .then((polygon, loaded) -> {
                    Assert.assertEquals("polygon vertex count are the same", polygon.getRegionOfInterest().getVertexCount(), loaded.getRegionOfInterest().getVertexCount());
                });

        new Tester<RectangleOverlay>(mapper)
                .create(this::createRectangle)
                .doBasicTests()
                .then((original, loaded) -> {

                    Assert.assertEquals("rectangle origin x", original.getOrigin(0), loaded.getOrigin(0));
                    Assert.assertEquals("rectangle origin y", original.getOrigin(1), loaded.getOrigin(1));
                    Assert.assertEquals("rectangle extent x", original.getExtent(0), loaded.getExtent(0));
                    Assert.assertEquals("rectangle extent y", original.getExtent(1), loaded.getExtent(1));

                });

        new Tester<LineOverlay>(mapper)
                .create(this::createLine)
                .doBasicTests()
                .then((original, loaded) -> {
                   Assert.assertEquals("line start x", original.getLineStart(0),loaded.getLineStart(0),0);
                   Assert.assertEquals("line start y", original.getLineStart(1),loaded.getLineStart(1),0);
                   Assert.assertEquals("line end x", original.getLineEnd(0),loaded.getLineEnd(0),0);
                   Assert.assertEquals("line end y", original.getLineEnd(1),loaded.getLineEnd(1),0);
                });
        
        
        
    }

    protected class Tester<T extends Overlay> {

        BiConsumer<T, T> then;

        T original;

        T loaded;

        final ObjectMapper mapper;

        public Tester(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        protected String getId() {
            return original.getName();
        }

        protected String msg(String msg) {
            return new StringBuilder()
                    .append(getId())
                    .append(" ")
                    .append(msg)
                    .toString();
        }

        public Tester<T> create(Callable<T> create) {
            try {
                original = create.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public Tester<T> doBasicTests() throws IOException {

            loaded = (T) writeAndRead(mapper, original);

            Assert.assertNotNull(msg("not null"), loaded);
            Assert.assertTrue(msg("right class"), loaded.getClass() == original.getClass());
            Assert.assertEquals(msg("has the same name"), original.getName(), loaded.getName());

            return this;
        }

        public Tester<T> then(BiConsumer<T, T> then) {
            then.accept(original, loaded);
            return this;
        }

    }

}
