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
package ijfx.core.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cyril MONGIS
 */
public class DimensionalGetter<R> implements Callable<R> {

    private final Object bean;

    private final Class<?> type;

    private final int dim;

    private Method method;

    private Logger logger = Logger.getLogger(DimensionalGetter.class.getName());

    public DimensionalGetter(Object bean, Class<?> type, String methodName, int dim) {
        this.bean = bean;
        this.type = type;
        this.dim = dim;

        try {
            method = bean.getClass().getMethod(methodName,int.class);
        } catch (NoSuchMethodException ex) {
            logger.log(Level.SEVERE, null, ex);
            method = null;
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, null, ex);
            method = null;
        }

    }

    @Override
    public R call() {
        try {
            return (R) method.invoke(bean, dim);
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
