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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.Before;
import org.junit.Test;

public class ParamTunerTest {
	
	
	public static final String watchedPath = "target/test_settings.xml";
	public static final String originalPath = "src/test/resources/settings_o.xml";
	public static final String newPath = "src/test/resources/settings_n.xml";
	
	
	
	Throwable threadThrowable;
	
	
	
	double setting1;
	int setting2;
	boolean mybool;
	String mystring;
	
	
	
	
	@Before
	public void setUp() throws Exception {
		threadThrowable = null;
		
		setting1 = 0;
		setting2 = 0;
		mybool = false;
		mystring = "";
		
		Files.copy(Paths.get(originalPath), Paths.get(watchedPath), StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Test
	public void testMain() throws Throwable {
		// ParamTuner usage:
		ParamTuner.load(watchedPath);
		ParamTuner.bind("setting1", Double.class, v -> setting1 = v);
		ParamTuner.bind("setting2", Integer.class, v -> setting2 = v);
		ParamTuner.bind("mybool", Boolean.class, v -> mybool = v);
		ParamTuner.bind("mystring", String.class, v -> mystring = v);
		
		
		// thread that simulate external software which modify the file settings.xml
		Thread t = new Thread(() -> {
			try {
				for (int i = 0;; i++) {
					Thread.sleep(1000);
					if (i % 2 == 0)
						Files.copy(Paths.get(newPath), Paths.get(watchedPath), StandardCopyOption.REPLACE_EXISTING);
					else
						Files.copy(Paths.get(originalPath), Paths.get(watchedPath),
								StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (InterruptedException e) {
				return;
			} catch (Throwable e) {
				threadThrowable = e;
			}
		});
		t.start();
		
		
		// main loop
		for (int i = 0; i < 25; i++) {
			Thread.sleep(200);
			System.out.println("setting1 (double) = " + setting1
					+ " ; setting2 (int) = " + setting2
					+ " ; mybool (bool) = " + mybool
					+ " ; mystring (string) = " + mystring);
		}
		
		t.interrupt();
		t.join();
		if (threadThrowable != null)
			throw threadThrowable;
	}
	
}
