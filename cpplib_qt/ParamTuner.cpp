#include <QtCore/QString>
#include <QtCore/QObject>
#include <QtCore/QFileSystemWatcher>
#include <iostream>
#include "ParamTuner.hpp"

using namespace std;

ParamTuner::ParamTuner(const char *path) :
		settingPath(path)
{
	settingWatcher.addPath(settingPath);
	// connect the signal from Qt Watcher to receiveSignal() slot
	connect(&settingWatcher, SIGNAL(fileChanged(QString)),
			this, SLOT(receiveSignal(QString)));
	connect(&settingWatcher, SIGNAL(directoryChanged(QString)),
			this, SLOT(receiveSignal(QString)));
}


int ParamTuner::lptBind(const string &setting, void *ptr)
{
	// TODO
	return 0;
}

void ParamTuner::receiveSignal(const QString &path)
{
	cout << "Le fichier a été modifié : " << path.toStdString() << endl;
	settingWatcher.addPath(settingPath);
}
