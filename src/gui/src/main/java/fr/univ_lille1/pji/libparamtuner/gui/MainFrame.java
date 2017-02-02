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

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import javax.swing.BoxLayout;
import java.awt.Toolkit;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPanel;
	private JTextField textField;
	private JButton btnCharger;
	
	/**
	 * Main test
	 */
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		EventQueue.invokeLater(() -> {
			MainFrame frame = new MainFrame();
			frame.setVisible(true);
		});
	}
	
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("ParamTuner GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 600);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int)(screenSize.getWidth()/2 - getSize().getWidth()/2),
				(int)(screenSize.getHeight()/2 - getSize().getHeight()/2));
		
		JPanel globalPanel = new JPanel();
		globalPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(globalPanel);
		globalPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel topPanel = new JPanel();
		globalPanel.add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Fichier XML :");
		topPanel.add(lblNewLabel, BorderLayout.WEST);
		
		textField = new JTextField();
		topPanel.add(textField, BorderLayout.CENTER);
		textField.addActionListener(e -> loadFile(textField.getText()));
		
		btnCharger = new JButton("Charger");
		topPanel.add(btnCharger, BorderLayout.EAST);
		btnCharger.addActionListener(e -> loadFile(textField.getText()));
		
		JScrollPane contentScroll = new JScrollPane();
		contentScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		globalPanel.add(contentScroll, BorderLayout.CENTER);
		
		contentPanel = new JPanel();
		contentScroll.setViewportView(contentPanel);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		
	}
	
	
	
	public void loadFile(String path) {
		
		
		
		
	}
	
	
	public void setFilePathAndLoad(String path) {
		textField.setText(path);
		loadFile(path);
	}
	
	
	
}
