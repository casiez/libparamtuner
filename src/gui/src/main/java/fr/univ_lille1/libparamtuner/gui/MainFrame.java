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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	public final JScrollPane contentScroll;
	private JButton btnSave;
	
	
	private ParameterFile loadedFile = null;
	private boolean saved = true;
	private boolean autosave = false;
	
	
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("ParamTuner GUI");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (confirmSaveBeforeClosingFile()) {
					dispose();
					System.exit(0);
				}
			}
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
		
		contentScroll = new JScrollPane();
		contentScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		globalPanel.add(contentScroll, BorderLayout.CENTER);
		
		contentPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(contentScroll.getViewport().getSize().width, super.getPreferredSize().height);
			}
			
			@Override
			public Dimension getMaximumSize() {
				return new Dimension(contentScroll.getViewport().getSize().width, super.getMaximumSize().height);
			}
			
		};
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
	
	/**
	 * Ask to the user if he want to save or not save the file before closing it.
	 * If the user want to save the file, it is saved inside this method.
	 * @return true if the user want to close the file (with or without saving) or if the file is already saved, false if the user click cancel.
	 */
	private boolean confirmSaveBeforeClosingFile() {
		if (saved)
			return true;
		int ret = JOptionPane.showOptionDialog(this, "Do you want to save the current file ?", "Save current file", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
		if (ret == JOptionPane.CANCEL_OPTION)
			return false;
		if (ret == JOptionPane.YES_OPTION)
			saveFile();
		return true;
	}

	public void loadFile(String path) {
		
		if (path.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please specify a file path", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!confirmSaveBeforeClosingFile())
			return;
		
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
