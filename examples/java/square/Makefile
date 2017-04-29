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

ifeq ($(OS),Windows_NT)
    CP_SEP = ;
else
    CP_SEP = :
endif


all: exec 

%.class: %.java
	javac -cp libParamTuner.jar $<

clean:
	rm -f $(TARGETS) *.class *.jar


.PHONY: clean all exec

libParamTuner.jar:
	cd ../../../src/java && mvn install && cp target/libParamTuner.jar ../../examples/java/square

	
exec: libParamTuner.jar Square.class
	java -cp "libParamTuner.jar$(CP_SEP)." Square