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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fr.univ_lille1.libparamtuner.gui.MainFrame;
import fr.univ_lille1.libparamtuner.parameters.BooleanParameter;
import fr.univ_lille1.libparamtuner.parameters.FloatParameter;
import fr.univ_lille1.libparamtuner.parameters.IntegerParameter;
import fr.univ_lille1.libparamtuner.parameters.Parameter;
import fr.univ_lille1.libparamtuner.parameters.StringParameter;

public abstract class ParameterPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected final MainFrame frame;
	protected final Parameter parameter;
	
	public ParameterPanel(MainFrame f, int index, Parameter p) {
		frame = f;
		parameter = p;
		
		setBackground((index % 2 == 0) ? new Color(215, 215, 215) : new Color(225, 225, 225));
		setBorder(new EmptyBorder(2, 2, 2, 2));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(new JLabel(p.name));
		
	}
	
	
	public void notifyContentModification() {
		frame.onContentModify();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(frame.contentScroll.getViewport().getSize().width, super.getPreferredSize().height);
	}
	
	
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(frame.contentScroll.getViewport().getSize().width, super.getMaximumSize().height);
	}
	
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(frame.contentScroll.getViewport().getSize().width, super.getMinimumSize().height);
	}
	
	
	
	public static ParameterPanel fromParameter(MainFrame f, int index, Parameter p) {
		if (p instanceof BooleanParameter) {
			return new BooleanParameterPanel(f, index, (BooleanParameter)p);
		}
		if (p instanceof FloatParameter) {
			return new FloatParameterPanel(f, index, (FloatParameter)p);
		}
		if (p instanceof IntegerParameter) {
			return new IntegerParameterPanel(f, index, (IntegerParameter)p);
		}
		if (p instanceof StringParameter) {
			return new StringParameterPanel(f, index, (StringParameter)p);
		}
		
		throw new IllegalArgumentException("Unsupported Parameter type : "+p.getClass().getName());
	}
	
	
}
