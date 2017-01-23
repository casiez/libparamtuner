# Project file for example1

TEMPLATE = app
TARGET = example_qt_windows
DEPENDPATH += . .. ../rapidxml-1.13
INCLUDEPATH += . .. ../rapidxml-1.13
QMAKE_CXXFLAGS += -std=c++11 -DFILE_SYSTEM_WATCHER_USE_QT
CONFIG += release thread console

# Input
HEADERS += libparamtuner.h FileSystemWatcher.hpp QtFileSystemWatcher.hpp

SOURCES += example_qt_windows.cpp libparamtuner.cpp