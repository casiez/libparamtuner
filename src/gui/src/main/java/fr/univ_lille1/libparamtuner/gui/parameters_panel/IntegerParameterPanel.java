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

import java.awt.EventQueue;

import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import fr.univ_lille1.libparamtuner.gui.MainFrame;
import fr.univ_lille1.libparamtuner.parameters.IntegerParameter;

public class IntegerParameterPanel extends ParameterPanel {
	private static final long serialVersionUID = 1L;
	
	private JSlider slider = null;
	
	/*
	 * Theses private properties are use to avoid pseudo-infinite loop between
	 * update events of the spinner and the slider.
	 * This is because when one component is updated by the user, the other component is updated automatically.
	 * If the value is converted between this two components, their listeners may be called multiple times.
	 * 
	 * We also ensure that notifyContentModification() is only called once per user interaction.
	 */
	private boolean isSpinnerChanging = false, isSliderChanging = false;

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
			if (isSpinnerChanging)
				return;
			try {
				isSpinnerChanging = true;
				
				if (!isSliderChanging) {
					p.setValue(((Double)spinner.getValue()).intValue());
					notifyContentModification();
					if (slider != null)
						slider.setValue(((Double)spinner.getValue()).intValue());
				}
			} finally {
				EventQueue.invokeLater(() -> {
					isSpinnerChanging = false;
				});
			}
		});
		
		
		
		if (minMaxValid) {
			slider = new JSlider(SwingConstants.HORIZONTAL, (int)p.getMin(), (int)p.getMax(), (int)value);
			slider.setPaintTicks(false);
			slider.setPaintLabels(false);
			slider.addChangeListener(e -> {
				if (isSliderChanging)
					return;
				try {
					isSliderChanging = true;
					
					int newValue = slider.getValue();
					if (!isSpinnerChanging) {
						p.setValue(newValue);
						notifyContentModification();
						spinner.setValue((double)newValue);
					}
				} finally {
					isSliderChanging = false;
				}
			});
			add(slider);
		}
		
		add(spinner);
		
		
	}
	
	
}
