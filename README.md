Copyright (c) <2015> <Alberto Ocampo, John Idarraga>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.




##############################################################
This is a minimal Maven project implementing an ImageJ 1.x plugin

It is intended as an ideal starting point to develop new ImageJ 1.x plugins
in an IDE of your choice. You can even collaborate with developers using a
different IDE than you.

In [Eclipse](http://eclipse.org), for example, it is as simple as
_File&gt;Import...&gt;Existing Maven Project_

In [Netbeans](http://netbeans.org), it is even simpler: _File&gt;Open_
Project. The same works in [IntelliJ](http://jetbrains.net).

If [jEdit](http://jedit.org) is your preferred IDE, you will need the [Maven
Plugin](http://plugins.jedit.org/plugins/?MavenPlugin).

Die-hard command-line developers can use Maven directly by calling _mvn_
in the project root.

However you build the project, in the end you will have the ```.jar``` file
(called *artifact* in Maven speak) in the _target/_ subdirectory.

To copy the artifact into the correct place, you can call ```mvn
-Dimagej.app.directory=/path/to/Fiji.app/```. This will not only copy your
artifact, but also all the dependencies. Restart your ImageJ or call
*Help>Refresh Menus* to see your plugin in the menus.

Developing plugins in an IDE is convenient, especially for debugging. To
that end, the plugin contains a _main()_ method which sets the _plugins.dir_
system property (so that the plugin is added to the Plugins menu), starts
ImageJ, loads an image and runs the plugin. See also
[this page](fiji.sc/Debugging#Debugging_plugins_in_an_IDE_.28Netbeans.2C_IntelliJ.2C_Eclipse.2C_etc.29)
for information how Fiji makes it easier to debug in IDEs.

Since this project is intended as a starting point for your own
developments, it is in the public domain.


If you cloned the ```minimal-ij1-plugin``` repository, you probably want to
publish the result in your own repository:

1. Call ```git status``` to verify .gitignore lists all the files (or file
   patterns) that should be ignored
2. Call ```git add .``` and ```git add -u``` to stage the current files for
   commit
3. Call ```git commit``` or ```git gui``` to commit the changes
4. [Create a new GitHub repository](https://github.com/new)
5. ```git remote set-url origin git@github.com:<username>/<projectname>```
6. ```git push origin HEAD```
