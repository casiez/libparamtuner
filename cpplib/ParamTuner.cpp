#include <QtCore/QString>
#include <QtCore/QObject>
#include <QtCore/QFileSystemWatcher>
#include <iostream>
#include "ParamTuner.hpp"

using namespace std;

ParamTuner::ParamTuner(const char *path) :
		settingWatcher(),
		settingPath(path)
{
	settingWatcher.addPath(settingPath);

		QObject::connect(&settingWatcher, SIGNAL(fileChanged(QString)),
				this, SLOT(receiveSignal(QString)));
		QObject::connect(&settingWatcher, SIGNAL(directoryChanged(QString)),
				this, SLOT(receiveSignal(QString)));
}


int ParamTuner::lptBind(const string &setting, void *ptr)
{
	return 0;
}

void ParamTuner::receiveSignal(const QString &path)
{
	cout << "Le fichier a été modifié : " << path.toStdString() << endl;
}
