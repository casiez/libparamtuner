#ifndef WIN32_FILE_SYSTEM_WATCHER_HPP
#define WIN32_FILE_SYSTEM_WATCHER_HPP

#include "FileSystemWatcher.hpp"

#include <windows.h>
#include <tchar.h>
#include <iostream>
#include <algorithm>
#include <stdio.h>

class Win32FileSystemWatcher : public FileSystemWatcher {
	
protected:
	
	std::wstring absoluteDirPath;
	std::wstring absolutePath;
	HANDLE threadHandle;
	HANDLE notificationHandle;
	HANDLE fileHandle;
	FILETIME lastModifTime;

	virtual void update() {
		if (!FindNextChangeNotification(notificationHandle)) {
			std::cerr << "Error: FindNextChangeNotification function failed." << std::endl;
		}
	}
	
	
	static DWORD WINAPI async(void *instance) {
		Win32FileSystemWatcher& _this = *((Win32FileSystemWatcher*) instance);
		
		while(true) {
			
			DWORD dwWaitStatus = WaitForMultipleObjects(1, &(_this.notificationHandle), false, INFINITE);
			
			switch (dwWaitStatus) {
				case WAIT_OBJECT_0:
					FILETIME newTime;
					GetFileTime(_this.fileHandle, nullptr, nullptr, &newTime);
					if (CompareFileTime(&newTime, &(_this.lastModifTime)) != 0) {
						_this.lastModifTime = newTime;
						
						/*  Il semblerai que Windows envoi le signal avant même
							que la dernière version du fichier soit complètement
							écrit sur le disque. De ce cas, le parseur XML lançait
							une exception.
							Le retard de 30ms laisse le temps au fichier d'être
							réellement sauvegardé.
						*/
						Sleep(30);
						_this.receiveSignal();
					}
					break;
				case WAIT_TIMEOUT:
					// Ne devrait pas survenir car le temps d'attente est INFINITE.
					std::cerr << "No changes in the timeout period." << std::endl;
					break;
				default:
					std::cerr << "Error: Unhandled wait status." << std::endl;
					break;
			}
			
			
		}
		
	}
	
public:
	Win32FileSystemWatcher(std::string path, voidfunc callback) :
			FileSystemWatcher(path, callback)
	{
		// lowercase path
		std::transform(path.begin(), path.end(), path.begin(), ::tolower);
		// convert to wstring
		absolutePath = std::wstring(path.begin(), path.end());
		// getting absolute path
		wchar_t finalPath[4096];
		GetFullPathName(absolutePath.c_str(), 4096, finalPath, nullptr);
		absolutePath = std::wstring(finalPath);
		
		// storing last modification time
		fileHandle = CreateFile(absolutePath.c_str(),
				GENERIC_READ,
				FILE_SHARE_READ | FILE_SHARE_DELETE | FILE_SHARE_WRITE,
				nullptr,
				OPEN_EXISTING,
				FILE_ATTRIBUTE_NORMAL,
				nullptr);
		GetFileTime(fileHandle, nullptr, nullptr, &lastModifTime);
		
		
		
		// getting parent directory
		absoluteDirPath = absolutePath.substr(0, absolutePath.find_last_of(L"/\\") + 1);
		
		notificationHandle = FindFirstChangeNotification(absoluteDirPath.c_str(), false, FILE_NOTIFY_CHANGE_ATTRIBUTES
                              | FILE_NOTIFY_CHANGE_SIZE
                              | FILE_NOTIFY_CHANGE_LAST_WRITE);
		
		if (notificationHandle == INVALID_HANDLE_VALUE || notificationHandle == nullptr) {
			std::cerr << "Error: FindFirstChangeNotification function failed. Error return status: " << GetLastError() << std::endl;
			return;
		}
		
		threadHandle = CreateThread(nullptr, 0, async, (void*) this, 0, nullptr);
		
	}
	
	virtual ~Win32FileSystemWatcher() {
		CloseHandle(threadHandle);
		CloseHandle(fileHandle);
		CloseHandle(notificationHandle);
	}
	
};



#endif
