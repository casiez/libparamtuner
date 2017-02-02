/* -*- mode: c++ -*-
 *
 * code/consoleExample.cpp --
 *
 * Initial software
 * Authors: Gery Casiez
 * Copyright © Univ. Lille
 *
 * This software may be used and distributed according to the terms of
 * the GNU General Public License version 2 or any later version.
 *
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
