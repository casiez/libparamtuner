/* -*- mode: c++ -*-
 *
 *  libParamTuner
 *  Copyright (C) 2017 Gery Casiez, Marc Baloup, Veïs Oudjail
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <iostream>
#include <string>
#include <unistd.h>
#include "libparamtuner.h"

using namespace std;

int main() {
	double varDouble = 2.0;
	int varInt = 1;
	bool varBool = false;
	string varString;

	ParamTuner::load("settings.xml");
	ParamTuner::bind("setting1", &varDouble);
	ParamTuner::bind("setting2", &varInt);
	ParamTuner::bind("mybool", &varBool);
	ParamTuner::bind("mystring", &varString);

	while (true) {
		usleep(1000*500); // 500 ms
		cout << "setting1 (double) = " << varDouble
			<< " ; setting2 (int) = " << varInt
			<< " ; mybool (bool) = " << varBool
			<< " ; mystring (string) = " << varString
			<< endl;
	}

}
