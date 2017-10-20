# ImageJFX Core





This package contains the core elements of ImageJ-FX which are : 

* the JavaFX UI elements
* the UiContext Service
* the UiPlugins

## Be Aware

Before you install ImageJ-FX, please be aware that ImageJ-FX
still remains on a experimental state and does not support
ImageJ1 Scripts or plugins.

I would recommend you to install it on a separate Fiji / ImageJ2 installation
installation

## Installation

For testing, download the [latest version of Fiji](http://fiji.sc/#download) and run
it. You must now install the ImageJ-FX repository in order to receive to download
its latest version through the official ImageJ update system.

### Easy update site installation

Download [this script](https://github.com/cmongis/imagejfx-core/raw/master/install-ijfx.js)
 and drag it into Fiji's window. A script window will open. 
Click on *Run it* and it will install the ImageJ-FX update site, launch
the update process. After some time, you should the list of 
jar files to install. Click on "*Apply all changes*".
Once the process over, close Fiji and restart the program.
ImageJ-FX should open.

### Manual Update site installation

Open Fiji and go to "*Help > Update...*". This will launch
the update process. Once the process over, you should
see a window appearing. Click on the bottom left button "Advanced"
then "Add update site". Fill its name "ImageJ-FX" and address
"http://site.imagejfx.net". Validate the change and 
click on "Apply all changes" to start the installation
process. Restart Fiji. Your newly installed ImageJ-FX window should appear instead
of the classical user interface.



## Missing important features in ImageJ-FX

*	ImageJ1 Plugin support and installation
*	3D support




## License
ImageJ FX is a user interface running on top of ImageJ and written in Java using the JavaFX technology. 
 
Copyright (C) 2015  Cyril MONGIS

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

