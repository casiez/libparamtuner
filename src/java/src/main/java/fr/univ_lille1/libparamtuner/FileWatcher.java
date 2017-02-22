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
package fr.univ_lille1.libparamtuner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;

public class FileWatcher extends Thread {
	public final File confFile;
	private final Path parentPath;
	
	private final WatchService watcher;
	private final WatchKey key;
	
	private final Consumer<FileWatcher> callback;
	
	/* package */ FileWatcher(File configFile, Consumer<FileWatcher> cb) throws IOException {
		super("libParamTuner Thread");
		callback = cb;
		confFile = configFile.getAbsoluteFile();
		if (!confFile.isFile())
			throw new IllegalArgumentException("configFile is not a regular file");
		if (confFile.getParentFile() == null)
			throw new IllegalArgumentException("configFile hasn't got a parent directory");
		
		parentPath = confFile.getParentFile().toPath();
		
		// initialize a new watcher service
		watcher = FileSystems.getDefault().newWatchService();
		
		key = parentPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
	}
	
	@Override
	public void run() {
		try {
			/*
			 * Based on https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java
			 */
			for (;;) {
				
				// wait for key to be signaled
				WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException e) {
					ParamTuner.printError("thread interrupted");
					watcher.close();
					return;
				}
				
				
				if (key == null || !key.equals(this.key))
					continue;
				
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					
					// TBD - provide example of how OVERFLOW event is handled
					if (kind == StandardWatchEventKinds.OVERFLOW)
						continue;
					
					@SuppressWarnings("unchecked")
					Path child = parentPath.resolve(((WatchEvent<Path>) event).context());
					if (!child.toFile().equals(confFile))
						continue;
					
					callback.accept(this);
					
				}
				
				// reset key and remove from set if directory no longer accessible
				boolean valid = key.reset();
				if (!valid) {
					ParamTuner.printError("WatchKey no longer valid");
					watcher.close();
					return;
				}
			}
		} catch (Exception e) {
			ParamTuner.printError("Exception in watcher thread:");
			e.printStackTrace();
		}
	}
}
