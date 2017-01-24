





CXX = g++
AR = ar cqs
MOC = moc

TARGET_LIB = libParamTuner-Qt.a libParamTuner.a

TARGET_TEST = test_nativ_linux \
							test_qt_linux \
							test_nativ_windows \
							test_qt_windows \


CPPFLAGS = -std=c++11

QTINCROOT = /usr/include/qt4
QTINCSUB = QtCore QtGui .
QTINC = $(addprefix -I$(QTINCROOT)/, $(QTINCSUB))




all: ${TARGET_LIB}





%.o: %.cpp
	${CXX} -c ${CPPFLAGS} ${LDINCS} $<

moc_%.o: moc_%.cpp
	${CXX} -c ${CPPFLAGS} ${LDINCS} ${QTINC} $<

moc_%.cpp: %.hpp
	${MOC} $(INCPATH) $< -o $@


${TARGET_LIB}:
	${AR} $@ $^

clean:
	rm -f ${TARGET_LIB} ${TARGET_TEST} *.o moc_*
.PHONY: clean all


libParamTuner-Qt.a: libparamtuner.o moc_QtFileSystemWatcher.o
libParamTuner.a: libparamtuner.o

# test files
test_nativ_linux: libParamTuner.a test_nativ_linux.o
	${CXX} -I.. ${CPPFLAGS} -o $@ test_nativ_linux.o -L. -lParamTuner -lpthread
test_qt_linux: libParamTuner-Qt.a test_qt_linux.o
	${CXX} -I.. ${CPPFLAGS} -o $@ test_qt_linux.o -L. -lParamTuner-Qt -lpthread -lQtGui
test_nativ_windows: libParamTuner.a test_nativ_windows.o
	${CXX} -I.. ${CPPFLAGS} -o $@ test_nativ_windows.o -L. -lParamTuner
test_qt_windows: libParamTuner-Qt.a test_qt_windows.o
	${CXX} -I.. ${CPPFLAGS} -o $@ test_nativ_windows.o -L. -lParamTuner -lQtGui4
