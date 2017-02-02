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
