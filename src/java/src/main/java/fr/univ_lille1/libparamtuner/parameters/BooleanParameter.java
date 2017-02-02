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
package fr.univ_lille1.libparamtuner.parameters;

import org.w3c.dom.Element;

public class BooleanParameter extends Parameter {
	
	public BooleanParameter(String n, boolean v) {
		super(n);
		setValue(v);
	}
	
	/* package */ BooleanParameter(Element el) {
		super(el);
	}
	
	public void setValue(boolean v) {
		value = Boolean.toString(v);
	}
	
	public boolean getValue() {
		return Boolean.parseBoolean(value);
	}
	
	
}
