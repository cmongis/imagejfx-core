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
package mongis.utils.bindings;

import mongis.utils.task.FakeTask;
import mongis.utils.UITesterBase;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class TaskButtonBindingTester extends UITesterBase{

    Button button;
    
    @Override
    public Node initApp() {
        
        
        button = new Button("Click me please :-)");
        new TaskButtonBinding(button)
                .setTextWhenRunning("I'm running man !")
                .setTextWhenSucceed("I did it !")
                .setTextWhenError("Damn, something happends")
                .setTaskFactory(this::generateMe);
        
        
       return button;
        
        
    }
    
    public static void main(String... args) {
        launch(args);
    }
    
    
    
    
    private Task<Boolean> generateMe(TaskButtonBinding binding) {
        return new FakeTask(4000);
    }

    @Override
    public String getStyleSheet() {
        return null;
    }
}
