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
void originalMainLoop() {

	while (true) {
		usleep(1000*500); // 500 ms
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
	
	ParamTuner::load("settings.xml");
	ParamTuner::bind("setting1", &varDouble);
	ParamTuner::bind("setting2", &varInt);
	ParamTuner::bind("mybool", &varBool);
	ParamTuner::bind("mystring", &varString);

	thread loopThread(originalMainLoop);

	return app.exec();
}
