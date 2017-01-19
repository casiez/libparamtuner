# Project file for example_nativ_linux

TEMPLATE = app
TARGET = example_nativ_linux
DEPENDPATH += . .. ../rapidxml-1.13
INCLUDEPATH += . .. ../rapidxml-1.13
QMAKE_CXXFLAGS += -std=c++11

# Input
HEADERS += libparamtuner.h FileSystemWatcher.hpp QtFileSystemWatcher.hpp

SOURCES += example_nativ_linux.cpp libparamtuner.cpp
