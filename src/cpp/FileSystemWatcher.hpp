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
	FileSystemWatcher(const std::string &path, voidfunc callback) :
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
