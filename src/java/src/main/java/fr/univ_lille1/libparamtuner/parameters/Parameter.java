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
	protected String desc = null;
	
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
	
	public void setDesc(String d) {
		if (d == null)
			d = "";
		desc = d;
	}
	
	public String getDesc() {
		return desc;
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
		
		setDesc(xmlEl.getAttribute("desc"));
	}
	
	
	/* package */ Element toXMLElement(Document doc) {
		Element el = doc.createElement(name);
		el.setAttribute("type", getType().getTypeAttributesValues()[0]);
		if (min != 0 || max != 0) {
			boolean isInteger = getClass() == IntegerParameter.class;
			el.setAttribute("min", isInteger ? ""+(int)min : ""+min);
			el.setAttribute("max", isInteger ? ""+(int)max : ""+max);
		}
		el.setAttribute("value", value);
		if (!desc.isEmpty())
			el.setAttribute("desc", desc);
		return el;
	}
	
	

	
	/**
	 * @deprecated Prefer using {@link #toXMLElement(Document)} because it is more flexible
	 * and allow more clean code when overriding. For the current method, overriding it
	 * requires to reimplement the code to add specific functionnality related to the subclass
	 */
	@Deprecated
	/* package */ String toXMLstring() {
		String res = "<";
		res += name + " type=\"";
		
		String type = Type.getStrTypeFromParamInstance(getClass());
		res+= type + "\" ";
		
		if (min != 0 || max != 0) {
			if (getClass() == IntegerParameter.class) {
				res += "min=\"" + (int)min + "\" ";
				res += "max=\"" + (int)max + "\" ";
			} else {
				res += "min=\"" + min + "\" ";
				res += "max=\"" + max + "\" ";
			}
		}
		
		res += "value=\"" + value + "\"/>";
		
		return res;
	}
	
	
	
	
	
	
	
	/* package */ static Parameter fromXMLElement(Element el) throws Exception {
		Type type = Type.getType(el.getAttribute("type"));
		
		if (type == null)
			throw new IllegalArgumentException("Element has not a valid type attribute : '" + type + "'");
		
		return type.parameterClass.getDeclaredConstructor(Element.class).newInstance(el);
		
	}	
	
}
