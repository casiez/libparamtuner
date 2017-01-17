# Project file for example1

TEMPLATE = app
TARGET = example1
DEPENDPATH += . .. ../rapidxml-1.13
INCLUDEPATH += . .. ../rapidxml-1.13
QMAKE_CXXFLAGS += -std=c++11

# Input
HEADERS += libparamtuner.h ParamTuner.hpp
SOURCES += example1.cpp libparamtuner.cpp ParamTuner.cpp
