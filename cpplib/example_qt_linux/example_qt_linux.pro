# Project file for example_qt_linux

TEMPLATE = app
TARGET = example_qt_linux
DEPENDPATH += . .. ../rapidxml-1.13
INCLUDEPATH += . .. ../rapidxml-1.13
QMAKE_CXXFLAGS += -std=c++11 -DFILE_SYSTEM_WATCHER_USE_QT

# Input
HEADERS += libparamtuner.h FileSystemWatcher.hpp QtFileSystemWatcher.hpp

SOURCES += example_qt_linux.cpp libparamtuner.cpp
