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

TARGETS = glut_without_lib \


CPPFLAGS = -std=c++11 -Wall -Wextra -Wno-unused-parameter

ifeq ($(OS),Windows_NT)
    CPPFLAGS += -DUNICODE
endif

ifeq ($(shell uname),Darwin)
	CPPFLAGS += -framework CoreServices
endif

LDINCS = -I../../../src/cpp

all: $(TARGETS)

%.o: %.cpp
	$(CXX) -c $(CPPFLAGS) $(LDINCS) -o $@ $<


clean:
	rm -f $(TARGETS) *.o *.exe *.a


.PHONY: clean all

libparamtuner.a:
	cd ../../../src/cpp && make libparamtuner.a
	cp ../../../src/cpp/libparamtuner.a ./

# targets
glut_with_lib: libparamtuner.a glut_with_lib.o
	$(CXX) $(CPPFLAGS) -o $@ glut_with_lib.o -lfreeglut -lopengl32 -lpthread -L. -lparamtuner

glut_without_lib: glut_without_lib.o
	$(CXX) $(CPPFLAGS) -o $@ glut_without_lib.o -L. -lfreeglut -lopengl32

