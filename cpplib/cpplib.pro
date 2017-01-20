# libParamTuner C++ static library

TEMPLATE = lib
release.DESTDIR = .
DEPENDPATH += . rapidxml-1.13
INCLUDEPATH += . rapidxml-1.13
QMAKE_CXXFLAGS += -std=c++11
CONFIG += staticlib release

# ###############################################
# comment this line to disable Qt library usage :
# ###############################################
CONFIG += lpt_use_qt


# Input
HEADERS += libparamtuner.h FileSystemWatcher.hpp

SOURCES += libparamtuner.cpp

CONFIG(lpt_use_qt) {
	DEFINES += FILE_SYSTEM_WATCHER_USE_QT
	HEADERS += QtFileSystemWatcher.hpp
	TARGET = ParamTuner-Qt
}
else {
	TARGET = ParamTuner
}

