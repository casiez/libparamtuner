package fr.univ_lille1.pji.libparamtuner.parameters;

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
