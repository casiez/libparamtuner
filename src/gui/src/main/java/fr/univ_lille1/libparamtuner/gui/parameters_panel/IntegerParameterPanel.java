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
import fr.univ_lille1.libparamtuner.parameters.IntegerParameter;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class IntegerParameterPanel extends ParameterPanel {
	
	
	public IntegerParameterPanel(MainFrame f, int index, IntegerParameter p) {
		super(f, index, p);
		
		boolean minMaxValid = p.getMax() != p.getMin();
		
		long value = !minMaxValid ? p.getValue()
				: (p.getValue() < p.getMin()) ? ((long) p.getMin())
						: (p.getValue() > p.getMax()) ? ((long) p.getMax()) : p.getValue();
		
		Spinner<Double> spinner = new Spinner<>(
				minMaxValid ? (long) p.getMin() : Long.MIN_VALUE,
				minMaxValid ? (long) p.getMax() : Long.MAX_VALUE, value);
		spinner.setEditable(true);
		
		// pressing ENTER after editing the spinner value is no more required
		// Here is how the value is automatically updated every time the editor's value change :
		spinner.getEditor().textProperty().addListener((o, old, newValue) -> {
			Double newV = null;
			try {
				newV = spinner.getValueFactory().getConverter().fromString(newValue);
			} catch (Exception e) {
				// ignore
			}
			if (newV != null) {
				spinner.getValueFactory().setValue(newV);
			}
		});
		spinner.valueProperty().addListener((o, old, newValue) -> {
			p.setValue(newValue.longValue());
			notifyContentModification();
		});
		
		
		
		if (minMaxValid) {
			Slider slider = new Slider((long) p.getMin(), (long) p.getMax(), value);
			slider.setShowTickMarks(false);
			slider.setShowTickLabels(false);
			slider.setMajorTickUnit(1);
			slider.setMinorTickCount(0);
			slider.setSnapToTicks(true);
			
			// manual binding (because auto binding does'nt not round double values)
			spinner.valueProperty().addListener((o, old, newValue) -> {
				slider.setValue(Math.round(newValue));
			});
			slider.valueProperty().addListener((o, old, newValue) -> {
				spinner.getValueFactory().setValue((double)Math.round((Double)newValue));
			});
			add(slider);
			HBox.setHgrow(slider, Priority.ALWAYS);
		}
		
		add(spinner);
		
		
	}
	
	
}
