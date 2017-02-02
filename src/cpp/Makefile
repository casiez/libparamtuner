CXX = g++
AR = ar cqs
MOC = moc

TARGET_LIB = libParamTuner-Qt.a libParamTuner.a

CPPFLAGS = -std=c++11 -Wall -Wextra -Wno-unused-parameter

ifeq ($(OS),Windows_NT)
    QTINCROOT = c:/Qt/4.8.6/include
    QTLIB = c:/Qt/4.8.6/lib
    CPPFLAGS += -DUNICODE
else
    QTINCROOT = /usr/include/qt4
endif


QTINCSUB = QtCore QtGui .
QTINC = $(addprefix -I$(QTINCROOT)/, $(QTINCSUB))

DOCDIR = ../../doc/cpp


all: ${TARGET_LIB}

doc:
	mkdir -p $(DOCDIR) && doxygen doxygen.conf

# ligne de compilation spécifique pour la version Qt car on passe
# un define en option : -DFILE_SYSTEM_WATCHER_USE_QT
libparamtuner-Qt.o: libparamtuner.cpp
	${CXX} -DFILE_SYSTEM_WATCHER_USE_QT ${QTINC} -c ${CPPFLAGS} ${LDINCS} -o $@ $<

%.o: %.cpp
	${CXX} -c ${CPPFLAGS} ${LDINCS} ${QTINC} -o $@ $<

moc_%.cpp: %.hpp
	${MOC} $(INCPATH) $< -o $@


${TARGET_LIB}:
	${AR} $@ $^

clean:
	rm -rf ${TARGET_LIB} *.o moc_* *.exe ${DOCDIR}

.PHONY: clean all doc

libParamTuner-Qt.a: libparamtuner-Qt.o moc_QtFileSystemWatcher.o
libParamTuner.a: libparamtuner.o
