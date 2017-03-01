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
package fr.univ_lille1.libparamtuner.gui.parameters_panel;

import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.univ_lille1.libparamtuner.gui.MainFrame;
import fr.univ_lille1.libparamtuner.parameters.StringParameter;

public class StringParameterPanel extends ParameterPanel {
	private static final long serialVersionUID = 1L;

	public StringParameterPanel(MainFrame f, int index, StringParameter p) {
		super(f, index, p);
		

		JEditorPane editorPane = new JEditorPane("text/plain", p.getValue()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				Dimension pSize = super.getPreferredSize();
				System.out.println("p "+pSize);
				System.out.println("m "+getMinimumSize());
				return pSize;
			}
			
			@Override
			public Dimension getMinimumSize() {
				return new Dimension(10, 10);
			}
		};
		editorPane.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				p.setValue(editorPane.getText());
				notifyContentModification();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				p.setValue(editorPane.getText());
				notifyContentModification();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				p.setValue(editorPane.getText());
				notifyContentModification();
			}
		});
		add(editorPane);
		
	}
	
}
