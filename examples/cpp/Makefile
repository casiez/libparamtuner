CXX = g++

TARGET_TEST = console_native_linux \
							console_qt_linux \
							console_native_windows \
							console_qt_windows \


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

LDINCS = -I../../src/cpp

all: ${TARGET_TEST}

%.o: %.cpp
	${CXX} -c ${CPPFLAGS} ${LDINCS} ${QTINC} -o $@ $<


clean:
	rm -f ${TARGET_LIB} ${TARGET_TEST} *.o moc_* *.exe *.a


.PHONY: clean all

libParamTuner-Qt.a:
	cd ../../src/cpp && make libParamTuner-Qt.a && cp libParamTuner-Qt.a ../../examples/cpp/
libParamTuner.a:
	cd ../../src/cpp && make libParamTuner.a && cp libParamTuner.a ../../examples/cpp/

# test files
console_native_linux: libParamTuner.a console_native_linux.o
	${CXX} ${CPPFLAGS} -o $@ console_native_linux.o -L. -lParamTuner -lpthread
console_qt_linux: libParamTuner-Qt.a console_qt_linux.o
	${CXX} ${CPPFLAGS} -o $@ console_qt_linux.o -L. -lParamTuner-Qt -lpthread -lQtGui -lQtCore
console_native_windows: libParamTuner.a console_native_windows.o
	${CXX} ${CPPFLAGS} -o $@ console_native_windows.o -L. -lParamTuner
console_qt_windows: libParamTuner-Qt.a console_qt_windows.o
	${CXX} ${CPPFLAGS} -o $@ console_qt_windows.o -L. -lParamTuner-Qt -L${QTLIB} -lQtGui4 -lQtCore4
