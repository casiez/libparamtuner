#
#  libParamTuner
#  Copyright (C) 2017 Marc Baloup, Veïs Oudjail
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
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
