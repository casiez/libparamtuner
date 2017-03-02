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
import fr.univ_lille1.libparamtuner.parameters.FloatParameter;

public class FloatParameterPanel extends ParameterPanel {
	private static final long serialVersionUID = 1L;
	
	private JSlider slider = null;
	
	/*
	 * Theses private properties are use to avoid pseudo-infinite loop between
	 * update events of the spinner and the slider.
	 * This is because when one component is updated by the user, the other component is updated to.
	 * If the value is converted between this two components, their listeners may be called multiple times.
	 * 
	 * We also ensure that notifyContentModification() is only called once per user interaction.
	 */
	private boolean isSpinnerChanging = false, isSliderChanging = false;

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
			if (isSpinnerChanging)
				return;
			try {
				isSpinnerChanging = true;
				
				if (!isSliderChanging) {
					p.setValue((double)spinner.getValue());
					notifyContentModification();
					if (slider != null)
						slider.setValue(realValueToSliderValue((double)spinner.getValue(), p));
				}
			} finally {
				EventQueue.invokeLater(() -> {
					isSpinnerChanging = false;
				});
			}
		});
		
		
		
		if (minMaxValid) {
			/*
			 * JSlider doesn't support double or float values, so we have to convert integer values to the real value
			 * and vice-versa (via realValueToSliderValue() and sliderValueToRealValue()).
			 * JSlider need to have a specific range to avoid unwanted value updates between 
			 */
			double sliderPrecision = p.getMax() - p.getMin();
			while (sliderPrecision < 1000) {
				sliderPrecision *= 2;
			}
			int intSliderPrecision = (sliderPrecision > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) sliderPrecision;
			
			slider = new JSlider(SwingConstants.HORIZONTAL, 0, intSliderPrecision, 0);
			slider.setValue(realValueToSliderValue(value, p));
			slider.addChangeListener(e -> {
				if (isSliderChanging)
					return;
				try {
					isSliderChanging = true;
					
					double realValue = sliderValueToRealValue(slider.getValue(), p);
					if (!isSpinnerChanging) {
						p.setValue(realValue);
						notifyContentModification();
						spinner.setValue(realValue);
					}
				} finally {
					isSliderChanging = false;
				}
			});
			add(slider);
		}
		
		add(spinner);
		
		
	}
	
	
	private double sliderValueToRealValue(int sldrValue, FloatParameter p) {
		if (slider == null) return 0;
		return sldrValue * (p.getMax() - p.getMin()) / slider.getMaximum() + p.getMin();
	}
	
	private int realValueToSliderValue(double realValue, FloatParameter p) {
		if (slider == null) return 0;
		return (int)((realValue - p.getMin()) * slider.getMaximum() / (p.getMax() - p.getMin()));
	}
	
}
