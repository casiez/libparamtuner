#ifndef QT_FILE_SYSTEM_WATCHER_HPP
#define QT_FILE_SYSTEM_WATCHER_HPP

#include "FileSystemWatcher.hpp"
#include <QtCore/QString>
#include <QtCore/QObject>
#include <QtCore/QFileSystemWatcher>



class QtFileSystemWatcher : public QObject, public FileSystemWatcher {
	Q_OBJECT
	
protected:
	QFileSystemWatcher settingWatcher;
	QString settingPath;

	virtual void update() {
		/* Parfois, il est nécessaire de remettre le fichier dans le Watcher
		 * pour être notifié de la modification suivante. Dans d'autres cas,
		 * ce n'est pas nécessaire et un message d'erreur peut apparaitre
		 * dans le terminal.
		 */
		settingWatcher.addPath(settingPath);
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
		FileSystemWatcher::receiveSignal();
	}
	
	
	
	
};



#endif
