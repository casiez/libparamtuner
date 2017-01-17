# Project file for example1

TEMPLATE = lib
TARGET = ParamTuner-Qt
DEPENDPATH += . rapidxml-1.13
INCLUDEPATH += . rapidxml-1.13
QMAKE_CXXFLAGS += -std=c++11

# Input
HEADERS += libparamtuner.h ParamTuner.hpp rapidxml.hpp rapidxml_iterators.hpp rapidxml_print.hpp rapidxml_utils.hpp
SOURCES += libparamtuner.cpp ParamTuner.cpp
