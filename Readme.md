# libParamTuner

Cross-platform library to ease the interactive tuning of parameters at run time and without the need to recompile code.

## Minimal example
```cpp
#include <iostream>
#include <string>
#include "paramtuner.h"

#ifdef _WIN32
#   include <windows.h>
#   define SLEEP(ms) (Sleep(ms))
#else
#   include <unistd.h>
#   define SLEEP(ms) (usleep(ms * 1000))
#endif

using namespace std;

int main() {
	double varDouble = 2.0;
	int varInt = 1;
	bool varBool = false;
	string varString;

	ParamTuner::load("settings.xml");
	// Use the line below if you want to explicitly update
	// all parameters using update() function 
	// ParamTuner::load("settings.xml", true);
	ParamTuner::bind("setting1", &varDouble);
	ParamTuner::bind("setting2", &varInt);
	ParamTuner::bind("mybool", &varBool);
	ParamTuner::bind("mystring", &varString);

	while (true) {
		SLEEP(500); // 500 ms
		// Uncomment the line below if you use ParamTuner::load("settings.xml", true);
		// ParamTuner::update();
		cout << "setting1 (double) = " << varDouble
			<< " ; setting2 (int) = " << varInt
			<< " ; mybool (bool) = " << varBool
			<< " ; mystring (string) = " << varString
			<< endl;
	}
}
```

The associated settings.xml file is as follows:

```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ParamList>
	<mybool type="bool" value="true"/>
	<mystring type="string" value="hello libParamTuner"/>
	<setting1 type="double" min="0.0" max="100.0" value="0.3"/>
	<setting2 type="int" min="0" max="50" value="5"/>
</ParamList>
```

## C++ Library

The C++ libParamTuner library use system-dependent libraries
that are already installed in their respective OS :
* Windows ([FindFirstChangeNotification() function](https://msdn.microsoft.com/en-us/library/aa364417%28VS.85%29.aspx))
* Linux (kernel > 2.6.13, with [Inotify](https://en.wikipedia.org/wiki/Inotify))
* Mac OS X (version > 10.5, with [FSEvents](https://developer.apple.com/library/content/documentation/Darwin/Conceptual/FSEvents_ProgGuide/Introduction/Introduction.html))

### Compilation

* In directory `src/cpp`, run `make`
* Library file : `libparamtuner.a`
* Header file : `paramtuner.h`

### Usage and documentation

Read detailed documentation in the header file `paramtuner.h`

### Example files

Some examples files are available in `examples/cpp` directory.
You can compile them with `make [C++ filename without extension]` command.



## Java 8+ Library

### Compilation and installation

The Java library use Maven. In directory `src/java`, run `mvn clean install`.
This command will create a libParamTuner.jar in the `target` subdirectory.
Also, it will install it into your local maven repository, so you can use
it in an other Maven project.

To use it in a Maven project, add theses lines in your POM's
`<dependencies>` section :
```
<dependency>
	<groupId>fr.univ_lille1.libparamtuner</groupId>
	<artifactId>libParamTuner</artifactId>
	<version>0.0.1-SNAPSHOT</version>
<dependency>
```

If you have a non-Maven project, just add `libParamTuner.jar` to the
build path of your project.

To get the javadoc, run `mvn javadoc:javadoc` , then go to the subdirectory
`target/site/apidocs`



## ParamTuner GUI

This Java (Swing) interface allow developers to change value in
real-time without having to edit the settings file manually.

### Available binaries
See the [releases](https://github.com/casiez/libparamtuner/releases) section to directly download a binary version.

### Compilation

The Java application use Maven. First, you have to compile the Java library
(see instruction above). Then, in directory `src/gui`, run `mvn package`.
The executable Jar is in subdirectory `target`.

If you want to create an installer with an executable, run `mvn jfx:native`. Note that icns icons for macOS were created using the command `makeicns -in ParamTunerGUI.png -out ParamTunerGUI.icns`

### Usage

    java -jar ParamTunerGUI.jar path/to/settings.xml

or

    java -jar ParamTunerGUI.jar

then you can put the path to the XML directly into the GUI.

## More information

libParamTuner has been presented as a demo during the [IHM'17 conference](http://ihm17.afihm.org/).
More information (in French) available on [HAL](https://hal.archives-ouvertes.fr/hal-01577686).
