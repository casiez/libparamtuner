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


all: $(TARGET_LIB)

doc:
	mkdir -p $(DOCDIR) && doxygen doxygen.conf

# ligne de compilation spécifique pour la version Qt car on passe
# un define en option : -DFILE_SYSTEM_WATCHER_USE_QT
libparamtuner-Qt.o: libparamtuner.cpp
	$(CXX) -DFILE_SYSTEM_WATCHER_USE_QT $(QTINC) -c $(CPPFLAGS) $(LDINCS) -o $@ $<

%.o: %.cpp
	$(CXX) -c $(CPPFLAGS) $(LDINCS) $(QTINC) -o $@ $<

moc_%.cpp: %.hpp
	$(MOC) $(INCPATH) $< -o $@


$(TARGET_LIB):
	$(AR) $@ $^

clean:
	rm -rf $(TARGET_LIB) *.o moc_* *.exe $(DOCDIR)

.PHONY: clean all doc

libParamTuner-Qt.a: libparamtuner-Qt.o moc_QtFileSystemWatcher.o
libParamTuner.a: libparamtuner.o
