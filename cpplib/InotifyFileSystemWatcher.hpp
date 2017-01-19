#ifndef INOTIFY_FILE_SYSTEM_WATCHER_HPP
#define INOTIFY_FILE_SYSTEM_WATCHER_HPP

#include "FileSystemWatcher.hpp"

#include <sys/inotify.h>
#include <thread>
#include <stdio.h>
#include <unistd.h>
#include <iostream>

#define EVENT_SIZE (sizeof(struct inotify_event))
#define EVENT_BUF_LEN (1024*(EVENT_SIZE+16))


class InotifyFileSystemWatcher : public FileSystemWatcher {
	
protected:

	int inotifyFileDesc;
	int inotifyListenerDesc;
	std::thread watcherThread;

	virtual void update() {
		inotify_rm_watch(inotifyFileDesc, inotifyListenerDesc);
		inotifyListenerDesc = inotify_add_watch(inotifyFileDesc, FileSystemWatcher::getPath().c_str(), IN_MODIFY);
	}
	
	
	void async() {
		if (inotifyFileDesc < 0) {
			perror("inotify_init");
			return;
		}
		int length;
		char buffer[EVENT_BUF_LEN];
		while ((length = read(inotifyFileDesc, buffer, EVENT_BUF_LEN))) {
			int i = 0;
			while (i < length) {
				struct inotify_event *event = (struct inotify_event *) &buffer[i];
				FileSystemWatcher::receiveSignal();
				i += EVENT_SIZE + event->len;
			}
		}
		
	}
	
public:
	InotifyFileSystemWatcher(const std::string &path, voidfunc callback) :
			FileSystemWatcher(path, callback),
			inotifyFileDesc(inotify_init()),
			inotifyListenerDesc(inotify_add_watch(inotifyFileDesc, path.c_str(), IN_MODIFY)),
			watcherThread(&InotifyFileSystemWatcher::async, this)
	{
	}
	
	virtual ~InotifyFileSystemWatcher() {
		inotify_rm_watch(inotifyFileDesc, inotifyListenerDesc);
		close(inotifyFileDesc);
	}

	//void receiveSignal(const QString &path) {
	//	if (path != settingPath)
	//		return;
	//	FileSystemWatcher::receiveSignal();
	//}
	
	
	
	
};



#endif
