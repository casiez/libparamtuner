#ifndef FILE_SYSTEM_WATCHER_HPP
#define FILE_SYSTEM_WATCHER_HPP

#include <string>

typedef void (*voidfunc)(void);



class FileSystemWatcher {
	
private:
	std::string path;
	voidfunc callback;

protected:
	
	virtual void update() = 0;
	
public:
	FileSystemWatcher(const std::string path, voidfunc callback) :
			path(path),
			callback(callback) {
	}
	
	
	virtual ~FileSystemWatcher() {}
	
	
	void receiveSignal() {
		update();
		callback();
	}
	
	std::string getPath() const {
		return path;
	}
	
	
	
	
};



#endif
