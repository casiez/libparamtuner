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
#include <QtGui/QApplication>
#include <windows.h>
#include <thread>
#include "libparamtuner.h"

using namespace std;


// déclaration des variables qui seront modifiés par la librairie
double varDouble = 2.0;
int varInt = 1;
bool varBool = false;
string varString;

/*
 * Boucle d'origine du code exemple.
 * Il sera exécuté dans un thread séparé car Qt se sers de sa
 * boucle d'évènement pour lancer les signaux lorsque le fichier
 * de settings est modifé.
 */
DWORD WINAPI originalMainLoop(void *data) {

	while (true) {
		Sleep(500); // 500 ms
		cout << "setting1 (double) = " << varDouble
			<< " ; setting2 (int) = " << varInt
			<< " ; mybool (bool) = " << varBool
			<< " ; mystring (string) = " << varString
			<< endl;
	}
}


/*
 * la boucle d'évènement de Qt (app.exec()) doit obligatoirement être lancé
 * depuis le thread principal (le main())
 */
int main(int argc, char* argv[]) {

	QApplication app(argc, argv);
	
	lptLoad("settings.xml");
	lptBind("setting1", &varDouble);
	lptBind("setting2", &varInt);
	lptBind("mybool", &varBool);
	lptBind("mystring", &varString);

	CreateThread(nullptr, 0, originalMainLoop, nullptr, 0, nullptr);

	return app.exec();
}
