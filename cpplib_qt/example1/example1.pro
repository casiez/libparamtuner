# Project file for example1

TEMPLATE = app
TARGET = example1
DEPENDPATH += . ..
INCLUDEPATH += . ..
QMAKE_CXXFLAGS += -std=c++11

# Input
HEADERS += libparamtuner.h ParamTuner.hpp
SOURCES += example1.cpp libparamtuner.cpp ParamTuner.cpp
