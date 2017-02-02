package fr.univ_lille1.pji.libparamtuner.gui;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;

import javax.swing.UIManager;

public class Main {
	
	public static void main(String[] args) {

		boolean headless = GraphicsEnvironment.isHeadless();
		
		if (headless) {
			System.err.println("Headless mode not supported (need graphical environment).");
			System.exit(1);
		}

		try {
			// GUI will have the OS theme or desktop environment's theme
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		MainFrame frame = new MainFrame();
		EventQueue.invokeLater(() -> {
			frame.setVisible(true);
		});
		if (args.length > 0) {
			EventQueue.invokeLater(() -> {
				frame.setFilePathAndLoad(args[0]);
			});
		}
		
	}
	
}
