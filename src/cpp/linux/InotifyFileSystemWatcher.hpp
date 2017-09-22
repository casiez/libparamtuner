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
#ifndef INOTIFY_FILE_SYSTEM_WATCHER_HPP
#define INOTIFY_FILE_SYSTEM_WATCHER_HPP

#include "../FileSystemWatcher.hpp"

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
	bool end = false;

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
		while ((length = read(inotifyFileDesc, buffer, EVENT_BUF_LEN)) > 0 && !end) {
			int i = 0;
			while (i < length) {
				struct inotify_event *event = (struct inotify_event *) &buffer[i];
				FileSystemWatcher::receiveSignal();
				i += EVENT_SIZE + event->len;
			}
		}
		
	}
	
public:
	InotifyFileSystemWatcher(const std::string &path, void (*callback)(void)) :
			FileSystemWatcher(path, callback),
			inotifyFileDesc(inotify_init()),
			inotifyListenerDesc(inotify_add_watch(inotifyFileDesc, path.c_str(), IN_MODIFY)),
			watcherThread(&InotifyFileSystemWatcher::async, this)
	{
	}
	
	virtual ~InotifyFileSystemWatcher() {
		end = true;
		inotify_rm_watch(inotifyFileDesc, inotifyListenerDesc);
		close(inotifyFileDesc);
		watcherThread.join(); // thread should terminate normally when all descriptors are closed
	}

	
	
	
};



#endif
