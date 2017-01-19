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
