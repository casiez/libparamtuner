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

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import fr.univ_lille1.libparamtuner.gui.parameters_panel.ParameterPanel;
import fr.univ_lille1.libparamtuner.parameters.Parameter;
import fr.univ_lille1.libparamtuner.parameters.ParameterFile;

import javax.swing.BoxLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPanel;
	private JTextField textField;
	
	
	private ParameterFile loadedFile = null;
	private boolean saved = true;
	private boolean autosave = false;
	private JButton btnSave;
	
	
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
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) { }
			@Override
			public void windowIconified(WindowEvent e) { }
			@Override
			public void windowDeiconified(WindowEvent e) { }
			
			@Override
			public void windowDeactivated(WindowEvent e) { }
			
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO demander confirmation
			}
			
			@Override
			public void windowClosed(WindowEvent e) { }
			
			@Override
			public void windowActivated(WindowEvent e) { }
		});
		setSize(500, 600);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int)(screenSize.getWidth()/2 - getSize().getWidth()/2),
				(int)(screenSize.getHeight()/2 - getSize().getHeight()/2));
		
		JPanel globalPanel = new JPanel();
		globalPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
		setContentPane(globalPanel);
		globalPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel topPanel = new JPanel();
		topPanel.setBorder(new EmptyBorder(2, 2, 3, 2));
		globalPanel.add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		
		JLabel lblNewLabel = new JLabel("XML File :");
		topPanel.add(lblNewLabel);
		
		textField = new JTextField();
		topPanel.add(textField);
		textField.addActionListener(e -> loadFile(textField.getText()));
		
		JButton btnCharger = new JButton("Load");
		topPanel.add(btnCharger);
		btnCharger.addActionListener(e -> loadFile(textField.getText()));

		btnSave = new JButton("Save");
		btnSave.addActionListener(e -> saveFile());
		btnSave.setEnabled(false);
		topPanel.add(btnSave);
		
		JCheckBox chckbxAutosave = new JCheckBox("Autosave", autosave);
		chckbxAutosave.addActionListener(e -> setAutosave(chckbxAutosave.isSelected()));
		topPanel.add(chckbxAutosave);
		
		JScrollPane contentScroll = new JScrollPane();
		contentScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		globalPanel.add(contentScroll, BorderLayout.CENTER);
		
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
		contentScroll.setViewportView(contentPanel);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		
	}
	
	
	
	private void saveFile() {
		if (saved || loadedFile == null)
			return;
		
		try {
			loadedFile.save();
		} catch (TransformerException | ParserConfigurationException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		setSaved(true);
		
	}

	public void loadFile(String path) {
		
		if (path.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please specify a file path", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!saved) {
			int ret = JOptionPane.showOptionDialog(this, "Do you want to save the current file ?", "Save current file", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if (ret == JOptionPane.CANCEL_OPTION)
				return;
			if (ret == JOptionPane.YES_OPTION)
				saveFile();
		}
		
		clearConfigEntries();
		
		
		ParameterFile pFile;
		
		
		try {
			pFile = new ParameterFile(path, true, true);
			
			for (Parameter p : pFile.getAll()) {
				addConfigEntry(p);
			}
			
			
			loadedFile = pFile;
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
			clearConfigEntries();
			return;
		}
		
	}
	
	
	private void clearConfigEntries() {
		contentPanel.removeAll();
		contentPanel.revalidate();
		contentPanel.repaint();
	}
	
	private void addConfigEntry(Parameter p) {
		contentPanel.add(ParameterPanel.fromParameter(this, contentPanel.getComponentCount(), p));
		contentPanel.revalidate();
		contentPanel.repaint();
	}
	
	
	public void setFilePathAndLoad(String path) {
		textField.setText(path);
		loadFile(path);
	}
	
	
	
	public void onContentModify() {
		setSaved(false);
		if (autosave)
			saveFile();
	}
	
	private void setSaved(boolean s) {
		saved = s;
		updateSaveButton();
	}
	
	
	private void setAutosave(boolean as) {
		autosave = as;
		updateSaveButton();
		if (autosave && !saved) {
			saveFile();
		}
	}
	
	
	private void updateSaveButton() {
		btnSave.setEnabled(!autosave && !saved);
	}
	
}
