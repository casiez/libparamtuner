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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Parameter {
	
	public final String name;
	protected String value;
	protected double min;
	protected double max;
	
	protected Parameter(String n, String v, double m, double M) {
		name = n;
		value = v;
		setMinMax(m, M);
	}
	
	protected Parameter(String n, String v) {
		this(n, v, 0, 0);
	}
	
	protected Parameter(String n) {
		this(n, "", 0, 0);
	}
	
	protected Parameter(String n, double m, double M) {
		this(n, "", m, M);
	}
	
	public String getStringValue() {
		return value;
	}
	
	public Type getType() {
		return Type.getTypeFromParamInstance(getClass());
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}
	
	public void setMinMax(double m, double M) {
		if (m > M) {
			min = M;
			max = m;
		}
		else {
			min = m;
			max = M;
		}
	}
	
	
	
	/* package */ Parameter(Element xmlEl) {
		name = xmlEl.getTagName();
		value = xmlEl.getAttribute("value");
		
		double m, M;
		
		try {
			m = Double.parseDouble(xmlEl.getAttribute("min"));
		} catch (NumberFormatException e) {
			m = 0;
		}
		try {
			M = Double.parseDouble(xmlEl.getAttribute("max"));
		} catch (NumberFormatException e) {
			M = 0;
		}
		setMinMax(m, M);
	}
	
	
	/* package */ Element toXMLElement(Document doc) {
		Element el = doc.createElement(name);
		el.setAttribute("value", value);
		el.setAttribute("type", Type.getTypeFromParamInstance(this.getClass()).getTypeAttributesValues()[0]);
		if (min != 0 || max != 0) {
			el.setAttribute("min", Double.toString(min));
			el.setAttribute("max", Double.toString(max));
		}
		return el;
	}
	
	
	
	/* package */ static Parameter fromXMLElement(Element el) throws Exception {
		String typeAttrValue = el.getAttribute("type");
		
		Type type = Type.getType(typeAttrValue);
		
		if (type == null)
			throw new IllegalArgumentException("Element has not a valid type attribute : '" + type + "'");
		
		return type.parameterClass.getDeclaredConstructor(Element.class).newInstance(el);
		
	}
	
	
	
	
	
}
