# Project file for example1

TEMPLATE = app
TARGET = example2
DEPENDPATH += . .. ../rapidxml-1.13
INCLUDEPATH += . .. ../rapidxml-1.13
QMAKE_CXXFLAGS += -std=c++11

# Input
HEADERS += libparamtuner.h FileSystemWatcher.hpp QtFileSystemWatcher.hpp

SOURCES += example2.cpp libparamtuner.cpp
