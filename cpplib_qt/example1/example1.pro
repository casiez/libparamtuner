# Project file for example1

TEMPLATE = app
TARGET = example1
DEPENDPATH += . .. ../rapidxml-1.13
INCLUDEPATH += . .. ../rapidxml-1.13
QMAKE_CXXFLAGS += -std=c++11 -DFILE_SYSTEM_WATCHER_USE_QT

# Input
HEADERS += libparamtuner.h FileSystemWatcher.hpp QtFileSystemWatcher.hpp

SOURCES += example1.cpp libparamtuner.cpp
