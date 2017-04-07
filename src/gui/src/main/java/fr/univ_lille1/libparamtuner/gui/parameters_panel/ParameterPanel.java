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
import fr.univ_lille1.libparamtuner.parameters.FloatParameter;
import fr.univ_lille1.libparamtuner.parameters.IntegerParameter;
import fr.univ_lille1.libparamtuner.parameters.Parameter;
import fr.univ_lille1.libparamtuner.parameters.StringParameter;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public abstract class ParameterPanel extends HBox {
	
	protected final MainFrame frame;
	protected final Parameter parameter;
	
	public ParameterPanel(MainFrame f, int index, Parameter p) {
		super(3);
		frame = f;
		parameter = p;
		
		
		
		setBackground(new Background(new BackgroundFill((index % 2 == 0) ? Color.rgb(220, 220, 220) : Color.TRANSPARENT, null, null)));
		setBorder(new Border(new BorderStroke(Color.TRANSPARENT, null, null, new BorderWidths(2))));
		setAlignment(Pos.CENTER_LEFT);
		
		Label l = new Label(p.name);
		l.setTooltip(new Tooltip(p.name + " of type " + p.getType().name()));
		
		
		// the minWidth property of the label is bounded to the global "minWidth" property.
		l.minWidthProperty().bind(f.minLabelSize);
		
		// listener that update the global "minWidth" property if the current label is larger
		// so other labels will have the same "minWidth".
		l.widthProperty().addListener((o, old, newValue) -> {
			if ((Double)newValue > f.minLabelSize.doubleValue()) {
				f.minLabelSize.setValue(newValue);
			}
		});
		
		
		
		add(l);
		
		
	}
	
	
	
	protected void add(Node e) {
		getChildren().add(e);
	}
	
	
	public void notifyContentModification() {
		frame.onContentModify();
	}
	
	
	
	public static ParameterPanel fromParameter(MainFrame f, int index, Parameter p) {
		if (p instanceof BooleanParameter) {
			return new BooleanParameterPanel(f, index, (BooleanParameter) p);
		}
		if (p instanceof FloatParameter) {
			return new FloatParameterPanel(f, index, (FloatParameter) p);
		}
		if (p instanceof IntegerParameter) {
			return new IntegerParameterPanel(f, index, (IntegerParameter) p);
		}
		if (p instanceof StringParameter) {
			return new StringParameterPanel(f, index, (StringParameter) p);
		}
		
		throw new IllegalArgumentException("Unsupported Parameter type : " + p.getClass().getName());
	}
	
	
}
