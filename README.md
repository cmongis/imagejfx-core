## Be Aware before installing ImageJ-FX
ImageJ-FX
still remains on a experimental state and does not support
ImageJ1 Scripts or plugins. Thus, after installing ImageJ-FX on your installation, you won't be able to move back to the old Fiji. For these reaons, I would recommend you to install ImageJ-FX on a separate Fiji / ImageJ2 installation.

### 1. Installation

For a safe testing, download the [latest version of Fiji](http://fiji.sc/#download), extract it, and start
it. The next step consists into adding ImageJ-FX repository to your update site list in order to download the latest ImageJ-FX version and receive occasional updates.

#### 1.1 Easy update site installation

Download [this script](https://github.com/cmongis/imagejfx-core/raw/master/install-ijfx.js)
 and drag it into Fiji's window. A script window will open. 
Click on *Run it* and it will install the ImageJ-FX update site, launch
the update process. After some time, you should the list of 
jar files to install. Click on "*Apply all changes*".
Once the process over, close Fiji and restart the program.
ImageJ-FX should open.

#### 1.2 Manual Update site installation

Open Fiji and go to "*Help > Update...*". This will launch
the update process. Once the process over, you should
see a window appearing. Click on the bottom left button "Advanced"
then "Add update site". Fill its name "ImageJ-FX" and address
"http://site.imagejfx.net". Validate the change and 
click on "Apply all changes" to start the installation
process. Restart Fiji. Your newly installed ImageJ-FX window should appear instead
of the classical user interface.



### 3. Missing  features

*	ImageJ1 Plugin support and installation
*	3D support




### 4. License
ImageJ FX is a user interface running on top of ImageJ and written in Java using the JavaFX technology. 
 
Copyright (C) 2015  Cyril MONGIS

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

