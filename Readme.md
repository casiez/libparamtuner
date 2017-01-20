# libParamTuner

Library allowing developers to set up internal software settings
in real-time. It avoid repetitively compile a whole software every time
we need to change a constant value inside the source code.

## C++ Library

The C++ libParamTuner library can be compiled for 2 purpose :

* If you develop Qt Application, libParamTuner can depend on Qt too ([QFileSystemWatcher](http://doc.qt.io/qt-4.8/qfilesystemwatcher.html))
* For other C++ developers, libParamTuner can use system-dependent libraries that are
  already installed in their respective OS :
    * Windows ([FindFirstChangeNotification() function](https://msdn.microsoft.com/en-us/library/aa364417%28VS.85%29.aspx))
    * Linux (kernel > 2.6.13, with [Inotify](https://en.wikipedia.org/wiki/Inotify))
	* Mac OS X (version > 10.5, with [FSEvents](https://developer.apple.com/library/content/documentation/Darwin/Conceptual/FSEvents_ProgGuide/Introduction/Introduction.html))

Read `cpplib/Readme.md` for more informations about compilation.

## ParamTuner GUI

(Soon)

This Java (Swing) interface will allow developers to change value in
real-time without having to edit the settings file manually.

## Java Library

(Later, if we have time)
