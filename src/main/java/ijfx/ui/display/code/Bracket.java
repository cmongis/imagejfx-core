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
public class Bracket {
    private int openPosition;
    private int closePosition;
    private boolean complete;

    public Bracket() {
    }

    public Bracket(int openPosition, int closePosition) {
        this.openPosition = openPosition;
        this.closePosition = closePosition;
    }
    

    public int getOpenPosition() {
        return openPosition;
    }

    public int getClosePosition() {
        return closePosition;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setOpenPosition(int openPosition) {
        this.openPosition = openPosition;
    }

    public void setClosePosition(int closePosition) {
        this.closePosition = closePosition;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    
    
}
