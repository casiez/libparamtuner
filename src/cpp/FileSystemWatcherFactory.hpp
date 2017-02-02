#ifndef FILE_SYSTEM_WATCHER_FACTORY_HPP
#define FILE_SYSTEM_WATCHER_FACTORY_HPP

#include "FileSystemWatcher.hpp"



#ifdef FILE_SYSTEM_WATCHER_USE_QT
	#include "QtFileSystemWatcher.hpp"
#elif defined __linux__
	#include "linux/InotifyFileSystemWatcher.hpp"
#elif defined __APPLE__

#elif defined _WIN32
	#include "windows/Win32FileSystemWatcher.hpp"
#endif


#include <string>


FileSystemWatcher* createFileSystemWatcher(const std::string &path, voidfunc callback) {
	
	#ifdef FILE_SYSTEM_WATCHER_USE_QT
		return new QtFileSystemWatcher(path, callback);
	#elif defined __linux__
		return new InotifyFileSystemWatcher(path, callback);
	#elif defined __APPLE__
		#error "not yet implemented"
	#elif defined _WIN32
		return new Win32FileSystemWatcher(path, callback);
	#else
		#error "operating system not supported"
	#endif
	
}



#endif








