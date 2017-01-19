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
#include <windows.h>
#include "libparamtuner.h"

using namespace std;

int main() {
	double s1 = 2.0;
	int s2 = 1;
	bool b = false;
	string s;

	lptLoad("settings.xml");
	lptBind("setting1", &s1);
	lptBind("setting2", &s2);
	lptBind("mybool", &b);
	lptBind("mystring", &s);

	while (true) {
		Sleep(500); // 500 ms
		cout << "setting1 (double) = " << s1
			<< " ; setting2 (int) = " << s2
			<< " ; mybool (bool) = " << b
			<< " ; mystring (string) = " << s
			<< endl;
	}

}
