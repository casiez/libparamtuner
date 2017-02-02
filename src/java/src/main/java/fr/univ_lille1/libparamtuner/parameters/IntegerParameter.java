package fr.univ_lille1.libparamtuner.parameters;

import org.w3c.dom.Element;

public class IntegerParameter extends Parameter {
	
	public IntegerParameter(String n, long v, long m, long M) {
		super(n, m, M);
		setValue(v);
	}
	
	public IntegerParameter(String n, long v) {
		this(n, v, 0, 0);
	}
	
	/* package */ IntegerParameter(Element el) {
		super(el);
	}
	
	public void setValue(long v) {
		value = Long.toString(v);
	}
	
	public long getValue() {
		return Long.parseLong(value);
	}
	
	
}
