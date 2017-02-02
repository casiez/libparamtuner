package fr.univ_lille1.libparamtuner.parameters;

import org.w3c.dom.Element;

public class StringParameter extends Parameter {
	
	public StringParameter(String n, String v) {
		super(n);
		setValue(v);
	}
	
	/* package */ StringParameter(Element el) {
		super(el);
	}
	
	public void setValue(String v) {
		if (v == null)
			throw new IllegalArgumentException("value can't be null");
		value = v;
	}
	
	public String getValue() {
		return value;
	}
	
	
}
