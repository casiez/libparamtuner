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

import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import fr.univ_lille1.libparamtuner.gui.MainFrame;
import fr.univ_lille1.libparamtuner.parameters.IntegerParameter;

public class IntegerParameterPanel extends ParameterPanel {
	private static final long serialVersionUID = 1L;
	
	private JSlider slider = null;

	public IntegerParameterPanel(MainFrame f, int index, IntegerParameter p) {
		super(f, index, p);
		
		boolean minMaxValid = p.getMax() != p.getMin();
		
		long value = !minMaxValid ? p.getValue() :
			(p.getValue() < p.getMin()) ? ((long)p.getMin()) :
				(p.getValue() > p.getMax()) ? ((long)p.getMax()) :
					p.getValue();
		
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(value,
				minMaxValid ? (long)p.getMin() : Long.MIN_VALUE,
				minMaxValid ? (long)p.getMax() : Long.MAX_VALUE, 1));
		spinner.addChangeListener(e -> {
			if (slider != null) {
				slider.setValue(((Double)spinner.getValue()).intValue());
			}
			p.setValue(((Double)spinner.getValue()).intValue());
			notifyContentModification();
		});
		
		
		
		if (minMaxValid) {
			slider = new JSlider(SwingConstants.HORIZONTAL, (int)p.getMin(), (int)p.getMax(), (int)value);
			// s need to be final to be used in following listener :
			slider.addChangeListener(e -> {
				int realValue = slider.getValue();
				spinner.setValue((double)realValue);
				p.setValue(realValue);
				notifyContentModification();
			});
			add(slider);
		}
		
		add(spinner);
		
		
	}
	
	
}
