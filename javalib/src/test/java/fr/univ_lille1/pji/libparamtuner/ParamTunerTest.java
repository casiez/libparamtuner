package fr.univ_lille1.pji.libparamtuner;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.Before;
import org.junit.Test;

public class ParamTunerTest {
	
	
	public static final String watchedPath = "target/test_settings.xml";
	public static final String originalPath = "src/test/resources/settings_o.xml";
	public static final String newPath = "src/test/resources/settings_n.xml";
	
	
	
	
	double setting1;
	int setting2;
	boolean mybool;
	String mystring;
	
	
	
	
	@Before
	public void setUp() throws Exception {
		setting1 = 0;
		setting2 = 0;
		mybool = false;
		mystring = "";
		
		Files.copy(Paths.get(originalPath), Paths.get(watchedPath), StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Test
	public void testMain() throws Exception {
		
		// utilisation de ParamTuner :
		ParamTuner.load(watchedPath);
		ParamTuner.bindDouble("setting1", v -> setting1 = v);
		ParamTuner.bindInt("setting2", v -> setting2 = v);
		ParamTuner.bindBoolean("mybool", v -> mybool = v);
		ParamTuner.bindString("mystring", v -> mystring = v);
		
		
		// thread qui va provoquer automatiquement un changement dans le fichier settings.xml
		Thread t = new Thread(() -> {
			try {
				for(int i = 0; ; i++) {
					Thread.sleep(1000);
					if (i%2 == 0)
						Files.copy(Paths.get(newPath), Paths.get(watchedPath), StandardCopyOption.REPLACE_EXISTING);
					else
						Files.copy(Paths.get(originalPath), Paths.get(watchedPath), StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (InterruptedException e) {
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		t.start();
		
		
		// boucle du programme principal
		for(int i = 0; i < 25; i++) {
			Thread.sleep(200);
			System.out.println("setting1 (double) = "+setting1
					+" ; setting2 (int) = "+setting2
					+" ; mybool (bool) = "+mybool
					+" ; mystring (string) = "+mystring);
		}
		
		t.interrupt();
	}
	
}
