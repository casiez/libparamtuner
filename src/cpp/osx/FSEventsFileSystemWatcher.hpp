/*
 *  libParamTuner
 *  Copyright (C) 2017 Géry Casiez
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

// Code based on https://developer.apple.com/library/content/documentation/Darwin/Conceptual/FSEvents_ProgGuide/UsingtheFSEventsFramework/UsingtheFSEventsFramework.html#//apple_ref/doc/uid/TP40005289-CH4-SW4

#ifndef FSEVENTS_FILE_SYSTEM_WATCHER_HPP
#define FSEVENTS_FILE_SYSTEM_WATCHER_HPP

#include "../FileSystemWatcher.hpp"

#include <CoreServices/CoreServices.h>
#include <thread>
#include <iostream>

class FSEventsFileSystemWatcher : public FileSystemWatcher {

	FSEventStreamRef stream;
	std::thread watcherThread;
	
protected:
	
	
	static void mycallback(ConstFSEventStreamRef /*streamRef*/, void *clientCallBackInfo, size_t /*numEvents*/,
	    void */*eventPaths*/, const FSEventStreamEventFlags /*eventFlags*/[], const FSEventStreamEventId /*eventIds*/[])
	{
	 	FSEventsFileSystemWatcher *sysWatcher = (FSEventsFileSystemWatcher*)clientCallBackInfo;
	 	sysWatcher->callSystemWatcher();
	}

	void callSystemWatcher() {
		FileSystemWatcher::receiveSignal();
	}

	virtual void update() {

	}

	void async() {
		FSEventStreamScheduleWithRunLoop(stream, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode);
	    FSEventStreamStart(stream);
		CFRunLoopRun();
	}
	
public:
	FSEventsFileSystemWatcher(std::string path, voidfunc callback) :
			FileSystemWatcher(path, callback)
	{
	    CFStringRef mypath = CFStringCreateWithCString(kCFAllocatorDefault, path.c_str(), kCFStringEncodingMacRoman);
	    CFArrayRef pathsToWatch = CFArrayCreate(NULL, (const void **)&mypath, 1, NULL);

	    CFAbsoluteTime latency = 0.1; /* Latency in seconds */
	 
		FSEventStreamContext context;
		context.version = 0;
		context.info = this;
		context.retain = NULL;
		context.release = NULL;
		context.copyDescription = NULL;

	    stream = FSEventStreamCreate(NULL,
	        (FSEventStreamCallback)(&FSEventsFileSystemWatcher::mycallback),
	        &context,
	        pathsToWatch,
	        kFSEventStreamEventIdSinceNow, 
	        latency,
	         kFSEventStreamCreateFlagFileEvents  
	    );

	    watcherThread = std::thread(&FSEventsFileSystemWatcher::async, this);	
	}
	
	virtual ~FSEventsFileSystemWatcher() {
	    FSEventStreamStop(stream);
	    FSEventStreamInvalidate(stream);
	    FSEventStreamRelease(stream);
	}
};



#endif
