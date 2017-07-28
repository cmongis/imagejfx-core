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
package mongis.utils;

import ijfx.core.property.Getter;
import java.util.concurrent.Callable;

/**
 *
 * @author cyril
 */
public class ObservableProgressHandler implements ProgressHandler{

    private double total = 1;
    
    private double progress = 0;
    
    private String message;
    
    private Runnable onChange =  ()->{};

    private Getter<Boolean> cancel = ()->false;
    
    
    public ObservableProgressHandler() {
        
    }
    
    public ObservableProgressHandler(Runnable onChange) {
        this();
        setOnChange(onChange);
    }
    
    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    public void setCancel(Getter<Boolean> cancel) {
        this.cancel = cancel;
    }
    
    private void fireChangeEvent() {
        onChange.run();
    }
    
    
    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    public void setProgress(double progress) {
        this.progress = progress;
        fireChangeEvent();
    }

    @Override
    public void setProgress(double workDone, double total) {
        setProgress(workDone/total);
        
    }

    @Override
    public void setProgress(long workDone, long total) {
        setProgress(1.0 * workDone / total);
    }

    @Override
    public void setStatus(String message) {
        this.message = message;
    }

    @Override
    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public void increment(double inc) {
        progress += inc/total;
        fireChangeEvent();
    }

    @Override
    public boolean isCancelled() {
        return cancel.get();
    }

    public String getMessage() {
        return message;
    }
    
    
    
}
