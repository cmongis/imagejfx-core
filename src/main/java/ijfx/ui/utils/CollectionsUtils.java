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
package ijfx.ui.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javafx.util.Pair;

/**
 *
 * @author Cyril MONGIS
 */
public class CollectionsUtils {

    public static <T> List<T> toAdd(Collection<? extends T> source, Collection<? extends T> target) {

        return source
                .stream()
                .filter(t -> target.contains(t) == false)
                .collect(Collectors.toList());

    }

    public static <T> List<T> toAdd(Collection<? extends T> source, Collection<? extends T> target, Comparator<T> comparator) {

        return source
                .stream()
                .filter(t1 -> target.stream().filter(t2 -> comparator.compare(t1, t2) == 0).count() == 0)
                .collect(Collectors.toList());

    }

    public static <T> List<T> toRemove(Collection<? extends T> source, Collection<? extends T> target, Comparator<T> comparator) {

        return target
                .stream()
                .filter(t1 -> source.stream().filter(t2 -> comparator.compare(t1, t2) == 0).count() == 0)
                .collect(Collectors.toList());

    }

    public static <T> List<T> toRemove(Collection<? extends T> source, Collection<? extends T> target) {

        return target
                .stream()
                .filter(t -> source.contains(t) == false)
                .collect(Collectors.toList());

    }

    public static <T> void synchronize(Collection<? extends T> source, Collection<T> target) {

        List<T> toAdd = toAdd(source, target);
        List<T> toRemove = toRemove(source, target);

        target.addAll(toAdd);
        target.removeAll(toRemove);
    }

    public static <T> void synchronize(Collection<? extends T> source, Collection<T> target, Comparator<T> comparator, BiConsumer<T, T> updater) {

        List<T> toAdd = toAdd(source, target, comparator);
        List<T> toRemove = toAdd(source, target, comparator);
        
        target.addAll(toAdd);
        target.removeAll(toRemove);
        
        // updating
        target
                .stream()
                .map(inTarget -> {
                    T equivalent = source
                            .stream()
                            .filter(inSource -> comparator.compare(inTarget, inSource) == 0)
                            .findFirst()
                            .orElse(null);
                    if (equivalent != null) {
                        return new Pair<T, T>(equivalent, inTarget);
                    } else {
                        return null;
                    }
                })
                .forEach(pair -> updater.accept(pair.getKey(), pair.getValue()));

    }

    public static <T> void syncronizeContent(Collection<T> source, Collection<T> dest) {

        dest.addAll(source.stream().filter(e -> !dest.contains(e)).collect(Collectors.toList()));
        dest.removeAll(dest.stream().filter(e -> source.contains(e) == false).collect(Collectors.toList()));

    }
}
