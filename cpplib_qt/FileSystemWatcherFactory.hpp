#ifndef FILE_SYSTEM_WATCHER_FACTORY_HPP
#define FILE_SYSTEM_WATCHER_FACTORY_HPP

#include "FileSystemWatcher.hpp"



#ifdef FILE_SYSTEM_WATCHER_USE_QT
	#include "QtFileSystemWatcher.hpp"
#else
	
#endif


#include <string>


FileSystemWatcher* createFileSystemWatcher(const std::string &path, voidfunc callback) {
	
	#ifdef FILE_SYSTEM_WATCHER_USE_QT
		return new QtFileSystemWatcher(path, callback);
	#else
		#error "not yet implemented"
	#endif
	
}



#endif








