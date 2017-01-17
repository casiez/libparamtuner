# libParamTuner

C++ library which allows developers to set up internal software settings
in real-time. It avoid repetitively compile a whole software every time
we need to change a constant value inside the source code (for testing
purpose).

## Library for Qt 4.8+ applications

This first version of the library is prefered for Qt application, because
it use Qt library to works.

Read `cpplib_qt/Readme.md` for more informations.

## Library for other C++ applications

(Soon)

This version will use plateform-dependant libraries to watch file
modifications

## ParamTuner GUI

(Soon)

This Java (Swing) interface will allow developers to change value in
real-time without having to edit the settings file manually.

## Library for Java applications

(Later, if we have time)




## Tools that we used

### File system monitoring librairies

#### Cross plateform
* [QFileSystemWatcher](http://doc.qt.io/qt-4.8/qfilesystemwatcher.html) 

#### Linux
* [libfam](https://en.wikipedia.org/wiki/File_Alteration_Monitor)
* [inotify](https://en.wikipedia.org/wiki/Inotify)

#### OS X
* [DSEvents](https://developer.apple.com/library/mac/documentation/Darwin/Conceptual/FSEvents_ProgGuide/Introduction/Introduction.html#//apple_ref/doc/uid/TP40005289-CH1-DontLinkElementID_15)

#### Windows
* [FindFirstChangeNotification](https://msdn.microsoft.com/en-us/library/aa364417%28VS.85%29.aspx)

