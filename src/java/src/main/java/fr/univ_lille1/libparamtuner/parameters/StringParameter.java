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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StringParameter extends Parameter {
	
	private List<String> values = new ArrayList<>();
	
	public StringParameter(String n, String v, String... possibleValues) {
		super(n);
		setValue(v);
		
		for (String pV : possibleValues) {
			if (pV != null)
				values.add(pV);
		}
		
	}
	
	/* package */ StringParameter(Element el) {
		super(el);
		
		NodeList valueNodes = el.getChildNodes();
		for (int i = 0; i < valueNodes.getLength(); i++) {
			Node n = valueNodes.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element valueEl = (Element) n;
			if (!valueEl.getTagName().equalsIgnoreCase("value"))
				continue;
			String v = valueEl.getTextContent();
			if (v != null)
				values.add(v);
		}
		
	}
	
	public void setValue(String v) {
		if (v == null)
			throw new IllegalArgumentException("value can't be null");
		value = v;
	}
	
	public String getValue() {
		return value;
	}
	
	
	public List<String> getPossibleValues() {
		return Collections.unmodifiableList(values);
	}
	
	
	
	@Override
	/* package */ Element toXMLElement(Document doc) {
		Element el = super.toXMLElement(doc);
		for (String value : values) {
			Element valueEl = doc.createElement("value");
			valueEl.appendChild(doc.createTextNode(value));
			el.appendChild(valueEl);
		}
		return el;
	}
	
	
}
