CXX = g++
AR = ar cqs
MOC = moc

TARGET_LIB = libParamTuner-Qt.a libParamTuner.a

TARGET_TEST = test_native_linux \
							test_qt_linux \
							test_native_windows \
							test_qt_windows \


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

DOCDIR = doc
FORMAT_DOC = html latex man

TARGET_DOC = $(addprefix ${DOCDIR}/, ${FORMAT_DOC})

doc:
	cd ${DOCDIR} && doxygen doxygen.conf

all: ${TARGET_LIB}

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
	rm -f ${TARGET_LIB} ${TARGET_TEST} *.o moc_* *.exe

realclean: clean
	rm -rf ${TARGET_DOC}	

.PHONY: clean realclean all libqt lib doc

libParamTuner-Qt.a: libparamtuner-Qt.o moc_QtFileSystemWatcher.o
libParamTuner.a: libparamtuner.o

# test files
test_native_linux: libParamTuner.a test_native_linux.o
	${CXX} -I.. ${CPPFLAGS} -o $@ test_native_linux.o -L. -lParamTuner -lpthread
test_qt_linux: libParamTuner-Qt.a test_qt_linux.o
	${CXX} -I.. ${CPPFLAGS} -o $@ test_qt_linux.o -L. -lParamTuner-Qt -lpthread -lQtGui -lQtCore
test_native_windows: libParamTuner.a test_native_windows.o
	${CXX} -I.. ${CPPFLAGS} -o $@ test_native_windows.o -L. -lParamTuner
test_qt_windows: libParamTuner-Qt.a test_qt_windows.o
	${CXX} -I.. ${CPPFLAGS} -o $@ test_qt_windows.o -L. -lParamTuner-Qt -L${QTLIB} -lQtGui4 -lQtCore4
