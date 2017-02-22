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

TARGET_LIB = libParamTuner.a

CPPFLAGS = -std=c++11 -Wall -Wextra -Wno-unused-parameter

ifeq ($(OS),Windows_NT)
    CPPFLAGS += -DUNICODE
endif


DOCDIR = ../../doc/cpp


all: $(TARGET_LIB)

doc:
	mkdir -p $(DOCDIR) && doxygen doxygen.conf

%.o: %.cpp
	$(CXX) -c $(CPPFLAGS) $(LDINCS) -o $@ $<



$(TARGET_LIB):
	$(AR) $@ $^

clean:
	rm -rf $(TARGET_LIB) *.o *.exe $(DOCDIR)

.PHONY: clean all doc

libParamTuner.a: libparamtuner.o
