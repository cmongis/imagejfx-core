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
import java.util.Optional;

import org.fxmisc.richtext.model.SegmentOps;

public class LinkedImageOps<S> implements SegmentOps<LinkedImage<S>, S> {

    private final EmptyLinkedImage<S> emptySeg = new EmptyLinkedImage<>();

    @Override
    public int length(LinkedImage<S> seg) {
        return seg == emptySeg ? 0 : 1;
    }

    @Override
    public char charAt(LinkedImage<S> seg, int index) {
        return seg == emptySeg ? '\0' : '\ufffc';
    }

    @Override
    public String getText(LinkedImage<S> seg) {
        return seg == emptySeg ? "" : "\ufffc";
    }

    @Override
    public LinkedImage<S> subSequence(LinkedImage<S> seg, int start, int end) {
        if (start < 0) {
            throw new IllegalArgumentException("Start cannot be negative. Start = " + start);
        }
        if (end > length(seg)) {
            throw new IllegalArgumentException("End cannot be greater than segment's length");
        }
        return start == 0 && end == 1
                ? seg
                : emptySeg;
    }

    @Override
    public LinkedImage<S> subSequence(LinkedImage<S> seg, int start) {
        if (start < 0) {
            throw new IllegalArgumentException("Start cannot be negative. Start = " + start);
        }
        return start == 0
                ? seg
                : emptySeg;
    }

    @Override
    public S getStyle(LinkedImage<S> seg) {
        return seg.getStyle();
    }

    @Override
    public LinkedImage<S> setStyle(LinkedImage<S> seg, S style) {
        return seg == emptySeg ? emptySeg : seg.setStyle(style);
    }

    @Override
    public Optional<LinkedImage<S>> join(LinkedImage<S> currentSeg, LinkedImage<S> nextSeg) {
        return Optional.empty();
    }

    @Override
    public LinkedImage<S> createEmpty() {
        return emptySeg;
    }
}
