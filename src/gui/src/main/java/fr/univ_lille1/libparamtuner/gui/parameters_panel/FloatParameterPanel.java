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
import fr.univ_lille1.libparamtuner.parameters.FloatParameter;

public class FloatParameterPanel extends ParameterPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int SLIDER_PRECISION = 1000; // TODO adapter pour régler les soucis de précisions
	
	private JSlider slider = null;

	public FloatParameterPanel(MainFrame f, int index, FloatParameter p) {
		super(f, index, p);
		
		boolean minMaxValid = p.getMax() != p.getMin();
		
		double value = !minMaxValid ? p.getValue() :
			(p.getValue() < p.getMin()) ? p.getMin() :
				(p.getValue() > p.getMax()) ? p.getMax() :
					p.getValue();

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(value,
				minMaxValid ? p.getMin() : -Double.MAX_VALUE,
				minMaxValid ? p.getMax() : Double.MAX_VALUE, 1));
		spinner.addChangeListener(e -> {
			if (slider != null)
				slider.setValue(realValueToSliderValue((double)spinner.getValue(), p));
			p.setValue((double)spinner.getValue());
			notifyContentModification();
		});
		
		
		
		if (minMaxValid) {
			slider = new JSlider(SwingConstants.HORIZONTAL, 0, SLIDER_PRECISION, realValueToSliderValue(value, p));
			// s need to be final to be used in following listener :
			slider.addChangeListener(e -> {
				double realValue = sliderValueToRealValue(slider.getValue(), p);
				spinner.setValue(realValue);
				p.setValue(realValue);
				notifyContentModification();
			});
			add(slider);
		}
		
		add(spinner);
		
		
	}
	
	
	private static double sliderValueToRealValue(int sldrValue, FloatParameter p) {
		return sldrValue * (p.getMax() - p.getMin()) / SLIDER_PRECISION + p.getMin();
	}
	
	private static int realValueToSliderValue(double realValue, FloatParameter p) {
		return (int)((realValue - p.getMin()) * SLIDER_PRECISION / (p.getMax() - p.getMin()));
	}
	
}
