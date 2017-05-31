package fr.univ_lille1.libparamtuner.gui;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicUtils {
	
	public static void waitForValue(AtomicBoolean a, boolean wantedValue, long interval) throws InterruptedException {
		while(a.get() != wantedValue) {
			Thread.sleep(interval);
		}
	}
	
}
