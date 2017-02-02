/*
 *  libParamTuner
 *  Copyright (C) 2017 Marc Baloup, Veïs Oudjail
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
#ifndef QT_FILE_SYSTEM_WATCHER_HPP
#define QT_FILE_SYSTEM_WATCHER_HPP

#include "FileSystemWatcher.hpp"
#include <QtCore/QString>
#include <QtCore/QObject>
#include <QtCore/QFileSystemWatcher>

#ifdef _WIN32
	#include <windows.h>
#endif


class QtFileSystemWatcher : public QObject, public FileSystemWatcher {
	Q_OBJECT
	
protected:
	QFileSystemWatcher settingWatcher;
	QString settingPath;

	virtual void update() {
		#ifdef __linux__
			/* Parfois, il est nécessaire de remettre le fichier dans le Watcher
			 * pour être notifié de la modification suivante. Dans d'autres cas,
			 * ce n'est pas nécessaire et un message d'erreur peut apparaitre
			 * dans le terminal.
			 */
			settingWatcher.addPath(settingPath);
		#endif
	}
	
public:
	QtFileSystemWatcher(const std::string &path, voidfunc callback) :
			FileSystemWatcher(path, callback),
			settingPath(path.c_str()) {
		settingWatcher.addPath(settingPath);
		// connect the signal from Qt Watcher to receiveSignal() slot
		connect(&settingWatcher, SIGNAL(fileChanged(QString)),
				this, SLOT(receiveSignal(QString)));
	}
	
public slots:
	void receiveSignal(const QString &path) {
		if (path != settingPath)
			return;
		#ifdef _WIN32
			/*  Il semblerai que Windows envoi le signal avant même
				que la dernière version du fichier soit complètement
				écrit sur le disque. De ce cas, le parseur XML lançait
				une exception.
				Le retard de 30ms laisse le temps au fichier d'être
				réellement sauvegardé.
			*/
			Sleep(30);
		#endif
		FileSystemWatcher::receiveSignal();
	}
	
};



#endif
