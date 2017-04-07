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
package fr.univ_lille1.libparamtuner.gui;

import java.awt.GraphicsEnvironment;

import javafx.application.Application;

public class Main {
	
	public static void main(String[] args) {
		
		boolean headless = GraphicsEnvironment.isHeadless();
		
		if (headless) {
			System.err.println("Headless mode not supported (need graphical environment).");
			System.exit(1);
		}
		
		Application.launch(MainFrame.class, args);
		
	}
	
}
