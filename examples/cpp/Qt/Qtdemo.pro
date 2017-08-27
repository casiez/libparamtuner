#
# Authors: Géry Casiez
#

TEMPLATE  = app

QT += widgets gui

INCLUDEPATH += ../../../src/cpp/
LIBS += -L../../../src/cpp/ -lparamtuner -framework CoreServices

HEADERS += MyCanvas.h
SOURCES += main.cpp MyCanvas.cpp



