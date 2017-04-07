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

import fr.univ_lille1.libparamtuner.gui.MainFrame;
import fr.univ_lille1.libparamtuner.parameters.BooleanParameter;
import javafx.scene.control.CheckBox;

public class BooleanParameterPanel extends ParameterPanel {
	
	public BooleanParameterPanel(MainFrame f, int index, BooleanParameter p) {
		super(f, index, p);
		
		CheckBox box = new CheckBox("Boolean value");
		box.setSelected(p.getValue());
		box.selectedProperty().addListener((o, old, newValue) -> {
			p.setValue(newValue);
			notifyContentModification();
		});
		box.setBackground(getBackground());
		add(box);
	}
	
}
