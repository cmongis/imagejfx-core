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
package ijfx.core.mainwindow;

import ijfx.core.activity.Activity;
import ijfx.core.hint.Hint;
import ijfx.ui.UiPlugin;
import javafx.concurrent.Task;
import javax.management.Notification;

/**
 *
 * @author cyril
 */
public interface MainWindow {
    
    void displayHint(Hint hint);
    void displayActivity(Activity activity);
    void displayNotification(Notification notification);
    void displaySideMenuAction(SideMenuAction action);
    void registerUiPlugin(UiPlugin uiPlugin);
    void addForegroundTask(Task task);
    void addBackgroundTask(Task task);
    void setReady(boolean ready);
            
    
    
}
