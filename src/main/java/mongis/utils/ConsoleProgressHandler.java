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

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Cyril MONGIS
 */
public class ConsoleProgressHandler implements ProgressHandler {

    private String message = "";

    private double total = 1.0;

    private double progress = 0;

    private long startTime = 0;
    
    boolean canceled = false;
    
    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setProgress(double progress) {
        if(this.progress == 0 && progress != 0) {
            startTime = System.currentTimeMillis();
        }
        
        printProgress(startTime,100,Math.round(progress * 100));
        
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
        setProgress(progress + (inc/total)); 
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    private static void printProgress(long startTime, long total, long current) {
        long eta = current == 0 ? 0
                : (total - current) * (System.currentTimeMillis() - startTime) / current;

        String etaHms = current == 0 ? "N/A"
                : String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        percent = percent > 100 ? 100 : percent;
        string
                .append('\r')
                .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% [", percent))
                .append(String.join("", Collections.nCopies(percent, "=")))
                .append('>')
                .append(String.join("", Collections.nCopies(100 - percent, " ")))
                .append(']')
                .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        System.out.print(string);
    }

}
