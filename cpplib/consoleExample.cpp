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
#include <QtGui/QApplication>
#include "libparamtuner.h"

int main(int argc, char* argv[]) {

	QApplication app(argc, argv);

	double s1 = 2.0;
	int s2 = 1;
	bool b = false;
	std::string s;

	std::cout << lptLoad("settings.xml") << std::endl;
	// lptBind("setting1", &s1);
	// lptBind("setting2", &s2);
	// lptBind("mybool", &b);
	// lptBind("mystring", &s);

	// pour QT, l'appel à app.exec() est nécessaire
	// pour recevoir les évènements de modification du fichier
	/*while (false) {
		usleep(1000*500); // 500 ms
		std::cout << "Setting 1 = " << s1 << " setting 2 = " << s2 << std::endl;
	}*/

	return app.exec();
}
