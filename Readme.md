# libParamTuner

Cross-platform library to ease the interactive tuning of parameters without the need to recompile code.

## Minimal example
```
#include <iostream>
#include <string>
#include <unistd.h>
#include "libparamtuner.h"
using namespace std;

int main() {
	double varDouble = 2.0;
	int varInt = 1;
	bool varBool = false;
	string varString;

	// Loads settings.xml file where each parameter is defined
	ParamTuner::load("settings.xml");
	// Binds each parameter of the xml file to a variable
	ParamTuner::bind("setting1", &varDouble);
	ParamTuner::bind("setting2", &varInt);
	ParamTuner::bind("mybool", &varBool);
	ParamTuner::bind("mystring", &varString);

	while (true) {
		// Modifying and saving the xml file will update the values in the console
		usleep(1000*500); // 500 ms
		cout << "setting1 (double) = " << varDouble
			<< " ; setting2 (int) = " << varInt
			<< " ; mybool (bool) = " << varBool
			<< " ; mystring (string) = " << varString
			<< endl;
	}
}
```
## C++ Library

The C++ libParamTuner library can be compiled for 2 purpose :

* If you develop Qt Application, libParamTuner use [QFileSystemWatcher](http://doc.qt.io/qt-4.8/qfilesystemwatcher.html) class
* For other C++ developers, libParamTuner use system-dependent libraries that are
  already installed in their respective OS :
    * Windows ([FindFirstChangeNotification() function](https://msdn.microsoft.com/en-us/library/aa364417%28VS.85%29.aspx))
    * Linux (kernel > 2.6.13, with [Inotify](https://en.wikipedia.org/wiki/Inotify))
	* Mac OS X (version > 10.5, with [FSEvents](https://developer.apple.com/library/content/documentation/Darwin/Conceptual/FSEvents_ProgGuide/Introduction/Introduction.html))

### Compilation

For Qt 4.8+ applications

* In directory `src/cpp`, run `make libParamTuner-Qt.a`
* Library file : `libParamTuner-Qt.a`
* Header file : `libparamtuner.h`

For native Windows, Linux (and Mac OS ? ) C++ applications

* In directory `src/cpp`, run `make libParamTuner.a`
* Library file : `libParamTuner.a`
* Header file : `libparamtuner.h`

### Usage and documentation

Read detailed documentation in the header file `libparamtuner.h`

### Test files

Some 'test_*.cpp' files are available in `cpplib` directory.
You can compile them with `make [C++ filename without extension]` command.
`native` and `qt` tests are respectively for native OS dependant libraries and for
Qt libraries.


## Java 8+ Library

### Compilation and installation

The Java library use Maven. In directory `src/java`, run `mvn clean install`.
This command will create a libParamTuner.jar in the `target` subdirectory.
Also, it will install it into your local maven repository, so you can use
it in an other Maven project.

To use it in a Maven project, add theses lines in your POM's
`<dependencies>` section :

    <dependency>
        <groupId>fr.univ_lille1.pji.libparamtuner</groupId>
        <artifactId>libParamTuner</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    <dependency>

If you have a non-Maven project, just add `libParamTuner.jar` to the
build path of your project.

To get the javadoc, run `mvn javadoc:javadoc` , then go to the subdirectory
`target/site/apidocs`

## ParamTuner GUI

This Java (Swing) interface will allow developers to change value in
real-time without having to edit the settings file manually.

### Compilation

The Java application use Maven. First, you have to compile the Java library
(see instruction above). Then, in directory `src/gui`, run `mvn package`.
The executable Jar is in subdirectory `target`.

### Usage

    java -jar ParamTunerGUI.jar path/to/settings.xml

or

    java -jar ParamTunerGUI.jar

then you can put the path to the XML directly into the GUI.

## TODO list :

* Add graphical examples (Java Swing ? )
* Create unit test files for C++ and Java lib
* Add Mac-OS compatibility to C++ library
* Make the GUI
* Configure CI via Github if possible.
