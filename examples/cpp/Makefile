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

TARGETS = console_linux console_windows \


CPPFLAGS = -std=c++11 -Wall -Wextra -Wno-unused-parameter

ifeq ($(OS),Windows_NT)
    CPPFLAGS += -DUNICODE
endif

LDINCS = -I../../src/cpp

all: $(TARGETS)

%.o: %.cpp
	$(CXX) -c $(CPPFLAGS) $(LDINCS) -o $@ $<


clean:
	rm -f $(TARGETS) *.o *.exe *.a


.PHONY: clean all

libParamTuner.a:
	cd ../../src/cpp && make libParamTuner.a && cp libParamTuner.a ../../examples/cpp/

# targets
console_linux: libParamTuner.a console_linux.o
	$(CXX) $(CPPFLAGS) -o $@ console_linux.o -L. -lParamTuner -lpthread
console_windows: libParamTuner.a console_windows.o
	$(CXX) $(CPPFLAGS) -o $@ console_windows.o -L. -lParamTuner
