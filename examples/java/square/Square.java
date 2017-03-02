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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import fr.univ_lille1.libparamtuner.ParamTuner;

public class Square {
	
	static int x = 10, y = 10; 
	static int width = 200, height = 200;
	static String message = "Hello world";
	
	public static void main(String[] args) throws InterruptedException {
		
		// Load settings file 
		ParamTuner.load("settings.xml");

		ParamTuner.bind("x", Integer.class, v -> x = v);
		ParamTuner.bind("y", Integer.class, v -> y = v);
		ParamTuner.bind("width", Integer.class, v -> width = v);
		ParamTuner.bind("height", Integer.class, v -> height = v);
		ParamTuner.bind("message", String.class, v -> message = v);
		
		// Init frame example
		JFrame frame = new JFrame("Square example");
		frame.setSize(600, 400);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setContentPane(new JPanel() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void paintComponent(Graphics g) {
				g.setColor(Color.RED);
				g.fillRect(x, y, width, height);
				
			    Font font = new Font("Courier", Font.BOLD, 20);
			    g.setFont(font);
			    g.setColor(Color.red);          
			    g.drawString(message, 10, 250);  
			}
			
		});
		frame.setVisible(true);
		
		// Update loop
		for (;;) {
			Thread.sleep(50);
			frame.revalidate();
			frame.repaint();
		}
		
		
	}
	
}

