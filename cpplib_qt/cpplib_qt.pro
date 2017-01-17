# Project file for example1

TEMPLATE = lib
TARGET = ParamTuner-Qt
DEPENDPATH += . rapidxml-1.13
INCLUDEPATH += . rapidxml-1.13
QMAKE_CXXFLAGS += -std=c++11

# Input
HEADERS += libparamtuner.h FileSystemWatcher.hpp QtFileSystemWatcher.hpp

SOURCES += libparamtuner.cpp
