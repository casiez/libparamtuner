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

import java.util.List;

import fr.univ_lille1.libparamtuner.gui.MainFrame;
import fr.univ_lille1.libparamtuner.parameters.StringParameter;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class StringParameterPanel extends ParameterPanel {
	
	public StringParameterPanel(MainFrame f, int index, StringParameter p) {
		super(f, index, p);
		
		List<String> possibleValues = p.getPossibleValues();
		if (possibleValues.isEmpty()) {
			// no predefined values
			
			TextArea textArea = new TextArea(p.getValue());
			textArea.setPrefWidth(20);
			textArea.textProperty().addListener((ChangeListener<String>) ((observable, old, newText) -> {
				p.setValue(newText);
				notifyContentModification();
			}));
			add(textArea);
			HBox.setHgrow(textArea, Priority.ALWAYS);
		}
		else {
			// at least one predefined value
			ComboBox<String> box = new ComboBox<>(FXCollections.observableList(possibleValues));
			
			// user can always set custom value if he want (just write in the combo box)
			box.setEditable(true);
			
			box.setValue(p.getValue()); // the current value may not be in the list of predefined values
			
			box.valueProperty().addListener((observable, old, newText) -> {
				p.setValue(newText);
				notifyContentModification();
			});
			add(box);
		}
		
		
		
	}
	
}
